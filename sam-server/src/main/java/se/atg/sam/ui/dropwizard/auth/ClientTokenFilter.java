package se.atg.sam.ui.dropwizard.auth;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.HttpHeaders;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import se.atg.sam.auth.OAuth2Service;
import se.atg.sam.model.auth.OAuth2IdToken;
import se.atg.sam.ui.dropwizard.configuration.OAuthConfiguration;

public class ClientTokenFilter implements ClientRequestFilter {

  private static final Object CACHE_KEY = new Object();

  private LoadingCache<Object, OAuth2IdToken> tokenCache;

  public ClientTokenFilter(final String username, final OAuthConfiguration config) {

    final OAuth2Service oAuth2Service = new OAuth2Service(config);
    tokenCache = CacheBuilder.newBuilder()
      .expireAfterWrite(OAuth2Service.EXPIRY, TimeUnit.SECONDS)
      .build(CacheLoader.from(() ->
        oAuth2Service.createIdToken(username)
      ));
  }

  @Override
  public void filter(ClientRequestContext request) throws IOException {
    if (request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
      return;
    }
    request.getHeaders().add(HttpHeaders.AUTHORIZATION, createHeader());
  }

  private StringBuilder createHeader() {
    try {
      final OAuth2IdToken token = tokenCache.get(CACHE_KEY);
      return new StringBuilder().append(token.type).append(' ').append(token.token);
    } catch (ExecutionException exc) {
      throw new RuntimeException("Failed to get authentication token", exc);
    }
  }

  public static ClientTokenFeature feature(String username, OAuthConfiguration config) {
    final ClientTokenFilter filter = new ClientTokenFilter(username, config);
    return new ClientTokenFeature(filter);
  }

  public static class ClientTokenFeature implements Feature {

    private ClientTokenFilter filter;

    public ClientTokenFeature(ClientTokenFilter filter) {
      this.filter = filter;
    }

    @Override
    public boolean configure(FeatureContext context) {
      context.register(filter);
      return true;
    }
  }
}

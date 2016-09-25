package se.atg.cmdb.ui.rest;

import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.joda.time.Weeks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTSigner.Options;
import com.google.common.collect.ImmutableMap;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import se.atg.cmdb.model.auth.OAuth2AccessToken;
import se.atg.cmdb.model.auth.OAuth2Code;
import se.atg.cmdb.model.auth.OAuth2IdToken;
import se.atg.cmdb.model.github.User;
import se.atg.cmdb.ui.dropwizard.configuration.OAuthConfiguration;

@Path("/")
@Api("oauth2")
@Produces(Defaults.MEDIA_TYPE_JSON)
public class OAuth2Resource {

  private static final int EXPIRY = Weeks.ONE.toStandardSeconds().getSeconds();
  private static final Options JWT_OPTIONS = new Options().setIssuedAt(true).setJwtId(true).setExpirySeconds(EXPIRY);
  private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2Resource.class);

  private final Client restClient;
  private final OAuthConfiguration config;
  private final JWTSigner signer;

  public OAuth2Resource(Client restClient, OAuthConfiguration config) {
    this.restClient = restClient;
    this.config = config;
    this.signer = new JWTSigner(config.getIdTokenSignKey());
  }

  @POST
  @Path("/services/oauth2/token")
  @ApiOperation("Generate a OAuth2 bearer token (JWT)")
  public OAuth2IdToken token(
    @ApiParam("Code") OAuth2Code code
  ) {
    try {
      LOGGER.info("Generate token for {}", code);

      final OAuth2AccessToken accesstoken = requestAccessToken(code);
      final User user = requestAuthenticatedUser(accesstoken);
      final OAuth2IdToken idToken = createIdToken(user.login);

      LOGGER.info("Issued {} for {}", idToken, user);
      return idToken;
    } catch (RuntimeException exc) {
      LOGGER.error("Failed to verify {}", code, exc);
      throw new WebApplicationException("Failed to authenticate with given code and state");
    }
  }

  private OAuth2AccessToken requestAccessToken(OAuth2Code code) {
    return restClient.target(config.getAccessTokenEndpoint())
      .queryParam("client_id", config.getClientId())
      .queryParam("client_secret", config.getClientSecret())
      .queryParam("code", code.code)
      .queryParam("state", code.state)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .post(Entity.form(new Form()), OAuth2AccessToken.class);
  }

  private User requestAuthenticatedUser(OAuth2AccessToken accessToken) {
    return restClient.target(config.getUserEndpoint())
      .request(MediaType.APPLICATION_JSON_TYPE)
      .header(HttpHeaders.AUTHORIZATION, "token " + accessToken.token)
      .get(User.class);
  }

  private OAuth2IdToken createIdToken(final String subject) {

    final Map<String,Object> claims = new ImmutableMap.Builder<String,Object>()
      .put("iss", config.getIdTokenIssuer())
      .put("aud", config.getIdTokenIssuer())
      .put("sub", subject)
      .build();

    final String token = signer.sign(claims, JWT_OPTIONS);
    return new OAuth2IdToken(token);
  }
}

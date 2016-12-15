package se.atg.sam.ui.rest;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.jersey.caching.CacheControl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import se.atg.sam.auth.OAuth2Service;
import se.atg.sam.helpers.RestHelper;
import se.atg.sam.model.auth.OAuth2AccessToken;
import se.atg.sam.model.auth.OAuth2Code;
import se.atg.sam.model.auth.OAuth2IdToken;
import se.atg.sam.model.github.User;
import se.atg.sam.ui.dropwizard.configuration.OAuthConfiguration;

@Path("/")
@Api("oauth2")
@Produces(Defaults.MEDIA_TYPE_JSON)
public class OAuth2Resource {

  private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2Resource.class);

  private final Client restClient;
  private final OAuthConfiguration config;
  private final OAuth2Service service;

  public OAuth2Resource(Client restClient, OAuth2Service service, OAuthConfiguration config) {
    this.restClient = restClient;
    this.config = config;
    this.service = service;
  }

  @GET
  @PermitAll
  @Path("/services/oauth2/user")
  @CacheControl(noCache = true)
  @ApiOperation("Get authenticated user.")
  public User getAuthenticatedUser(
    @Context SecurityContext securityContext
  ) {
    final se.atg.sam.model.User user = RestHelper.getUser(securityContext);
    return new User() {{
      this.login = user.name;
    }};
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
      final OAuth2IdToken idToken = service.createIdToken(user.login);

      LOGGER.info("Issued {} for {}", idToken, user);
      return idToken;
    } catch (RuntimeException exc) {
      LOGGER.error("Failed to verify {}", code, exc);
      throw new WebApplicationException("Failed to authenticate with given code and state");
    }
  }

  private OAuth2AccessToken requestAccessToken(OAuth2Code code) {
    return requestAccessToken(code.code, code.state, config.getClientId(), config.getClientSecret());
  }

  private OAuth2AccessToken requestAccessToken(String code, String state, String clientId, String clientSecret) {
    return restClient.target(config.getAccessTokenEndpoint())
      .queryParam("client_id", clientId)
      .queryParam("client_secret", clientSecret)
      .queryParam("code", code)
      .queryParam("state", state)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .post(Entity.form(new Form()), OAuth2AccessToken.class);
  }

  private User requestAuthenticatedUser(OAuth2AccessToken accessToken) {
    return restClient.target(config.getUserEndpoint())
      .request(MediaType.APPLICATION_JSON_TYPE)
      .header(HttpHeaders.AUTHORIZATION, "token " + accessToken.token)
      .get(User.class);
  }
}

package se.atg.sam.ui.rest;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWTVerifyException;

import io.dropwizard.jersey.caching.CacheControl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import se.atg.sam.auth.OAuth2Service;
import se.atg.sam.auth.OAuth2Service.JwtField;
import se.atg.sam.helpers.Mapper;
import se.atg.sam.helpers.RestHelper;
import se.atg.sam.model.auth.OAuth2Code;
import se.atg.sam.model.auth.OAuth2IdToken;
import se.atg.sam.model.auth.OAuth2Token;
import se.atg.sam.model.auth.User;
import se.atg.sam.model.github.GithubUser;
import se.atg.sam.ui.dropwizard.configuration.OAuthConfiguration;
import se.atg.sam.ui.dropwizard.configuration.OAuthUserConfiguration;

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
    return RestHelper.getUser(securityContext);
  }

  @POST
  @Path("/services/oauth2/token")
  @ApiOperation("Generate a OAuth2 bearer token (JWT)")
  public OAuth2IdToken token(
    @ApiParam("Code") OAuth2Code code
  ) {
    try {
      LOGGER.info("Generate token for {}", code);

      final OAuth2Token accesstoken = requestAccessToken(code);
      final User user = requestAuthenticatedUser(accesstoken);
      final OAuth2IdToken idToken = service.createIdToken(user.name, user.email);

      LOGGER.info("Issued {} for {}", idToken, user);
      return idToken;
    } catch (RuntimeException exc) {
      LOGGER.error("Failed to verify {}", code, exc);
      throw new WebApplicationException("Failed to authenticate with given code and state");
    }
  }

  private OAuth2Token requestAccessToken(OAuth2Code code) {
    return requestAccessToken(code.code, code.state, config.getClientId(), config.getClientSecret(), config.getOrigin());
  }

  private OAuth2Token requestAccessToken(String code, String state, String clientId, String clientSecret, String origin) {

    final Form form = new Form()
      .param("client_id", clientId)
      .param("client_secret", clientSecret)
      .param("code", code)
      .param("state", state)
      .param("redirect_uri", origin + "/oauth")
      .param("grant_type", "authorization_code");

    return restClient.target(config.getAccessTokenEndpoint())
      .request(MediaType.APPLICATION_JSON_TYPE)
      .post(Entity.form(form), OAuth2Token.class);
  }

  private User requestAuthenticatedUser(OAuth2Token token) {

    final OAuthUserConfiguration userConfig = config.getUserConfig();
    if (StringUtils.isEmpty(token.idToken)) {
      return getUserFromGithub(userConfig, token);
    }
    return getUserFromIdToken(userConfig, token);
  }

  private User getUserFromGithub(OAuthUserConfiguration userConfig, OAuth2Token token) {
    final GithubUser githubUser = restClient.target(userConfig.getGithubEndpoint())
      .request(MediaType.APPLICATION_JSON_TYPE)
      .header(HttpHeaders.AUTHORIZATION, "token " + token.accessToken)
      .get(GithubUser.class);
    LOGGER.trace("User from Github {}", githubUser);
    return new User(githubUser.login, Optional.ofNullable(githubUser.email));
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static User getUserFromIdToken(OAuthUserConfiguration userConfig, OAuth2Token token) {

    final Map<String, Object> claims = verifyIdToken(userConfig, token);
    final String subject = (String) claims.get(JwtField.subject.id);
    final List<String> emails = (List) claims.get(JwtField.emails.id);
    LOGGER.trace("Claims from open id connect provider {}", claims);
    return new User(subject, Mapper.singleEntry(emails));
  }

  private static Map<String, Object> verifyIdToken(OAuthUserConfiguration userConfig, OAuth2Token token) {

    final BigInteger exponent = decodeBase64Integer(userConfig.getIdTokenPublicKeyExponent());
    final BigInteger modulus = decodeBase64Integer(userConfig.getIdTokenPublicKeyModulus());
    final RSAPublicKeySpec rsaKeySpec = new RSAPublicKeySpec(modulus, exponent);
    return verifyIdToken(rsaKeySpec, token.idToken, userConfig.getIdTokenIssuer(), userConfig.getIdTokenAudience());
  }

  private static Map<String, Object> verifyIdToken(RSAPublicKeySpec rsaKeySpec, String idToken, String issuer, String audience) {

    LOGGER.debug("Verifying id-token {} for expected issuer {} for audience {}", idToken, issuer, audience);

    try {
      final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      final RSAPublicKey rsaKey = (RSAPublicKey) keyFactory.generatePublic(rsaKeySpec);
      return OAuth2Service.verify(idToken, rsaKey, audience, issuer);
    } catch (RuntimeException | IOException | GeneralSecurityException e) {
      throw new RuntimeException("Internal error in id-token configuration", e);
    } catch (JWTVerifyException e) {
      throw new RuntimeException("Failed to verify id-token", e);
    }
  }

  private static BigInteger decodeBase64Integer(String base64) {
    final byte[] byteContent = Base64.decodeBase64(base64);
    return new BigInteger(1, byteContent);
  }
}

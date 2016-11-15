package se.atg.cmdb.auth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.joda.time.Weeks;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTSigner.Options;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.google.common.collect.ImmutableMap;

import se.atg.cmdb.model.auth.OAuth2IdToken;
import se.atg.cmdb.ui.dropwizard.configuration.OAuthConfiguration;

public class OAuth2Service {

  public static final String JWT_ISSUER = "iss";
  public static final String JWT_AUDIENCE = "aud";
  public static final String JWT_SUBJECT = "sub";
  public static final String JWT_EXPIRY = "exp";
  public static final String JWT_ISSUED_AT = "iat";
  public static final String JWT_NOT_VALID_BEFORE = "nbf";

  public static final int EXPIRY = Weeks.ONE.toStandardSeconds().getSeconds();
  private static final Options JWT_OPTIONS = new Options().setIssuedAt(true).setJwtId(true).setExpirySeconds(EXPIRY);

  private JWTSigner signer;
  private JWTVerifier verifier;
  private OAuthConfiguration config;

  public OAuth2Service(OAuthConfiguration config) {
    this.config = config;
    this.signer = new JWTSigner(config.getIdTokenSignKey());
    this.verifier = new JWTVerifier(config.getIdTokenSignKey(), config.getIdTokenIssuer(), config.getIdTokenIssuer());
  }

  public OAuth2IdToken createIdToken(final String subject) {

    final Map<String,Object> claims = new ImmutableMap.Builder<String,Object>()
      .put(JwtField.issuer.id, config.getIdTokenIssuer())
      .put(JwtField.audience.id, config.getIdTokenIssuer())
      .put(JwtField.subject.id, subject)
      .build();
    return sign(claims);
  }

  public Map<String, Object> verify(String token) throws GeneralSecurityException, IOException, JWTVerifyException {
    return verifier.verify(token);
  }

  public OAuth2IdToken sign(Map<String, ?> claims) {
    return sign(claims, JWT_OPTIONS);
  }

  @SuppressWarnings("unchecked")
  public OAuth2IdToken sign(Map<String, ?> claims, Options options) {
    final String token = signer.sign((Map<String,Object>) claims, options);
    return new OAuth2IdToken(token);
  }

  public enum JwtField {
    issuer(JWT_ISSUER), audience(JWT_AUDIENCE), subject(JWT_SUBJECT),
    expiry(JWT_EXPIRY), issuedAt(JWT_ISSUED_AT), notValidBefore(JWT_NOT_VALID_BEFORE);

    public final String id;
    JwtField(String id) {
      this.id = id;
    }
  }
}

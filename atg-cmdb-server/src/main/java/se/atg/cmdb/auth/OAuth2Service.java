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

  @SuppressWarnings("unchecked")
  public OAuth2IdToken sign(Map<String, ?> claims) {
    final String token = signer.sign((Map<String,Object>) claims, JWT_OPTIONS);
    return new OAuth2IdToken(token);
  }

  public enum JwtField {
    issuer("iss"), audience("aud"), subject("sub");

    public final String id;
    JwtField(String id) {
      this.id = id;
    }
  }
}

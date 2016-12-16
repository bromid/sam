package se.atg.sam.ui.dropwizard.auth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWTExpiredException;
import com.auth0.jwt.JWTVerifyException;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import se.atg.sam.auth.OAuth2Service;
import se.atg.sam.auth.OAuth2Service.JwtField;
import se.atg.sam.model.User;

public class IdTokenAuthenticator implements Authenticator<String, User> {

  private static final Logger LOGGER = LoggerFactory.getLogger(IdTokenAuthenticator.class);
  private OAuth2Service service;

  public IdTokenAuthenticator(OAuth2Service service) {
    this.service = service;
  }

  @Override
  public Optional<User> authenticate(String token) throws AuthenticationException {
    try {
      return verifyToken(token);
    } catch (RuntimeException | IOException | GeneralSecurityException exc) {
      throw new AuthenticationException("Failed to verify the JWT token.", exc);
    } catch (JWTExpiredException exc) {
      LOGGER.info("The JWT token has expired: {}. {}", token, exc.getLocalizedMessage());
    } catch (JWTVerifyException exc) {
      LOGGER.info("Invalid JWT token supplied: {}. {}", token, exc.getLocalizedMessage());
    }
    return Optional.empty();
  }

  private Optional<User> verifyToken(String token) throws IOException, GeneralSecurityException, JWTVerifyException {
    LOGGER.debug("Authenticating token: {}", token);

    final Map<String, Object> claims = service.verify(token);
    final String subject = (String) claims.get(JwtField.subject.id);
    return Optional.of(new User(subject));
  }
}

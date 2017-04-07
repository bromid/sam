package se.atg.sam.ui.dropwizard.auth;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import se.atg.sam.model.User;

public class BasicAuthenticator implements Authenticator<BasicCredentials, User> {

  private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthenticator.class);

  @Override
  public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {

    final String username = credentials.getUsername();
    LOGGER.debug("Authenticating user {}", username);
    if ("secret".equals(credentials.getPassword())) {
      return Optional.of(new User(username));
    }
    return Optional.empty();
  }
}

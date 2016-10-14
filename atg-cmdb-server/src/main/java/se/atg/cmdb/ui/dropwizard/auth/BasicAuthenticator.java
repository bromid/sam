package se.atg.cmdb.ui.dropwizard.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import se.atg.cmdb.model.User;

public class BasicAuthenticator implements Authenticator<BasicCredentials, User> {

  private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthenticator.class);

  @Override
  public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {

    final String username = credentials.getUsername();
    LOGGER.debug("Authenticating user {}", username);
    if ("secret".equals(credentials.getPassword())) {
      return Optional.of(new User(username));
    }
    return Optional.absent();
  }
}

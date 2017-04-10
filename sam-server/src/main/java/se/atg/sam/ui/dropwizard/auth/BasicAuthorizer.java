package se.atg.sam.ui.dropwizard.auth;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.auth.Authorizer;
import se.atg.sam.model.auth.User;

public class BasicAuthorizer implements Authorizer<User> {

  private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthorizer.class);

  @Override
  public boolean authorize(User user, String role) {
    LOGGER.debug("Authorize user {} for role {}", user, role);
    return ArrayUtils.contains(user.roles, role);
  }
}

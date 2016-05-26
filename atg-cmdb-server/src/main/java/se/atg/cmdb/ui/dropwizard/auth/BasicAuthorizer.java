package se.atg.cmdb.ui.dropwizard.auth;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.auth.Authorizer;
import se.atg.cmdb.model.User;

public class BasicAuthorizer implements Authorizer<User> {

	static final Logger logger = LoggerFactory.getLogger(BasicAuthorizer.class);

    @Override
    public boolean authorize(User user, String role) {
    	logger.debug("Authorize user {} with roles {} for role {}", user, user.roles, role);
        return ArrayUtils.contains(user.roles, role);
    }
}

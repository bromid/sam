package se.atg.cmdb.helpers;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.SecurityContext;

import org.bson.Document;

import io.dropwizard.jersey.validation.Validators;
import se.atg.cmdb.model.User;

public abstract class RESTHelper {

	private static final ValidatorFactory VALIDATOR_FACTORY = Validators.newValidatorFactory();

	public static String verifyHash(Document bson, String etag) {

		final String hash = Mapper.getHash(bson);
		if (hash != null && etag != null && !hash.equals(etag)) {
			throw new WebApplicationException("Invalid precondition. Existing hash: " + hash, 422);
		}
		return hash;
	}

	public static EntityTag getEntityTag(Document server) {
		return new EntityTag(Mapper.getHash(server), false);
	}

	public static <T> void validate(T obj, Class<?>... validationGroups) {

		final Validator validator = VALIDATOR_FACTORY.getValidator();
		final Set<ConstraintViolation<T>> violations = validator.validate(obj, validationGroups);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
	}

	public static User getUser(SecurityContext securityContext) {
		return (User) securityContext.getUserPrincipal();
	}
}

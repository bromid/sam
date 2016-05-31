package se.atg.cmdb.helpers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

import io.dropwizard.jersey.validation.Validators;
import jersey.repackaged.com.google.common.collect.Lists;
import se.atg.cmdb.model.PaginatedCollection;
import se.atg.cmdb.model.User;

public abstract class RESTHelper {

	private static final ValidatorFactory VALIDATOR_FACTORY = Validators.newValidatorFactory();

	public static Optional<Function<String,Bson>> parseFilterFromQuery(String queryString) {

		if (queryString == null || queryString.isEmpty()) {
			return Optional.empty();
		}

		final String[] array = queryString.split(",");
		if (array.length == 1) {
			return Optional.of(t->Filters.eq(t, array[0]));
		}
		return Optional.of(t->Filters.all(t, Arrays.asList(array)));
	}

	public static Optional<String> verifyHash(Document bson, Request request) {

		final Optional<EntityTag> entityTag = getEntityTag(bson);
		entityTag.ifPresent(t->{
			final ResponseBuilder response = request.evaluatePreconditions(t);
			if (response != null) {
				throw new WebApplicationException(response.build());
			}
		});
		return entityTag.map(EntityTag::getValue);
	}

	public static Optional<EntityTag> getEntityTag(Document bson) {

		final Optional<String> hash = Mapper.getHash(bson);
		return hash.map(t->new EntityTag(t, false));
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

	public static <T> PaginatedCollection<T> paginatedList(UriBuilder uriBuilder, Integer limit, Integer start, Stream<T> stream) {

		Stream<T> limitedStream = stream;
		if (start != null && start > 0) {
			limitedStream = limitedStream.skip(start);
		}
		if (limit != null && limit > 0) {
			limitedStream = limitedStream.limit(limit+1);
		}
		final List<T> list = limitedStream.collect(Collectors.toList());
		return paginatedList(uriBuilder, limit, start, list);
	}

	public static <T> PaginatedCollection<T> paginatedList(Iterable<T> iterable) {

		final List<T> list = Lists.newArrayList(iterable);
		return paginatedList(null, null, null, list);
	}

	public static <T> PaginatedCollection<T> paginatedList(Integer limit, Integer start, Iterable<T> iterable) {

		final List<T> list = Lists.newArrayList(iterable);
		return paginatedList(null, limit, start, list);
	}

	public static <T> PaginatedCollection<T> paginatedList(UriBuilder uriBuilder, Integer limit, Integer start, List<T> list) {
		return new PaginatedCollection<T>(uriBuilder, Optional.ofNullable(limit), Optional.ofNullable(start), list);
	}
}

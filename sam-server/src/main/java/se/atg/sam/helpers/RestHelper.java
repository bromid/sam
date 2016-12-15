package se.atg.sam.helpers;

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

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.dropwizard.jersey.validation.Validators;
import se.atg.sam.helpers.JsonHelper.UpdateMetaResult;
import se.atg.sam.model.PaginatedCollection;
import se.atg.sam.model.User;
import se.atg.sam.model.View;

public abstract class RestHelper {

  public static final int HTTP_STATUS_VALIDATION_ERROR = 422;
  public static final String INTEGER_MAX = "2147483647";

  private static final ValidatorFactory VALIDATOR_FACTORY = Validators.newValidatorFactory();

  public static Optional<Function<String, Bson>> parseFilterFromQuery(String queryString) {

    if (StringUtils.isEmpty(queryString)) {
      return Optional.empty();
    }

    final String[] array = queryString.split(",");
    if (array.length == 1) {
      return Optional.of(t -> Filters.eq(t, array[0]));
    }
    return Optional.of(t -> Filters.all(t, Arrays.asList(array)));
  }

  public static Optional<String> verifyHash(Document bson, Request request) {

    final Optional<EntityTag> entityTag = getEntityTag(bson);
    entityTag.ifPresent(t -> {
      final ResponseBuilder response = request.evaluatePreconditions(t);
      if (response != null) {
        throw new WebApplicationException(response.build());
      }
    });
    return entityTag.map(EntityTag::getValue);
  }

  public static Optional<EntityTag> getEntityTag(Document bson) {

    final Optional<String> hash = Mapper.getHash(bson);
    return hash.map(t -> new EntityTag(t, false));
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
      limitedStream = limitedStream.limit(limit + 1);
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

  public static Document createAndAddMeta(
      Object object,
      MongoCollection<Document> collection,
      ObjectMapper objectMapper,
      SecurityContext securityContext
    ) {

    final User user = RestHelper.getUser(securityContext);
    final Document updated = JsonHelper.addMetaForCreate(object, user.name, objectMapper);
    collection.insertOne(updated);
    return updated;
  }

  public static Document replaceAndUpdateMeta(
      Document existing,
      Object updateObject,
      MongoCollection<Document> collection,
      ObjectMapper objectMapper,
      SecurityContext securityContext,
      Request request
    ) {

    final Optional<String> hash = verifyHash(existing, request);

    final User user = getUser(securityContext);
    final String updatedJson = JsonHelper.objectToJson(updateObject, objectMapper, View.Db.class);
    final UpdateMetaResult result = JsonHelper.updateMetaForUpdate(updatedJson, Mapper.getMeta(existing), hash, user.name);

    MongoHelper.updateDocument(existing, result.document, hash, collection);
    return result.document;
  }

  public static Document mergeAndUpdateMeta(
      Document existing,
      Object updateObject,
      int mergeDepth,
      MongoCollection<Document> collection,
      ObjectMapper objectMapper,
      SecurityContext securityContext,
      Request request
    ) {

    final Optional<String> hash = verifyHash(existing, request);
    final JsonNode updateNode = JsonHelper.objectToJsonNode(updateObject, objectMapper);
    JsonHelper.merge(existing, updateNode, mergeDepth, objectMapper);

    final User user = getUser(securityContext);
    JsonHelper.updateMetaForUpdate(existing, hash, user.name);

    MongoHelper.updateDocument(existing, existing, hash, collection);
    return existing;
  }

  public static String verifyNotEmpty(String param, String msg) {

    final String trimmed = StringUtils.trimToNull(param);
    if (trimmed == null) {
      throw validationError(msg);
    }
    return trimmed;
  }

  public static WebApplicationException validationError(String string) {
    return new WebApplicationException(string, HTTP_STATUS_VALIDATION_ERROR);
  }
}

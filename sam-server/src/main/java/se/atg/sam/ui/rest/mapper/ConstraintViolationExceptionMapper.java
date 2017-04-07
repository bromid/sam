package se.atg.sam.ui.rest.mapper;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import io.dropwizard.jersey.validation.ValidationErrorMessage;
import se.atg.sam.helpers.RestHelper;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

  @Override
  public Response toResponse(ConstraintViolationException exc) {

    final ImmutableList<String> violations = FluentIterable.from(exc.getConstraintViolations())
      .transform(violation -> violation.getPropertyPath() + " " + violation.getMessage())
      .toList();

    final int statusCode = RestHelper.HTTP_STATUS_VALIDATION_ERROR;
    return Response.status(statusCode)
      .entity(new ValidationErrorMessage(violations))
      .build();
  }
}

package se.atg.sam.ui.rest.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoServerException;

import io.dropwizard.jersey.errors.ErrorMessage;

@Provider
public class MongoServerExceptionMapper implements ExceptionMapper<MongoServerException> {

  private static final Logger logger = LoggerFactory.getLogger(MongoServerExceptionMapper.class);

  @Override
  public Response toResponse(MongoServerException exc) {

    logger.error("Got mongo server exception", exc);
    final Status errorCode = Status.INTERNAL_SERVER_ERROR;
    return Response.status(errorCode)
      .entity(new ErrorMessage(
        errorCode.getStatusCode(),
        "Database exception: " + exc.getCode(),
        exc.getLocalizedMessage()
    )).build();
  }
}

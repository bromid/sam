package se.atg.sam.ui.rest.integrationtest;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

public class EntityResponse<T> {

  public final T entity;
  public final Response response;
  public final String etag;

  public EntityResponse(Response response, Class<T> clazz) {
    this.response = response;
    this.entity = (T) response.readEntity(clazz);
    this.etag = response.getHeaderString("etag");
  }

  public EntityResponse(Response response, GenericType<T> genericType) {
    this.response = response;
    this.entity = (T) response.readEntity(genericType);
    this.etag = response.getHeaderString("etag");
  }
}

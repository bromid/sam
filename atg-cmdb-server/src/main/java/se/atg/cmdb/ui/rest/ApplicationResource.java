package se.atg.cmdb.ui.rest;

import java.io.IOException;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import io.dropwizard.jersey.PATCH;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import se.atg.cmdb.dao.Collections;
import se.atg.cmdb.helpers.JsonHelper;
import se.atg.cmdb.helpers.MongoHelper;
import se.atg.cmdb.helpers.RestHelper;
import se.atg.cmdb.model.Application;
import se.atg.cmdb.model.ApplicationLink;
import se.atg.cmdb.model.PaginatedCollection;
import se.atg.cmdb.model.Server;
import se.atg.cmdb.model.User;
import se.atg.cmdb.ui.dropwizard.auth.Roles;

@Path("/")
@Api("applications")
@Produces(Defaults.MEDIA_TYPE_JSON)
public class ApplicationResource {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationResource.class);
  private static final Bson ALL = new BsonDocument();

  private final MongoDatabase database;
  private final ObjectMapper objectMapper;

  public ApplicationResource(MongoDatabase database, ObjectMapper objectMapper) {
    this.database = database;
    this.objectMapper = objectMapper;
  }

  @GET
  @RolesAllowed(Roles.READ)
  @Path("services/application")
  @ApiOperation("Fetch all applications")
  public PaginatedCollection<Application> getApplications() {
    return findApplications(ALL);
  }

  @GET
  @RolesAllowed(Roles.READ)
  @Path("services/application/{id}")
  @ApiOperation(value = "Fetch application", response = Application.class)
  public Response getApplication(
    @ApiParam @PathParam("id") String id
  ) {
    final Document application = findApplication(Filters.eq("id", id));
    return Response
      .ok(new Application(application))
      .tag(RestHelper.getEntityTag(application).orElse(null))
      .build();
  }

  @PUT
  @Path("services/application")
  @RolesAllowed(Roles.EDIT)
  @ApiOperation(value = "Create a new application", code = 201, response = ApplicationLink.class)
  public Response createApplication(
    @ApiParam("Application") Application application,
    @Context UriInfo uriInfo,
    @Context SecurityContext securityContext
  ) throws JsonParseException, JsonMappingException, IOException {
    logger.info("Create application: {}", application);

    RestHelper.validate(application, Application.Create.class);

    final User user = RestHelper.getUser(securityContext);
    final Document bson = JsonHelper.addMetaForCreate(application, user.name, objectMapper);
    database.getCollection(Collections.APPLICATIONS).insertOne(bson);

    return linkResponse(Status.CREATED, bson, uriInfo);
  }

  @PATCH
  @RolesAllowed(Roles.EDIT)
  @Path("services/application/{id}")
  @ApiOperation(value = "Update application", response = ApplicationLink.class)
  @ApiImplicitParams(
    @ApiImplicitParam(name = "body", paramType = "body", required = true, dataType = "se.atg.cmdb.model.Application")
  )
  public Response updateApplication(
    @ApiParam("Application id") @PathParam("id") String id,
    @ApiParam(hidden = true) JsonNode applicationJson,
    @Context UriInfo uriInfo,
    @Context Request request,
    @Context SecurityContext securityContext
  ) throws IOException {

    final Application application = objectMapper.treeToValue(applicationJson, Application.class);
    RestHelper.validate(application, Server.Update.class);

    final Document existing = findApplication(Filters.eq("id", id));
    final Optional<String> hash = RestHelper.verifyHash(existing, request);
    JsonHelper.merge(existing, applicationJson, objectMapper);

    final User user = RestHelper.getUser(securityContext);
    JsonHelper.updateMetaForUpdate(existing, hash, user.name);

    MongoHelper.updateDocument(existing, hash, database.getCollection(Collections.APPLICATIONS));
    return linkResponse(Status.OK, existing, uriInfo);
  }

  @DELETE
  @Path("services/application/{id}")
  @RolesAllowed(Roles.EDIT)
  @ApiOperation(value = "Remove an application")
  public Response deleteApplication(
    @ApiParam("application id") @PathParam("id") String id,
    @Context Request request
  ) {
    logger.info("Delete application: {}", id);

    final Bson filter = Filters.eq("id", id);
    final Document existing = findApplication(filter);
    final Optional<String> hash = RestHelper.verifyHash(existing, request);
    MongoHelper.deleteDocument(filter, hash, database.getCollection(Collections.APPLICATIONS));
    return Response.noContent().build();
  }

  private PaginatedCollection<Application> findApplications(Bson filter) {

    final MongoCollection<Document> collection = database.getCollection(Collections.APPLICATIONS);
    final FindIterable<Document> query;
    if (filter == ALL) {
      query = collection.find();
    } else {
      query = collection.find(filter);
    }
    return RestHelper.paginatedList(query.map(Application::new));
  }

  private Document findApplication(Bson filter) {
    final Document bson = database.getCollection(Collections.APPLICATIONS)
      .find(filter)
      .first();
    if (bson == null) {
      throw new WebApplicationException(Status.NOT_FOUND);
    }
    return bson;
  }

  private Response linkResponse(Status status, Document bson, UriInfo uriInfo) {
    final ApplicationLink response = new ApplicationLink(uriInfo.getBaseUri(), bson.getString("id"), bson.getString("name"));
    return Response
      .status(status)
      .location(response.link.getUri())
      .entity(response)
      .tag(RestHelper.getEntityTag(bson).orElse(null))
      .build();
  }
}

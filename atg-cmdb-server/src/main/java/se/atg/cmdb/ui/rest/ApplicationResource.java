package se.atg.cmdb.ui.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import io.dropwizard.jersey.PATCH;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import se.atg.cmdb.dao.Collections;
import se.atg.cmdb.helpers.MongoHelper;
import se.atg.cmdb.helpers.RestHelper;
import se.atg.cmdb.model.Application;
import se.atg.cmdb.model.ApplicationLink;
import se.atg.cmdb.model.PaginatedCollection;
import se.atg.cmdb.model.ServerDeployment;
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
  @Path("services/application")
  @ApiOperation("Fetch all applications")
  public PaginatedCollection<Application> getApplications() {
    return findApplications(ALL);
  }

  @GET
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

  @GET
  @Path("services/application/{id}/deployment")
  @ApiOperation(value = "Fetch deployed instances of the application")
  public PaginatedCollection<ServerDeployment> getApplicationDeployments(
    @ApiParam @PathParam("id") String id
  ) {
    return RestHelper.paginatedList(
      findApplicationDeployments(id)
    );
  }

  @PUT
  @Path("services/application/{id}")
  @RolesAllowed(Roles.EDIT)
  @ApiOperation(value = "Create or replace an application")
  @ApiResponses({
    @ApiResponse(code = 201, message = "A new application was created.", response = ApplicationLink.class),
    @ApiResponse(code = 200, message = "The application was successfully replaced.", response = ApplicationLink.class),
    @ApiResponse(code = 404, message = "No application exists with the supplied id.", response = ErrorMessage.class),
    @ApiResponse(code = 412, message = "No application exists with the supplied id and hash.", response = ErrorMessage.class),
    @ApiResponse(code = 422, message = "The supplied application is not valid.", response = ErrorMessage.class),
  })
  public Response createOrReplaceApplication(
    @ApiParam("Application id") @PathParam("id") String id,
    @ApiParam("Application") Application application,
    @Context UriInfo uriInfo,
    @Context Request request,
    @Context SecurityContext securityContext
  ) throws JsonParseException, JsonMappingException, IOException {
    logger.info("Create or replace application: {}, {}", id, application);

    RestHelper.validate(application, Application.Create.class);

    final Optional<Document> existing = getApplicationForCreateOrReplace(id);
    final MongoCollection<Document> collection = database.getCollection(Collections.APPLICATIONS);

    if (!existing.isPresent()) {
      final Document updated = RestHelper.createAndAddMeta(application, collection, objectMapper, securityContext);
      return linkResponse(Status.CREATED, updated, uriInfo);
    }

    final Document updated = RestHelper.replaceAndUpdateMeta(existing.get(), application, collection, objectMapper, securityContext, request);
    return linkResponse(Status.OK, updated, uriInfo);
  }

  @PATCH
  @RolesAllowed(Roles.EDIT)
  @Path("services/application/{id}")
  @ApiOperation(value = "Update application")
  @ApiResponses({
    @ApiResponse(code = 200, message = "The application was successfully updated.", response = ApplicationLink.class),
    @ApiResponse(code = 404, message = "No application exists with the supplied id.", response = ErrorMessage.class),
    @ApiResponse(code = 412, message = "No application exists with the supplied id and hash.", response = ErrorMessage.class),
    @ApiResponse(code = 422, message = "The supplied application is not valid.", response = ErrorMessage.class),
  })
  public Response updateApplication(
    @ApiParam("Application id") @PathParam("id") String id,
    @ApiParam("Application") Application application,
    @Context UriInfo uriInfo,
    @Context Request request,
    @Context SecurityContext securityContext
  ) throws IOException {
    logger.info("Update application: {}", application);

    RestHelper.validate(application, Application.Update.class);

    final Document existing = getApplicationForUpdate(id);
    final MongoCollection<Document> collection = database.getCollection(Collections.APPLICATIONS);
    final Document updated = RestHelper.mergeAndUpdateMeta(existing, application, collection, objectMapper, securityContext, request);
    return linkResponse(Status.OK, updated, uriInfo);
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

    final List<Bson> pipeline = new ArrayList<>(2);
    if (filter != ALL) {
      pipeline.add(Aggregates.match(filter));
    }
    pipeline.add(Aggregates.lookup(Collections.GROUPS, "group", "id", "group"));

    return RestHelper.paginatedList(database
        .getCollection(Collections.APPLICATIONS)
        .aggregate(pipeline)
        .map(Application::new)
    );
  }

  private Document getApplicationForUpdate(String id) {
    final Optional<Document> bson = getApplicationForCreateOrReplace(id);
    return bson.orElseThrow(() -> new WebApplicationException(Status.NOT_FOUND));
  }

  private Optional<Document> getApplicationForCreateOrReplace(String id) {
    return Optional.ofNullable(
      database.getCollection(Collections.APPLICATIONS)
        .find(Filters.eq("id", id))
        .first()
    );
  }

  private Document findApplication(Bson filter) {
    final Document bson = database.getCollection(Collections.APPLICATIONS)
       .aggregate(Lists.newArrayList(
           Aggregates.match(filter),
           Aggregates.lookup(Collections.GROUPS, "group", "id", "group")
       )).first();
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

  private Iterable<ServerDeployment> findApplicationDeployments(String applicationId) {
    return database.getCollection(Collections.SERVERS)
      .find(
        Filters.eq("deployments.applicationId", applicationId)
      ).projection(Projections.fields(
        Projections.include("hostname", "environment"),
        Projections.elemMatch("deployments")
      )).map(ServerDeployment::fromBson);
  }
}

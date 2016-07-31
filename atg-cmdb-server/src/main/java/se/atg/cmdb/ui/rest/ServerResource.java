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
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UnwindOptions;

import io.dropwizard.jersey.PATCH;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import se.atg.cmdb.dao.Collections;
import se.atg.cmdb.helpers.JsonHelper;
import se.atg.cmdb.helpers.Mapper;
import se.atg.cmdb.helpers.MongoHelper;
import se.atg.cmdb.helpers.RestHelper;
import se.atg.cmdb.model.Deployment;
import se.atg.cmdb.model.PaginatedCollection;
import se.atg.cmdb.model.Server;
import se.atg.cmdb.model.ServerLink;
import se.atg.cmdb.model.User;
import se.atg.cmdb.ui.dropwizard.auth.Roles;

@Path("/")
@Api("servers")
@Produces(Defaults.MEDIA_TYPE_JSON)
public class ServerResource {

  private static final Logger logger = LoggerFactory.getLogger(ServerResource.class);
  private static final Bson ALL = new BsonDocument();

  private final MongoDatabase database;
  private final ObjectMapper objectMapper;

  public ServerResource(MongoDatabase database, ObjectMapper objectMapper) {
    this.database = database;
    this.objectMapper = objectMapper;
  }

  @GET
  @Path("services/server")
  @ApiOperation("Fetch all servers")
  public PaginatedCollection<Server> getServers() {
    return findServers(ALL);
  }

  @GET
  @Path("services/server/{environment}")
  @ApiOperation("Fetch all servers in an environment")
  public PaginatedCollection<Server> getServersInEnvironment(
    @ApiParam("Test environment") @PathParam("environment") String environment
  ) {
    return findServers(
      Filters.eq("environment", environment)
    );
  }

  @GET
  @Path("services/server/{environment}/{hostname}")
  @ApiOperation(value = "Fetch a server", response = Server.class)
  public Response getServer(
    @ApiParam("Server hostname") @PathParam("hostname") String hostname,
    @ApiParam("Test environment") @PathParam("environment") String environment
  ) {
    final Document server = findServer(Filters.and(
      Filters.eq("environment", environment),
      Filters.eq("hostname", hostname)
    ));
    return Response
      .ok(new Server(server))
      .tag(RestHelper.getEntityTag(server).orElse(null))
      .build();
  }

  @GET
  @Path("services/server/{environment}/{hostname}/deployment")
  @ApiOperation(value = "Fetch all deployed applications on the server", response = Server.class)
  public PaginatedCollection<Deployment> getServerDeployments(
    @ApiParam("Server hostname") @PathParam("hostname") String hostname,
    @ApiParam("Test environment") @PathParam("environment") String environment
  ) {
    final Document document = findServer(Filters.and(
      Filters.eq("environment", environment),
      Filters.eq("hostname", hostname)
    ));
    final Server server = new Server(document);
    return RestHelper.paginatedList(server.deployments);
  }

  @GET
  @Path("services/server/{environment}/{hostname}/deployment/{applicationId}")
  @ApiOperation(value = "Get a deployed application on the server", response = Deployment.class)
  public Deployment getServerDeployment(
    @ApiParam("Server hostname") @PathParam("hostname") String hostname,
    @ApiParam("Test environment") @PathParam("environment") String environment,
    @ApiParam("Application id") @PathParam("applicationId") String applicationId
  ) {
    final Document document = database.getCollection(Collections.SERVERS)
      .find(Filters.and(
        Filters.eq("environment", environment),
        Filters.eq("hostname", hostname)
      )).projection(Projections.elemMatch("deployments",
        Filters.eq("applicationId", applicationId)
      )).first();
    if (document == null) {
      throw new WebApplicationException(Status.NOT_FOUND);
    }

    final Deployment deployment = Mapper.mapObject(document, "deployments", Deployment::fromDeploymentBson);
    if (deployment == null) {
      throw new WebApplicationException(Status.NOT_FOUND);
    }
    return deployment;
  }

  @GET
  @Path("services/server/environment")
  @ApiOperation("Fetch all server environments")
  public PaginatedCollection<String> getServerEnvironments() {
    return getEnvironments();
  }

  @PUT
  @Path("services/server/{environment}/{hostname}/deployment")
  @RolesAllowed(Roles.EDIT)
  @ApiOperation(value = "Add a deployed applications to the server", response = ServerLink.class)
  public Response addDeployment(
    @ApiParam("Server hostname") @PathParam("hostname") String hostname,
    @ApiParam("Test environment") @PathParam("environment") String environment,
    @ApiParam("Deployment") Deployment deployment,
    @Context UriInfo uriInfo,
    @Context Request request,
    @Context SecurityContext securityContext
  ) {
    logger.info("Add deployment: {}", deployment);
    RestHelper.validate(deployment);

    final Document existing = getServerForUpdate(hostname, environment);
    final Optional<String> hash = RestHelper.verifyHash(existing, request);

    final Document update = JsonHelper.entityToBson(deployment, objectMapper);
    Mapper.upsertList(existing, update, "deployments", Deployment::sameApplicationId);

    final User user = RestHelper.getUser(securityContext);
    JsonHelper.updateMetaForUpdate(existing, hash, user.name);

    MongoHelper.updateDocument(existing, hash, database.getCollection(Collections.SERVERS));
    return linkResponse(Status.OK, existing, uriInfo);
  }

  @PUT
  @Path("services/server")
  @RolesAllowed(Roles.EDIT)
  @ApiOperation(value = "Create a new server", code = 201, response = ServerLink.class)
  public Response createServer(
    @ApiParam("Server") Server server,
    @Context UriInfo uriInfo,
    @Context SecurityContext securityContext
  ) throws JsonParseException, JsonMappingException, IOException {
    logger.info("Create server: {}", server);

    RestHelper.validate(server, Server.Create.class);

    final User user = RestHelper.getUser(securityContext);
    final Document bson = JsonHelper.addMetaForCreate(server, user.name, objectMapper);

    database.getCollection(Collections.SERVERS).insertOne(bson);
    return linkResponse(Status.CREATED, bson, uriInfo);
  }

  @PATCH
  @RolesAllowed(Roles.EDIT)
  @Path("services/server/{environment}/{hostname}")
  @ApiOperation(value = "Update server", response = ServerLink.class)
  public Response updateServer(
    @ApiParam("Server hostname") @PathParam("hostname") String hostname,
    @ApiParam("Test environment") @PathParam("environment") String environment,
    @ApiParam("Server") Server server,
    @Context UriInfo uriInfo,
    @Context Request request,
    @Context SecurityContext securityContext
  ) throws IOException {
    logger.info("Update server: {}", server);

    RestHelper.validate(server, Server.Update.class);

    final Document existing = getServerForUpdate(hostname, environment);
    final MongoCollection<Document> collection = database.getCollection(Collections.SERVERS);
    final Document updated = RestHelper.mergeAndUpdateMeta(existing, server, collection, objectMapper, securityContext, request);
    return linkResponse(Status.OK, updated, uriInfo);
  }

  @DELETE
  @Path("services/server/{environment}/{hostname}")
  @RolesAllowed(Roles.EDIT)
  @ApiOperation(value = "Remove a server")
  public Response deleteServer(
    @ApiParam("Server hostname") @PathParam("hostname") String hostname,
    @ApiParam("Test environment") @PathParam("environment") String environment,
    @Context Request request
  ) {
    logger.info("Delete server: {}, {}", environment, hostname);

    final Document existing = getServerForUpdate(hostname, environment);
    final Optional<String> hash = RestHelper.verifyHash(existing, request);
    MongoHelper.deleteDocument(Filters.and(
        Filters.eq("environment", environment),
        Filters.eq("hostname", hostname)
      ), hash,
      database.getCollection(Collections.SERVERS)
    );
    return Response.noContent().build();
  }

  private PaginatedCollection<String> getEnvironments() {
    return RestHelper.paginatedList(database
    .getCollection(Collections.SERVERS)
    .aggregate(Lists.newArrayList(
        Aggregates.group("$environment")
      )).map(t->t.getString("_id"))
    );
  }

  private Document getServerForUpdate(String hostname, String environment) {

    final Document bson = database.getCollection(Collections.SERVERS)
      .find(Filters.and(
        Filters.eq("environment", environment),
        Filters.eq("hostname", hostname)
      )).first();
    if (bson == null) {
      throw new WebApplicationException(Status.NOT_FOUND);
    }
    return bson;
  }

  private Document findServer(Bson filter) {

    final Document bson = database.getCollection(Collections.SERVERS)
      .aggregate(getServerQuery(filter))
      .first();
    if (bson == null) {
      throw new WebApplicationException(Status.NOT_FOUND);
    }
    return bson;
  }

  private PaginatedCollection<Server> findServers(Bson filter) {
    return RestHelper.paginatedList(database
      .getCollection(Collections.SERVERS)
      .aggregate(getServerQuery(filter))
      .map(Server::new)
    );
  }

  private static List<Bson> getServerQuery(Bson filter) {

    final List<Bson> pipeline = new ArrayList<>(6);
    if (filter != ALL) {
      pipeline.add(Aggregates.match(filter));
    }
    pipeline.add(Aggregates.unwind("$deployments", new UnwindOptions().preserveNullAndEmptyArrays(true)));
    pipeline.add(Aggregates.lookup(Collections.APPLICATIONS, "deployments.applicationId", "id", "applications"));
    pipeline.add(Aggregates.unwind("$applications", new UnwindOptions().preserveNullAndEmptyArrays(true)));
    pipeline.add(Aggregates.group(
      new Document().append("hostname", "$hostname").append("environment", "$environment"),
      new BsonField("fqdn", new Document("$first", "$fqdn")),
      new BsonField("description", new Document("$first", "$description")),
      new BsonField("os", new Document("$first", "$os")),
      new BsonField("network", new Document("$first", "$network")),
      new BsonField("meta", new Document("$first", "$meta")),
      new BsonField("attributes", new Document("$first", "$attributes")),
      new BsonField("applications", new Document("$push", "$applications")),
      new BsonField("deployments", new Document("$push", "$deployments"))));
    pipeline.add(Aggregates.sort(Sorts.ascending("_id")));
    return pipeline;
  }

  private Response linkResponse(Status status, Document bson, UriInfo uriInfo) {

    final ServerLink response = ServerLink.buildFromUri(uriInfo.getBaseUri(), bson.getString("hostname"), bson.getString("environment"));
    return Response
      .status(status)
      .location(response.link.getUri())
      .entity(response)
      .tag(RestHelper.getEntityTag(bson).orElse(null))
      .build();
  }
}

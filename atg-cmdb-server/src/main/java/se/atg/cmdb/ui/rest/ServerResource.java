package se.atg.cmdb.ui.rest;

import java.io.IOException;
import java.util.function.Function;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.bson.Document;
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
import se.atg.cmdb.helpers.JSONHelper;
import se.atg.cmdb.helpers.Mapper;
import se.atg.cmdb.helpers.RESTHelper;
import se.atg.cmdb.model.PaginatedCollection;
import se.atg.cmdb.model.Server;
import se.atg.cmdb.model.ServerLink;
import se.atg.cmdb.model.User;
import se.atg.cmdb.ui.dropwizard.auth.Roles;

@Path("/")
@Api("servers")
@Produces(Defaults.MEDIA_TYPE_JSON)
public class ServerResource {

	static final String SERVER_COLLECTION = "servers";
	static final Logger logger = LoggerFactory.getLogger(ServerResource.class);

	private final MongoDatabase database;
	private final ObjectMapper objectMapper;

	public ServerResource(MongoDatabase database, ObjectMapper objectMapper) {
		this.database = database;
		this.objectMapper = objectMapper;
	}

	@GET
	@Path("server")
	@RolesAllowed(Roles.READ)
	@ApiOperation("Fetch all servers")
	public PaginatedCollection<Server> getServers() {
		return getServers(MongoCollection::find);
	}

	@GET
	@Path("server/{environment}")
	@RolesAllowed(Roles.READ)
	@ApiOperation("Fetch all servers in an environment")
	public PaginatedCollection<Server> getServersInEnvironment(
		@ApiParam("Test environment") @PathParam("environment") String environment
	) {
		return getServers(t->t.find(Filters.eq("environment", environment)));
	}

	@GET
	@Path("server/{environment}/{name}")
	@RolesAllowed(Roles.READ)
	@ApiOperation(value = "Fetch a server", response=Server.class)
	public Response getServer(
		@ApiParam("Server hostname") @PathParam("name") String hostname,
		@ApiParam("Test environment") @PathParam("environment") String environment
	) {
		final Document server = getServer(t->
			t.find(Filters.and(
				Filters.eq("environment", environment),
				Filters.eq("hostname", hostname)
		)));
		return Response
			.ok(new Server(server))
			.tag(RESTHelper.getEntityTag(server))
			.build();
	}

	@PUT
	@Path("server")
	@RolesAllowed(Roles.EDIT)
	@ApiOperation(value = "Create a new server", code=201, response=ServerLink.class)
	@ApiImplicitParams(
		@ApiImplicitParam(name="body", paramType="body", required=true, dataType="se.atg.cmdb.model.Server")
	)
	public Response createServer(
		@ApiParam(hidden=true) String serverJson,
		@Context UriInfo uriInfo,
		@Context SecurityContext securityContext
	) throws JsonParseException, JsonMappingException, IOException {

		final Server server = objectMapper.readValue(serverJson, Server.class);
		RESTHelper.validate(server, Server.Create.class);

		final User user = RESTHelper.getUser(securityContext);
		final Document bson = JSONHelper.addMetaForCreate(serverJson, user.name);
		addServer(bson);
		return linkResponse(Status.CREATED, bson, uriInfo);
	}

	@PATCH
	@RolesAllowed(Roles.EDIT)
	@Path("server/{environment}/{name}")
	@ApiOperation(value = "Update server", response=ServerLink.class)
	@ApiImplicitParams(
		@ApiImplicitParam(name="body", paramType="body", required=true, dataType="se.atg.cmdb.model.Server")
	)
	public Response updateServer(
		@ApiParam("Server hostname") @PathParam("name") String hostname,
		@ApiParam("Test environment") @PathParam("environment") String environment,
		@ApiParam(hidden=true) JsonNode serverJson,
		@HeaderParam("ETag") String etag,
		@Context UriInfo uriInfo,
		@Context SecurityContext securityContext
	) throws IOException {

		final Server server = objectMapper.treeToValue(serverJson, Server.class);
		RESTHelper.validate(server, Server.Update.class);

		final Document existing = getServer(t->
			t.find(Filters.and(
				Filters.eq("environment", environment),
				Filters.eq("hostname", hostname)
		)));
		RESTHelper.verifyHash(existing, etag);
		JSONHelper.merge(existing, serverJson, objectMapper);

		final User user = RESTHelper.getUser(securityContext);
		JSONHelper.updateMetaForUpdate(existing, user.name);

		updateServer(existing);
		return linkResponse(Status.OK, existing, uriInfo);
	}

	private PaginatedCollection<Server> getServers(Function<MongoCollection<Document>, FindIterable<Document>> find) {
		return RESTHelper.paginatedList(
			find.apply(database.getCollection(SERVER_COLLECTION)).map(Server::new)
		);
	}

	private Document getServer(Function<MongoCollection<Document>, FindIterable<Document>> find) {

		final Document bson = find.apply(database.getCollection(SERVER_COLLECTION)).first();
		if (bson == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return bson;
	}

	private void updateServer(Document server) {
		database.getCollection(SERVER_COLLECTION)
			.replaceOne(Filters.eq("_id", server.get("_id")), server);
	}

	private void addServer(Document server) {
		database.getCollection(SERVER_COLLECTION)
			.insertOne(server);
	}

	private Response linkResponse(Status status, Document bson, UriInfo uriInfo) {
		final ServerLink response = new ServerLink(uriInfo.getBaseUri(), bson.getString("hostname"), bson.getString("environment"));
		return Response
			.status(status)
			.location(response.link.getUri())
			.entity(response)
			.tag(new EntityTag(Mapper.getHash(bson), true))
			.build();
	}
}
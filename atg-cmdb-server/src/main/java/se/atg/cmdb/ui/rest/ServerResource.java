package se.atg.cmdb.ui.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
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
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UnwindOptions;
import com.mongodb.client.result.UpdateResult;

import io.dropwizard.jersey.PATCH;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import se.atg.cmdb.dao.Collections;
import se.atg.cmdb.helpers.JSONHelper;
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

	static final Logger logger = LoggerFactory.getLogger(ServerResource.class);

	private final MongoDatabase database;
	private final ObjectMapper objectMapper;

	private static final Bson ALL = new BsonDocument();

	public ServerResource(MongoDatabase database, ObjectMapper objectMapper) {
		this.database = database;
		this.objectMapper = objectMapper;
	}

	@GET
	@Path("server")
	@RolesAllowed(Roles.READ)
	@ApiOperation("Fetch all servers")
	public PaginatedCollection<Server> getServers() {
		return findServers(ALL);
	}

	@GET
	@Path("server/{environment}")
	@RolesAllowed(Roles.READ)
	@ApiOperation("Fetch all servers in an environment")
	public PaginatedCollection<Server> getServersInEnvironment(
		@ApiParam("Test environment") @PathParam("environment") String environment
	) {
		return findServers(
			Filters.eq("environment", environment)
		);
	}

	@GET
	@Path("server/{environment}/{hostname}")
	@RolesAllowed(Roles.READ)
	@ApiOperation(value = "Fetch a server", response=Server.class)
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
			.tag(RESTHelper.getEntityTag(server).orElse(null))
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
	@Path("server/{environment}/{hostname}")
	@ApiOperation(value = "Update server", response=ServerLink.class)
	@ApiImplicitParams(
		@ApiImplicitParam(name="body", paramType="body", required=true, dataType="se.atg.cmdb.model.Server")
	)
	public Response updateServer(
		@ApiParam("Server hostname") @PathParam("hostname") String hostname,
		@ApiParam("Test environment") @PathParam("environment") String environment,
		@ApiParam(hidden=true) JsonNode serverJson,
		@Context UriInfo uriInfo,
		@Context Request request,
		@Context SecurityContext securityContext
	) throws IOException {

		final Document existing = database
			.getCollection(Collections.SERVERS)
			.find(Filters.and(
					Filters.eq("environment", environment),
					Filters.eq("hostname", hostname)
			)).first();	
		if (existing == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		final Server server = objectMapper.treeToValue(serverJson, Server.class);
		RESTHelper.validate(server, Server.Update.class);

		final Optional<String> hash = RESTHelper.verifyHash(existing, request);
		JSONHelper.merge(existing, serverJson, objectMapper);

		final User user = RESTHelper.getUser(securityContext);
		JSONHelper.updateMetaForUpdate(existing, hash, user.name);

		updateServer(existing, hash);
		return linkResponse(Status.OK, existing, uriInfo);
	}

	private PaginatedCollection<Server> findServers(Bson filter) {
		return RESTHelper.paginatedList(
			database.getCollection(Collections.SERVERS)
				.aggregate(getServerQuery(filter))
				.map(Server::new)
		);
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

	private static List<Bson> getServerQuery(Bson filter) {

		final List<Bson> pipeline = new ArrayList<>(6);
		if (filter != ALL) {
			pipeline.add(Aggregates.match(filter));
		}
		pipeline.add(Aggregates.unwind("$applications", new UnwindOptions().preserveNullAndEmptyArrays(true)));
		pipeline.add(Aggregates.lookup("applications", "applications", "id", "applications"));
		pipeline.add(Aggregates.unwind("$applications", new UnwindOptions().preserveNullAndEmptyArrays(true)));
		pipeline.add(Aggregates.group(
			new Document().append("hostname", "$hostname").append("environment", "$environment"),
			new BsonField("fqdn", new Document("$first", "$fqdn")),
			new BsonField("description", new Document("$first", "$description")),
			new BsonField("os", new Document("$first", "$os")),
			new BsonField("network", new Document("$first", "$network")),
			new BsonField("meta", new Document("$first", "$meta")),
			new BsonField("attributes", new Document("$first", "$attributes")),
			new BsonField("applications", new Document("$push", "$applications"))
		));
		pipeline.add(Aggregates.sort(Sorts.ascending("_id")));
		return pipeline;
	}

	private void updateServer(Document server, Optional<String> hash) {

		Bson filter = Filters.eq("_id", server.get("_id"));
		if (hash.isPresent()) {
			filter = Filters.and(
				filter,
				Filters.eq("meta.hash", hash.get())
			);
		}
		final UpdateResult result = database.getCollection(Collections.SERVERS).replaceOne(filter, server);
		if (result.getMatchedCount() != 1) {
			throw new WebApplicationException("Concurrent modification", 422);
		}
	}

	private void addServer(Document server) {
		database.getCollection(Collections.SERVERS)
			.insertOne(server);
	}

	private Response linkResponse(Status status, Document bson, UriInfo uriInfo) {
		final ServerLink response = ServerLink.buildFromURI(uriInfo.getBaseUri(), bson.getString("hostname"), bson.getString("environment"));
		return Response
			.status(status)
			.location(response.link.getUri())
			.entity(response)
			.tag(RESTHelper.getEntityTag(bson).orElse(null))
			.build();
	}
}
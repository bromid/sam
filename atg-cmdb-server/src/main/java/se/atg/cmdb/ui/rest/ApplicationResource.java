package se.atg.cmdb.ui.rest;

import java.io.IOException;
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
import se.atg.cmdb.helpers.JSONHelper;
import se.atg.cmdb.helpers.MongoHelper;
import se.atg.cmdb.helpers.RESTHelper;
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
	@Path("application")
	@ApiOperation("Fetch all applications")
	public PaginatedCollection<Application> getApplications() {
		return findApplications(ALL);
	}

	@GET
	@RolesAllowed(Roles.READ)
	@Path("application/{id}")
	@ApiOperation(value="Fetch application", response=Application.class)
	public Response getApplication(
		@ApiParam @PathParam("id") String id
	) {
		final Document application = findApplication(Filters.eq("id", id));
		return Response
			.ok(new Application(application))
			.tag(RESTHelper.getEntityTag(application).orElse(null))
			.build();
	}

	@PUT
	@Path("application")
	@RolesAllowed(Roles.EDIT)
	@ApiOperation(value="Create a new application", code=201, response=ApplicationLink.class)
	public Response createApplication(
		@ApiParam("Application") Application application,
		@Context UriInfo uriInfo,
		@Context SecurityContext securityContext
	) throws JsonParseException, JsonMappingException, IOException {
		logger.info("Create application: {}", application);

		RESTHelper.validate(application, Application.Create.class);

		final User user = RESTHelper.getUser(securityContext);
		final Document bson = JSONHelper.addMetaForCreate(application, user.name, objectMapper);
		database.getCollection(Collections.APPLICATIONS).insertOne(bson);

		return linkResponse(Status.CREATED, bson, uriInfo);
	}

	@PATCH
	@RolesAllowed(Roles.EDIT)
	@Path("application/{id}")
	@ApiOperation(value = "Update application", response=ApplicationLink.class)
	@ApiImplicitParams(
		@ApiImplicitParam(name="body", paramType="body", required=true, dataType="se.atg.cmdb.model.Application")
	)
	public Response updateServer(
		@ApiParam("Application id") @PathParam("id") String id,
		@ApiParam(hidden=true) JsonNode serverJson,
		@Context UriInfo uriInfo,
		@Context Request request,
		@Context SecurityContext securityContext
	) throws IOException {

		final Document existing = findApplication(Filters.eq("id", id));
		if (existing == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		final Application application = objectMapper.treeToValue(serverJson, Application.class);
		RESTHelper.validate(application, Server.Update.class);

		final Optional<String> hash = RESTHelper.verifyHash(existing, request);
		JSONHelper.merge(existing, serverJson, objectMapper);

		final User user = RESTHelper.getUser(securityContext);
		JSONHelper.updateMetaForUpdate(existing, hash, user.name);

		MongoHelper.updateDocument(existing, hash, database.getCollection(Collections.APPLICATIONS));
		return linkResponse(Status.OK, existing, uriInfo);
	}

	private PaginatedCollection<Application> findApplications(Bson filter) {

		final MongoCollection<Document> collection = database.getCollection(Collections.APPLICATIONS);
		final FindIterable<Document> query;
		if (filter == ALL) {
			query = collection.find();
		} else {
			query = collection.find(filter);
		}
		return RESTHelper.paginatedList(query.map(Application::new));
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
			.tag(RESTHelper.getEntityTag(bson).orElse(null))
			.build();
	}
}

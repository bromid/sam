package se.atg.cmdb.ui.rest;

import java.util.function.Function;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import se.atg.cmdb.helpers.RESTHelper;
import se.atg.cmdb.model.Application;
import se.atg.cmdb.model.ApplicationLink;
import se.atg.cmdb.model.PaginatedCollection;

@Path("/")
@Api("applications")
@Produces(Defaults.MEDIA_TYPE_JSON)
public class ApplicationResource {

	static final String APPLICATION_COLLECTION = "applications";
	static final Logger logger = LoggerFactory.getLogger(ApplicationResource.class);

	private final MongoDatabase database;
	private final ObjectMapper objectMapper;

	public ApplicationResource(MongoDatabase database, ObjectMapper objectMapper) {
		this.database = database;
		this.objectMapper = objectMapper;
	}

	@GET
	@Path("app")
	@ApiOperation("Fetch all applications")
	public PaginatedCollection<Application> getApplications() {
		return getApplications(MongoCollection::find);
	}

	@GET
	@Path("app/{id}")
	@ApiOperation("Fetch application")
	public Application getApplication(
		@ApiParam @PathParam("id") String id
	) {
		return getApplication(t->t.find(Filters.eq("id", id)));
	}

	@PUT
	@Path("app")
	@ApiOperation("Create a new application")
	public Response createServer(
		@ApiParam("Application") @Valid Application application,
		@Context UriInfo uriInfo
	) throws JsonProcessingException {
		logger.info("Create application: {}", application);

		final Document doc = Document.parse(objectMapper.writeValueAsString(application));
		database.getCollection(APPLICATION_COLLECTION).insertOne(doc);

		final ApplicationLink response = new ApplicationLink(uriInfo.getBaseUri(), application.id);
		return Response
			.created(response.link.getUri())
			.entity(response)
			.build();
	}

	private PaginatedCollection<Application> getApplications(Function<MongoCollection<Document>,FindIterable<Document>> find) {
		return RESTHelper.paginatedList(
			find.apply(database.getCollection(APPLICATION_COLLECTION))
			.map(Application::new)
		);
	}

	private Application getApplication(Function<MongoCollection<Document>,FindIterable<Document>> find) {

		final Document bson = find.apply(database.getCollection(APPLICATION_COLLECTION)).first();
		logger.debug("Application bson: {}", bson);
		if (bson == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return new Application(bson);
	}
}

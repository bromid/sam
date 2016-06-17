package se.atg.cmdb.ui.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import se.atg.cmdb.dao.Collections;
import se.atg.cmdb.helpers.RESTHelper;
import se.atg.cmdb.model.Asset;
import se.atg.cmdb.model.PaginatedCollection;
import se.atg.cmdb.ui.dropwizard.auth.Roles;

@Path("/")
@Api("assets")
@Produces(Defaults.MEDIA_TYPE_JSON)
public class AssetResource {

	private static final Logger logger = LoggerFactory.getLogger(AssetResource.class);
	private static final Bson ALL = new BsonDocument();

	private final MongoDatabase database;
	private final ObjectMapper objectMapper;

	public AssetResource(MongoDatabase database, ObjectMapper objectMapper) {
		this.database = database;
		this.objectMapper = objectMapper;
	}

	@GET
	@Path("services/asset")
	@RolesAllowed(Roles.READ)
	@ApiOperation("Fetch all assets")
	public PaginatedCollection<Asset> getAssets() {
		return findAssets(ALL);
	}

	@GET
	@Path("services/asset/{id}")
	@RolesAllowed(Roles.READ)
	@ApiOperation(value = "Fetch an asset", response=Asset.class)
	public Response getAsset(
		@ApiParam("id") @PathParam("id") String id
	) {
		final Document asset = findAsset(Filters.eq("id", id));
		return Response
			.ok(new Asset(asset))
			.tag(RESTHelper.getEntityTag(asset).orElse(null))
			.build();
	}

	private PaginatedCollection<Asset> findAssets(Bson filter) {
		
		final MongoCollection<Document> collection = database.getCollection(Collections.ASSETS);
		final FindIterable<Document> query;
		if (filter == ALL) {
			query = collection.find();
		} else {
			query = collection.find(filter);
		}
		return RESTHelper.paginatedList(query.map(Asset::new));
	}

	private Document findAsset(Bson filter) {
		final Document bson = database.getCollection(Collections.ASSETS)
			.find(filter)
			.first();
		if (bson == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return bson;
	}
}

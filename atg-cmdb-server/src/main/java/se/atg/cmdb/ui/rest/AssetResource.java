package se.atg.cmdb.ui.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import se.atg.cmdb.model.Asset;
import se.atg.cmdb.model.PaginatedCollection;

@Path("/")
@Api("assets")
@Produces(Defaults.MEDIA_TYPE_JSON)
public class AssetResource {

	static final Logger logger = LoggerFactory.getLogger(AssetResource.class);

	private final MongoDatabase database;
	private final ObjectMapper objectMapper;

	public AssetResource(MongoDatabase database, ObjectMapper objectMapper) {
		this.database = database;
		this.objectMapper = objectMapper;
	}

	@GET
	@Path("asset")
	public PaginatedCollection<Asset> getAssets() {
		return null;
	}

	@GET
	@Path("asset/{id}")
	public Asset getAsset(
		@ApiParam("id") @PathParam("id") String id
	) {
		return null;
	}
}

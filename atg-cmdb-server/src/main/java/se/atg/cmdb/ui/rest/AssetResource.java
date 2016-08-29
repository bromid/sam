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
import se.atg.cmdb.model.Asset;
import se.atg.cmdb.model.AssetLink;
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
  @ApiOperation("Fetch all assets")
  public PaginatedCollection<Asset> getAssets() {
    return findAssets(ALL);
  }

  @GET
  @Path("services/asset/{id}")
  @ApiOperation(value = "Fetch an asset", response = Asset.class)
  public Response getAsset(
    @ApiParam("id") @PathParam("id") String id
  ) {
    final Document asset = findAsset(Filters.eq("id", id));
    return Response
        .ok(new Asset(asset))
        .tag(RestHelper.getEntityTag(asset).orElse(null))
        .build();
  }

  @PUT
  @Path("services/asset/{id}")
  @RolesAllowed(Roles.EDIT)
  @ApiOperation(value = "Create or replace an asset")
  @ApiResponses({
    @ApiResponse(code = 201, message = "A new asset was created.", response = AssetLink.class),
    @ApiResponse(code = 200, message = "The asset was successfully replaced.", response = AssetLink.class),
    @ApiResponse(code = 404, message = "No asset exists with the supplied id.", response = ErrorMessage.class),
    @ApiResponse(code = 412, message = "No asset exists with the supplied id and hash.", response = ErrorMessage.class),
    @ApiResponse(code = 422, message = "The supplied asset is not valid.", response = ErrorMessage.class),
  })
  public Response createOrReplaceAsset(
    @ApiParam("Asset id") @PathParam("id") String id,
    @ApiParam("Asset") Asset asset,
    @Context UriInfo uriInfo,
    @Context Request request,
    @Context SecurityContext securityContext
  ) throws JsonParseException, JsonMappingException, IOException {
    logger.info("Create or replace asset: {}, {}", id, asset);

    RestHelper.validate(asset, Asset.Create.class);

    final Optional<Document> existing = getAssetForCreateOrReplace(id);
    final MongoCollection<Document> collection = database.getCollection(Collections.ASSETS);

    if (!existing.isPresent()) {
      final Document updated = RestHelper.createAndAddMeta(asset, collection, objectMapper, securityContext);
      return linkResponse(Status.CREATED, updated, uriInfo);
    }

    final Document updated = RestHelper.replaceAndUpdateMeta(existing.get(), asset, collection, objectMapper, securityContext, request);
    return linkResponse(Status.OK, updated, uriInfo);
  }

  @PATCH
  @RolesAllowed(Roles.EDIT)
  @Path("services/asset/{id}")
  @ApiOperation(value = "Update asset")
  @ApiResponses({
    @ApiResponse(code = 200, message = "The asset was successfully updated.", response = AssetLink.class),
    @ApiResponse(code = 404, message = "No asset exists with the supplied id.", response = ErrorMessage.class),
    @ApiResponse(code = 412, message = "No asset exists with the supplied id and hash.", response = ErrorMessage.class),
    @ApiResponse(code = 422, message = "The supplied asset is not valid.", response = ErrorMessage.class),
  })
  public Response updateAsset(
    @ApiParam("Asset id") @PathParam("id") String id,
    @ApiParam("Asset") Asset asset,
    @Context UriInfo uriInfo,
    @Context Request request,
    @Context SecurityContext securityContext
  ) throws IOException {
    logger.info("Update asset: {}", asset);

    RestHelper.validate(asset, Asset.Update.class);

    final Document existing = getAssetForUpdate(id);
    final MongoCollection<Document> collection = database.getCollection(Collections.ASSETS);
    final Document updated = RestHelper.mergeAndUpdateMeta(existing, asset, collection, objectMapper, securityContext, request);
    return linkResponse(Status.OK, updated, uriInfo);
  }

  @DELETE
  @Path("services/asset/{id}")
  @RolesAllowed(Roles.EDIT)
  @ApiOperation(value = "Remove an asset")
  public Response deleteAsset(
    @ApiParam("asset id") @PathParam("id") String id,
    @Context Request request
  ) {
    logger.info("Delete asset: {}", id);

    final Document existing = getAssetForUpdate(id);
    final Optional<String> hash = RestHelper.verifyHash(existing, request);

    final MongoCollection<Document> collection = database.getCollection(Collections.ASSETS);
    MongoHelper.deleteDocument(Filters.eq("id", id), hash, collection);

    return Response.noContent().build();
  }

  private PaginatedCollection<Asset> findAssets(Bson filter) {

    final List<Bson> pipeline = new ArrayList<>(2);
    if (filter != ALL) {
      pipeline.add(Aggregates.match(filter));
    }
    pipeline.add(Aggregates.lookup(Collections.GROUPS, "group", "id", "group"));

    return RestHelper.paginatedList(database
        .getCollection(Collections.ASSETS)
        .aggregate(pipeline)
        .map(Asset::new)
    );
  }

  private Document getAssetForUpdate(String id) {
    final Optional<Document> bson = getAssetForCreateOrReplace(id);
    return bson.orElseThrow(() -> new WebApplicationException(Status.NOT_FOUND));
  }

  private Optional<Document> getAssetForCreateOrReplace(String id) {
    return Optional.ofNullable(
      database.getCollection(Collections.ASSETS)
        .find(Filters.eq("id", id))
        .first()
    );
  }

  private Document findAsset(Bson filter) {
    final Document bson = database.getCollection(Collections.ASSETS)
        .aggregate(Lists.newArrayList(
            Aggregates.match(filter),
            Aggregates.lookup(Collections.GROUPS, "group", "id", "group")
        )).first();
    if (bson == null) {
      throw new WebApplicationException(Status.NOT_FOUND);
    }
    return bson;
  }

  private static Response linkResponse(Status status, Document bson, UriInfo uriInfo) {
    final AssetLink response = new AssetLink(uriInfo.getBaseUri(), bson.getString("id"), bson.getString("name"));
    return Response
      .status(status)
      .location(response.link.getUri())
      .entity(response)
      .tag(RestHelper.getEntityTag(bson).orElse(null))
      .build();
  }
}

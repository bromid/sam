package se.atg.cmdb.ui.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UnwindOptions;

import io.dropwizard.jersey.PATCH;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import se.atg.cmdb.dao.Collections;
import se.atg.cmdb.helpers.JsonHelper;
import se.atg.cmdb.helpers.Mapper;
import se.atg.cmdb.helpers.MongoHelper;
import se.atg.cmdb.helpers.RestHelper;
import se.atg.cmdb.helpers.TreeHelper;
import se.atg.cmdb.model.Group;
import se.atg.cmdb.model.GroupLink;
import se.atg.cmdb.model.PaginatedCollection;
import se.atg.cmdb.model.Tag;
import se.atg.cmdb.model.User;
import se.atg.cmdb.ui.dropwizard.auth.Roles;

@Path("/")
@Api("groups")
@Produces(Defaults.MEDIA_TYPE_JSON)
public class GroupResource {

  private static final Logger logger = LoggerFactory.getLogger(GroupResource.class);

  private final MongoDatabase database;
  private final ObjectMapper objectMapper;

  public GroupResource(MongoDatabase database, ObjectMapper objectMapper) {
    this.database = database;
    this.objectMapper = objectMapper;
  }

  @GET
  @Path("services/group")
  @ApiOperation("Fetch all groups")
  public PaginatedCollection<Group> getGroups(
    @ApiParam("A comma separated list of tags") @QueryParam("tags") String tags,
    @ApiParam(PaginatedCollection.LIMIT_DESC) @QueryParam("limit") Integer limit,
    @ApiParam(PaginatedCollection.START_DESC) @QueryParam("start") Integer start,
    @Context UriInfo uriInfo
  ) {
    final Optional<Function<String, Bson>> tagsFilter = RestHelper.parseFilterFromQuery(tags);
    final Map<String, Group> groups = getAllGroups();
    return RestHelper.paginatedList(uriInfo.getRequestUriBuilder(), limit, start,
      getRootGroupIds(tagsFilter)
        .stream()
        .map(t -> createTree(t, groups))
    );
  }

  @GET
  @Path("services/group/tag")
  @ApiOperation("Fetch all group tags")
  public PaginatedCollection<Tag> getGroupTags() {
    return getTags();
  }

  @GET
  @Path("services/group/id")
  @ApiOperation("Fetch all group ids")
  public PaginatedCollection<String> getGroupIds() {
    return getIds();
  }

  @GET
  @Path("services/group/{id}")
  @ApiOperation("Fetch group")
  public Group getGroup(
    @ApiParam("id") @PathParam("id") String id
  ) {
    final Map<String, Group> groups = getAllGroups();
    final Group group = groups.get(id);
    if (group == null) {
      throw new WebApplicationException(Status.NOT_FOUND);
    }
    return TreeHelper.createTree(group.id, groups, Group::resetGroups, Group::addGroup, Group::new);
  }

  @GET
  @Path("services/group/{id}/group")
  @ApiOperation("Fetch group")
  public PaginatedCollection<Group> getSubGroups(
    @ApiParam("id") @PathParam("id") String id,
    @ApiParam(PaginatedCollection.LIMIT_DESC) @QueryParam("limit") Integer limit,
    @ApiParam(PaginatedCollection.START_DESC) @QueryParam("start") Integer start,
    @Context UriInfo uriInfo
  ) {
    final Map<String, Group> groups = getAllGroups();
    final Group group = groups.get(id);
    if (group == null) {
      throw new WebApplicationException(Status.NOT_FOUND);
    }
    return RestHelper.paginatedList(uriInfo.getRequestUriBuilder(), limit, start,
      group.groups
        .stream()
        .map(t -> createTree(t.id, groups))
    );
  }

  @PUT
  @Path("services/group/{id}")
  @RolesAllowed(Roles.EDIT)
  @ApiOperation(value = "Create or replace a group")
  @ApiResponses({
    @ApiResponse(code = 201, message = "A new group was created.", response = GroupLink.class),
    @ApiResponse(code = 200, message = "The group was successfully replaced.", response = GroupLink.class),
    @ApiResponse(code = 404, message = "No group exists with the supplied id.", response = ErrorMessage.class),
    @ApiResponse(code = 412, message = "No group exists with the supplied id and hash.", response = ErrorMessage.class),
    @ApiResponse(code = 422, message = "The supplied group is not valid.", response = ErrorMessage.class),
  })
  public Response createOrReplaceGroup(
    @ApiParam("Group id") @PathParam("id") String id,
    @ApiParam("Group") Group group,
    @Context UriInfo uriInfo,
    @Context Request request,
    @Context SecurityContext securityContext
  ) throws JsonParseException, JsonMappingException, IOException {
    logger.info("Create or replace group: {}, {}", id, group);

    RestHelper.validate(group, Group.Create.class);

    final Optional<Document> existing = getGroupForCreateOrUpdate(id);
    final MongoCollection<Document> collection = database.getCollection(Collections.GROUPS);

    if (!existing.isPresent()) {
      final Document updated = RestHelper.createAndAddMeta(group, collection, objectMapper, securityContext);
      return linkResponse(Status.CREATED, updated, uriInfo);
    }

    final Document updated = RestHelper.replaceAndUpdateMeta(existing.get(), group, collection, objectMapper, securityContext, request);
    return linkResponse(Status.OK, updated, uriInfo);
  }

  @PUT
  @Path("services/group/{id}/group/{subGroupId}")
  @RolesAllowed(Roles.EDIT)
  @ApiOperation("Add an existing sub group to a group.")
  @ApiResponses({
    @ApiResponse(code = 200, message = "The subgroup was successfully added to the group.", response = GroupLink.class),
    @ApiResponse(code = 404, message = "No group exists with the supplied id.", response = ErrorMessage.class),
    @ApiResponse(code = 412, message = "No group exists with the supplied id and hash.", response = ErrorMessage.class),
    @ApiResponse(code = 422, message = "The supplied subgroup id is not valid.", response = ErrorMessage.class),
  })
  public Response addSubGroup(
    @ApiParam("Group id") @PathParam("id") String groupId,
    @ApiParam("Subgroup id") @PathParam("subGroupId") String subGroupId,
    @Context UriInfo uriInfo,
    @Context Request request,
    @Context SecurityContext securityContext
  ) {
    logger.info("Add subgroup {} to {}", subGroupId, groupId);

    final Document existing = getGroupForUpdate(groupId);
    final Optional<String> hash = RestHelper.verifyHash(existing, request);

    final Document update = JsonHelper.entityToBson(new Group(subGroupId), objectMapper);
    Mapper.upsertList(existing, update, "groups", (item) -> item.get("id").equals(subGroupId));

    final User user = RestHelper.getUser(securityContext);
    JsonHelper.updateMetaForUpdate(existing, hash, user.name);

    MongoHelper.updateDocument(existing, existing, hash, database.getCollection(Collections.GROUPS));
    return linkResponse(Status.OK, existing, uriInfo);
  }

  @PATCH
  @RolesAllowed(Roles.EDIT)
  @Path("services/group/{id}")
  @ApiOperation(value = "Update group")
  @ApiResponses({
    @ApiResponse(code = 200, message = "The group was successfully updated.", response = GroupLink.class),
    @ApiResponse(code = 404, message = "No group exists with the supplied id.", response = ErrorMessage.class),
    @ApiResponse(code = 412, message = "No group exists with the supplied id and hash.", response = ErrorMessage.class),
    @ApiResponse(code = 422, message = "The supplied group is not valid.", response = ErrorMessage.class),
  })
  public Response updateGroup(
    @ApiParam("Group id") @PathParam("id") String id,
    @ApiParam("Group") Group group,
    @Context UriInfo uriInfo,
    @Context Request request,
    @Context SecurityContext securityContext
  ) throws IOException {
    logger.info("Update group: {}", group);

    RestHelper.validate(group, Group.Update.class);

    final Document existing = getGroupForUpdate(id);
    final MongoCollection<Document> collection = database.getCollection(Collections.GROUPS);
    final Document updated = RestHelper.mergeAndUpdateMeta(existing, group, collection, objectMapper, securityContext, request);
    return linkResponse(Status.OK, updated, uriInfo);
  }

  @DELETE
  @Path("services/group/{id}")
  @RolesAllowed(Roles.EDIT)
  @ApiOperation(value = "Remove a group")
  public Response deleteGroup(
    @ApiParam("group id") @PathParam("id") String id,
    @Context Request request
  ) {
    logger.info("Delete group: {}", id);

    final Document existing = getGroupForUpdate(id);
    final Optional<String> hash = RestHelper.verifyHash(existing, request);

    final MongoCollection<Document> collection = database.getCollection(Collections.GROUPS);
    MongoHelper.deleteDocument(Filters.eq("id", id), hash, collection);

    return Response.noContent().build();
  }

  private Document getGroupForUpdate(String id) {
    final Optional<Document> bson = getGroupForCreateOrUpdate(id);
    return bson.orElseThrow(() -> new WebApplicationException(Status.NOT_FOUND));
  }

  private Optional<Document> getGroupForCreateOrUpdate(String id) {
    return Optional.ofNullable(
      database
        .getCollection(Collections.GROUPS)
        .find(Filters.eq("id", id))
        .first()
    );
  }

  @DELETE
  @Path("services/group/{id}/group/{subGroupId}")
  @RolesAllowed(Roles.EDIT)
  @ApiOperation("Remove sub group")
  @ApiResponses({
    @ApiResponse(code = 200, message = "The subgroup was successfully removed from the group.", response = GroupLink.class),
    @ApiResponse(code = 404, message = "No group exists with the supplied id.", response = ErrorMessage.class),
    @ApiResponse(code = 412, message = "No group exists with the supplied id and hash.", response = ErrorMessage.class),
    @ApiResponse(code = 422, message = "The supplied subgroup id is not valid.", response = ErrorMessage.class),
  })
  public Response removeSubGroup(
    @ApiParam("Group id") @PathParam("id") String groupId,
    @ApiParam("Subgroup id") @PathParam("subGroupId") String subGroupId,
    @Context UriInfo uriInfo,
    @Context Request request,
    @Context SecurityContext securityContext
  ) {
    logger.info("Remove subgroup {} from {}", subGroupId, groupId);

    final Document existing = getGroupForUpdate(groupId);
    final Optional<String> hash = RestHelper.verifyHash(existing, request);
    Mapper.removeFromList(existing, "groups", (item) -> item.get("id").equals(subGroupId));

    final User user = RestHelper.getUser(securityContext);
    JsonHelper.updateMetaForUpdate(existing, hash, user.name);

    MongoHelper.updateDocument(existing, existing, hash, database.getCollection(Collections.GROUPS));
    return linkResponse(Status.OK, existing, uriInfo);
  }

  /*
   * Fetch all groups joined (mongodb lookup) with their applications and assets.
   */
  private Map<String,Group> getAllGroups() {
    return Maps.uniqueIndex(database
      .getCollection(Collections.GROUPS)
      .aggregate(Lists.newArrayList(
        Aggregates.lookup(Collections.APPLICATIONS, "id", "group", "applications"),
        Aggregates.lookup(Collections.ASSETS, "id", "group", "assets")
      )).map(Group::new),
      t->t.id
    );
  }

  private PaginatedCollection<String> getIds() {
    return RestHelper.paginatedList(database
      .getCollection(Collections.GROUPS)
      .find()
      .projection(Projections.include("id"))
      .map(t->t.getString("id"))
    );
  }

  private PaginatedCollection<Tag> getTags() {
    return RestHelper.paginatedList(database
      .getCollection(Collections.GROUPS)
      .aggregate(Lists.newArrayList(
        Aggregates.unwind("$tags"),
        Aggregates.group("$tags")
      )).map(t->new Tag(t.getString("_id")))
    );
  }

  /*
   * Fetch root groups. That's all groups that has no inbound references from any other group.
   */
  private List<String> getRootGroupIds(Optional<Function<String,Bson>> filterProvider) {

    final List<Bson> pipeline = new ArrayList<>(5);

    /*
     * Optional filter, needs to be applied both before and after self join to include
     * groups with inbound links from non tagged groups
     */
    Bson inboundLinksFilter = Filters.size("inbound_links", 0);
    if (filterProvider.isPresent()) {

      final Bson tagFilter = filterProvider.get().apply("tags");
      pipeline.add(Aggregates.match(tagFilter));

      final Bson inboundLinksTagFilter = filterProvider.get().apply("inbound_links.tags");
      inboundLinksFilter = Filters.or(inboundLinksFilter, Filters.not(inboundLinksTagFilter));
    }

    // Unwind groups field to be able to self-join
    pipeline.add(Aggregates.unwind("$groups", new UnwindOptions().preserveNullAndEmptyArrays(true)));

    // Self join on inbound references: group.groups -> group.id and filter no inbound references
    pipeline.add(Aggregates.lookup(Collections.GROUPS, "id", "groups.id", "inbound_links"));
    pipeline.add(Aggregates.match(inboundLinksFilter));

    // Group on id to get distinct group names
    pipeline.add(Aggregates.group("$id"));

    return database
      .getCollection(Collections.GROUPS)
      .aggregate(pipeline)
      .map(t->t.getString("_id"))
      .into(Lists.newArrayList());
  }

  private static Group createTree(String groupId, Map<String, Group> groups) {

    final Group group = groups.get(groupId);
    if (group == null) {
      return new Group(groupId);
    }
    if (group.getGroups().isEmpty()) {
      return group;
    }
    return TreeHelper.createTree(groupId, groups, Group::resetGroups, Group::addGroup, Group::new);
  }

  private static Response linkResponse(Status status, Document bson, UriInfo uriInfo) {
    final GroupLink response = new GroupLink(uriInfo.getBaseUri(), bson.getString("id"), bson.getString("name"));
    return Response
      .status(status)
      .location(response.link.getUri())
      .entity(response)
      .tag(RestHelper.getEntityTag(bson).orElse(null))
      .build();
  }
}

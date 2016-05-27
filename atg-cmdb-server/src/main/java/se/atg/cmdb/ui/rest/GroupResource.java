package se.atg.cmdb.ui.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import se.atg.cmdb.helpers.RESTHelper;
import se.atg.cmdb.helpers.TreeHelper;
import se.atg.cmdb.model.Group;
import se.atg.cmdb.model.PaginatedCollection;
import se.atg.cmdb.model.Tag;

@Path("/")
@Api("groups")
@Produces(Defaults.MEDIA_TYPE_JSON)
public class GroupResource {

	static final String APPLICATION_COLLECTION = "applications";
	static final String GROUP_COLLECTION = "groups";
	static final Logger logger = LoggerFactory.getLogger(GroupResource.class);

	private final MongoDatabase database;
	private final ObjectMapper objectMapper;

	public GroupResource(MongoDatabase database, ObjectMapper objectMapper) {
		this.database = database;
		this.objectMapper = objectMapper;
	}

	@GET
	@Path("group")
	@ApiOperation("Fetch all groups")
	public PaginatedCollection<Group> getGroups(
		@ApiParam("A comma separated list of tags") @QueryParam("tags") String tags,
		@ApiParam(PaginatedCollection.LIMIT_DESC) @QueryParam("limit") Integer limit,
		@ApiParam(PaginatedCollection.START_DESC) @QueryParam("start") Integer start,
		@Context UriInfo uriInfo
	) {
		final Optional<Function<String, Bson>> tagsFilter = RESTHelper.parseFilterFromQuery(tags);
		final Map<String, Group> groups = getAllGroups();
		return RESTHelper.paginatedList(uriInfo.getRequestUriBuilder(), limit, start,
			getRootGroupIds(tagsFilter)
				.stream()
				.map(t->createTree(t, groups))
		);
	}

	@GET
	@Path("group/{id}")
	@ApiOperation("Fetch group")
	public Group getGroup(
		@ApiParam("id") @PathParam("id") String id
	) {
		final Map<String, Group> groups = getAllGroups();
		final Group group = groups.get(id);
		if (group == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return TreeHelper.createTree(group.id, groups, Group::resetGroups, Group::addGroup);
	}

	@GET
	@Path("group/tag")
	@ApiOperation("Fetch all group tags")
	public PaginatedCollection<Tag> getGroupTags() {
		return getTags();
	}

	/*
	 * Fetch all groups joined (mongodb lookup) with their applications.
	 */
	private Map<String,Group> getAllGroups() {
		return Maps.uniqueIndex(
			database.getCollection(GROUP_COLLECTION)
				.aggregate(Lists.newArrayList(
					Aggregates.lookup("applications", "id", "group", "applications")
				)).map(Group::new),
			t->t.id
		);
	}

	private PaginatedCollection<Tag> getTags() {
        return RESTHelper.paginatedList(database
        	.getCollection(GROUP_COLLECTION)
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

		// Add empty groups field (if field is missing)
		pipeline.add(Aggregates.project(Projections.fields(
			Projections.include("id"),
			Projections.computed("groups", new Document().append("$ifNull", Lists.newArrayList("$groups", "[]")))
		)));

		// Unwind groups field to be able to self-join
		pipeline.add(Aggregates.unwind("$groups"));

		// Self join on inbound references: group.groups -> group.id and filter no inbound references
		pipeline.add(Aggregates.lookup(GROUP_COLLECTION, "id", "groups", "inbound_links"));
		pipeline.add(Aggregates.match(inboundLinksFilter));

		// Group on id to get distinct group names
		pipeline.add(Aggregates.group("$id"));

		return database
			.getCollection(GROUP_COLLECTION)
			.aggregate(pipeline)
			.map(t->t.getString("_id"))
			.into(Lists.newArrayList());
	}

	private static Group createTree(String groupId, Map<String, Group> groups) {

		final Group group = groups.get(groupId);
		if (group.getGroups().isEmpty()) {
			return group;
		}
		return TreeHelper.createTree(groupId, groups, Group::resetGroups, Group::addGroup);
	}
}
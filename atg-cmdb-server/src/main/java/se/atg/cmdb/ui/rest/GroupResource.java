package se.atg.cmdb.ui.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

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
		@ApiParam("A comma separated list of tags") @QueryParam("tags") String tags 
	) {
		final Optional<Bson> tagsFilter = RESTHelper.parseFilterFromQuery(tags, tags);
		final Map<String, Group> groups = getAllGroups();
		return RESTHelper.paginatedList(
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

	/*
	 * Fetch root groups. That's all groups that has no inbound references from any other group.
	 *  - 
	 *  - 
	 *  - 
	 *  - 
	 */
	private List<String> getRootGroupIds(Optional<Bson> filter) {

		final List<Bson> pipeline = new ArrayList<>(5);

		// Optional filter
		if (filter.isPresent()) {
			pipeline.add(Aggregates.match(filter.get()));
		}

		// Add empty groups field (if field is missing)
		pipeline.add(Aggregates.project(Projections.fields(
			Projections.include("id"),
			Projections.computed("groups", new Document().append("$ifNull", Lists.newArrayList("$groups", "[]")))
		)));

		// Unwind groups field to be able to self-join
		pipeline.add(Aggregates.unwind("$groups"));

		// Self join on inbound references: group.groups -> group.id and filter on no inbound references
		pipeline.add(Aggregates.lookup(GROUP_COLLECTION, "id", "groups", "groups"));
		pipeline.add(Aggregates.match(Filters.size("groups", 0)));

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
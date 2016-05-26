package se.atg.cmdb.ui.rest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.bson.Document;
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
import se.atg.cmdb.helpers.TreeHelper;
import se.atg.cmdb.model.Group;

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
	public List<Group> getGroups() {

		final Map<String, Group> groups = getAllGroups();
		return getRootGroupIds()
			.stream()
			.map(t->createTree(t, groups))
			.collect(Collectors.toList());
	}

	@GET
	@Path("group/{id}")
	@ApiOperation("Fetch all groups")
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
	 *  - Add empty groups field (if field is missing)
	 *  - Unwind groups field to be able to self-join
	 *  - Self join on inbound references: group.groups -> group.id
	 *  - Group on id to get distinct group names
	 */
	private List<String> getRootGroupIds() {
		return Lists.newArrayList(database
			.getCollection(GROUP_COLLECTION)
			.aggregate(Lists.newArrayList(
				Aggregates.project(Projections.fields(
					Projections.include("id"),
					Projections.computed("groups", new Document().append("$ifNull", Lists.newArrayList("$groups", "[]")))
				)),
				Aggregates.unwind("$groups"),
				Aggregates.lookup(GROUP_COLLECTION, "id", "groups", "groups"),
				Aggregates.match(Filters.size("groups", 0)),
				Aggregates.group("$id")
			)).map(t->t.getString("_id"))
		);
	}

	private static Group createTree(String groupId, Map<String, Group> groups) {

		final Group group = groups.get(groupId);
		if (group.getGroups().isEmpty()) {
			return group;
		}
		return TreeHelper.createTree(groupId, groups, Group::resetGroups, Group::addGroup);
	}
}
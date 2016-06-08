package se.atg.cmdb.ui.rest;

import java.util.Arrays;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import se.atg.cmdb.dao.Collections;
import se.atg.cmdb.helpers.JSONHelper;
import se.atg.cmdb.model.Group;
import se.atg.cmdb.model.PaginatedCollection;
import se.atg.cmdb.model.Server;
import se.atg.cmdb.ui.rest.integrationtest.helpers.TestHelper;

public class GroupIntegrationTest {

	@Inject 
	private MongoDatabase database;
	@Inject
	private WebTarget testEndpoint;
	@Inject
	private ObjectMapper objectMapper;

	private MongoCollection<Document> groups;

	@Before
	public void setUp() {
		groups = database.getCollection(Collections.GROUPS);
		groups.deleteMany(new Document());
	}

	@Test
	public void testGetGroup() {

		final Group group = new Group() {{
			id = "my-group";
			name = "My group";
		}};
		final Document bson = JSONHelper.entityToBson(group, objectMapper);
		groups.insertOne(bson);		

		final Group response = getGroup(group.id);
		Assert.assertEquals(group, response);
	}

	@Test
	public void testGetGroups() {

		final Group group1 = new Group() {{
			id = "group1";
			name = "My group 1";
		}};
		groups.insertOne(JSONHelper.entityToBson(group1, objectMapper));		

		final Group group2 = new Group() {{
			id = "group2";
			name = "My group 2";
		}};
		groups.insertOne(JSONHelper.entityToBson(group2, objectMapper));	

		final PaginatedCollection<Group> response = testEndpoint.path("group")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(new GenericType<PaginatedCollection<Group>>(){});

		Assert.assertNull(response.next);
		Assert.assertNull(response.previous);
		Assert.assertNull(response.start);
		Assert.assertNull(response.limit);

		Assert.assertEquals(2, response.items.size());
		TestHelper.assertEquals(Arrays.asList(group1, group2), response.items, Group::getId);
	}

	private Group getGroup(String id) {

		final Response response = testEndpoint.path("group").path(id)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get();
		TestHelper.assertSuccessful(response);
		return response.readEntity(Group.class);
	}
}
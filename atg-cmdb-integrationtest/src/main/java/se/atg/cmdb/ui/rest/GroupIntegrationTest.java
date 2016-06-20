package se.atg.cmdb.ui.rest;

import java.util.Arrays;
import java.util.List;

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

    final Group group1 = new Group() {{
      id = "my-group";
      name = "My group";
    }};
    groups.insertOne(JSONHelper.addMetaForCreate(group1, "intergration-test", objectMapper));

    final Group response = getGroup(group1.id);
    TestHelper.isEqualExceptMeta(group1, response);
  }

  @Test
  public void testGetGroups() {

    final Group group1 = new Group() {{
      id = "group1";
      name = "My group 1";
    }};
    groups.insertOne(JSONHelper.addMetaForCreate(group1, "intergration-test", objectMapper));

    final Group group2 = new Group() {{
      id = "group2";
      name = "My group 2";
    }};
    groups.insertOne(JSONHelper.addMetaForCreate(group2, "intergration-test", objectMapper));

    final PaginatedCollection<Group> response = testEndpoint.path("group")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(new GenericType<PaginatedCollection<Group>>() {});

    Assert.assertNull(response.next);
    Assert.assertNull(response.previous);
    Assert.assertNull(response.start);
    Assert.assertNull(response.limit);

    Assert.assertEquals(2, response.items.size());
    TestHelper.assertEquals(Arrays.asList(group1, group2), response.items, Group::getId, TestHelper::isEqualExceptMeta);
  }

  @Test
  public void testGetGroupTree() {

    final Group root1 = new Group() {{
      id = "root1";
      name = "Root Group 1";
      groups = Arrays.asList(new Group("sub11"), new Group("sub12"));
    }};
    final Group root2 = new Group() {{
      id = "root2";
      name = "Root Group 2";
      groups = Arrays.asList(new Group("sub21"), new Group("sub22"));
    }};
    final Group root3 = new Group() {{
      id = "root3";
      name = "Empty Group";
    }};

    final List<Group> groupsToAdd = Arrays.asList(
      root1,
      root2,
      root3,
      new Group() {{
        id = "sub11";
        name = "Sub-group 1 to Group 1";
      }},
      new Group() {{
        id = "sub12";
        name = "Sub-group 2 to Group 1";
      }},
      new Group() {{
        id = "sub21";
        name = "Sub-group 1 to Group 2";
      }},
      new Group() {{
        id = "sub22";
        name = "Sub-group 2 to Group 2";
      }}
    );
    groups.insertMany(JSONHelper.entitiesToBson(groupsToAdd, objectMapper));

    final PaginatedCollection<Group> response = testEndpoint.path("group")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(new GenericType<PaginatedCollection<Group>>() {});

    Assert.assertNull(response.next);
    Assert.assertNull(response.previous);
    Assert.assertNull(response.start);
    Assert.assertNull(response.limit);

    Assert.assertEquals(3, response.items.size());
    TestHelper.assertEquals(Arrays.asList(root1, root2, root3), response.items, Group::getId, GroupIntegrationTest::verifyGroups);
  }

  private Group getGroup(String id) {

    final Response response = testEndpoint.path("group").path(id)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    TestHelper.assertSuccessful(response);
    return response.readEntity(Group.class);
  }

  private static void verifyGroups(Group expected, Group actual) {
    if (expected.groups != null) {
      Assert.assertNotNull("Expected subgroups for " + actual, actual.groups);
      TestHelper.assertEquals(expected.groups, actual.groups, Group::getId, GroupIntegrationTest::verifyGroups);
    }
    Assert.assertEquals(expected.id, actual.id);
  }
}
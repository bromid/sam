package se.atg.cmdb.ui.rest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import se.atg.cmdb.dao.Collections;
import se.atg.cmdb.helpers.JsonHelper;
import se.atg.cmdb.model.Application;
import se.atg.cmdb.model.ApplicationLink;
import se.atg.cmdb.model.Asset;
import se.atg.cmdb.model.AssetLink;
import se.atg.cmdb.model.Group;
import se.atg.cmdb.model.GroupLink;
import se.atg.cmdb.model.PaginatedCollection;
import se.atg.cmdb.model.Tag;
import se.atg.cmdb.ui.rest.integrationtest.helpers.TestHelper;

public class GroupIntegrationTest {

  @Inject
  private MongoDatabase database;
  @Inject
  private WebTarget testEndpoint;
  @Inject
  private Client client;
  @Inject
  private ObjectMapper objectMapper;

  private MongoCollection<Document> groups;
  private MongoCollection<Document> applications;
  private MongoCollection<Document> assets;

  @Before
  public void setUp() {
    groups = database.getCollection(Collections.GROUPS);
    groups.deleteMany(new Document());

    applications = database.getCollection(Collections.APPLICATIONS);
    applications.deleteMany(new Document());

    assets = database.getCollection(Collections.ASSETS);
    assets.deleteMany(new Document());
  }

  @Test
  public void testGetGroup() {

    final Group group1 = new Group() {{
      id = "my-group";
      name = "My group";
    }};
    groups.insertOne(JsonHelper.addMetaForCreate(group1, "intergration-test", objectMapper));

    final Group response = getGroup(group1.id);
    TestHelper.isEqualExceptMeta(group1, response);
  }

  @Test
  public void testGetGroups() {

    final Group group1 = new Group() {{
      id = "group1";
      name = "My group 1";
    }};
    groups.insertOne(JsonHelper.addMetaForCreate(group1, "intergration-test", objectMapper));

    final Group group2 = new Group() {{
      id = "group2";
      name = "My group 2";
    }};
    groups.insertOne(JsonHelper.addMetaForCreate(group2, "intergration-test", objectMapper));

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
    groups.insertMany(JsonHelper.entitiesToBson(groupsToAdd, objectMapper));

    final PaginatedCollection<Group> response = testEndpoint.path("group")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(new GenericType<PaginatedCollection<Group>>() {});

    Assert.assertNull(response.next);
    Assert.assertNull(response.previous);
    Assert.assertNull(response.start);
    Assert.assertNull(response.limit);

    TestHelper.assertEquals(Arrays.asList(root1, root2, root3), response.items, Group::getId, GroupIntegrationTest::verifyGroups);
  }

  @Test
  public void getGroupIds() {

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
    groups.insertMany(JsonHelper.entitiesToBson(groupsToAdd, objectMapper));

    final PaginatedCollection<String> response = testEndpoint.path("group").path("id")
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get(new GenericType<PaginatedCollection<String>>() {});

    Assert.assertNull(response.next);
    Assert.assertNull(response.previous);
    Assert.assertNull(response.start);
    Assert.assertNull(response.limit);

    final List<String> listOfExpectedIds = groupsToAdd.stream().map(t->t.id).collect(Collectors.toList());
    TestHelper.assertEquals(listOfExpectedIds, response.items, Function.identity(), Objects::equals);
  }

  @Test
  public void addNewGroup() {

    final Group group1 = new Group() {{
      id = "group1";
      name = "Group 1";
      description = "Group description";
    }};
    createGroup(group1);

    final Group group2 = new Group() {{
      id = "group2";
      name = "Group 2";
      description = "Group description";
      groups = Arrays.asList(new Group("group1"), new Group("group3"));
      tags = Arrays.asList(new Tag("tag1"));
    }};
    final GroupResponse createResponse = createGroup(group2);
    Assert.assertNotNull(createResponse.db);

    final Group response = getGroup(createResponse.link);
    TestHelper.assertEquals(Arrays.asList(group1), response.groups, Group::getId, TestHelper::isEqualExceptMeta);

    response.groups = null;
    group2.groups = null;
    TestHelper.isEqualExceptMeta(group2, response);
  }

  @Test
  public void addNewGroupWithApplicationsAndAssets() {

    final Application application1 = new Application() {{
      id = "my-application1";
      name = "My Application 1";
      description = "Min testserver";
      group = new GroupLink("group1");
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application1, "integration-test",  objectMapper));

    final Application application2 = new Application() {{
      id = "my-application2";
      name = "My Application 2";
      description = "Min testserver";
      group = new GroupLink("group1");
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application2, "integration-test",  objectMapper));

    final Asset asset1 = new Asset() {{
      id = "my-asset1";
      name = "Min pryl1";
      description = "Very useful asset";
      group = new GroupLink("group1");
    }};
    assets.insertOne(JsonHelper.addMetaForCreate(asset1, "integration-test", objectMapper));

    final Asset asset2 = new Asset() {{
      id = "my-asset2";
      name = "Min pryl2";
      description = "Very useful asset";
      group = new GroupLink("group1");
    }};
    assets.insertOne(JsonHelper.addMetaForCreate(asset2, "integration-test", objectMapper));

    final Group group1 = new Group() {{
      id = "group1";
      name = "Group 1";
      description = "Group description";
    }};
    final GroupResponse createResponse = createGroup(group1);
    Assert.assertNotNull(createResponse.db);

    final Group response = getGroup(createResponse.link);
    Assert.assertEquals(group1.id, response.id);
    Assert.assertEquals(group1.name, response.name);
    Assert.assertEquals(group1.description, response.description);

    final List<String> expectedApplicationIds = Lists.newArrayList(application1.id, application2.id);
    final List<String> actualApplicationIds = response.applications.stream().map(t->t.getId()).collect(Collectors.toList());
    Assert.assertEquals(expectedApplicationIds, actualApplicationIds);

    final List<String> expectedAssetIds = Lists.newArrayList(asset1.id, asset2.id);
    final List<String> actualAssetIds = response.assets.stream().map(t->t.getId()).collect(Collectors.toList());
    Assert.assertEquals(expectedAssetIds, actualAssetIds);
  }

  @Test
  public void newGroupMustHaveId() {

    final Group group1 = new Group() {{
      name = "Group 1";
    }};
    final Response response = testEndpoint.path("group").path("group-id")
        .request(MediaType.APPLICATION_JSON_TYPE)
        .put(Entity.json(group1));
      TestHelper.assertValidationError("id may not be null", response);
  }

  @Test
  public void newGroupMustHaveName() {

    final Group group1 = new Group() {{
      id = "group1";
    }};
    final Response response = testEndpoint.path("group").path(group1.id)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .put(Entity.json(group1));
    TestHelper.assertValidationError("name may not be null", response);
  }

  @Test
  public void newGroupCantHaveApplications() {

    final Group group1 = new Group() {{
      id = "Group1";
      name = "Group 1";
      applications = Lists.newArrayList(new ApplicationLink("id", "name"));
    }};
    final Response response = testEndpoint.path("group").path(group1.id)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .put(Entity.json(group1));
    TestHelper.assertValidationError("applications must be null", response);
  }

  @Test
  public void newGroupCantHaveAssets() {

    final Group group1 = new Group() {{
      id = "Group1";
      name = "Group 1";
      assets = Lists.newArrayList(new AssetLink("id", "name"));
    }};
    final Response response = testEndpoint.path("group").path(group1.id)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .put(Entity.json(group1));
    TestHelper.assertValidationError("assets must be null", response);
  }

  @Test
  public void patchGroup() {

    /*
     * Create group
     */
    final Group group1 = new Group() {{
      id = "group1";
      name = "Group 1";
      description = "Group description";
      tags = Lists.newArrayList(new Tag("Tag1"), new Tag("Tag2"));
    }};
    final GroupResponse createGroupResponse = createGroup(group1);

    /*
     * Patch group
     */
    final Group groupPatch = new Group() {{
      name = "My new group";
      description = "Updated description";
      tags = Lists.newArrayList(new Tag("Tag2"), new Tag("Tag3"));
    }};
    final GroupResponse patchedGroupResponse = patchGroup(groupPatch, createGroupResponse.link);

    /*
     * Get and verify
     */
    final Group patchedGroup = getGroup(patchedGroupResponse.link);
    Assert.assertEquals(group1.id, patchedGroup.id);
    Assert.assertEquals(groupPatch.name, patchedGroup.name);
    Assert.assertEquals(groupPatch.description, patchedGroup.description);
    Assert.assertEquals(groupPatch.tags, patchedGroup.tags);
  }

  @Test
  public void patchGroupCantHaveApplications() {

    /*
     * Create group
     */
    final Group group1 = new Group() {{
      id = "group1";
      name = "Group 1";
      description = "Group description";
      tags = Lists.newArrayList(new Tag("Tag1"), new Tag("Tag2"));
    }};
    final GroupResponse createGroupResponse = createGroup(group1);

    /*
     * Patch group
     */
    final Group groupPatch = new Group() {{
      name = "My new group";
      applications = Lists.newArrayList(new ApplicationLink("id", "name"));
    }};

    final Response response = client.target(createGroupResponse.link)
      .request(MediaType.APPLICATION_JSON)
      .build("PATCH", Entity.json(groupPatch))
      .invoke();
    TestHelper.assertValidationError("applications must be null", response);
  }

  @Test
  public void patchGroupCantHaveAssets() {

    /*
     * Create group
     */
    final Group group1 = new Group() {{
      id = "group1";
      name = "Group 1";
      description = "Group description";
      tags = Lists.newArrayList(new Tag("Tag1"), new Tag("Tag2"));
    }};
    final GroupResponse createGroupResponse = createGroup(group1);

    /*
     * Patch group
     */
    final Group groupPatch = new Group() {{
      description = "Updated description";
      assets = Lists.newArrayList(new AssetLink("id", "name"));
    }};

    final Response response = client.target(createGroupResponse.link)
      .request(MediaType.APPLICATION_JSON)
      .build("PATCH", Entity.json(groupPatch))
      .invoke();
    TestHelper.assertValidationError("assets must be null", response);
  }

  @Test
  public void deleteGroup() {

    final Group group1 = new Group() {{
      id = "group1";
      name = "Group 1";
    }};
    final GroupResponse createResponse = createGroup(group1);

    final Response deleteResponse = client.target(createResponse.link)
      .request(MediaType.APPLICATION_JSON)
      .delete();
    TestHelper.assertSuccessful(deleteResponse);

    final Response response = client.target(createResponse.link)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    Assert.assertEquals(404, response.getStatus());
  }

  private GroupResponse createGroup(Group group) {

    final Response response = testEndpoint.path("group").path(group.id)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .put(Entity.json(group));
    TestHelper.assertSuccessful(response);

    final Document db = groups.find(Filters.eq("id", group.id)).first();
    final GroupLink link = response.readEntity(GroupLink.class);
    return new GroupResponse(link, db);
  }

  private GroupResponse patchGroup(Group groupPatch, Link groupLink) {

    final Response response = client.target(groupLink)
      .request(MediaType.APPLICATION_JSON)
      .build("PATCH", Entity.json(groupPatch))
      .invoke();
    TestHelper.assertSuccessful(response);

    final GroupLink link = response.readEntity(GroupLink.class);
    final Document db = groups.find(
      Filters.and(
        Filters.eq("id", link.id)
      )).first();
    return new GroupResponse(link, db);
  }

  private Group getGroup(String id) {

    final Response response = testEndpoint.path("group").path(id)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    TestHelper.assertSuccessful(response);
    return response.readEntity(Group.class);
  }

  private Group getGroup(Link link) {

    final Response response = client.target(link)
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

  static class GroupResponse {

    public Link link;
    public Document db;

    GroupResponse(GroupLink link, Document db) {
      Assert.assertEquals(link.id, db.getString("id"));
      Assert.assertEquals(link.name, db.getString("name"));
      this.db = db;
      this.link = link.link;
    }
  }
}

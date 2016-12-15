package se.atg.sam.ui.rest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import se.atg.sam.dao.Collections;
import se.atg.sam.model.Application;
import se.atg.sam.model.ApplicationLink;
import se.atg.sam.model.Asset;
import se.atg.sam.model.AssetLink;
import se.atg.sam.model.Group;
import se.atg.sam.model.GroupLink;
import se.atg.sam.model.PaginatedCollection;
import se.atg.sam.model.Tag;
import se.atg.sam.ui.rest.integrationtest.EntityResponse;
import se.atg.sam.ui.rest.integrationtest.helpers.RestHelper;
import se.atg.sam.ui.rest.integrationtest.helpers.TestHelper;

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
    groups.insertOne(TestHelper.addMetaForCreate(group1, objectMapper));

    final Group response = getGroup(group1.id);
    TestHelper.isEqualExceptMeta(group1, response);
  }

  @Test
  public void testGetGroups() {

    final Group group1 = new Group() {{
      id = "group1";
      name = "My group 1";
    }};
    groups.insertOne(TestHelper.addMetaForCreate(group1, objectMapper));

    final Group group2 = new Group() {{
      id = "group2";
      name = "My group 2";
    }};
    groups.insertOne(TestHelper.addMetaForCreate(group2, objectMapper));

    final PaginatedCollection<Group> response = testEndpoint.path("group")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(new GenericType<PaginatedCollection<Group>>() {});

    Assert.assertNull(response.next);
    Assert.assertNull(response.previous);
    Assert.assertNull(response.start);
    Assert.assertNull(response.limit);

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
    final Group root4 = new Group() {{
      id = "root4";
      name = "Contains non-existing subgroup";
      groups = Arrays.asList(new Group("sub11"), new Group("unknown"));
    }};

    final List<Group> groupsToAdd = Arrays.asList(
      root1,
      root2,
      root3,
      root4,
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
    groups.insertMany(TestHelper.addMetaForCreate(groupsToAdd, objectMapper));

    final PaginatedCollection<Group> response = testEndpoint.path("group")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(new GenericType<PaginatedCollection<Group>>() {});

    Assert.assertNull(response.next);
    Assert.assertNull(response.previous);
    Assert.assertNull(response.start);
    Assert.assertNull(response.limit);

    TestHelper.assertEquals(Arrays.asList(root1, root2, root3, root4), response.items, Group::getId, GroupIntegrationTest::verifyGroups);
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
    groups.insertMany(TestHelper.addMetaForCreate(groupsToAdd, objectMapper));

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

    final Group group3 = new Group("group3");

    final Group group2 = new Group() {{
      id = "group2";
      name = "Group 2";
      description = "Group description";
      groups = Arrays.asList(new Group("group1"), group3);
      tags = Arrays.asList(new Tag("tag1"));
    }};
    final GroupResponse createResponse = createGroup(group2);
    Assert.assertNotNull(createResponse.db);

    final EntityResponse<Group> response = getGroup(createResponse.link);
    TestHelper.assertEquals(Arrays.asList(group1, group3), response.entity.groups, Group::getId, TestHelper::isEqualDisregardMeta);

    response.entity.groups = null;
    group2.groups = null;
    TestHelper.isEqualExceptMeta(group2, response.entity);
  }

  @Test
  public void addNewGroupWithApplicationsAndAssets() {

    final Application application1 = new Application() {{
      id = "my-application1";
      name = "My Application 1";
      description = "Min testserver";
      group = new GroupLink("group1");
    }};
    applications.insertOne(TestHelper.addMetaForCreate(application1, objectMapper));

    final Application application2 = new Application() {{
      id = "my-application2";
      name = "My Application 2";
      description = "Min testserver";
      group = new GroupLink("group1");
    }};
    applications.insertOne(TestHelper.addMetaForCreate(application2, objectMapper));

    final Asset asset1 = new Asset() {{
      id = "my-asset1";
      name = "Min pryl1";
      description = "Very useful asset";
      group = new GroupLink("group1");
    }};
    assets.insertOne(TestHelper.addMetaForCreate(asset1, objectMapper));

    final Asset asset2 = new Asset() {{
      id = "my-asset2";
      name = "Min pryl2";
      description = "Very useful asset";
      group = new GroupLink("group1");
    }};
    assets.insertOne(TestHelper.addMetaForCreate(asset2, objectMapper));

    final Group group1 = new Group() {{
      id = "group1";
      name = "Group 1";
      description = "Group description";
    }};
    final GroupResponse createResponse = createGroup(group1);
    Assert.assertNotNull(createResponse.db);

    final EntityResponse<Group> response = getGroup(createResponse.link);
    Assert.assertEquals(group1.id, response.entity.id);
    Assert.assertEquals(group1.name, response.entity.name);
    Assert.assertEquals(group1.description, response.entity.description);

    final List<String> expectedApplicationIds = Lists.newArrayList(application1.id, application2.id);
    final List<String> actualApplicationIds = response.entity.applications.stream().map(t->t.getId()).collect(Collectors.toList());
    Assert.assertEquals(expectedApplicationIds, actualApplicationIds);

    final List<String> expectedAssetIds = Lists.newArrayList(asset1.id, asset2.id);
    final List<String> actualAssetIds = response.entity.assets.stream().map(t->t.getId()).collect(Collectors.toList());
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
    final EntityResponse<Group> patchedGroup = getGroup(patchedGroupResponse.link);
    Assert.assertEquals(group1.id, patchedGroup.entity.id);
    Assert.assertEquals(groupPatch.name, patchedGroup.entity.name);
    Assert.assertEquals(groupPatch.description, patchedGroup.entity.description);
    Assert.assertEquals(groupPatch.tags, patchedGroup.entity.tags);
  }

  @Test
  public void shallowPatchGroup() {

    /*
     * Create group
     */
    final Group group1 = new Group() {{
      id = "group1";
      name = "Group 1";
      description = "Group description";
      tags = Lists.newArrayList(new Tag("Tag1"), new Tag("Tag2"));
      attributes = ImmutableMap.of(
        "test1", ImmutableMap.of(
          "test11", "old",
          "test12", "old"
        ),
        "test2", ImmutableMap.of(
          "test21", "old"
        )
      );
    }};
    final GroupResponse createGroupResponse = createGroup(group1);

    /*
     * Patch group
     */
    final Group groupPatch = new Group() {{
      name = "My new group";
      description = "Updated description";
      tags = Lists.newArrayList(new Tag("Tag2"), new Tag("Tag3"));
      attributes = ImmutableMap.of(
        "test1", ImmutableMap.of(
          "test11", "new"
        )
      );
    }};
    final GroupResponse patchedGroupResponse = patchGroup(groupPatch, createGroupResponse.link, Optional.of(1));

    /*
     * Get and verify
     */
    final EntityResponse<Group> patchedGroup = getGroup(patchedGroupResponse.link);
    Assert.assertEquals(group1.id, patchedGroup.entity.id);
    Assert.assertEquals(groupPatch.name, patchedGroup.entity.name);
    Assert.assertEquals(groupPatch.description, patchedGroup.entity.description);
    Assert.assertEquals(groupPatch.tags, patchedGroup.entity.tags);
    Assert.assertEquals("new", TestHelper.mapPath(patchedGroup. entity.attributes, "test1", "test11"));
    Assert.assertNull(TestHelper.mapPath(patchedGroup.entity.attributes, "test1", "test12"));
    Assert.assertEquals("old", TestHelper.mapPath(patchedGroup.entity.attributes, "test2", "test21"));
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

  @Test
  public void getSubGroups() {

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
    final Group root4 = new Group() {{
      id = "root4";
      name = "Contains non-existing subgroup";
      groups = Arrays.asList(new Group("sub11"), new Group("unknown"));
    }};
    final Group sub11 = new Group() {{
      id = "sub11";
      name = "Sub-group 1 to Group 1";
    }};
    final Group sub12 = new Group() {{
      id = "sub12";
      name = "Sub-group 2 to Group 1";
    }};
    final Group sub21 = new Group() {{
      id = "sub21";
      name = "Sub-group 1 to Group 2";
    }};
    final Group sub22 = new Group() {{
      id = "sub22";
      name = "Sub-group 2 to Group 2";
    }};

    final List<Group> groupsToAdd = Arrays.asList(
      root1,
      root2,
      root3,
      root4,
      sub11,
      sub12,
      sub21,
      sub22
    );
    groups.insertMany(TestHelper.addMetaForCreate(groupsToAdd, objectMapper));

    final EntityResponse<PaginatedCollection<Group>> response1 = getSubGroups(root1.id);
    TestHelper.assertEquals(Arrays.asList(sub11, sub12), response1.entity.items, Group::getId, TestHelper::isEqualExceptMeta);

    final EntityResponse<PaginatedCollection<Group>> response2 = getSubGroups(root2.id);
    TestHelper.assertEquals(Arrays.asList(sub21, sub22), response2.entity.items, Group::getId, TestHelper::isEqualExceptMeta);

    final EntityResponse<PaginatedCollection<Group>> response3 = getSubGroups(root3.id);
    Assert.assertNull(response3.entity.items);

    final EntityResponse<PaginatedCollection<Group>> response4 = getSubGroups(root4.id);
    Assert.assertEquals(response4.entity.items.size(), 2);
  }

  @Test
  public void addSubGroup() {

    final Group root1 = new Group() {{
      id = "root1";
      name = "Root Group 1";
      groups = Arrays.asList(new Group("sub1"), new Group("sub2"));
    }};
    final Group root2 = new Group() {{
      id = "root2";
      name = "Root Group 2";
    }};
    final Group sub1 = new Group() {{
      id = "sub1";
      name = "Sub-group 1";
    }};
    final Group sub2 = new Group() {{
      id = "sub2";
      name = "Sub-group 2";
    }};
    final Group sub3 = new Group() {{
      id = "sub3";
      name = "Sub-group 3";
    }};
    final Group sub4 = new Group() {{
      id = "sub4";
      name = "Sub-group 4";
    }};

    final List<Group> groupsToAdd = Arrays.asList(
      root1,
      root2,
      sub1,
      sub2,
      sub3,
      sub4
    );
    groups.insertMany(TestHelper.addMetaForCreate(groupsToAdd, objectMapper));

    /*
     * Verify initial state
     */
    final EntityResponse<PaginatedCollection<Group>> response1 = getSubGroups(root1.id);
    TestHelper.assertEquals(Arrays.asList(sub1, sub2), response1.entity.items, Group::getId, TestHelper::isEqualExceptMeta);

    final EntityResponse<PaginatedCollection<Group>> response2 = getSubGroups(root2.id);
    Assert.assertNull(response2.entity.items);

    /*
     * Add a subgroup to a group
     */
    final EntityResponse<GroupLink> addSubGroupResponse1 = addSubGroup(root2.id, sub4.id);
    final EntityResponse<PaginatedCollection<Group>> response3 = getSubGroups(root2.id);
    TestHelper.assertEquals(Arrays.asList(sub4), response3.entity.items, Group::getId, TestHelper::isEqualExceptMeta);
    Assert.assertNotEquals(response1.etag, addSubGroupResponse1.etag);

    /*
     * Add a subgroup to a group with existing subgroups
     */
    final EntityResponse<GroupLink> addSubGroupResponse2 = addSubGroup(root1.id, sub3.id);
    final EntityResponse<PaginatedCollection<Group>> response4 = getSubGroups(root1.id);
    TestHelper.assertEquals(Arrays.asList(sub1, sub2, sub3), response4.entity.items, Group::getId, TestHelper::isEqualExceptMeta);
    Assert.assertNotEquals(response1.etag, addSubGroupResponse2.etag);

    /*
     * Verify that the operation is idempotent
     */
    final EntityResponse<GroupLink> addSubGroupResponse3 = addSubGroup(root1.id, sub3.id);
    final EntityResponse<PaginatedCollection<Group>> response5 = getSubGroups(root1.id);
    TestHelper.assertEquals(Arrays.asList(sub1, sub2, sub3), response5.entity.items, Group::getId, TestHelper::isEqualExceptMeta);
    Assert.assertEquals(addSubGroupResponse2.etag, addSubGroupResponse3.etag);

    /*
     * Add one more subgroup to a group
     */
    final EntityResponse<GroupLink> addSubGroupResponse4 = addSubGroup(root1.id, sub4.id);
    final EntityResponse<PaginatedCollection<Group>> response6 = getSubGroups(root1.id);
    TestHelper.assertEquals(Arrays.asList(sub1, sub2, sub3, sub4), response6.entity.items, Group::getId, TestHelper::isEqualExceptMeta);
    Assert.assertNotEquals(addSubGroupResponse3.etag, addSubGroupResponse4.etag);
  }

  @Test
  public void removeSubGroup() {

    final Group root1 = new Group() {{
      id = "root1";
      name = "Root Group 1";
      groups = Arrays.asList(new Group("sub1"), new Group("sub2"));
    }};
    final Group root2 = new Group() {{
      id = "root2";
      name = "Root Group 2";
    }};
    final Group sub1 = new Group() {{
      id = "sub1";
      name = "Sub-group 1";
    }};
    final Group sub2 = new Group() {{
      id = "sub2";
      name = "Sub-group 2";
    }};
    final Group sub3 = new Group() {{
      id = "sub3";
      name = "Sub-group 3";
    }};
    final Group sub4 = new Group() {{
      id = "sub4";
      name = "Sub-group 4";
    }};

    final List<Group> groupsToAdd = Arrays.asList(
      root1,
      root2,
      sub1,
      sub2,
      sub3,
      sub4
    );
    groups.insertMany(TestHelper.addMetaForCreate(groupsToAdd, objectMapper));

    /*
     * Verify initial state
     */
    final EntityResponse<PaginatedCollection<Group>> response1 = getSubGroups(root1.id);
    TestHelper.assertEquals(Arrays.asList(sub1, sub2), response1.entity.items, Group::getId, TestHelper::isEqualExceptMeta);

    final EntityResponse<PaginatedCollection<Group>> response2 = getSubGroups(root2.id);
    Assert.assertNull(response2.entity.items);

    /*
     * Remove a sub group
     */
    final EntityResponse<GroupLink> removeSubGroupResponse1 = removeSubGroup(root1.id, sub1.id);
    final EntityResponse<PaginatedCollection<Group>> response3 = getSubGroups(root1.id);
    TestHelper.assertEquals(Arrays.asList(sub2), response3.entity.items, Group::getId, TestHelper::isEqualExceptMeta);

    /*
     * Verify that the operation is idempotent
     */
    final EntityResponse<GroupLink> removeSubGroupResponse2 = removeSubGroup(root1.id, sub1.id);
    final EntityResponse<PaginatedCollection<Group>> response4 = getSubGroups(root1.id);
    TestHelper.assertEquals(Arrays.asList(sub2), response4.entity.items, Group::getId, TestHelper::isEqualExceptMeta);
    Assert.assertEquals(removeSubGroupResponse1.etag, removeSubGroupResponse2.etag);

    /*
     * Remove subgroup from group without subgroups
     */
    removeSubGroup(root2.id, sub1.id);
    final EntityResponse<PaginatedCollection<Group>> response5 = getSubGroups(root2.id);
    Assert.assertNull(response5.entity.items);

    /*
     * Remove last subgroup from group
     */
    removeSubGroup(root1.id, sub2.id);
    final EntityResponse<PaginatedCollection<Group>> response6 = getSubGroups(root1.id);
    Assert.assertNull(response6.entity.items);
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
    return patchGroup(groupPatch, groupLink, Optional.empty());
  }

  private GroupResponse patchGroup(Group groupPatch, Link groupLink, Optional<Integer> mergeDepth) {

    final Response response = RestHelper.queryParam(client.target(groupLink), mergeDepth, "mergedepth")
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

  private EntityResponse<Group> getGroup(Link link) {

    final Response response = client.target(link)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    TestHelper.assertSuccessful(response);
    return new EntityResponse<>(response, Group.class);
  }

  private EntityResponse<PaginatedCollection<Group>> getSubGroups(String id) {

    final Response response = testEndpoint.path("group").path(id).path("group")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    TestHelper.assertSuccessful(response);
    return new EntityResponse<>(response, new GenericType<PaginatedCollection<Group>>() {});
  }

  private EntityResponse<GroupLink> addSubGroup(String groupId, String subGroupId) {

    final Response response = testEndpoint.path("group").path(groupId).path("group").path(subGroupId)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .put(Entity.json(new Group()));
    TestHelper.assertSuccessful(response);
    return new EntityResponse<>(response, GroupLink.class);
  }

  private EntityResponse<GroupLink> removeSubGroup(String groupId, String subGroupId) {

    final Response response = testEndpoint.path("group").path(groupId).path("group").path(subGroupId)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .delete();
    TestHelper.assertSuccessful(response);
    return new EntityResponse<>(response, GroupLink.class);
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

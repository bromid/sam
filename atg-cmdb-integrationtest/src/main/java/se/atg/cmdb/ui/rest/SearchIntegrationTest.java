package se.atg.cmdb.ui.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
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

import se.atg.cmdb.dao.Collections;
import se.atg.cmdb.model.Application;
import se.atg.cmdb.model.Asset;
import se.atg.cmdb.model.Group;
import se.atg.cmdb.model.GroupLink;
import se.atg.cmdb.model.SearchResult;
import se.atg.cmdb.model.Server;
import se.atg.cmdb.model.Tag;
import se.atg.cmdb.model.search.ApplicationSearchResult;
import se.atg.cmdb.model.search.AssetSearchResult;
import se.atg.cmdb.model.search.GroupSearchResult;
import se.atg.cmdb.model.search.ServerSearchResult;
import se.atg.cmdb.ui.rest.integrationtest.helpers.TestHelper;

public class SearchIntegrationTest {

  @Inject
  private MongoDatabase database;
  @Inject
  private WebTarget testEndpoint;
  @Inject
  private ObjectMapper objectMapper;

  private MongoCollection<Document> servers;
  private MongoCollection<Document> groups;
  private MongoCollection<Document> applications;
  private MongoCollection<Document> assets;

  @Before
  public void setUp() {
    servers = database.getCollection(Collections.SERVERS);
    servers.deleteMany(new Document());

    groups = database.getCollection(Collections.GROUPS);
    groups.deleteMany(new Document());

    applications = database.getCollection(Collections.APPLICATIONS);
    applications.deleteMany(new Document());

    assets = database.getCollection(Collections.ASSETS);
    assets.deleteMany(new Document());
  }

  @Test
  public void searchParameterIsMandatory() {

    final Response response = testEndpoint
      .path("search")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(Response.class);
    TestHelper.assertValidationError("A search string must be provided in the query parameter 'q'", response);
  }

  @Test
  public void searchParameterCantBeEmpty() {

    final Response response = testEndpoint
      .path("search").queryParam("q", "")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(Response.class);
    TestHelper.assertValidationError("A search string must be provided in the query parameter 'q'", response);
  }

  @Test
  public void specialCharactersNotAllowedInWildcardSearch() {

    final Response response = testEndpoint
      .path("search").queryParam("q", "vl/tm*")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(Response.class);
    TestHelper.assertValidationError("The only allowed special characters in a wildcard search is the wildcard operator", response);
  }

  @Test
  public void multiplelWildcardOperatorsNotAllowed() {

    final Response response = testEndpoint
      .path("search").queryParam("q", "*ltma*")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(Response.class);
    TestHelper.assertValidationError("A wildcard search can only contain one wildcard operator", response);
  }

  @Test
  public void allowedCharactersInWildcardSearch() {

    final Response response = testEndpoint
      .path("search").queryParam("q", "special-id.atg*")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(Response.class);
    TestHelper.assertSuccessful(response);
  }

  @Test
  public void textSearchServer() {

    final ArrayList<Server> testServers = addServerTestset();

    final SearchResult searchResult = testEndpoint
      .path("search").queryParam("q", "vltma1")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(SearchResult.class);

    final Collection<ServerSearchResult> actualServers = searchResult.servers.items;
    Assert.assertNull(searchResult.groups.items);
    Assert.assertNull(searchResult.applications.items);
    Assert.assertNull(searchResult.assets.items);

    final Map<String, Server> expectedMap = ImmutableMap.of(
      testServers.get(0).fqdn, testServers.get(0),
      testServers.get(2).fqdn, testServers.get(2),
      testServers.get(3).fqdn, testServers.get(3),
      testServers.get(4).fqdn, testServers.get(4)
    );
    TestHelper.assertEquals(expectedMap, actualServers, ServerSearchResult::getFqdn, SearchIntegrationTest::verifyServerSearchResult);
  }

  @Test
  public void wildcardSearchBeginningOfServer() {

    final ArrayList<Server> testServers = addServerTestset();

    final SearchResult searchResult = testEndpoint
      .path("search").queryParam("q", "vltm*")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(SearchResult.class);

    final Collection<ServerSearchResult> actualServers = searchResult.servers.items;
    Assert.assertNull(searchResult.groups.items);
    Assert.assertNull(searchResult.applications.items);
    Assert.assertNull(searchResult.assets.items);

    final Map<String, Server> expectedMap = ImmutableMap.of(
      testServers.get(0).fqdn, testServers.get(0),
      testServers.get(2).fqdn, testServers.get(2),
      testServers.get(3).fqdn, testServers.get(3),
      testServers.get(4).fqdn, testServers.get(4)
    );
    TestHelper.assertEquals(expectedMap, actualServers, ServerSearchResult::getFqdn, SearchIntegrationTest::verifyServerSearchResult);
  }

  @Test
  public void wildcardSearchEndOfServer() {

    final ArrayList<Server> testServers = addServerTestset();

    final SearchResult searchResult = testEndpoint
      .path("search").queryParam("q", "*st1")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(SearchResult.class);

    final Collection<ServerSearchResult> actualServers = searchResult.servers.items;
    Assert.assertNull(searchResult.groups.items);
    Assert.assertNull(searchResult.applications.items);
    Assert.assertNull(searchResult.assets.items);

    final Map<String, Server> expectedMap = ImmutableMap.of(
      testServers.get(0).fqdn, testServers.get(0),
      testServers.get(1).fqdn, testServers.get(1),
      testServers.get(3).fqdn, testServers.get(3)
    );
    TestHelper.assertEquals(expectedMap, actualServers, ServerSearchResult::getFqdn, SearchIntegrationTest::verifyServerSearchResult);
  }

  @Test
  public void wildcardSearchMiddleOfServer() {

    final ArrayList<Server> testServers = addServerTestset();

    final SearchResult searchResult = testEndpoint
      .path("search").queryParam("q", "vlp*se")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(SearchResult.class);

    final Collection<ServerSearchResult> actualServers = searchResult.servers.items;
    Assert.assertNull(searchResult.groups.items);
    Assert.assertNull(searchResult.applications.items);
    Assert.assertNull(searchResult.assets.items);

    final Map<String, Server> expectedMap = ImmutableMap.of(
      testServers.get(1).fqdn, testServers.get(1),
      testServers.get(4).fqdn, testServers.get(4)
    );
    TestHelper.assertEquals(expectedMap, actualServers, ServerSearchResult::getFqdn, SearchIntegrationTest::verifyServerSearchResult);
  }

  @Test
  public void textSearchGroup() {

    final ArrayList<Group> testGroups = addGroupTestset();

    final SearchResult searchResult = testEndpoint
      .path("search").queryParam("q", "groupid1")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(SearchResult.class);

    final Collection<GroupSearchResult> actualGroups = searchResult.groups.items;
    Assert.assertNull(searchResult.servers.items);
    Assert.assertNull(searchResult.applications.items);
    Assert.assertNull(searchResult.assets.items);

    final Map<String, Group> expectedMap = ImmutableMap.of(
      testGroups.get(0).id, testGroups.get(0),
      testGroups.get(2).id, testGroups.get(2),
      testGroups.get(3).id, testGroups.get(3),
      testGroups.get(4).id, testGroups.get(4)
    );
    TestHelper.assertEquals(expectedMap, actualGroups, GroupSearchResult::getId, SearchIntegrationTest::verifyGroupSearchResult);
  }

  @Test
  public void wildcardSearchGroup() {

    final ArrayList<Group> testGroups = addGroupTestset();

    final SearchResult searchResult = testEndpoint
      .path("search").queryParam("q", "*pid1")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(SearchResult.class);

    final Collection<GroupSearchResult> actualGroups = searchResult.groups.items;
    Assert.assertNull(searchResult.servers.items);
    Assert.assertNull(searchResult.applications.items);
    Assert.assertNull(searchResult.assets.items);

    final Map<String, Group> expectedMap = ImmutableMap.of(
      testGroups.get(0).id, testGroups.get(0),
      testGroups.get(2).id, testGroups.get(2),
      testGroups.get(4).id, testGroups.get(4)
    );
    TestHelper.assertEquals(expectedMap, actualGroups, GroupSearchResult::getId, SearchIntegrationTest::verifyGroupSearchResult);
  }

  @Test
  public void textSearchApplications() {

    final ArrayList<Application> testApplications = addApplicationTestset();

    final SearchResult searchResult = testEndpoint
      .path("search").queryParam("q", "applicationid1")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(SearchResult.class);

    final Collection<ApplicationSearchResult> actualApplications = searchResult.applications.items;
    Assert.assertNull(searchResult.servers.items);
    Assert.assertNull(searchResult.groups.items);
    Assert.assertNull(searchResult.assets.items);

    final Map<String, Application> expectedMap = ImmutableMap.of(
      testApplications.get(0).id, testApplications.get(0),
      testApplications.get(2).id, testApplications.get(2),
      testApplications.get(3).id, testApplications.get(3)
    );
    TestHelper.assertEquals(
      expectedMap,
      actualApplications,
      ApplicationSearchResult::getId,
      SearchIntegrationTest::verifyApplicationSearchResult
    );
  }

  @Test
  public void wildcardSearchApplications() {

    final ArrayList<Application> testApplications = addApplicationTestset();

    final SearchResult searchResult = testEndpoint
      .path("search").queryParam("q", "appname*")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(SearchResult.class);

    final Collection<ApplicationSearchResult> actualApplications = searchResult.applications.items;
    Assert.assertNull(searchResult.servers.items);
    Assert.assertNull(searchResult.groups.items);
    Assert.assertNull(searchResult.assets.items);

    final Map<String, Application> expectedMap = ImmutableMap.of(
      testApplications.get(1).id, testApplications.get(1),
      testApplications.get(3).id, testApplications.get(3),
      testApplications.get(4).id, testApplications.get(4)
    );
    TestHelper.assertEquals(
      expectedMap,
      actualApplications,
      ApplicationSearchResult::getId,
      SearchIntegrationTest::verifyApplicationSearchResult
    );
  }

  @Test
  public void textSearchAssets() {

    final ArrayList<Asset> testAssets = addAssetTestset();

    final SearchResult searchResult = testEndpoint
      .path("search").queryParam("q", "assetid1")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(SearchResult.class);

    final Collection<AssetSearchResult> actualAssets = searchResult.assets.items;
    Assert.assertEquals(3, actualAssets.size());
    Assert.assertNull(searchResult.servers.items);
    Assert.assertNull(searchResult.groups.items);
    Assert.assertNull(searchResult.applications.items);

    final Map<String, Asset> expectedMap = ImmutableMap.of(
      testAssets.get(0).id, testAssets.get(0),
      testAssets.get(2).id, testAssets.get(2),
      testAssets.get(3).id, testAssets.get(3)
    );
    TestHelper.assertEquals(expectedMap, actualAssets, AssetSearchResult::getId, SearchIntegrationTest::verifyAssetSearchResult);
  }

  @Test
  public void wildcardSearchAssets() {

    final ArrayList<Asset> testAssets = addAssetTestset();

    final SearchResult searchResult = testEndpoint
      .path("search").queryParam("q", "ass*1")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(SearchResult.class);

    final Collection<AssetSearchResult> actualAssets = searchResult.assets.items;
    Assert.assertEquals(3, actualAssets.size());
    Assert.assertNull(searchResult.servers.items);
    Assert.assertNull(searchResult.groups.items);
    Assert.assertNull(searchResult.applications.items);

    final Map<String, Asset> expectedMap = ImmutableMap.of(
      testAssets.get(0).id, testAssets.get(0),
      testAssets.get(2).id, testAssets.get(2),
      testAssets.get(4).id, testAssets.get(4)
    );
    TestHelper.assertEquals(expectedMap, actualAssets, AssetSearchResult::getId, SearchIntegrationTest::verifyAssetSearchResult);
  }

  @Test
  public void textSearchCombined() {

    final ArrayList<Server> testServers = addServerTestset();
    final ArrayList<Group> testGroups = addGroupTestset();
    final ArrayList<Application> testApplications = addApplicationTestset();
    final ArrayList<Asset> testAssets = addAssetTestset();

    final SearchResult searchResult = testEndpoint
      .path("search").queryParam("q", "webbapp och asset")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(SearchResult.class);

    final Map<String, Server> expectedServersMap = ImmutableMap.of(
      testServers.get(4).fqdn,
      testServers.get(4)
    );
    final Collection<ServerSearchResult> actualServers = searchResult.servers.items;
    TestHelper.assertEquals(
      expectedServersMap,
      actualServers,
      ServerSearchResult::getFqdn,
      SearchIntegrationTest::verifyServerSearchResult
    );

    final Map<String, Group> expectedGroupsMap = ImmutableMap.of(
      testGroups.get(0).id, testGroups.get(0),
      testGroups.get(2).id, testGroups.get(2)
    );
    final Collection<GroupSearchResult> actualGroups = searchResult.groups.items;
    TestHelper.assertEquals(
      expectedGroupsMap,
      actualGroups,
      GroupSearchResult::getId,
      SearchIntegrationTest::verifyGroupSearchResult
    );

    final Map<String, Application> expectedApplicationsMap = ImmutableMap.of(
      testApplications.get(0).id, testApplications.get(0),
      testApplications.get(5).id, testApplications.get(5)
    );
    final Collection<ApplicationSearchResult> actualApplications = searchResult.applications.items;
    TestHelper.assertEquals(
      expectedApplicationsMap,
      actualApplications,
      ApplicationSearchResult::getId,
      SearchIntegrationTest::verifyApplicationSearchResult
    );

    final Map<String, Asset> expectedAssetsMap = ImmutableMap.of(
      testAssets.get(1).id, testAssets.get(1),
      testAssets.get(3).id, testAssets.get(3),
      testAssets.get(6).id, testAssets.get(6)
    );
    final Collection<AssetSearchResult> actualAssets = searchResult.assets.items;
    TestHelper.assertEquals(
      expectedAssetsMap,
      actualAssets,
      AssetSearchResult::getId,
      SearchIntegrationTest::verifyAssetSearchResult
    );
  }

  @Test
  public void wildcardSearchCombined() {

    final ArrayList<Server> testServers = addServerTestset();
    final ArrayList<Group> testGroups = addGroupTestset();
    final ArrayList<Application> testApplications = addApplicationTestset();
    final ArrayList<Asset> testAssets = addAssetTestset();

    final SearchResult searchResult = testEndpoint
      .path("search").queryParam("q", "web*")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(SearchResult.class);

    final Map<String, Server> expectedServersMap = ImmutableMap.of(
      testServers.get(5).fqdn,
      testServers.get(5)
    );
    final Collection<ServerSearchResult> actualServers = searchResult.servers.items;
    TestHelper.assertEquals(
      expectedServersMap,
      actualServers,
      ServerSearchResult::getFqdn,
      SearchIntegrationTest::verifyServerSearchResult
    );

    final Map<String, Group> expectedGroupsMap = ImmutableMap.of(
      testGroups.get(0).id, testGroups.get(0)
    );
    final Collection<GroupSearchResult> actualGroups = searchResult.groups.items;
    TestHelper.assertEquals(
      expectedGroupsMap,
      actualGroups,
      GroupSearchResult::getId,
      SearchIntegrationTest::verifyGroupSearchResult
    );

    final Map<String, Application> expectedApplicationsMap = ImmutableMap.of(
      testApplications.get(0).id, testApplications.get(0),
      testApplications.get(5).id, testApplications.get(5)
    );
    final Collection<ApplicationSearchResult> actualApplications = searchResult.applications.items;
    TestHelper.assertEquals(
      expectedApplicationsMap,
      actualApplications,
      ApplicationSearchResult::getId,
      SearchIntegrationTest::verifyApplicationSearchResult
    );

    final Map<String, Asset> expectedAssetsMap = ImmutableMap.of(
      testAssets.get(1).id, testAssets.get(1),
      testAssets.get(5).id, testAssets.get(5),
      testAssets.get(6).id, testAssets.get(6)
    );
    final Collection<AssetSearchResult> actualAssets = searchResult.assets.items;
    TestHelper.assertEquals(
      expectedAssetsMap,
      actualAssets,
      AssetSearchResult::getId,
      SearchIntegrationTest::verifyAssetSearchResult
    );
  }

  private ArrayList<Server> addServerTestset() {

    final Server server1 = new Server() {{
      hostname = "vlTma1";
      environment = "teST1";
      fqdn = "vLtma1.test1.hh.atg.SE";
      description = "Hit on servername";
    }};
    servers.insertOne(TestHelper.addMetaForCreate(server1, objectMapper));

    final Server server2 = new Server() {{
      hostname = "vLPma2";
      environment = "tesT1";
      fqdn = "vLPpma2.test1.hh.atg.SE";
      description = "No hit";
    }};
    servers.insertOne(TestHelper.addMetaForCreate(server2, objectMapper));

    final Server server3 = new Server() {{
      hostname = "vltmA1";
      environment = "tEst2";
      fqdn = "vltmA1.test2.hh.atg.Se";
      description = "Hit on servername";
    }};
    servers.insertOne(TestHelper.addMetaForCreate(server3, objectMapper));

    final Server server4 = new Server() {{
      hostname = "vlTMa4";
      environment = "test1";
      fqdn = "vlTMa4.test1.hh.atg.Se";
      description = "Hit on description/vltma1";
    }};
    servers.insertOne(TestHelper.addMetaForCreate(server4, objectMapper));

    final Server server5 = new Server() {{
      hostname = "VLpma5";
      environment = "VLTMA1";
      fqdn = "VLpma5.test1.hh.atg.sE";
      description = "Denna server kör alla webbapparna.";
    }};
    servers.insertOne(TestHelper.addMetaForCreate(server5, objectMapper));

    final Server server6 = new Server() {{
      hostname = "webapp1";
      environment = "test10";
      fqdn = "test.atg.se";
    }};
    servers.insertOne(TestHelper.addMetaForCreate(server6, objectMapper));

    return Lists.newArrayList(server1, server2, server3, server4, server5, server6);
  }

  private ArrayList<Group> addGroupTestset() {

    final Group group1 = new Group() {{
      id = "groupid1";
      name = "groupname1";
      description = "Hit on group id";
      tags = Lists.newArrayList(new Tag("tag1"), new Tag("tag2"), new Tag("webbappar"), new Tag("tag4"));
    }};
    groups.insertOne(TestHelper.addMetaForCreate(group1, objectMapper));

    final Group group2 = new Group() {{
      id = "groupid2";
      name = "groupname2";
      description = "No hit";
    }};
    groups.insertOne(TestHelper.addMetaForCreate(group2, objectMapper));

    final Group group3 = new Group() {{
      id = "groupid3";
      name = "groupid1";
      description = "Denna grupp innehåller webbappar och en massa annat.";
    }};
    groups.insertOne(TestHelper.addMetaForCreate(group3, objectMapper));

    final Group group4 = new Group() {{
      id = "groupid4";
      name = "groupname4";
      description = "Hit on description-groupid1.";
    }};
    groups.insertOne(TestHelper.addMetaForCreate(group4, objectMapper));

    final Group group5 = new Group() {{
      id = "groupid5";
      name = "groupname5";
      description = "Hit on tag";
      tags = Lists.newArrayList(new Tag("tag1"), new Tag("tag2"), new Tag("groupid1"), new Tag("tag4"));
    }};
    groups.insertOne(TestHelper.addMetaForCreate(group5, objectMapper));

    return Lists.newArrayList(group1, group2, group3, group4, group5);
  }

  private ArrayList<Application> addApplicationTestset() {

    final Application application1 = new Application() {{
      id = "applicationid1";
      name = "webbapp";
      description = "Hit on id";
      group = new GroupLink("my-group");
    }};
    applications.insertOne(TestHelper.addMetaForCreate(application1, objectMapper));

    final Application application2 = new Application() {{
      id = "applicationid2";
      name = "appname2";
      description = "No hit (no search on group).";
      group = new GroupLink("applicationid1");
    }};
    applications.insertOne(TestHelper.addMetaForCreate(application2, objectMapper));

    final Application application3 = new Application() {{
      id = "applicationid3";
      name = "applicationid1";
      description = "Hit on name";
    }};
    applications.insertOne(TestHelper.addMetaForCreate(application3, objectMapper));

    final Application application4 = new Application() {{
      id = "applicationid4";
      name = "appname4";
      description = "Hit on description:applicationid1:er";
    }};
    applications.insertOne(TestHelper.addMetaForCreate(application4, objectMapper));

    final Application application5 = new Application() {{
      id = "appname5";
      name = "Strange id";
    }};
    applications.insertOne(TestHelper.addMetaForCreate(application5, objectMapper));

    final Application application6 = new Application() {{
      id = "webbappar-id1";
      name = "Some Name";
    }};
    applications.insertOne(TestHelper.addMetaForCreate(application6, objectMapper));

    return Lists.newArrayList(application1, application2, application3, application4, application5, application6);
  }

  private ArrayList<Asset> addAssetTestset() {

    final Asset asset1 = new Asset() {{
      id = "assetid1";
      name = "assetname1";
      description = "Hit on id";
      group = new GroupLink("my-group");
    }};
    assets.insertOne(TestHelper.addMetaForCreate(asset1, objectMapper));

    final Asset asset2 = new Asset() {{
      id = "assetid2";
      name = "webbapp";
      description = "No hit (no search on group).";
      group = new GroupLink("assetid1");
    }};
    assets.insertOne(TestHelper.addMetaForCreate(asset2, objectMapper));

    final Asset asset3 = new Asset() {{
      id = "assetid3";
      name = "assetid1";
      description = "Hit on name";
    }};
    assets.insertOne(TestHelper.addMetaForCreate(asset3, objectMapper));

    final Asset asset4 = new Asset() {{
      id = "asset-id4";
      name = "assetname4";
      description = "Hit on description\\assetid1!";
    }};
    assets.insertOne(TestHelper.addMetaForCreate(asset4, objectMapper));

    final Asset asset5 = new Asset() {{
      id = "assetid5";
      name = "assetname1";
    }};
    assets.insertOne(TestHelper.addMetaForCreate(asset5, objectMapper));

    final Asset asset6 = new Asset() {{
      id = "webbap";
      name = "assetname6";
    }};
    assets.insertOne(TestHelper.addMetaForCreate(asset6, objectMapper));

    final Asset asset7 = new Asset() {{
      id = "webbapp";
      name = "assetname7";
    }};
    assets.insertOne(TestHelper.addMetaForCreate(asset7, objectMapper));

    return Lists.newArrayList(asset1, asset2, asset3, asset4, asset5, asset6, asset7);
  }

  private static void verifyServerSearchResult(Server expected, ServerSearchResult actual) {
    Assert.assertEquals(expected.hostname, actual.hostname);
    Assert.assertEquals(expected.environment, actual.environment);
    Assert.assertEquals(expected.fqdn, actual.fqdn);
    Assert.assertEquals(expected.description, actual.description);
  }

  private static void verifyGroupSearchResult(Group expected, GroupSearchResult actual) {
    Assert.assertEquals(expected.id, actual.id);
    Assert.assertEquals(expected.name, actual.name);
    Assert.assertEquals(expected.tags, actual.tags);
    Assert.assertEquals(expected.description, actual.description);
  }

  private static void verifyApplicationSearchResult(Application expected, ApplicationSearchResult actual) {
    Assert.assertEquals(expected.id, actual.id);
    Assert.assertEquals(expected.name, actual.name);
    Assert.assertEquals(expected.description, actual.description);
  }

  private static void verifyAssetSearchResult(Asset expected, AssetSearchResult actual) {
    Assert.assertEquals(expected.id, actual.id);
    Assert.assertEquals(expected.name, actual.name);
    Assert.assertEquals(expected.description, actual.description);
  }
}

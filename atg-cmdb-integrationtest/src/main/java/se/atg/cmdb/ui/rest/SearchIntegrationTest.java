package se.atg.cmdb.ui.rest;

import java.util.Collection;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

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
import se.atg.cmdb.helpers.JsonHelper;
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
  public void searchServer() {

    final Server server1 = new Server() {{
      hostname = "vltma1";
      environment = "test1";
      fqdn = "vltma1.test1.hh.atg.se";
      description = "Hit on servername";
    }};
    servers.insertOne(JsonHelper.addMetaForCreate(server1, "intergration-test", objectMapper));

    final Server server2 = new Server() {{
      hostname = "vltma2";
      environment = "test1";
      fqdn = "vltma2.test1.hh.atg.se";
      description = "No hit";
    }};
    servers.insertOne(JsonHelper.addMetaForCreate(server2, "intergration-test", objectMapper));

    final Server server3 = new Server() {{
      hostname = "vltma1";
      environment = "test2";
      fqdn = "vltma1.test2.hh.atg.se";
      description = "Hit on servername";
    }};
    servers.insertOne(JsonHelper.addMetaForCreate(server3, "intergration-test", objectMapper));

    final Server server4 = new Server() {{
      hostname = "vltma4";
      environment = "test1";
      fqdn = "vltma4.test1.hh.atg.se";
      description = "Hit on description/vltma1";
    }};
    servers.insertOne(JsonHelper.addMetaForCreate(server4, "intergration-test", objectMapper));

    final Server server5 = new Server() {{
      hostname = "vltma5";
      environment = "vltma1";
      fqdn = "vltma5.test1.hh.atg.se";
      description = "Hit on environment";
    }};
    servers.insertOne(JsonHelper.addMetaForCreate(server5, "intergration-test", objectMapper));

    final SearchResult searchResult = testEndpoint
      .path("search").queryParam("q", "vltma1")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(SearchResult.class);

    final Collection<ServerSearchResult> actualServers = searchResult.servers.items;
    Assert.assertEquals(4, actualServers.size());
    Assert.assertNull(searchResult.groups.items);
    Assert.assertNull(searchResult.applications.items);
    Assert.assertNull(searchResult.assets.items);

    final Map<String, Server> expectedMap = ImmutableMap.of(
        server1.fqdn, server1,
        server3.fqdn, server3,
        server4.fqdn, server4,
        server5.fqdn, server5
    );
    TestHelper.assertEquals(expectedMap, actualServers, ServerSearchResult::getFqdn, SearchIntegrationTest::verifyServerSearchResult);
  }

  @Test
  public void searchGroup() {

    final Group group1 = new Group() {{
      id = "groupid1";
      name = "groupname1";
      description = "Hit on group id";
    }};
    groups.insertOne(JsonHelper.addMetaForCreate(group1, "intergration-test", objectMapper));

    final Group group2 = new Group() {{
      id = "groupid2";
      name = "groupname2";
      description = "No hit";
    }};
    groups.insertOne(JsonHelper.addMetaForCreate(group2, "intergration-test", objectMapper));

    final Group group3 = new Group() {{
      id = "groupid3";
      name = "groupid1";
      description = "Hit on name";
    }};
    groups.insertOne(JsonHelper.addMetaForCreate(group3, "intergration-test", objectMapper));

    final Group group4 = new Group() {{
      id = "groupid4";
      name = "groupname4";
      description = "Hit on description-groupid1.";
    }};
    groups.insertOne(JsonHelper.addMetaForCreate(group4, "intergration-test", objectMapper));

    final Group group5 = new Group() {{
      id = "groupid5";
      name = "groupname5";
      description = "Hit on tag";
      tags = Lists.newArrayList(new Tag("tag1"), new Tag("tag2"), new Tag("groupid1"), new Tag("tag4"));
    }};
    groups.insertOne(JsonHelper.addMetaForCreate(group5, "intergration-test", objectMapper));

    final SearchResult searchResult = testEndpoint
      .path("search").queryParam("q", "groupid1")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(SearchResult.class);

    final Collection<GroupSearchResult> actualGroups = searchResult.groups.items;
    Assert.assertEquals(4, actualGroups.size());
    Assert.assertNull(searchResult.servers.items);
    Assert.assertNull(searchResult.applications.items);
    Assert.assertNull(searchResult.assets.items);

    final Map<String, Group> expectedMap = ImmutableMap.of(
        group1.id, group1,
        group3.id, group3,
        group4.id, group4,
        group5.id, group5
    );
    TestHelper.assertEquals(expectedMap, actualGroups, GroupSearchResult::getId, SearchIntegrationTest::verifyGroupSearchResult);
  }

  @Test
  public void searchApplications() {

    final Application application1 = new Application() {{
      id = "applicationid1";
      name = "applicationname1";
      description = "Hit on id";
      group = new GroupLink("my-group");
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application1, "integration-test",  objectMapper));

    final Application application2 = new Application() {{
      id = "applicationid2";
      name = "applicationname2";
      description = "No hit (no search on group).";
      group = new GroupLink("applicationid1");
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application2, "integration-test",  objectMapper));

    final Application application3 = new Application() {{
      id = "applicationid3";
      name = "applicationid1";
      description = "Hit on name";
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application3, "integration-test",  objectMapper));

    final Application application4 = new Application() {{
      id = "applicationid4";
      name = "applicationname4";
      description = "Hit on description:applicationid1:er";
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application4, "integration-test",  objectMapper));

    final SearchResult searchResult = testEndpoint
      .path("search").queryParam("q", "applicationid1")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(SearchResult.class);

    final Collection<ApplicationSearchResult> actualApplications = searchResult.applications.items;
    Assert.assertEquals(3, actualApplications.size());
    Assert.assertNull(searchResult.servers.items);
    Assert.assertNull(searchResult.groups.items);
    Assert.assertNull(searchResult.assets.items);

    final Map<String, Application> expectedMap = ImmutableMap.of(
        application1.id, application1,
        application3.id, application3,
        application4.id, application4
    );
    TestHelper.assertEquals(expectedMap, actualApplications, ApplicationSearchResult::getId, SearchIntegrationTest::verifyApplicationSearchResult);
  }

  @Test
  public void searchAssets() {

    final Asset asset1 = new Asset() {{
      id = "assetid1";
      name = "assetname1";
      description = "Hit on id";
      group = new GroupLink("my-group");
    }};
    assets.insertOne(JsonHelper.addMetaForCreate(asset1, "integration-test",  objectMapper));

    final Asset asset2 = new Asset() {{
      id = "assetid2";
      name = "assetname2";
      description = "No hit (no search on group).";
      group = new GroupLink("assetid1");
    }};
    assets.insertOne(JsonHelper.addMetaForCreate(asset2, "integration-test",  objectMapper));

    final Asset asset3 = new Asset() {{
      id = "assetid3";
      name = "assetid1";
      description = "Hit on name";
    }};
    assets.insertOne(JsonHelper.addMetaForCreate(asset3, "integration-test",  objectMapper));

    final Asset asset4 = new Asset() {{
      id = "assetid4";
      name = "assetname4";
      description = "Hit on description\\assetid1!";
    }};
    assets.insertOne(JsonHelper.addMetaForCreate(asset4, "integration-test",  objectMapper));

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
        asset1.id, asset1,
        asset3.id, asset3,
        asset4.id, asset4
    );
    TestHelper.assertEquals(expectedMap, actualAssets, AssetSearchResult::getId, SearchIntegrationTest::verifyAssetSearchResult);
  }

  @Test
  public void searchCombined() {

    /*
     * Servers
     */
    final Server server1 = new Server() {{
      hostname = "vltma1";
      environment = "test1";
      fqdn = "vltma1.test1.hh.atg.se";
    }};
    servers.insertOne(JsonHelper.addMetaForCreate(server1, "intergration-test", objectMapper));

    final Server server2 = new Server() {{
      hostname = "vltma2";
      environment = "test1";
      fqdn = "vltma2.test1.hh.atg.se";
    }};
    servers.insertOne(JsonHelper.addMetaForCreate(server2, "intergration-test", objectMapper));

    final Server server3 = new Server() {{
      hostname = "vltma1";
      environment = "test2";
      fqdn = "vltma1.test2.hh.atg.se";
      description = "Denna server kör alla webbapparna";
    }};
    servers.insertOne(JsonHelper.addMetaForCreate(server3, "intergration-test", objectMapper));

    /*
     * Groups
     */
    final Group group1 = new Group() {{
      id = "tillsammans1";
      name = "groupname1";
      description = "Hit on group id";
      tags = Lists.newArrayList(new Tag("tag1"), new Tag("webbappar"), new Tag("tag3"), new Tag("tag4"));
    }};
    groups.insertOne(JsonHelper.addMetaForCreate(group1, "intergration-test", objectMapper));

    final Group group2 = new Group() {{
      id = "groupid2";
      name = "groupname2";
      description = "Denna grupp innehåller webbappar och en massa annat.";
    }};
    groups.insertOne(JsonHelper.addMetaForCreate(group2, "intergration-test", objectMapper));

    final Group group3 = new Group() {{
      id = "groupid3";
      name = "groupid1";
      description = "Hit on name";
    }};
    groups.insertOne(JsonHelper.addMetaForCreate(group3, "intergration-test", objectMapper));

    /*
     * Applications
     */
    final Application application1 = new Application() {{
      id = "tillsammans1";
      name = "webbapp";
      description = "Hit on id";
      group = new GroupLink("my-group");
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application1, "integration-test",  objectMapper));

    final Application application2 = new Application() {{
      id = "applicationid2";
      name = "applicationname2";
      group = new GroupLink("applicationid1");
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application2, "integration-test",  objectMapper));

    final Application application3 = new Application() {{
      id = "webbappar-id1";
      name = "applicationid1";
      description = "Hit on name";
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application3, "integration-test",  objectMapper));

    /*
     * Assets
     */
    final Asset asset1 = new Asset() {{
      id = "tillsammans1";
      name = "webbapp";
    }};
    assets.insertOne(JsonHelper.addMetaForCreate(asset1, "integration-test",  objectMapper));

    final Asset asset2 = new Asset() {{
      id = "asset-id2";
      name = "assetname2";
      group = new GroupLink("webbapper");
    }};
    assets.insertOne(JsonHelper.addMetaForCreate(asset2, "integration-test",  objectMapper));

    final Asset asset3 = new Asset() {{
      id = "webbapp";
      name = "asset:id1";
      description = "This description doesn't render any hit.";
    }};
    assets.insertOne(JsonHelper.addMetaForCreate(asset3, "integration-test",  objectMapper));

    final Asset asset4 = new Asset() {{
      id = "webbap";
      name = "assetid1";
      group = new GroupLink("webbapp");
      description = "This description doesn't render any hit.";
    }};
    assets.insertOne(JsonHelper.addMetaForCreate(asset4, "integration-test",  objectMapper));

    final SearchResult searchResult = testEndpoint
      .path("search").queryParam("q", "webbapp och asset")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(SearchResult.class);

    final Collection<ServerSearchResult> actualServers = searchResult.servers.items;
    Assert.assertEquals(1, actualServers.size());

    final Map<String, Server> expectedServersMap = ImmutableMap.of(server3.fqdn, server3);
    TestHelper.assertEquals(expectedServersMap, actualServers, ServerSearchResult::getFqdn, SearchIntegrationTest::verifyServerSearchResult);

    final Collection<GroupSearchResult> actualGroups = searchResult.groups.items;
    Assert.assertEquals(2, actualGroups.size());

    final Map<String, Group> expectedGroupsMap = ImmutableMap.of(group1.id, group1, group2.id, group2);
    TestHelper.assertEquals(expectedGroupsMap, actualGroups, GroupSearchResult::getId, SearchIntegrationTest::verifyGroupSearchResult);

    final Collection<ApplicationSearchResult> actualApplications = searchResult.applications.items;
    Assert.assertEquals(2, actualApplications.size());

    final Map<String, Application> expectedApplicationsMap = ImmutableMap.of(application1.id, application1, application3.id, application3);
    TestHelper.assertEquals(expectedApplicationsMap, actualApplications, ApplicationSearchResult::getId, SearchIntegrationTest::verifyApplicationSearchResult);

    final Collection<AssetSearchResult> actualAssets = searchResult.assets.items;
    Assert.assertEquals(3, actualAssets.size());

    final Map<String, Asset> expectedAssetsMap = ImmutableMap.of(
        asset1.id, asset1,
        asset2.id, asset2,
        asset3.id, asset3
    );
    TestHelper.assertEquals(expectedAssetsMap, actualAssets, AssetSearchResult::getId, SearchIntegrationTest::verifyAssetSearchResult);
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

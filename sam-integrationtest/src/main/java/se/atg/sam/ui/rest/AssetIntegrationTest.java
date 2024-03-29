package se.atg.sam.ui.rest;

import java.util.Arrays;
import java.util.Optional;

import javax.ws.rs.NotFoundException;
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
import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import com.google.common.collect.ImmutableMap;

import se.atg.sam.dao.Collections;
import se.atg.sam.model.Asset;
import se.atg.sam.model.AssetLink;
import se.atg.sam.model.Group;
import se.atg.sam.model.GroupLink;
import se.atg.sam.model.PaginatedCollection;
import se.atg.sam.model.Server;
import se.atg.sam.ui.rest.integrationtest.helpers.RestHelper;
import se.atg.sam.ui.rest.integrationtest.helpers.TestHelper;

public class AssetIntegrationTest {

  @Inject
  private MongoDatabase database;
  @Inject
  private WebTarget testEndpoint;
  @Inject
  private Client client;
  @Inject
  private ObjectMapper objectMapper;

  private MongoCollection<Document> assets;
  private MongoCollection<Document> groups;

  @Before
  public void setUp() {

    groups = database.getCollection(Collections.GROUPS);
    groups.deleteMany(new Document());

    assets = database.getCollection(Collections.ASSETS);
    assets.deleteMany(new Document());
  }

  @Test
  public void getAsset() {

    final Group group1 = new Group() {{
      id = "group-id1";
      name = "First group";
    }};
    groups.insertOne(TestHelper.addMetaForCreate(group1, objectMapper));

    final Asset asset1 = new Asset() {{
      id = "my-asset1";
      name = "Min pryl1";
      description = "Very useful asset";
      group = new GroupLink("group-id1");
    }};
    assets.insertOne(TestHelper.addMetaForCreate(asset1, objectMapper));

    final Asset response = getAsset(asset1.id);
    verifyAsset(asset1, response);
  }

  @Test
  public void getAssets() {

    final Group group1 = new Group() {{
      id = "group-id1";
      name = "First group";
    }};
    groups.insertOne(TestHelper.addMetaForCreate(group1, objectMapper));

    final Asset asset1 = new Asset() {{
      id = "my-asset1";
      name = "Min pryl1";
      description = "Very useful asset";
      group = new GroupLink("group-id1");
    }};
    assets.insertOne(TestHelper.addMetaForCreate(asset1, objectMapper));

    final Asset asset2 = new Asset() {{
      id = "my-asset2";
      name = "Min pryl2";
      description = "Also a very useful asset";
      group = new GroupLink("group-id1");
    }};
    assets.insertOne(TestHelper.addMetaForCreate(asset2, objectMapper));

    final PaginatedCollection<Asset> response = testEndpoint.path("asset")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(new GenericType<PaginatedCollection<Asset>>(){});

    Assert.assertNull(response.next);
    Assert.assertNull(response.previous);
    Assert.assertNull(response.start);
    Assert.assertNull(response.limit);

    Assert.assertEquals(2, response.items.size());
    TestHelper.assertEquals(Arrays.asList(asset1, asset2), response.items, Asset::getId, AssetIntegrationTest::verifyAsset);
  }

  @Test(expected = NotFoundException.class)
  public void shouldReturnNotFoundWhenServerDoesNotExist() {
     testEndpoint
      .path("asset").path("asset-id")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(Server.class);
  }

  @Test
  public void addNewAsset() {

    final Group group1 = new Group() {{
      id = "group-id1";
      name = "First group";
    }};
    groups.insertOne(TestHelper.addMetaForCreate(group1, objectMapper));

    final Asset asset1 = new Asset() {{
      id = "my-asset1";
      name = "My Asset #1";
      description = "Very important asset";
      group = new GroupLink("group-id1");
      os = new Os() {{
        name = "RedHat";
        type = "Linux";
        version = "6.7";
      }};
      network = new Network() {{
        ipv4Address = "1.2.3.4";
      }};
    }};
    final AssetResponse createResponse = createAsset(asset1);
    Assert.assertNotNull(createResponse.db);

    final Asset response = getAsset(createResponse.link);
    verifyAsset(asset1, response);
  }

  @Test
  public void newAssetMustHaveId() {

    final Asset asset1 = new Asset() {{
      name = "My Asset #1";
    }};
    final Response response = testEndpoint.path("asset").path("asset-id")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .put(Entity.json(asset1));
    TestHelper.assertValidationError("id may not be null", response);
  }

  @Test
  public void newAssetMustHaveName() {

    final Asset asset1 = new Asset() {{
      id = "my-asset1";
    }};
    final Response response = testEndpoint.path("asset").path(asset1.id)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .put(Entity.json(asset1));
    TestHelper.assertValidationError("name may not be null", response);
  }

  @Test
  public void newAssetOsMustHaveName() {

    final Asset asset1 = new Asset() {{
      id = "my-asset1";
      name = "My Asset 1";
      os = new Os() {{
        type = "Linux";
      }};
    }};
    final Response response = testEndpoint.path("asset").path(asset1.id)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .put(Entity.json(asset1));
    TestHelper.assertValidationError("os.name may not be null", response);
  }

  @Test
  public void patchAsset() {

    final Group group1 = new Group() {{
      id = "group-id1";
      name = "First group";
    }};
    groups.insertOne(TestHelper.addMetaForCreate(group1, objectMapper));

    /*
     * Create Asset
     */
    final Asset asset1 = new Asset() {{
      id = "my-asset1";
      name = "My Asset 1";
      description = "My super asset";
    }};
    final AssetResponse createResponse = createAsset(asset1);
    Assert.assertNotNull(createResponse.db);

    /*
     * Patch Asset
     */
    final Asset assetPatch = new Asset() {{
      name = "My patched asset name";
      description = "New description";
      group = new GroupLink("group-id1");
    }};
    final AssetLink patchedAssetLink = patchAsset(assetPatch, createResponse.link);

    /*
     * Get and verify
     */
    final Asset patchedAsset = getAsset(patchedAssetLink.link);
    Assert.assertEquals(asset1.id, patchedAsset.id);
    Assert.assertEquals(assetPatch.name, patchedAsset.name);
    Assert.assertEquals(assetPatch.description, patchedAsset.description);
    Assert.assertEquals(group1.id, patchedAsset.group.id);
    Assert.assertEquals(group1.name, patchedAsset.group.name);
  }

  @Test
  public void shallowPatchAsset() {

    final Group group1 = new Group() {{
      id = "group-id1";
      name = "First group";
    }};
    groups.insertOne(TestHelper.addMetaForCreate(group1, objectMapper));

    /*
     * Create Asset
     */
    final Asset asset1 = new Asset() {{
      id = "my-asset1";
      name = "My Asset 1";
      description = "My super asset";
      os = new Os() {{
        name = "RedHat";
        type = "Linux";
        version = "6.7";
        attributes = ImmutableMap.of(
          "test1", "old"
        );
      }};
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
    final AssetResponse createResponse = createAsset(asset1);
    Assert.assertNotNull(createResponse.db);

    /*
     * Patch Asset
     */
    final Asset assetPatch = new Asset() {{
      name = "My patched asset name";
      group = new GroupLink("group-id1");
      os = new Os() {{
        version = "6.9";
        attributes = ImmutableMap.of(
          "test2", "new"
        );
      }};
      attributes = ImmutableMap.of(
        "test1", ImmutableMap.of(
          "test11", "new"
        )
      );
    }};
    final AssetLink patchedAssetLink = patchAsset(assetPatch, createResponse.link, Optional.of(1));

    /*
     * Get and verify
     */
    final Asset patchedAsset = getAsset(patchedAssetLink.link);
    Assert.assertEquals(asset1.id, patchedAsset.id);
    Assert.assertEquals(assetPatch.name, patchedAsset.name);
    Assert.assertEquals(asset1.description, patchedAsset.description);
    Assert.assertEquals(group1.id, patchedAsset.group.id);
    Assert.assertEquals(group1.name, patchedAsset.group.name);
    Assert.assertEquals("new", TestHelper.mapPath(patchedAsset.os.attributes, "test2"));
    Assert.assertNull(TestHelper.mapPath(patchedAsset.os.attributes, "test1"));
    Assert.assertEquals("new", TestHelper.mapPath(patchedAsset.attributes, "test1", "test11"));
    Assert.assertNull(TestHelper.mapPath(patchedAsset.attributes, "test1", "test12"));
    Assert.assertEquals("old", TestHelper.mapPath(patchedAsset.attributes, "test2", "test21"));
  }

  @Test
  public void deleteAsset() {

    final Asset asset1 = new Asset() {{
      id = "my-asset1";
      name = "My asset #1";
    }};
    final AssetResponse createResponse = createAsset(asset1);

    final Response deleteResponse = client.target(createResponse.link)
      .request(MediaType.APPLICATION_JSON)
      .delete();
    TestHelper.assertSuccessful(deleteResponse);

    final Response response = client.target(createResponse.link)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    Assert.assertEquals(404, response.getStatus());
  }

  private AssetResponse createAsset(Asset asset) {

    final Response response = testEndpoint.path("asset").path(asset.id)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .put(Entity.json(asset));
    TestHelper.assertSuccessful(response);

    final Document db = assets.find(Filters.eq("id", asset.id)).first();
    final AssetLink link = response.readEntity(AssetLink.class);
    return new AssetResponse(link, db);
  }

  private Asset getAsset(String id) {

    final Response response = testEndpoint
      .path("asset").path(id)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    TestHelper.assertSuccessful(response);
    return response.readEntity(Asset.class);
  }

  private Asset getAsset(Link link) {

    final Response response = client.target(link)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    TestHelper.assertSuccessful(response);
    return response.readEntity(Asset.class);
  }

  private AssetLink patchAsset(Asset assetPatch, Link link) {
    return patchAsset(assetPatch, link, Optional.empty());
  }

  private AssetLink patchAsset(Asset assetPatch, Link assetLink, Optional<Integer> mergeDepth) {

    final Response response = RestHelper.queryParam(client.target(assetLink), mergeDepth, "mergedepth")
      .request(MediaType.APPLICATION_JSON)
      .build("PATCH", Entity.json(assetPatch))
      .invoke();
    TestHelper.assertSuccessful(response);

    final AssetLink link = response.readEntity(AssetLink.class);
    return link;
  }

  private static void verifyAsset(Asset expected, Asset actual) {

    Assert.assertEquals(expected.id, actual.id);
    Assert.assertEquals(expected.name, actual.name);
    Assert.assertEquals(expected.description, actual.description);
    Assert.assertEquals(expected.attributes, actual.attributes);
    Assert.assertEquals(expected.network, actual.network);
    Assert.assertEquals(expected.os, actual.os);
    if (expected.group != null) {
      Assert.assertEquals(expected.group.id, actual.group.id);
      Assert.assertNotNull(actual.group.name);
      Assert.assertNotNull(actual.group.link);
    }
  }

  static class AssetResponse {

    public Link link;
    public Document db;

    AssetResponse(AssetLink link, Document db) {
      Assert.assertEquals(link.id, db.getString("id"));
      Assert.assertEquals(link.name, db.getString("name"));
      this.db = db;
      this.link = link.link;
    }
  }
}

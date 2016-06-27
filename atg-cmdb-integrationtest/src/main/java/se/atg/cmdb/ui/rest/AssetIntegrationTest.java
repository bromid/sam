package se.atg.cmdb.ui.rest;

import java.util.Arrays;

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

import se.atg.cmdb.dao.Collections;
import se.atg.cmdb.helpers.JsonHelper;
import se.atg.cmdb.model.Asset;
import se.atg.cmdb.model.AssetLink;
import se.atg.cmdb.model.PaginatedCollection;
import se.atg.cmdb.model.Server;
import se.atg.cmdb.ui.rest.integrationtest.helpers.TestHelper;

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

  @Before
  public void setUp() {
    assets = database.getCollection(Collections.ASSETS);
    assets.deleteMany(new Document());
  }

  @Test
  public void getAsset() {

    final Asset asset1 = new Asset() {{
      id = "my-asset1";
      name = "Min pryl1";
      description = "Very useful asset";
    }};
    assets.insertOne(JsonHelper.addMetaForCreate(asset1, "integration-test", objectMapper));

    final Asset response = getAsset(asset1.id);
    TestHelper.isEqualExceptMeta(asset1, response);
  }

  @Test
  public void getAssets() {

    final Asset asset1 = new Asset() {{
      id = "my-asset1";
      name = "Min pryl1";
      description = "Very useful asset";
    }};
    assets.insertOne(JsonHelper.addMetaForCreate(asset1, "integration-test", objectMapper));

    final Asset asset2 = new Asset() {{
      id = "my-asset2";
      name = "Min pryl2";
      description = "Also a very useful asset";
    }};
    assets.insertOne(JsonHelper.addMetaForCreate(asset2, "integration-test", objectMapper));

    final PaginatedCollection<Asset> response = testEndpoint.path("asset")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(new GenericType<PaginatedCollection<Asset>>(){});

    Assert.assertNull(response.next);
    Assert.assertNull(response.previous);
    Assert.assertNull(response.start);
    Assert.assertNull(response.limit);

    Assert.assertEquals(2, response.items.size());
    TestHelper.assertEquals(Arrays.asList(asset1, asset2), response.items, Asset::getId, TestHelper::isEqualExceptMeta);
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

    final Asset asset1 = new Asset() {{
      id = "my-asset1";
      name = "My Asset #1";
      description = "Very important asset";
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
    TestHelper.isEqualExceptMeta(asset1, response);
  }

  @Test
  public void newAssetMustHaveId() {

    final Asset asset1 = new Asset() {{
      name = "My Asset #1";
    }};
    final Response response = testEndpoint.path("asset")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .put(Entity.json(asset1));
    TestHelper.assertValidationError("id may not be null", response);
  }

  @Test
  public void newAssetMustHaveName() {

    final Asset asset1 = new Asset() {{
      id = "my-asset1";
    }};
    final Response response = testEndpoint.path("asset")
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
    final Response response = testEndpoint.path("asset")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .put(Entity.json(asset1));
    TestHelper.assertValidationError("os.name may not be null", response);
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

    final Response response = testEndpoint.path("asset")
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

package se.atg.cmdb.ui.rest;

import java.util.Arrays;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
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
import com.mongodb.client.model.Filters;

import se.atg.cmdb.dao.Collections;
import se.atg.cmdb.helpers.JSONHelper;
import se.atg.cmdb.model.Asset;
import se.atg.cmdb.model.AssetLink;
import se.atg.cmdb.model.PaginatedCollection;
import se.atg.cmdb.model.Server;
import se.atg.cmdb.model.ServerLink;
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
		final String json = JSONHelper.objectToJson(asset1, objectMapper);
		final Document bson = JSONHelper.addMetaForCreate(json, "integration-test");
		assets.insertOne(bson);

		final Asset response = getAsset(asset1.id);
		Assert.assertNotNull(response.meta);

		response.meta = null;
		asset1.meta = null;
		Assert.assertEquals(asset1, response);
	}

	@Test
	public void getAssets() {

		final Asset asset1 = new Asset() {{
			id = "my-asset1";
			name = "Min pryl1";
			description = "Very useful asset";
		}};
		assets.insertOne(JSONHelper.entityToBson(asset1, objectMapper));		

		final Asset asset2 = new Asset() {{
			id = "my-asset2";
			name = "Min pryl2";
			description = "Also a very useful asset";
		}};
		assets.insertOne(JSONHelper.entityToBson(asset2, objectMapper));	

		final PaginatedCollection<Asset> response = testEndpoint.path("asset")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(new GenericType<PaginatedCollection<Asset>>(){});

		Assert.assertNull(response.next);
		Assert.assertNull(response.previous);
		Assert.assertNull(response.start);
		Assert.assertNull(response.limit);

		Assert.assertEquals(2, response.items.size());
		TestHelper.assertEquals(Arrays.asList(asset1, asset2), response.items, Asset::getId);
	}

	@Test(expected=NotFoundException.class)
	public void shouldReturnNotFoundWhenServerDoesNotExist() {
		 testEndpoint
			.path("asset").path("asset-id")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(Server.class);
	}

	private Asset getAsset(String id) {

		final Response response = testEndpoint
			.path("asset").path(id)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get();
		TestHelper.assertSuccessful(response);
		return response.readEntity(Asset.class);
	}

	private Asset getAsset(AssetLink assetLink) {

		final Response response = client.target(assetLink.link)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get();
		TestHelper.assertSuccessful(response);
		return response.readEntity(Asset.class);
	}
}
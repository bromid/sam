package se.atg.cmdb.ui.rest;

import java.util.Collection;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.bson.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import se.atg.cmdb.dao.Collections;
import se.atg.cmdb.helpers.JSONHelper;
import se.atg.cmdb.model.SearchResult;
import se.atg.cmdb.model.Server;
import se.atg.cmdb.model.search.ServerSearchResult;
import se.atg.cmdb.ui.rest.integrationtest.helpers.TestHelper;

public class SearchIntegrationTest {

	@Inject 
	private MongoDatabase database;
	@Inject
	private WebTarget testEndpoint;
	@Inject
	private Client client;
	@Inject
	private ObjectMapper objectMapper;

	private MongoCollection<Document> servers;

	@Before
	public void setUp() {
		servers = database.getCollection(Collections.SERVERS);
		servers.deleteMany(new Document());
	}

	@Test
	public void searchServer() {

		final Server server1 = new Server() {{
			hostname = "vltma1";
			environment = "test1";
			fqdn = "vltma1.test1.hh.atg.se";
		}};
		servers.insertOne(JSONHelper.entityToBson(server1, objectMapper));

		final Server server2 = new Server() {{
			hostname = "vltma2";
			environment = "test1";
			fqdn = "vltma2.test1.hh.atg.se";
		}};
		servers.insertOne(JSONHelper.entityToBson(server2, objectMapper));

		final Server server3 = new Server() {{
			hostname = "vltma1";
			environment = "test2";
			fqdn = "vltma1.test2.hh.atg.se";
			description = "Min testserver";
		}};
		servers.insertOne(JSONHelper.entityToBson(server3, objectMapper));

		final SearchResult searchResult = testEndpoint
			.path("search").queryParam("q", "vltma1")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(SearchResult.class);

		final Collection<ServerSearchResult> servers = searchResult.servers.items;
		Assert.assertEquals(2, servers.size());

		final Map<String, Server> expectedMap = ImmutableMap.of(server1.fqdn, server1, server3.fqdn, server3);
		TestHelper.assertEquals(expectedMap, servers, ServerSearchResult::getFqdn, (actual, expected) -> Assert.assertEquals(expected.getHostname(), actual.getHostname()));
	}
}
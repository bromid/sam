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
import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import se.atg.cmdb.dao.Collections;
import se.atg.cmdb.helpers.JsonHelper;
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
    servers.insertOne(JsonHelper.entityToBson(server1, objectMapper));

    final Server server2 = new Server() {{
      hostname = "vltma2";
      environment = "test1";
      fqdn = "vltma2.test1.hh.atg.se";
    }};
    servers.insertOne(JsonHelper.entityToBson(server2, objectMapper));

    final Server server3 = new Server() {{
      hostname = "vltma1";
      environment = "test2";
      fqdn = "vltma1.test2.hh.atg.se";
      description = "Min testserver";
    }};
    servers.insertOne(JsonHelper.entityToBson(server3, objectMapper));

    final SearchResult searchResult = testEndpoint
      .path("search").queryParam("q", "vltma1")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(SearchResult.class);

    final Collection<ServerSearchResult> actualServers = searchResult.servers.items;
    Assert.assertEquals(2, actualServers.size());

    final Map<String, Server> expectedMap = ImmutableMap.of(server1.fqdn, server1, server3.fqdn, server3);
    TestHelper.assertEquals(expectedMap, actualServers, ServerSearchResult::getFqdn, (expected, actual) -> {
      Assert.assertEquals(expected.hostname, actual.hostname);
      Assert.assertEquals(expected.environment, actual.environment);
      Assert.assertEquals(expected.fqdn, actual.fqdn);
      Assert.assertEquals(expected.description, actual.description);
    });
  }
}

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
import se.atg.cmdb.model.PaginatedCollection;
import se.atg.cmdb.model.Server;
import se.atg.cmdb.model.ServerLink;
import se.atg.cmdb.ui.rest.integrationtest.helpers.TestHelper;

public class ServerIntegrationTest {

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
	public void getServer() {

		final Server server = new Server() {{
			hostname = "vltma2";
			environment = "test5";
			fqdn = "vltma2.test1.hh.atg.se";
			description = "Min testserver";
		}};
		final String json = JSONHelper.objectToJson(server, objectMapper);
		final Document bson = JSONHelper.addMetaForCreate(json, "integration-test");
		servers.insertOne(bson);

		final Server response = getServer(server.environment, server.hostname);
		Assert.assertNotNull(response.meta);

		response.meta = null;
		server.meta = null;
		Assert.assertEquals(server, response);
	}

	@Test
	public void getServers() {

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

		final PaginatedCollection<Server> response = testEndpoint.path("server")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(new GenericType<PaginatedCollection<Server>>(){});

		Assert.assertNull(response.next);
		Assert.assertNull(response.previous);
		Assert.assertNull(response.start);
		Assert.assertNull(response.limit);

		Assert.assertEquals(2, response.items.size());
		TestHelper.assertEquals(Arrays.asList(server1, server2), response.items, Server::getFqdn);
	}

	@Test(expected=NotFoundException.class)
	public void shouldReturnNotFoundWhenServerDoesNotExist() {
		 testEndpoint
			.path("server").path("test1").path("vltma1")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(Server.class);
	}

	@Test
	public void addNewServer(){

		final Server server = new Server() {{
			environment = "ci";
			hostname = "somehost";
			fqdn = "somehost.ci.hh.atg.se";
			os = new OS() {{
				name = "RedHat";
				type = "Linux";
				version = "6.7";
			}};
		}};
		final ServerLink link = createServer(server);
		final Server response = getServer(link);

		response.meta = null;
		Assert.assertEquals(response, server);
	}

	@Test
	public void patchServer() {

		/*
		 * Create server
		 */
		final Server server = new Server() {{
			environment = "ci";
			hostname = "somehost";
			fqdn = "somehost.ci.hh.atg.se";
			os = new OS() {{
				name = "RedHat";
				type = "Linux";
				version = "6.7";
			}};
		}};
		final ServerLink link = createServer(server);

		/*
		 * Patch server 
		 */
		final Server serverPatch = new Server() {{
			environment = "test1";
			os = new OS() {{
				version = "6.9";
			}};
		}};
		final Response response = client.target(link.link)
			.request(MediaType.APPLICATION_JSON)
			.build("PATCH", Entity.json(serverPatch))
			.invoke();
		TestHelper.assertSuccessful(response);
		final ServerLink patchedServerLink = response.readEntity(ServerLink.class);

		/*
		 * Get and verify
		 */
		final Server patchedServer = getServer(patchedServerLink);
		Assert.assertEquals(server.hostname, patchedServer.hostname);
		Assert.assertEquals(server.fqdn, patchedServer.fqdn);
		Assert.assertEquals(server.os.name, patchedServer.os.name);
		Assert.assertEquals(server.os.type, patchedServer.os.type);
		Assert.assertEquals(serverPatch.environment, patchedServer.environment);
		Assert.assertEquals(serverPatch.os.version, patchedServer.os.version);
	}

	private ServerLink createServer(final Server server) {

		final Response response = testEndpoint.path("server")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.put(Entity.json(server));
		TestHelper.assertSuccessful(response);
		Assert.assertNotNull(servers.find(Filters.eq("fqdn", server.fqdn)).first());
		return response.readEntity(ServerLink.class);
	}

	private Server getServer(String environment, String hostName) {

		final Response response = testEndpoint
			.path("server").path(environment).path(hostName)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get();
		TestHelper.assertSuccessful(response);
		return response.readEntity(Server.class);
	}

	private Server getServer(ServerLink serverLink) {

		final Response response = client.target(serverLink.link)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get();
		TestHelper.assertSuccessful(response);
		return response.readEntity(Server.class);
	}
}
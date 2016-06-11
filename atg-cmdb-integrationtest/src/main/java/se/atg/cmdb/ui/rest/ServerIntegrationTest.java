package se.atg.cmdb.ui.rest;

import java.util.Arrays;
import java.util.List;

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
import se.atg.cmdb.helpers.JSONHelper;
import se.atg.cmdb.helpers.Mapper;
import se.atg.cmdb.model.Application;
import se.atg.cmdb.model.Deployment;
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
	private MongoCollection<Document> applications;

	@Before
	public void setUp() {
		servers = database.getCollection(Collections.SERVERS);
		servers.deleteMany(new Document());

		applications = database.getCollection(Collections.APPLICATIONS);
		applications.deleteMany(new Document());
	}

	@Test
	public void getServer() {

		final Server server1 = new Server() {{
			hostname = "vltma2";
			environment = "test5";
			fqdn = "vltma2.test1.hh.atg.se";
			description = "Min testserver";
		}};
		servers.insertOne(JSONHelper.addMetaForCreate(server1, "integration-test", objectMapper));

		final Server response = getServer(server1.environment, server1.hostname);
		TestHelper.isEqualExceptMeta(server1, response);
	}

	@Test
	public void getServers() {

		final Server server1 = new Server() {{
			hostname = "vltma1";
			environment = "test1";
			fqdn = "vltma1.test1.hh.atg.se";
		}};
		servers.insertOne(JSONHelper.addMetaForCreate(server1, "integration-test", objectMapper));		

		final Server server2 = new Server() {{
			hostname = "vltma2";
			environment = "test1";
			fqdn = "vltma2.test1.hh.atg.se";
		}};
		servers.insertOne(JSONHelper.addMetaForCreate(server2, "integration-test", objectMapper));	

		final PaginatedCollection<Server> response = testEndpoint.path("server")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(new GenericType<PaginatedCollection<Server>>(){});

		Assert.assertNull(response.next);
		Assert.assertNull(response.previous);
		Assert.assertNull(response.start);
		Assert.assertNull(response.limit);

		Assert.assertEquals(2, response.items.size());
		TestHelper.assertEquals(Arrays.asList(server1, server2), response.items, Server::getFqdn, TestHelper::isEqualExceptMeta);
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

		final Server server1 = new Server() {{
			environment = "ci";
			hostname = "somehost";
			fqdn = "somehost.ci.hh.atg.se";
			os = new OS() {{
				name = "RedHat";
				type = "Linux";
				version = "6.7";
			}};
		}};
		final ServerResponse createResponse = createServer(server1);
		final Server response = getServer(createResponse.link);
		TestHelper.isEqualExceptMeta(server1, response);
	}

	@Test
	public void newServerCantHaveId(){

		final Server server = new Server() {{
			id = "not-allowed";
			environment = "ci";
			hostname = "somehost";
			fqdn = "somehost.ci.hh.atg.se";
		}};
		final Response response = testEndpoint.path("server")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.put(Entity.json(server));
		TestHelper.assertValidationError("id must be null", response);
	}

	@Test
	public void newServerMustHaveHostname(){

		final Server server = new Server() {{
			environment = "ci";
			fqdn = "somehost.ci.hh.atg.se";
		}};
		final Response response = testEndpoint.path("server")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.put(Entity.json(server));
		TestHelper.assertValidationError("hostname may not be null", response);
	}

	@Test
	public void newServerMustHaveEnvironment(){

		final Server server = new Server() {{
			hostname = "somehost";
			fqdn = "somehost.ci.hh.atg.se";
		}};
		final Response response = testEndpoint.path("server")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.put(Entity.json(server));
		TestHelper.assertValidationError("environment may not be null", response);
	}

	@Test
	public void patchedServerCantHaveId(){

		final Server server = new Server() {{
			environment = "ci";
			hostname = "somehost";
			fqdn = "somehost.ci.hh.atg.se";
		}};
		final ServerResponse createServer = createServer(server);

		final Server serverPatch = new Server() {{
			id = "not-allowed";
			environment = "ci";
			hostname = "somehost";
			fqdn = "somehost.ci.hh.atg.se";
		}};
		final Response response = client.target(createServer.link)
				.request(MediaType.APPLICATION_JSON)
				.build("PATCH", Entity.json(serverPatch))
				.invoke();
		TestHelper.assertValidationError("id must be null", response);
	}

	@Test
	public void addNewServerWithApplications(){

		/*
		 * Create server
		 */
		final Server server = new Server() {{
			hostname = "vltma1";
			environment = "qa";
			fqdn = "vltma1.qa.hh.atg.se";
			os = new OS() {{
				name = "RedHat";
				type = "Linux";
				version = "6.7";
			}};
			network = new Network() {{
				ipv4Address = "10.0.0.1";
			}};
			deployments = Arrays.asList(
				new Deployment("my-application1"),
				new Deployment("my-application2") {{
					version = "1.2.3";
					releaseNotes = "https://service.test1.hh.atg.se/info/atgse/service/release-notes";
				}}
			);
		}};
		final ServerResponse createServerResponse = createServer(server);

		/*
		 * Verify server applications in db
		 */
		verifyServerDeployments(server.deployments, Mapper.mapList(createServerResponse.db, "deployments", Deployment::fromDeploymentBson));

		/*
		 * Add applications to db
		 */
		final Application application1 = new Application() {{
			id = "my-application1";
			name = "My Application 1";
		}};
		applications.insertOne(JSONHelper.addMetaForCreate(application1, "integration.test", objectMapper));

		final Application application2 = new Application() {{
			id = "my-application2";
			name = "My Application 2";
		}};
		applications.insertOne(JSONHelper.addMetaForCreate(application2, "integration.test", objectMapper));

		/*
		 * Get and verify server
		 */
		final Server response = getServer(createServerResponse.link);
		verifyServerDeployments(server.deployments, response.deployments);

		server.deployments = null;
		response.deployments = null;
		response.meta = null;
		Assert.assertEquals(server, response);
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
		final ServerResponse createServerResponse = createServer(server);

		/*
		 * Patch server 
		 */
		final Server serverPatch = new Server() {{
			environment = "test1";
			os = new OS() {{
				version = "6.9";
			}};
		}};
		final ServerResponse patchedServerResponse = patchServer(serverPatch, createServerResponse.link);

		/*
		 * Get and verify
		 */
		final Server patchedServer = getServer(patchedServerResponse.link);
		Assert.assertEquals(server.hostname, patchedServer.hostname);
		Assert.assertEquals(server.fqdn, patchedServer.fqdn);
		Assert.assertEquals(server.os.name, patchedServer.os.name);
		Assert.assertEquals(server.os.type, patchedServer.os.type);
		Assert.assertEquals(serverPatch.environment, patchedServer.environment);
		Assert.assertEquals(serverPatch.os.version, patchedServer.os.version);
	}

	private ServerResponse createServer(final Server server) {

		final Response response = testEndpoint.path("server")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.put(Entity.json(server));
		TestHelper.assertSuccessful(response);

		final Document db = servers.find(Filters.eq("fqdn", server.fqdn)).first();
		final ServerLink link = response.readEntity(ServerLink.class);
		return new ServerResponse(link, db);
	}

	private ServerResponse patchServer(Server serverPatch, Link serverLink) {

		final Response response = client.target(serverLink)
			.request(MediaType.APPLICATION_JSON)
			.build("PATCH", Entity.json(serverPatch))
			.invoke();
		TestHelper.assertSuccessful(response);

		final ServerLink link = response.readEntity(ServerLink.class);
		final Document db = servers.find(
			Filters.and(
				Filters.eq("hostname", link.hostname),
				Filters.eq("environment", link.environment)
			)).first();
		return new ServerResponse(link, db);
	}

	private Server getServer(String environment, String hostName) {

		final Response response = testEndpoint
			.path("server").path(environment).path(hostName)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get();
		TestHelper.assertSuccessful(response);
		return response.readEntity(Server.class);
	}

	private Server getServer(Link link) {

		final Response response = client.target(link)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get();
		TestHelper.assertSuccessful(response);
		return response.readEntity(Server.class);
	}

	private static void verifyServerDeployments(List<Deployment> expectedDeployments, List<Deployment> actualDeployments) {
		TestHelper.assertEquals(expectedDeployments, actualDeployments, Deployment::getApplicationId, (actual, expected) -> {
			Assert.assertEquals(expected.applicationLink.id, actual.applicationLink.id);
			Assert.assertEquals(expected.version, actual.version);
			Assert.assertEquals(expected.releaseNotes, actual.releaseNotes);
		});
	}

	static class ServerResponse {
		public Link link;
		public Document db;
		public ServerResponse(ServerLink link, Document db) {
			Assert.assertEquals(link.environment, db.getString("environment"));
			Assert.assertEquals(link.hostname, db.getString("hostname"));
			this.db = db;
			this.link = link.link;
		}
	}
}
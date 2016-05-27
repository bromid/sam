package se.atg.cmdb.ui.rest;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.bson.Document;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import se.atg.cmdb.model.Meta;
import se.atg.cmdb.model.Server;

public class ServerIntegrationTest {

	private static final String CMDB_URL = "http://localhost:8090";
	private static MongoClient mongoClient;
	final WebTarget client = createClient();
	final static MongoCollection<Document> servers;
	final static MongoDatabase database;

	static {
		mongoClient = new MongoClient("192.168.50.16", 27017);
		database = mongoClient.getDatabase("test");
		servers = database.getCollection("servers");	
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		servers.drop();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		mongoClient.dropDatabase("test");
	}

	@After
	public void tearDown(){
		servers.drop();
	}

	@Test(expected=NotFoundException.class)
	public void shouldReturnNotFoundWhenServerDoesNotExist() {
		client
			.path("server").path("vltma1").path("test1")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(Server.class);
	}

	@Test
	public void addNewServer(){

		final Server server = new Server() {{
			environment = "ci";
			hostname = "somehost.hh.atg.se";
			os = new OS() {{
				name = "RedHat";
				type = "Linux";
				version = "6.7";
			}};
		}};
		createServer(server);

		final Server response = getServer(server.environment, server.hostname);

		final Meta responseMeta = response.meta;
		response.meta = null;
		Assert.assertEquals(response, server);
	}

	private void createServer(final Server server) {
		client
			.path("server")
			.request()
			.put(Entity.entity(server, MediaType.APPLICATION_JSON));
	}

	private Server getServer(String environment, String hostName) {
		final Server response = client
				.path("server").path(environment).path(hostName)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.get(Server.class);
		return response;
	}

	@Test
	public void patchServer() {

		final Server server = new Server() {{
			environment = "ci";
			hostname = "somehost.hh.atg.se";
			os = new OS() {{
				name = "RedHat";
				type = "Linux";
				version = "6.7";
			}};
		}};
		createServer(server);

		final Server serverPatch = new Server() {{
			environment = "ci";
			hostname = "somehost.hh.atg.se";
			os = new OS() {{
				version = "6.9";
			}};
		}};

		final Response patchResponse = client
			.path("server")
			.path("ci")
			.path("somehost.hh.atg.se")
			.request()
			.build("PATCH", Entity.entity(serverPatch, MediaType.APPLICATION_JSON))
			.invoke();
		Assert.assertEquals(Status.OK.getStatusCode(), patchResponse.getStatusInfo().getStatusCode());

		Server response = getServer("ci", "somehost.hh.atg.se");
		final Meta responseMeta = response.meta;
		response.meta = null;

		Assert.assertEquals("6.9", response.os.version);
	}

	private static WebTarget createClient() {

		final ClientConfig config = new ClientConfig();
		config.register(ObjectMapperProvider.class);

		final Client client = ClientBuilder.newClient(config);
		client.register(HttpAuthenticationFeature.basic("integration-test", "secret"));
		return client
			.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
			.target(CMDB_URL)
			.path("services");
	}
}
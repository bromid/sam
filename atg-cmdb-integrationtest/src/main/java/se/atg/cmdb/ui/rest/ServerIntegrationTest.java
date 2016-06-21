package se.atg.cmdb.ui.rest;

import java.util.Arrays;
import java.util.Collection;
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
import se.atg.cmdb.helpers.JsonHelper;
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
    servers.insertOne(JsonHelper.addMetaForCreate(server1, "integration-test", objectMapper));

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
    servers.insertOne(JsonHelper.addMetaForCreate(server1, "integration-test", objectMapper));

    final Server server2 = new Server() {{
      hostname = "vltma2";
      environment = "test1";
      fqdn = "vltma2.test1.hh.atg.se";
    }};
    servers.insertOne(JsonHelper.addMetaForCreate(server2, "integration-test", objectMapper));

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

  @Test(expected = NotFoundException.class)
  public void shouldReturnNotFoundWhenServerDoesNotExist() {
     testEndpoint
      .path("server").path("test1").path("vltma1")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(Server.class);
  }

  @Test
  public void addNewServer() {

    final Server server1 = new Server() {{
      environment = "ci";
      hostname = "somehost";
      fqdn = "somehost.ci.hh.atg.se";
      os = new Os() {{
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
  public void newServerCantHaveId() {

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
  public void newServerMustHaveHostname() {

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
  public void newServerMustHaveEnvironment() {

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
  public void patchedServerCantHaveId() {

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
  public void addNewServerWithApplications() {

    /*
     * Create server
     */
    final Server server = new Server() {{
      hostname = "vltma1";
      environment = "qa";
      fqdn = "vltma1.qa.hh.atg.se";
      os = new Os() {{
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
    final List<Deployment> dbDeployments = Mapper.mapList(createServerResponse.db, "deployments", Deployment::fromDeploymentBson);
    verifyServerDeployments(server.deployments, dbDeployments);;

    /*
     * Add applications to db
     */
    final Application application1 = new Application() {{
      id = "my-application1";
      name = "My Application 1";
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application1, "integration.test", objectMapper));

    final Application application2 = new Application() {{
      id = "my-application2";
      name = "My Application 2";
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application2, "integration.test", objectMapper));

    /*
     * Get and verify server
     */
    final Server response = getServer(createServerResponse.link);
    verifyServerDeploymentsRest(server.deployments, response.deployments);

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
      os = new Os() {{
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
      os = new Os() {{
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

  @Test
  public void getServerDeployments() {

    /*
     * Create server
     */
    final Deployment deployment1 = new Deployment("application1") {{
      version = "1.0.0";
      releaseNotes = "http://localhost/release-notes1";
      description = "Description 1";
    }};
    final Deployment deployment2 = new Deployment("application2") {{
      version = "1.2.1";
      releaseNotes = "http://localhost/release-notes2";
      description = "Description 2";
    }};
    final Deployment deployment3 = new Deployment("application3") {{
      version = "0.0.1";
      releaseNotes = "http://localhost/release-notes3";
      description = "Description 3";
    }};
    final Server server1 = new Server() {{
      hostname = "vltma2";
      environment = "test5";
      fqdn = "vltma2.test1.hh.atg.se";
      description = "Min testserver";
      deployments = Arrays.asList(deployment1, deployment2, deployment3);
    }};
    servers.insertOne(JsonHelper.addMetaForCreate(server1, "integration-test", objectMapper));

    /*
     * Create applications
     */
    final Application application1 = new Application() {{
      id = "application1";
      name = "Application 1";
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application1, "integration-test", objectMapper));

    final Application application3 = new Application() {{
      id = "application3";
      name = "Application 3";
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application3, "integration-test", objectMapper));

    final PaginatedCollection<Deployment> response = getServerDeployments(server1.environment, server1.hostname);
    Assert.assertEquals(3, response.items.size());

    verifyServerDeploymentsRest(server1.deployments, response.items);
  }

  @Test
  public void getServerDeployment() {

    /*
     * Create server
     */
    final Deployment deployment1 = new Deployment("application1") {{
      version = "1.0.0";
      releaseNotes = "http://localhost/release-notes1";
      description = "Description 1";
    }};
    final Deployment deployment2 = new Deployment("application2") {{
      version = "1.2.1";
      releaseNotes = "http://localhost/release-notes2";
      description = "Description 2";
    }};
    final Deployment deployment3 = new Deployment("application3") {{
      version = "0.0.1";
      releaseNotes = "http://localhost/release-notes3";
      description = "Description 3";
    }};
    final Server server1 = new Server() {{
      hostname = "vltma2";
      environment = "test5";
      fqdn = "vltma2.test1.hh.atg.se";
      description = "Min testserver";
      deployments = Arrays.asList(deployment1, deployment2, deployment3);
    }};
    servers.insertOne(JsonHelper.addMetaForCreate(server1, "integration-test", objectMapper));

    /*
     * Create applications
     */
    final Application application1 = new Application() {{
      id = "application1";
      name = "Application 1";
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application1, "integration-test", objectMapper));

    final Application application3 = new Application() {{
      id = "application3";
      name = "Application 3";
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application3, "integration-test", objectMapper));

    final Deployment response = getServerDeployment(server1.environment, server1.hostname, application3.id);
    verifyServerDeploymentRest(deployment3, response);
  }

  @Test
  public void addServerDeployment() {

    /*
     * Create server and applicatio
     */
    final Server server1 = new Server() {{
      hostname = "vltma2";
      environment = "test5";
      fqdn = "vltma2.test5.hh.atg.se";
      description = "Min testserver";
    }};
    final ServerResponse server = createServer(server1);

    final Application application1 = new Application() {{
      id = "my-app1";
      name = "My application 1";
      description = "Important app";
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application1, "integration-test", objectMapper));

    final Application application2 = new Application() {{
      id = "my-app2";
      name = "My application 2";
      description = "Another important app";
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application2, "integration-test", objectMapper));

    final PaginatedCollection<Deployment> serverDeployments = getServerDeployments(server.link);
    Assert.assertNull(serverDeployments.items);

    /*
     * Add deployment of application
     */
    final Deployment deployment1 = new Deployment("my-app1") {{
      version = "1.2.3";
      releaseNotes = "http://www.atg.se/release-notes1";
    }};
    final ServerLink addDeploymentResponse1 = addServerDeployment(server.link, deployment1);
    final Server serverAfterAdd1 = getServer(addDeploymentResponse1.link);
    Assert.assertEquals(1, serverAfterAdd1.deployments.size());
    verifyServerDeploymentRest(deployment1, serverAfterAdd1.deployments.get(0));

    /*
     * Add one more deployment
     */
    final Deployment deployment2 = new Deployment("my-app2") {{
      version = "1.0.0";
      releaseNotes = "http://www.atg.se/release-notes2";
    }};
    final ServerLink addDeploymentResponse2 = addServerDeployment(server.link, deployment2);
    final Server serverAfterAdd2 = getServer(addDeploymentResponse2.link);
    verifyServerDeploymentsRest(Arrays.asList(deployment1, deployment2), serverAfterAdd2.deployments);

    /*
     * Update deployment
     */
    deployment2.version = "1.1.0";
    final ServerLink updateDeploymentResponse1 = addServerDeployment(server.link, deployment2);
    final Server serverAfterUpdate1 = getServer(updateDeploymentResponse1.link);
    verifyServerDeploymentsRest(Arrays.asList(deployment1, deployment2), serverAfterUpdate1.deployments);
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

  private Deployment getServerDeployment(String environment, String hostName, String applicationId) {

    final Response response = testEndpoint
      .path("server").path(environment).path(hostName).path("deployment").path(applicationId)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    TestHelper.assertSuccessful(response);
    return response.readEntity(Deployment.class);
  }

  private PaginatedCollection<Deployment> getServerDeployments(String environment, String hostName) {

    final Response response = testEndpoint
      .path("server").path(environment).path(hostName).path("deployment")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    TestHelper.assertSuccessful(response);
    return response.readEntity(new GenericType<PaginatedCollection<Deployment>>(){});
  }

  private PaginatedCollection<Deployment> getServerDeployments(Link link) {

    final Response response = client.target(link)
      .path("deployment")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    TestHelper.assertSuccessful(response);
    return response.readEntity(new GenericType<PaginatedCollection<Deployment>>(){});
  }

  private ServerLink addServerDeployment(Link link, Deployment deployment) {

    final Response response = client.target(link)
        .path("deployment")
        .request(MediaType.APPLICATION_JSON_TYPE)
        .put(Entity.json(deployment));
      TestHelper.assertSuccessful(response);
      return response.readEntity(ServerLink.class);
  }

  private static void verifyServerDeployments(Collection<Deployment> expectedDeployments, Collection<Deployment> actualDeployments) {
    TestHelper.assertEquals(expectedDeployments, actualDeployments, Deployment::getApplicationId, ServerIntegrationTest::verifyServerDeployment);
  }

  private static void verifyServerDeploymentsRest(Collection<Deployment> expectedDeployments, Collection<Deployment> actualDeployments) {
    TestHelper.assertEquals(expectedDeployments, actualDeployments, Deployment::getApplicationId, ServerIntegrationTest::verifyServerDeploymentRest);
  }

  private static void verifyServerDeployment(Deployment expected, Deployment actual) {
    Assert.assertEquals(expected.applicationLink.id, actual.applicationLink.id);
    Assert.assertEquals(expected.version, actual.version);
    Assert.assertEquals(expected.releaseNotes, actual.releaseNotes);
    Assert.assertEquals(expected.description, actual.description);
  }

  private static void verifyServerDeploymentRest(Deployment expected, Deployment actual) {
    verifyServerDeployment(expected, actual);
    Assert.assertNotNull(actual.applicationLink.link);
  }

  static class ServerResponse {

    public Link link;
    public Document db;

    ServerResponse(ServerLink link, Document db) {
      Assert.assertEquals(link.environment, db.getString("environment"));
      Assert.assertEquals(link.hostname, db.getString("hostname"));
      this.db = db;
      this.link = link.link;
    }
  }
}

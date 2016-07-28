package se.atg.cmdb.ui.rest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

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
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import se.atg.cmdb.dao.Collections;
import se.atg.cmdb.helpers.JsonHelper;
import se.atg.cmdb.model.Application;
import se.atg.cmdb.model.ApplicationLink;
import se.atg.cmdb.model.Deployment;
import se.atg.cmdb.model.Group;
import se.atg.cmdb.model.GroupLink;
import se.atg.cmdb.model.PaginatedCollection;
import se.atg.cmdb.model.Server;
import se.atg.cmdb.model.ServerDeployment;
import se.atg.cmdb.ui.rest.integrationtest.helpers.TestHelper;

public class ApplicationIntegrationTest {

  @Inject
  private MongoDatabase database;
  @Inject
  private WebTarget testEndpoint;
  @Inject
  private Client client;
  @Inject
  private ObjectMapper objectMapper;

  private MongoCollection<Document> applications;
  private MongoCollection<Document> groups;
  private MongoCollection<Document> servers;

  @Before
  public void setUp() {

    groups = database.getCollection(Collections.GROUPS);
    groups.deleteMany(new Document());

    applications = database.getCollection(Collections.APPLICATIONS);
    applications.deleteMany(new Document());

    servers = database.getCollection(Collections.SERVERS);
    servers.deleteMany(new Document());
  }

  @Test
  public void getApplication() {

    final Group group1 = new Group() {{
      id = "my-group";
      name = "My group";
    }};
    groups.insertOne(JsonHelper.addMetaForCreate(group1, "intergration-test", objectMapper));

    final Application application1 = new Application() {{
      id = "my-application1";
      name = "My Application 1";
      description = "Min testserver";
      group = new GroupLink("my-group");
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application1, "integration-test",  objectMapper));

    final Application response = getApplication(application1.id);
    verifyApplication(application1, response);
  }

  @Test
  public void getApplications() {

    final Group group1 = new Group() {{
      id = "my-group";
      name = "My group";
    }};
    groups.insertOne(JsonHelper.addMetaForCreate(group1, "intergration-test", objectMapper));

    final Application application1 = new Application() {{
      id = "my-application1";
      name = "My Application 1";
      description = "Min testserver";
      group = new GroupLink("my-group");
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application1, "integration-test",  objectMapper));

    final Application application2 = new Application() {{
      id = "my-application2";
      name = "My Application 2";
      description = "Min testserver";
      group = new GroupLink("my-group");
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application2, "integration-test",  objectMapper));

    final PaginatedCollection<Application> response = testEndpoint.path("application")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(new GenericType<PaginatedCollection<Application>>(){});

    Assert.assertNull(response.next);
    Assert.assertNull(response.previous);
    Assert.assertNull(response.start);
    Assert.assertNull(response.limit);

    Assert.assertEquals(2, response.items.size());
    TestHelper.assertEquals(
      Arrays.asList(application1, application2),
      response.items,
      Application::getId,
      ApplicationIntegrationTest::verifyApplication
    );
  }

  @Test
  public void getApplicationDeployments() {

    final String applicationId1 = "application1";
    final String applicationId2 = "application2";
    final String applicationId3 = "application3";
    final Deployment deployment1 = new Deployment(applicationId1) {{
      version = "1.0.0";
      releaseNotes = "http://localhost/release-notes1";
      description = "Description 1";
    }};
    final Deployment deployment2 = new Deployment(applicationId2) {{
      version = "1.2.1";
      releaseNotes = "http://localhost/release-notes2";
      description = "Description 2";
    }};
    final Deployment deployment3 = new Deployment(applicationId3) {{
      version = "0.0.1";
      releaseNotes = "http://localhost/release-notes3";
      description = "Description 3";
    }};
    final Deployment deployment4 = new Deployment(applicationId1) {{
      version = "1.0";
      releaseNotes = "http://localhost/release-notes4";
      description = "Description 4";
    }};
    final Deployment deployment5 = new Deployment(applicationId2) {{
      version = "1.0";
      releaseNotes = "http://localhost/release-notes5";
      description = "Description 5";
    }};

    final Server server1 = new Server() {{
      hostname = "vltma1";
      environment = "test1";
      fqdn = "vltma1.test1.hh.atg.se";
      description = "Min testserver #1";
      deployments = Arrays.asList(deployment1, deployment2, deployment3);
    }};
    servers.insertOne(JsonHelper.addMetaForCreate(server1, "integration-test", objectMapper));

    final Server server2 = new Server() {{
      hostname = "vltma2";
      environment = "test1";
      fqdn = "vltma2.test1.hh.atg.se";
      description = "Min testserver #2";
      deployments = Arrays.asList(deployment3, deployment4, deployment5);
    }};
    servers.insertOne(JsonHelper.addMetaForCreate(server2, "integration-test", objectMapper));

    final PaginatedCollection<ServerDeployment> response = testEndpoint
      .path("application").path(applicationId1).path("deployment")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get(new GenericType<PaginatedCollection<ServerDeployment>>(){});

    final Collection<ServerDeployment> actualDeployments = response.items;
    Assert.assertEquals(2, actualDeployments.size());

    final Map<String, ServerDeployment> expectedMap = ImmutableMap.of(
      serverId(server1), new ServerDeployment(server1, deployment1),
      serverId(server2), new ServerDeployment(server2, deployment4)
    );
    TestHelper.assertEquals(expectedMap, actualDeployments, ApplicationIntegrationTest::serverId, Assert::assertEquals);
  }

  @Test
  public void addNewApplication() {

    final Group group1 = new Group() {{
      id = "my-group";
      name = "My group";
    }};
    groups.insertOne(JsonHelper.addMetaForCreate(group1, "intergration-test", objectMapper));

    final Application application1 = new Application() {{
      id = "my-application1";
      name = "My Application 1";
      description = "Min testserver";
      group = new GroupLink("my-group");
    }};
    final ApplicationResponse createResponse = createApplication(application1);

    final Application response = getApplication(createResponse.link);
    verifyApplication(application1, response);
  }

  @Test
  public void newApplicationMustHaveId() {

    final Application application1 = new Application() {{
      name = "My Application #1";
    }};
    final Response response = testEndpoint.path("application").path("app-id")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .put(Entity.json(application1));
    TestHelper.assertValidationError("id may not be null", response);
  }

  @Test
  public void newApplicationMustHaveName() {

    final Application application1 = new Application() {{
      id = "my-application1";
    }};
    final Response response = testEndpoint.path("application").path(application1.id)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .put(Entity.json(application1));
    TestHelper.assertValidationError("name may not be null", response);
  }

  @Test
  public void patchApplication() {

    final Group group1 = new Group() {{
      id = "my-group";
      name = "My group";
    }};
    groups.insertOne(JsonHelper.addMetaForCreate(group1, "intergration-test", objectMapper));

    /*
     * Create Application
     */
    final Application application1 = new Application() {{
      id = "my-application1";
      name = "My Application 1";
      description = "My super application";
    }};
    final ApplicationResponse createResponse = createApplication(application1);
    Assert.assertNotNull(createResponse.db);

    /*
     * Patch server
     */
    final Application applicationPatch = new Application() {{
      name = "My patched application name";
      description = "New description";
      group = new GroupLink("my-group");
    }};
    final Response response = client.target(createResponse.link)
      .request(MediaType.APPLICATION_JSON)
      .build("PATCH", Entity.json(applicationPatch))
      .invoke();
    TestHelper.assertSuccessful(response);
    final ApplicationLink patchedApplicationLink = response.readEntity(ApplicationLink.class);

    /*
     * Get and verify
     */
    final Application patchedApplication = getApplication(patchedApplicationLink.link);
    Assert.assertEquals(application1.id, patchedApplication.id);
    Assert.assertEquals(applicationPatch.name, patchedApplication.name);
    Assert.assertEquals(applicationPatch.description, patchedApplication.description);
    Assert.assertEquals(group1.id, patchedApplication.group.id);
    Assert.assertEquals(group1.name, patchedApplication.group.name);
  }

  @Test
  public void deleteApplication() {

    final Application application1 = new Application() {{
      id = "my-application1";
      name = "My Application #1";
    }};
    final ApplicationResponse createResponse = createApplication(application1);

    final Response deleteResponse = client.target(createResponse.link)
      .request(MediaType.APPLICATION_JSON)
      .delete();
    TestHelper.assertSuccessful(deleteResponse);

    final Response response = client.target(createResponse.link)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    Assert.assertEquals(404, response.getStatus());
  }

  private ApplicationResponse createApplication(final Application application) {

    final Response response = testEndpoint.path("application").path(application.id)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .put(Entity.json(application));
    TestHelper.assertSuccessful(response);

    final Document db = applications.find(Filters.eq("id", application.id)).first();
    final ApplicationLink link = response.readEntity(ApplicationLink.class);
    return new ApplicationResponse(link, db);
  }

  private Application getApplication(String id) {

    final Response response = testEndpoint
      .path("application").path(id)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    TestHelper.assertSuccessful(response);
    return response.readEntity(Application.class);
  }

  private Application getApplication(Link link) {

    final Response response = client.target(link)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    TestHelper.assertSuccessful(response);
    return response.readEntity(Application.class);
  }

  private static String serverId(Server server) {
    return server.hostname + "-" + server.environment;
  }

  private static String serverId(ServerDeployment deployment) {
    return deployment.hostname + "-" + deployment.environment;
  }

  public static void verifyApplication(Application expected, Application actual) {

    Assert.assertEquals(expected.id, actual.id);
    Assert.assertEquals(expected.name, actual.name);
    Assert.assertEquals(expected.description, actual.description);
    Assert.assertEquals(expected.attributes, actual.attributes);
    if (expected.group != null) {
      Assert.assertEquals(expected.group.id, actual.group.id);
      Assert.assertNotNull(actual.group.name);
      Assert.assertNotNull(actual.group.link);
    }
  }

  static class ApplicationResponse {

    public Link link;
    public Document db;

    ApplicationResponse(ApplicationLink link, Document db) {
      Assert.assertEquals(link.id, db.getString("id"));
      Assert.assertEquals(link.name, db.getString("name"));
      this.db = db;
      this.link = link.link;
    }
  }
}

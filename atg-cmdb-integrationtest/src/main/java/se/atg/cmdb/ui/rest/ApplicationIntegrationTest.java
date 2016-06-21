package se.atg.cmdb.ui.rest;

import java.util.Arrays;

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
import se.atg.cmdb.helpers.JsonHelper;
import se.atg.cmdb.model.Application;
import se.atg.cmdb.model.ApplicationLink;
import se.atg.cmdb.model.PaginatedCollection;
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

  @Before
  public void setUp() {
    applications = database.getCollection(Collections.APPLICATIONS);
    applications.deleteMany(new Document());
  }

  @Test
  public void getApplication() {

    final Application application1 = new Application() {{
      id = "my-application1";
      name = "My Application 1";
      description = "Min testserver";
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application1, "integration-test",  objectMapper));

    final Application response = getApplication(application1.id);
    TestHelper.isEqualExceptMeta(application1, response);
  }

  @Test
  public void getApplications() {

    final Application application1 = new Application() {{
      id = "my-application1";
      name = "My Application 1";
      description = "Min testserver";
    }};
    applications.insertOne(JsonHelper.addMetaForCreate(application1, "integration-test",  objectMapper));

    final Application application2 = new Application() {{
      id = "my-application2";
      name = "My Application 2";
      description = "Min testserver";
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
    TestHelper.assertEquals(Arrays.asList(application1, application2), response.items, Application::getId, TestHelper::isEqualExceptMeta);
  }

  @Test
  public void addNewApplication() {

    final Application application1 = new Application() {{
      id = "my-application1";
      name = "My Application 1";
      description = "Min testserver";
    }};
    final ApplicationLink link = createApplication(application1);
    final Application response = getApplication(link);
    TestHelper.isEqualExceptMeta(application1, response);
  }

  @Test
  public void patchApplication() {

    /*
     * Create Application
     */
    final Application application1 = new Application() {{
      id = "my-application1";
      name = "My Application 1";
      description = "Min testserver";
    }};
    final ApplicationLink link = createApplication(application1);

    /*
     * Patch server
     */
    final Application applicationPatch = new Application() {{
      name = "My Patched application name";
      description = "New description";
    }};
    final Response response = client.target(link.link)
      .request(MediaType.APPLICATION_JSON)
      .build("PATCH", Entity.json(applicationPatch))
      .invoke();
    TestHelper.assertSuccessful(response);
    final ApplicationLink patchedApplicationLink = response.readEntity(ApplicationLink.class);

    /*
     * Get and verify
     */
    final Application patchedApplication = getApplication(patchedApplicationLink);
    Assert.assertEquals(application1.id, patchedApplication.id);
    Assert.assertEquals(applicationPatch.name, patchedApplication.name);
    Assert.assertEquals(applicationPatch.description, patchedApplication.description);
  }

  private ApplicationLink createApplication(final Application application) {

    final Response response = testEndpoint.path("application")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .put(Entity.json(application));
    TestHelper.assertSuccessful(response);
    Assert.assertNotNull(applications.find(Filters.eq("id", application.id)).first());
    return response.readEntity(ApplicationLink.class);
  }

  private Application getApplication(String id) {

    final Response response = testEndpoint
      .path("application").path(id)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    TestHelper.assertSuccessful(response);
    return response.readEntity(Application.class);
  }

  private Application getApplication(ApplicationLink applicationLink) {

    final Response response = client.target(applicationLink.link)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    TestHelper.assertSuccessful(response);
    return response.readEntity(Application.class);
  }
}

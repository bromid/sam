package se.atg.cmdb.ui.rest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.google.inject.Inject;

import se.atg.cmdb.ui.rest.InfoResource.Info;
import se.atg.cmdb.ui.rest.integrationtest.helpers.TestHelper;

public class SmokeTest {

  @Inject
  private WebTarget testEndpoint;
  @Inject
  private Client client;

  @Test
  public void testStartPage() {

    final Response response = client.target(testEndpoint.getUriBuilder().replacePath("/"))
      .request(MediaType.TEXT_HTML_TYPE)
      .get();
    TestHelper.assertSuccessful(response);
  }

  @Test
  public void testDocumentation() {

    final Response response = client.target(testEndpoint.getUriBuilder().replacePath("/docs/"))
      .request(MediaType.TEXT_HTML_TYPE)
      .get();
    TestHelper.assertSuccessful(response);
  }

  public void testInfo() {

    final Response infoResponse = testEndpoint.path("info")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    TestHelper.assertSuccessful(infoResponse);

    final Info info = infoResponse.readEntity(InfoResource.Info.class);
    final Response releaseNotesResponse = client.target(info.releaseNotes)
      .request(MediaType.TEXT_HTML_TYPE)
      .get();
    TestHelper.assertSuccessful(releaseNotesResponse);
  }
}

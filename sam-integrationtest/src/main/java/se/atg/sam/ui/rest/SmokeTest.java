package se.atg.sam.ui.rest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import se.atg.sam.model.github.User;
import se.atg.sam.ui.rest.InfoResource;
import se.atg.sam.ui.rest.InfoResource.Info;
import se.atg.sam.ui.rest.integrationtest.helpers.TestHelper;

public class SmokeTest {

  @Inject
  private WebTarget testEndpoint;
  @Inject
  private Client client;
  @Inject @Named("basic")
  private Client basicAuthClient;
  @Inject @Named("anonymous")
  private Client anonymousAuthClient;

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

  @Test
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

  @Test
  public void requireSigninForUserInformation() {

    final Response response = anonymousAuthClient.target(testEndpoint.getUriBuilder())
      .path("oauth2").path("user")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    TestHelper.assertUnauthenticated(response);
  }

  @Test
  public void basicAuthentication() {

    final Response response = basicAuthClient.target(testEndpoint.getUriBuilder())
      .path("oauth2").path("user")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    TestHelper.assertSuccessful(response);

    final User user = response.readEntity(User.class);
    Assert.assertEquals("integration-test", user.login);
  }

  @Test
  public void tokenAuthentication() {

    final Response response = testEndpoint
      .path("oauth2").path("user")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();
    TestHelper.assertSuccessful(response);

    final User user = response.readEntity(User.class);
    Assert.assertEquals("integration-test", user.login);
  }
}

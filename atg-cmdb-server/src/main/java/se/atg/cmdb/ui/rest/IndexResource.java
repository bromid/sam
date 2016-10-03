package se.atg.cmdb.ui.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.views.View;
import se.atg.cmdb.ui.dropwizard.configuration.OAuthConfiguration;
import se.atg.cmdb.ui.dropwizard.view.IndexView;
import se.atg.cmdb.ui.dropwizard.view.IndexView.Oauth;
import se.atg.cmdb.ui.dropwizard.view.IndexView.Settings;

@Path("/")
@Produces(Defaults.META_TYPE_HTML)
public class IndexResource {

  private final View indexView;

  public IndexResource(ObjectMapper objectMapper, OAuthConfiguration oauthConfig) throws JsonProcessingException {

    final Settings settings = new Settings() {{
      oauth = new Oauth() {{
        url = oauthConfig.getAuthorizeEndpoint();
        clientId = oauthConfig.getClientId();
        origin = oauthConfig.getOrigin();
      }};
    }};

    final String settingsJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(settings);
    this.indexView = new IndexView(settingsJson);
  }

  @GET
  @Path("{path:.*}")
  public View getIndex(
      @PathParam("path") String path
  ) {
    return indexView;
  }
}

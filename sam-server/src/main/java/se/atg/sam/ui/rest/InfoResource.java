package se.atg.sam.ui.rest;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import se.atg.sam.ui.dropwizard.view.ReleaseNotesView;

@Path("/")
@Api("info")
@Produces(Defaults.MEDIA_TYPE_JSON)
public class InfoResource {

  private static final ReleaseNotesView releaseNotesView = new ReleaseNotesView();

  @GET
  @Path("/services/info/release-notes")
  @ApiOperation("Fetch release notes")
  @Produces(Defaults.META_TYPE_HTML)
  public ReleaseNotesView getReleaseNote() {
    return releaseNotesView;
  }

  @GET
  @Path("/services/info")
  @ApiOperation("Fetch meta-data like version and release notes")
  public Info getInfo(
    @Context UriInfo uriInfo
  ) {
    return new Info() {{
      version = getClass().getPackage().getImplementationVersion();
      releaseNotes = Link.fromMethod(InfoResource.class, "getReleaseNote")
        .baseUri(uriInfo.getBaseUri())
        .build()
        .getUri();
    }};
  }

  public static class Info {
    public URI releaseNotes;
    public String version;
  }
}

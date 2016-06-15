package se.atg.cmdb.ui.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import io.dropwizard.views.View;
import se.atg.cmdb.ui.dropwizard.view.IndexView;

@Path("/")
@Produces(Defaults.META_TYPE_HTML)
public class IndexResource {

  private static final View indexView = new IndexView();

  @GET
  @Path("{path:.*}")
  public View getIndex(
      @PathParam("path") String path
  ) {
    return indexView;
  }
}

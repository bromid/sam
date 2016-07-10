package se.atg.cmdb.ui.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import se.atg.cmdb.dao.Collections;
import se.atg.cmdb.helpers.RestHelper;
import se.atg.cmdb.model.PaginatedCollection;
import se.atg.cmdb.model.SearchResult;
import se.atg.cmdb.model.search.ApplicationSearchResult;
import se.atg.cmdb.model.search.AssetSearchResult;
import se.atg.cmdb.model.search.GroupSearchResult;
import se.atg.cmdb.model.search.ServerSearchResult;

@Path("/")
@Api("search")
@Produces(Defaults.MEDIA_TYPE_JSON)
public class SearchResource {

  static final Logger logger = LoggerFactory.getLogger(ServerResource.class);

  private final MongoDatabase database;

  public SearchResource(MongoDatabase database) {
    this.database = database;
  }

  @GET
  @Path("services/search")
  public SearchResult getAssets(
    @ApiParam(value = "Sökparameter", required = true) @QueryParam("q") String query
  ) {

    final PaginatedCollection<ServerSearchResult> servers = RestHelper.paginatedList(
      database.getCollection(Collections.SERVERS)
        .find(Filters.text(query))
        .projection(Projections.metaTextScore("score"))
        .sort(Sorts.metaTextScore("score"))
        .map(ServerSearchResult::new)
    );

    final PaginatedCollection<GroupSearchResult> groups = RestHelper.paginatedList(
      database.getCollection(Collections.GROUPS)
        .find(Filters.text(query))
        .projection(Projections.metaTextScore("score"))
        .sort(Sorts.metaTextScore("score"))
        .map(GroupSearchResult::new)
    );

    final PaginatedCollection<ApplicationSearchResult> applications = RestHelper.paginatedList(
        database.getCollection(Collections.APPLICATIONS)
          .find(Filters.text(query))
          .projection(Projections.metaTextScore("score"))
          .sort(Sorts.metaTextScore("score"))
          .map(ApplicationSearchResult::new)
    );

    final PaginatedCollection<AssetSearchResult> assets = RestHelper.paginatedList(
        database.getCollection(Collections.ASSETS)
          .find(Filters.text(query))
          .projection(Projections.metaTextScore("score"))
          .sort(Sorts.metaTextScore("score"))
          .map(AssetSearchResult::new)
    );

    return new SearchResult(servers, groups, applications, assets);
  }
}

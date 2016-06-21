package se.atg.cmdb.ui.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import se.atg.cmdb.dao.Collections;
import se.atg.cmdb.helpers.RestHelper;
import se.atg.cmdb.model.SearchResult;
import se.atg.cmdb.model.search.ServerSearchResult;

@Path("/")
@Api("search")
@Produces(Defaults.MEDIA_TYPE_JSON)
public class SearchResource {

  static final Logger logger = LoggerFactory.getLogger(ServerResource.class);

  private final MongoDatabase database;
  private final ObjectMapper objectMapper;

  public SearchResource(MongoDatabase database, ObjectMapper objectMapper) {
    this.database = database;
    this.objectMapper = objectMapper;
  }

  @GET
  @Path("services/search")
  public SearchResult getAssets(
    @ApiParam(value = "Sökparameter", required = true) @QueryParam("q") String query
  ) {
    final SearchResult result = new SearchResult();
    result.servers = RestHelper.paginatedList(
      database.getCollection(Collections.SERVERS)
        .find(Filters.text(query))
        .projection(Projections.metaTextScore("score"))
        .sort(Sorts.metaTextScore("score"))
        .map(ServerSearchResult::new)
    );
    return result;
  }
}

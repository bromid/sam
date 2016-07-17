package se.atg.cmdb.ui.rest;

import java.util.regex.Pattern;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.Function;
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

  private static final char wildcardChar = '*';
  private static final String wildcardString = String.valueOf(wildcardChar);
  private static final String mongoCaseInsensitivity = "i";
  private static final Pattern wordCharacterPattern = Pattern.compile("[\\w,\\-,\\.," + wildcardChar + "]+", Pattern.UNICODE_CHARACTER_CLASS);

  private final MongoDatabase database;

  public SearchResource(MongoDatabase database) {
    this.database = database;
  }

  @GET
  @Path("services/search")
  public SearchResult getAssets(
    @ApiParam(value = "Search string", required = true) @QueryParam("q") String query
  ) {

    final String trimmedQuery = RestHelper.verifyNotEmpty(query, "A search string must be provided in the query parameter 'q'");
    final int wildcardOccurences = StringUtils.countMatches(trimmedQuery, wildcardChar);
    if (wildcardOccurences > 1) {
      throw RestHelper.validationError("A wildcard search can only contain one wildcard operator.");
    }

    final SearchResult searchResult;
    if (wildcardOccurences == 0) {
      searchResult = textSearch(trimmedQuery);
    } else {
      searchResult = wildcardSearch(trimmedQuery);
    }
    return searchResult;
  }

  private SearchResult wildcardSearch(String query) {

    final String mongoDbQuery = convertWildcardQuery(query);

    final PaginatedCollection<ServerSearchResult> servers = wildcardSearch(
      Filters.or(
        Filters.regex("hostname", mongoDbQuery, mongoCaseInsensitivity),
        Filters.regex("environment", mongoDbQuery, mongoCaseInsensitivity),
        Filters.regex("fqdn", mongoDbQuery, mongoCaseInsensitivity)
      ),
      Collections.SERVERS,
      ServerSearchResult::new
    );

    final PaginatedCollection<GroupSearchResult> groups = wildcardSearch(
      Filters.or(
        Filters.regex("id", mongoDbQuery, mongoCaseInsensitivity),
        Filters.regex("name", mongoDbQuery, mongoCaseInsensitivity),
        Filters.regex("tags", mongoDbQuery, mongoCaseInsensitivity)
      ),
      Collections.GROUPS,
      GroupSearchResult::new
    );

    final PaginatedCollection<ApplicationSearchResult> applications = wildcardSearch(
      Filters.or(
        Filters.regex("id", mongoDbQuery, mongoCaseInsensitivity),
        Filters.regex("name", mongoDbQuery, mongoCaseInsensitivity)
      ),
      Collections.APPLICATIONS,
      ApplicationSearchResult::new
    );

    final PaginatedCollection<AssetSearchResult> assets = wildcardSearch(
      Filters.or(
        Filters.regex("id", mongoDbQuery, mongoCaseInsensitivity),
        Filters.regex("name", mongoDbQuery, mongoCaseInsensitivity)
      ),
      Collections.ASSETS,
      AssetSearchResult::new
    );
    return new SearchResult(servers, groups, applications, assets);
  }

  private <T> PaginatedCollection<T> wildcardSearch(Bson filter, String collection, Function<Document,T> mapper) {
    return RestHelper.paginatedList(
      database.getCollection(collection)
        .find(filter)
        .map(mapper)
    );
  }

  private SearchResult textSearch(String query) {

    final PaginatedCollection<ServerSearchResult> servers = textSearch(query, Collections.SERVERS, ServerSearchResult::new);
    final PaginatedCollection<GroupSearchResult> groups = textSearch(query, Collections.GROUPS, GroupSearchResult::new);
    final PaginatedCollection<ApplicationSearchResult> applications = textSearch(query, Collections.APPLICATIONS, ApplicationSearchResult::new);
    final PaginatedCollection<AssetSearchResult> assets = textSearch(query, Collections.ASSETS, AssetSearchResult::new);
    return new SearchResult(servers, groups, applications, assets);
  }

  private <T> PaginatedCollection<T> textSearch(String query, String collection, Function<Document,T> mapper) {
    return RestHelper.paginatedList(
      database.getCollection(collection)
        .find(Filters.text(query))
        .projection(Projections.metaTextScore("score"))
        .sort(Sorts.metaTextScore("score"))
        .map(mapper)
    );
  }

  private static String convertWildcardQuery(String query) {

    if (!wordCharacterPattern.matcher(query).matches()) {
      throw RestHelper.validationError("The only allowed special characters in a wildcard search is the wildcard operator.");
    }

    final StringBuffer sb = new StringBuffer(query);
    if (sb.charAt(0) == wildcardChar) {
      return sb.deleteCharAt(0).append('$').toString();
    }
    if (sb.charAt(sb.length() - 1) == wildcardChar) {
      return sb.insert(0, '^').deleteCharAt(sb.length() - 1).toString();
    }
    return query.replace(wildcardString, ".*?");
  }
}

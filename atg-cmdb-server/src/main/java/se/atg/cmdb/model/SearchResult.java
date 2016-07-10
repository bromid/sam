package se.atg.cmdb.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import se.atg.cmdb.model.search.ApplicationSearchResult;
import se.atg.cmdb.model.search.AssetSearchResult;
import se.atg.cmdb.model.search.GroupSearchResult;
import se.atg.cmdb.model.search.ServerSearchResult;

public class SearchResult {

  public PaginatedCollection<ServerSearchResult> servers;
  public PaginatedCollection<GroupSearchResult> groups;
  public PaginatedCollection<ApplicationSearchResult> applications;
  public PaginatedCollection<AssetSearchResult> assets;

  public SearchResult() {}

  @JsonCreator
  public SearchResult(
    @JsonProperty("servers") PaginatedCollection<ServerSearchResult> servers,
    @JsonProperty("groups") PaginatedCollection<GroupSearchResult> groups,
    @JsonProperty("applications") PaginatedCollection<ApplicationSearchResult> applications,
    @JsonProperty("assets") PaginatedCollection<AssetSearchResult> assets
  ) {
    this.groups = groups;
    this.servers = servers;
    this.assets = assets;
    this.applications = applications;
  }
}

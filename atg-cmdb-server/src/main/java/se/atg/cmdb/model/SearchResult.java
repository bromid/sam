package se.atg.cmdb.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import se.atg.cmdb.model.search.ServerSearchResult;

public class SearchResult {

	public PaginatedCollection<GroupLink> groups;
	public PaginatedCollection<ServerSearchResult> servers;
	public PaginatedCollection<AssetLink> assets;
	public PaginatedCollection<ApplicationLink> applications;

	public SearchResult() {}

	@JsonCreator
	public SearchResult(
		@JsonProperty("groups") PaginatedCollection<GroupLink> groups,
		@JsonProperty("servers") PaginatedCollection<ServerSearchResult> servers,
		@JsonProperty("assets") PaginatedCollection<AssetLink> assets,
		@JsonProperty("applications") PaginatedCollection<ApplicationLink> applications
	) {
		this.groups = groups;
		this.servers = servers;
		this.assets = assets;
		this.applications = applications;
	}
}
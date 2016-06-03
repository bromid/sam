package se.atg.cmdb.model;

import se.atg.cmdb.model.search.ServerSearchResult;

public class SearchResult {

	public PaginatedCollection<GroupLink> groups;
	public PaginatedCollection<ServerSearchResult> servers;
	public PaginatedCollection<AssetLink> assets;
	public PaginatedCollection<ApplicationLink> applications;
}
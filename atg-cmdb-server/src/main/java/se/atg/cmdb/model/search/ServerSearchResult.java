package se.atg.cmdb.model.search;

import javax.ws.rs.core.Link;

import org.bson.Document;

public class ServerSearchResult {

	public final String hostname;
	public final String environment;
	public final String fqdn;
	public final String description;
	public final Double score;
	public Link link;

	public ServerSearchResult(Document bson) {
		this.hostname = bson.getString("hostname");
		this.environment = bson.getString("hostname");
		this.fqdn = bson.getString("fqdn");
		this.description = bson.getString("description");
		this.score = bson.getDouble("score");
	}
}

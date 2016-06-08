package se.atg.cmdb.model.search;

import javax.ws.rs.core.Link;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ServerSearchResult {

	public final String hostname;
	public final String environment;
	public final String fqdn;
	public final String description;
	public final Double score;
	public Link link;

	@JsonCreator
	public ServerSearchResult(
		@JsonProperty("hostname") String hostname,
		@JsonProperty("environment") String environment,
		@JsonProperty("fqdn") String fqdn,
		@JsonProperty("description") String description,
		@JsonProperty("score") Double score,
		@JsonProperty("link") Link link
	) {
		this.hostname = hostname;
		this.environment = environment;
		this.fqdn = fqdn;
		this.description = description;
		this.score = score;
		this.link = link;
	}

	public ServerSearchResult(Document bson) {
		this.hostname = bson.getString("hostname");
		this.environment = bson.getString("hostname");
		this.fqdn = bson.getString("fqdn");
		this.description = bson.getString("description");
		this.score = bson.getDouble("score");
	}

	public String getFqdn() {
		return fqdn;
	}

	public String getHostname() {
		return hostname;
	}
}

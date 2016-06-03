package se.atg.cmdb.model;

import java.net.URI;

import javax.ws.rs.core.Link;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import se.atg.cmdb.ui.rest.ServerResource;

public class ServerLink {

	public final String hostname;
	public final String environment;
	public Link link;

	@JsonCreator
	public ServerLink(
		@JsonProperty("hostname") String hostname,
		@JsonProperty("environment") String environment,
		@JsonProperty("link") Link link
	) {
		this(hostname, environment);
		this.link = link;
	}

	public ServerLink(String hostname, String environment) {
		this.hostname = hostname;
		this.environment = environment;
	}

	public ServerLink(Document bson) {
		this.hostname = bson.getString("hostname");
		this.environment = bson.getString("environment");
	}

	public static ServerLink buildFromURI(URI baseUri, String hostname, String environment) {

		final ServerLink link = new ServerLink(hostname, environment);
		link.link = Link.fromMethod(ServerResource.class, "getServer")
			.baseUri(baseUri)
			.rel("self")
			.build(environment, hostname);
		return link;
	}
}

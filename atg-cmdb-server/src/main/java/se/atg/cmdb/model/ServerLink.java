package se.atg.cmdb.model;

import java.net.URI;

import javax.ws.rs.core.Link;

import org.bson.Document;

import se.atg.cmdb.ui.rest.ServerResource;

public class ServerLink {

	public final String hostname;
	public final String environment;
	public Link link;

	public ServerLink(Document bson) {
		this.hostname = bson.getString("hostname");
		this.environment = bson.getString("environment");
	}

	public ServerLink(URI baseUri, String hostname, String environment) {
		this.hostname = hostname;
		this.environment = environment;
		this.link = Link.fromMethod(ServerResource.class, "getServer")
			.baseUri(baseUri)
			.rel("self")
			.build(environment, hostname);
	}
}

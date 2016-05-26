package se.atg.cmdb.model;

import java.net.URI;

import javax.ws.rs.core.Link;

import se.atg.cmdb.ui.rest.ServerResource;

public class ServerLink {

	public final String hostname;
	public final String environment;
	public final Link link;

	public ServerLink(URI baseUri, String hostname, String environment) {
		this.hostname = hostname;
		this.environment = environment;
		this.link = Link.fromMethod(ServerResource.class, "getServer")
			.baseUri(baseUri)
			.rel("self")
			.build(environment, hostname);
	}
}

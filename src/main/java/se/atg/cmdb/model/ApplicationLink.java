package se.atg.cmdb.model;

import java.net.URI;

import javax.ws.rs.core.Link;

import org.bson.Document;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;

import se.atg.cmdb.ui.rest.ApplicationResource;

public class ApplicationLink {

	public final String id;

	@InjectLink(
		resource=ApplicationResource.class,
		method="getApplication",
		style=Style.ABSOLUTE,
		rel="self",
		bindings={@Binding(name="id", value="${instance.id}")}
	)
	public Link link;

	public ApplicationLink(Document bson) {
		this.id = bson.getString("id");
	}

	public ApplicationLink(String id) {
		this.id = id;
	}

	public ApplicationLink(URI baseUri, String id) {
		this.id = id;
		this.link = Link.fromMethod(ApplicationResource.class, "getApplication")
			.baseUri(baseUri)
			.rel("self")
			.build(id);
	}

	public static ApplicationLink fromId(String id) {
		return new ApplicationLink(id);
	}
	
	public static ApplicationLink fromBson(Document bson) {
		return new ApplicationLink(bson);
	}

	public String getId() {
		return id;
	}
}

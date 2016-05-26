package se.atg.cmdb.model;

import java.net.URI;

import javax.ws.rs.core.Link;

import org.bson.Document;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;

import com.fasterxml.jackson.annotation.JsonCreator;

import se.atg.cmdb.ui.rest.GroupResource;

public class GroupLink {

	public final String id;

	@InjectLink(
		resource=GroupResource.class,
		method="getGroup",
		style=Style.ABSOLUTE,
		rel="self",
		bindings={@Binding(name="id", value="${instance.id}")}
	)
	public Link link;

	public GroupLink(Document bson) {
		this.id = bson.getString("id");
	}

	@JsonCreator
	public GroupLink(String id) {
		this.id = id;
	}

	public GroupLink(URI baseUri, String id) {
		this.id = id;
		this.link = Link.fromMethod(GroupResource.class, "getGroup")
			.baseUri(baseUri)
			.rel("self")
			.build(id);
	}

	public static GroupLink fromId(String id) {
		return new GroupLink(id);
	}
	
	public static GroupLink fromBson(Document bson) {
		return new GroupLink(bson);
	}

	public String getId() {
		return id;
	}
}

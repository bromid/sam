package se.atg.cmdb.model;

import java.net.URI;

import javax.ws.rs.core.Link;

import org.bson.Document;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;

import com.fasterxml.jackson.annotation.JsonCreator;

import se.atg.cmdb.ui.rest.AssetResource;

public class AssetLink {

	public final String id;
	public final String name;

	@InjectLink(
		resource=AssetResource.class,
		method="getAsset",
		style=Style.ABSOLUTE,
		rel="self",
		bindings={@Binding(name="id", value="${instance.id}")}
	)
	public Link link;

	public AssetLink(Document bson) {
		this.id = bson.getString("id");
		this.name = bson.getString("name");
	}

	@JsonCreator
	public AssetLink(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public AssetLink(URI baseUri, String id, String name) {
		this(id, name);
		this.link = Link.fromMethod(AssetResource.class, "getAsset")
			.baseUri(baseUri)
			.rel("self")
			.build(id);
	}

	public static AssetLink fromBson(Document bson) {
		return new AssetLink(bson);
	}

	public String getId() {
		return id;
	}
}

package se.atg.cmdb.model;

import java.net.URI;

import javax.ws.rs.core.Link;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.Document;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import se.atg.cmdb.ui.rest.ApplicationResource;
import se.atg.cmdb.ui.rest.Defaults;

public class ApplicationLink {

	public final String id;
	public final String name;

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
		this.name = bson.getString("name");
	}

	@JsonCreator
	public ApplicationLink(
		@JsonProperty("id") String id,
		@JsonProperty("name") String name
	) {
		this.id = id;
		this.name = name;
	}

	public ApplicationLink(URI baseUri, String id, String name) {
		this.id = id;
		this.name = name;
		this.link = Link.fromMethod(ApplicationResource.class, "getApplication")
			.baseUri(baseUri)
			.rel("self")
			.build(id);
	}

	public static ApplicationLink fromBson(Document bson) {
		return new ApplicationLink(bson);
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, Defaults.STYLE);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}

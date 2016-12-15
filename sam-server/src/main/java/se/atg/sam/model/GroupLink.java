package se.atg.sam.model;

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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import se.atg.sam.ui.rest.Defaults;
import se.atg.sam.ui.rest.GroupResource;
import se.atg.sam.ui.rest.serializer.StringInDatabaseSerializer;

@JsonSerialize(using = StringInDatabaseSerializer.class)
public class GroupLink implements StringInDatabase {

  public final String id;
  public final String name;

  @InjectLink(
    resource = GroupResource.class,
    method = "getGroup",
    style = Style.ABSOLUTE,
    rel = "self",
    bindings = {@Binding(name = "id", value = "${instance.id}")}
  )
  public Link link;

  @JsonCreator
  public GroupLink(String id) {
    this(id, null);
  }

  @JsonCreator
  public GroupLink(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name
  ) {
    this.id = id;
    this.name = name;
  }

  public GroupLink(Document bson) {
    this.id = bson.getString("id");
    this.name = bson.getString("name");
  }

  public GroupLink(URI baseUri, String id, String name) {
    this(id, name);
    this.link = Link.fromMethod(GroupResource.class, "getGroup")
      .baseUri(baseUri)
      .rel("self")
      .build(id);
  }

  public static GroupLink fromBson(Document bson) {
    return new GroupLink(bson);
  }

  public String getId() {
    return id;
  }

  @Override
  public String stringInDatabase() {
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

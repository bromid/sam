package se.atg.cmdb.model;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.annotations.ApiModel;
import se.atg.cmdb.helpers.Mapper;
import se.atg.cmdb.ui.rest.Defaults;

@ApiModel(description = "An asset is identified by an id.")
@JsonPropertyOrder({ "id", "name", "description", "os", "network", "meta", "attributes" })
public class Asset extends Base {

  @Size(min = 1, max = 50)
  public String id;
  @Size(min = 1, max = 50)
  public String name;
  @Valid
  public OS os;
  @Valid
  public Network network;

  public Asset() {
  }

  public Asset(Document bson) {
    super(bson);

    this.id = bson.getString("id");
    this.name = bson.getString("name");
    this.os = Mapper.mapObject(bson, "os", OS::fromBson);
    this.network = Mapper.mapObject(bson, "network", Network::fromBson);
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

  public static class OS {

    @NotNull(groups = Create.class)
    @Size(min = 1, groups = Update.class)
    public String name;
    @NotNull(groups = Create.class)
    @Size(min = 1, groups = Update.class)
    public String type;
    public String version;
    public Map<String, Object> attributes;

    public OS() {
    }

    public OS(Document bson) {
      this.name = bson.getString("name");
      this.type = bson.getString("type");
      this.version = bson.getString("version");
      this.attributes = Mapper.mapAttributes(bson);
    }

    public static OS fromBson(Document bson) {
      return new OS(bson);
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

  public static class Network {

    public String ipv4Address;
    public String ipv6Address;
    public Map<String, Object> attributes;

    public Network() {
    }

    public Network(Document bson) {
      this.ipv4Address = bson.getString("ipv4Address");
      this.ipv6Address = bson.getString("ipv6Address");
      this.attributes = Mapper.mapAttributes(bson);
    }

    public static Network fromBson(Document bson) {
      return new Network(bson);
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

  public interface Create extends Update {
  }

  public interface Update extends Base.Validation {
  }
}
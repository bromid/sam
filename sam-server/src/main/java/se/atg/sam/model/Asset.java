package se.atg.sam.model;

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
import se.atg.sam.helpers.Mapper;
import se.atg.sam.ui.rest.Defaults;

@ApiModel(description = "An asset is identified by an id.")
@JsonPropertyOrder({ "id", "name", "description", "os", "network", "meta", "attributes" })
public class Asset extends Base {

  @NotNull(groups = Create.class)
  @Size(min = 1, max = 50)
  public String id;
  @NotNull(groups = Create.class)
  @Size(min = 1, max = 50)
  public String name;
  public GroupLink group;
  @Valid
  public Os os;
  @Valid
  public Network network;

  public Asset() {
  }

  public Asset(Document bson) {
    super(bson);

    this.id = bson.getString("id");
    this.name = bson.getString("name");
    this.group = Mapper.mapObject(bson, "group", GroupLink::fromBson);
    this.os = Mapper.mapObject(bson, "os", Os::fromBson);
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

  public static class Os {

    @NotNull(groups = SubtypeCreate.class)
    @Size(min = 1, groups = SubtypeUpdate.class)
    public String name;
    @NotNull(groups = SubtypeCreate.class)
    @Size(min = 1, groups = SubtypeUpdate.class)
    public String type;
    public String version;
    public Map<String, Object> attributes;

    public Os() {
    }

    public Os(Document bson) {
      this.name = bson.getString("name");
      this.type = bson.getString("type");
      this.version = bson.getString("version");
      this.attributes = Mapper.mapAttributes(bson);
    }

    public static Os fromBson(Document bson) {
      return new Os(bson);
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

  public interface Create extends SubtypeCreate, Update {}

  public interface Update extends SubtypeUpdate {}

  public interface SubtypeCreate extends SubtypeUpdate {}

  public interface SubtypeUpdate extends Base.Validation {}
}

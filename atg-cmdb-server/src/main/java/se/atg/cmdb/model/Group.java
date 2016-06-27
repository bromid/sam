package se.atg.cmdb.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.annotations.ApiModel;
import se.atg.cmdb.helpers.Mapper;
import se.atg.cmdb.ui.rest.Defaults;

@ApiModel(description = "A group belongs to a tree of other groups and has applications and assets. It can also be tagged to be easier to find than browsing the group-tree.")
@JsonPropertyOrder({ "id", "name", "description", "groups", "applications", "tags", "meta" })
public class Group extends Base {

  @NotNull(groups = Create.class)
  @Size(min = 1, max = 50)
  public String id;
  @NotNull(groups = Create.class)
  @Size(min = 1, max = 50)
  public String name;
  public List<Group> groups;
  public List<ApplicationLink> applications;
  public List<AssetLink> assets;
  @JsonView(View.Api.class)
  public List<Tag> tags;

  public Group() {
  }

  @JsonCreator
  public Group(String id) {
    this.id = id;
  }

  public Group(Document bson) {
    super(bson);

    this.id = bson.getString("id");
    this.name = bson.getString("name");
    this.groups = Mapper.mapList(bson, "groups", Group::fromBson);

    this.applications = Mapper.mapList(bson, "applications", ApplicationLink::fromBson);
    this.assets = Mapper.mapList(bson, "assets", AssetLink::fromBson);
    this.tags = Mapper.mapList(bson, "tags", Tag::new);
  }

  public List<String> resetGroups() {

    if (this.groups == null) {
      return Collections.emptyList();
    }
    final List<String> groups = this.groups.stream()
      .map(Group::getId)
      .collect(Collectors.toList());
    this.groups.clear();
    return groups;
  }

  public List<Group> getGroups() {
    if (groups == null) {
      return Collections.emptyList();
    }
    return groups;
  }

  public void addGroup(Group group) {
    groups.add(group);
  }

  public String getId() {
    return id;
  }

  @JsonProperty("tags")
  @JsonView(View.Db.class)
  public List<String> getTags() {
    if (tags == null) {
      return Collections.emptyList();
    }
    return tags.stream()
      .map(t->t.name)
      .collect(Collectors.toList());
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

  public static Group fromBson(Document bson) {
    return new Group(bson);
  }

  public interface Create extends Update {}

  public interface Update extends Base.Validation {}
}

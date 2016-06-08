package se.atg.cmdb.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

@ApiModel(description = "A group belongs to a tree of other groups and has applications and assets. It can also be tagged to be easier to find than browsing the group-tree.")
@JsonPropertyOrder({ "id", "name", "description", "groups", "applications", "tags", "meta" })
public class Group extends Base {

	@NotNull
	@Size(min = 1, max = 50)
	public String id;
	@NotNull
	@Size(min = 1, max = 50)
	public String name;
	public List<Group> groups;
	public List<ApplicationLink> applications;
	public List<AssetLink> assets;
	public List<Tag> tags;

	public Group() {
	}

	@SuppressWarnings("unchecked")
	public Group(Document bson) {
		super(bson);

		this.id = bson.getString("id");
		this.name = bson.getString("name");
		this.groups = bson.get("groups", List.class);

		this.applications = Mapper.mapList(bson, "applications", ApplicationLink::fromBson);
		this.assets = Mapper.mapList(bson, "assets", AssetLink::fromBson);
		this.tags = Mapper.mapList(bson, "tags", Tag::new);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> resetGroups() {

		if (this.groups == null) {
			return Collections.emptyList();
		}

		final Stream<Object> typeErasure = (Stream) this.groups.stream();
		final List<String> groups = typeErasure.map(t -> t.toString()).collect(Collectors.toList());
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
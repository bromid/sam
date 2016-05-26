package se.atg.cmdb.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import se.atg.cmdb.helpers.Mapper;
import se.atg.cmdb.ui.rest.Defaults;

@JsonPropertyOrder({ "id", "name", "description", "groups", "applications", "tags", "meta" })
public class Group extends Base {

	@NotNull
	@Size(min = 1)
	public String id;
	@NotNull
	@Size(min = 1)
	public String name;
	@Size(min = 1, max = 500)
	public String description;
	public List<Group> groups;
	public List<ApplicationLink> applications;
	public List<Tag> tags;

	public Group() {
	}

	@SuppressWarnings("unchecked")
	public Group(Document bson) {
		super(bson);

		this.id = bson.getString("id");
		this.name = bson.getString("name");
		this.description = bson.getString("description");
		this.groups = bson.get("groups", List.class);

		this.tags = Mapper.mapList(bson, "tags", Tag::new);
		this.applications = Mapper.mapList(bson, "applications", ApplicationLink::fromBson);
		this.attributes = Mapper.mapAttributes(bson);
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

	public String toString() {
		return ReflectionToStringBuilder.toString(this, Defaults.STYLE);
	}
}
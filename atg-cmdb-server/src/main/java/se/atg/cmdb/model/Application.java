package se.atg.cmdb.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import se.atg.cmdb.helpers.Mapper;
import se.atg.cmdb.ui.rest.Defaults;

@JsonPropertyOrder({"id","name","version","meta"})
public class Application extends Base {

	@NotNull @Size(min = 1)
	public String id;
	@NotNull @Size(min = 1)
	public String name;
	@NotNull @Size(min = 1)
	public String version;
	public GroupLink group;

	@JsonIgnore 
	public String hash;

	public Application() {}
	public Application(Document bson) {
		super(bson);

		this.id = bson.getString("id");
		this.name = bson.getString("name");
		this.version = bson.getString("version");
		this.group = Mapper.mapObject(bson, "group", GroupLink::fromId);
		this.attributes = Mapper.mapAttributes(bson);
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

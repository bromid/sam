package se.atg.cmdb.model;

import java.util.Map;

import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import org.bson.Document;

import se.atg.cmdb.helpers.Mapper;

public abstract class Base {

	@Size(min = 10, max = 500)
	public String description;
	@Null(groups=Validation.class)
	public Meta meta;
	public Map<String, Object> attributes;

	Base() {}
	Base(Document bson) {
		this.description = bson.getString("description");
		this.meta = Mapper.mapObject(bson, "meta", Meta::fromBson);
		this.attributes = Mapper.mapAttributes(bson);
	}

	public interface Validation {}
}
package se.atg.cmdb.model;

import java.util.Map;

import javax.validation.constraints.Null;

import org.bson.Document;

import se.atg.cmdb.helpers.Mapper;

public class Base {

	@Null(groups=Validation.class)
	public Meta meta;
	public Map<String, Object> attributes;

	Base() {}
	Base(Document bson) {
		this.meta = Mapper.mapObject(bson, "meta", Meta::fromBson);
		this.attributes = Mapper.mapAttributes(bson);
	}

	public interface Validation {}
}
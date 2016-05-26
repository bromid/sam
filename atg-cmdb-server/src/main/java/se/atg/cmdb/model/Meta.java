package se.atg.cmdb.model;

import java.time.ZonedDateTime;

import org.bson.Document;

import se.atg.cmdb.helpers.Mapper;

public class Meta {

	public String createdBy;
	public ZonedDateTime created;
	public String updatedBy;
	public ZonedDateTime updated;
	public String refreshedBy;
	public ZonedDateTime refreshed;
	public String hash;

	public Meta() {}
	public Meta(Document bson) {
		this.createdBy = bson.getString("createdBy");
		this.created = Mapper.mapDateTime(bson, "created");
		this.updatedBy = bson.getString("updatedBy");
		this.updated = Mapper.mapDateTime(bson, "updated");
		this.updatedBy = bson.getString("refreshedBy");
		this.updated = Mapper.mapDateTime(bson, "refreshed");
		this.hash = bson.getString("hash");
	}

	public static Meta fromBson(Document bson) {
		return new Meta(bson);
	}
}
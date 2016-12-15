package se.atg.sam.model;

import java.time.ZonedDateTime;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.Document;

import se.atg.sam.helpers.Mapper;
import se.atg.sam.ui.rest.Defaults;

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
    this.refreshedBy = bson.getString("refreshedBy");
    this.refreshed = Mapper.mapDateTime(bson, "refreshed");
    this.hash = bson.getString("hash");
  }

  public static Meta fromBson(Document bson) {
    return new Meta(bson);
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

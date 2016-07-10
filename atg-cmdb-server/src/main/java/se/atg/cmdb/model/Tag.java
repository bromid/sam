package se.atg.cmdb.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import se.atg.cmdb.ui.rest.Defaults;
import se.atg.cmdb.ui.rest.serializer.StringInDatabaseSerializer;

@JsonSerialize(using = StringInDatabaseSerializer.class)
public class Tag implements StringInDatabase {

  public String name;
  public boolean editable = true;

  public Tag() {}

  @JsonCreator
  public Tag(String name) {
    this.name = name;
  }

  public static Tag fromName(String name) {
    return new Tag(name);
  }

  @Override
  public String stringInDatabase() {
    return name;
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

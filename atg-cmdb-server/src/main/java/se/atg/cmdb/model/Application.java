package se.atg.cmdb.model;

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

@ApiModel(description = "An application is running on a server and is identified by an id. All applications belongs to one or more groups and needs to be versioned")
@JsonPropertyOrder({ "id", "name", "version", "group", "meta" })
public class Application extends Base {

  @NotNull(groups = Create.class)
  @Size(min = 1, max = 50)
  public String id;
  @NotNull(groups = Create.class)
  @Size(min = 1, max = 50)
  public String name;
  public GroupLink group;

  public Application() {}

  public Application(Document bson) {
    super(bson);

    this.id = bson.getString("id");
    this.name = bson.getString("name");
    this.group = Mapper.mapObject(bson, "group", GroupLink::fromId);
    this.attributes = Mapper.mapAttributes(bson);
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

  public interface Create extends Update {}

  public interface Update extends Base.Validation {}
}

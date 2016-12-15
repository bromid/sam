package se.atg.sam.model;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.annotations.ApiModel;
import se.atg.sam.helpers.Mapper;
import se.atg.sam.ui.rest.Defaults;

@ApiModel(description = "A server is an asset which can have applications. It is identified by a hostname and environment")
@JsonPropertyOrder({ "hostname", "environment", "fqdn", "os", "network", "applications", "meta" })
public class Server extends Asset {

  @NotNull(groups = Create.class)
  @Size(min = 1, max = 50, groups = Update.class)
  public String hostname;
  public String fqdn;
  @NotNull(groups = Create.class)
  @Size(min = 1, max = 50, groups = Update.class)
  public String environment;
  @Valid
  public List<Deployment> deployments;

  public Server() {
  }

  public Server(Document bson) {
    super(bson);

    final Object id = bson.get("_id");
    if (id instanceof Document && id != null) {

      final Document compoundId = (Document) id;
      this.hostname = compoundId.getString("hostname");
      this.environment = compoundId.getString("environment");
    } else {
      this.hostname = bson.getString("hostname");
      this.environment = bson.getString("environment");
    }
    this.fqdn = bson.getString("fqdn");

    final Map<String,Document> applicationMap = Mapper.mapListToMap(bson, "applications", t->t.getString("id"));
    this.deployments = Mapper.mapList(bson, "deployments", applicationMap, t->t.getString("applicationId"), Deployment::fromBson);
  }

  public String getHostname() {
    return hostname;
  }

  public String getFqdn() {
    return fqdn;
  }

  @Null(groups = { Create.class, Update.class })
  public String getId() {
    return id;
  }

  @Null(groups = { Create.class, Update.class })
  public String getName() {
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

  public interface Create extends Update, SubtypeCreate {}

  public interface Update extends SubtypeUpdate {}
}

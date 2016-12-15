package se.atg.sam.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.annotations.ApiModel;
import se.atg.sam.ui.rest.Defaults;

@ApiModel(description = "Information about a deployed application including which server it's deployed on.")
@JsonPropertyOrder({ "hostname", "environment", "version", "releaseNotes", "description", "attributes" })
public class ServerDeployment extends Deployment {

  public String hostname;
  public String environment;

  @JsonCreator
  public ServerDeployment(
    @JsonProperty("hostname") String hostname,
    @JsonProperty("environment") String environment,
    @JsonProperty("applicationId") String applicationId,
    @JsonProperty("version") String version,
    @JsonProperty("releaseNotes") String releaseNotes,
    @JsonProperty("description") String description,
    @JsonProperty("attributes") Map<String,Object> attributes
  ) {
    super(applicationId, version, releaseNotes, description, attributes);
    this.hostname = hostname;
    this.environment = environment;
    this.applicationLink = null;
  }

  @SuppressWarnings("unchecked")
  public ServerDeployment(Document bson) {

    final List<Document> deployments = (List<Document>) bson.get("deployments");
    Validate.isTrue(deployments.size() == 1);

    final Document deployment = deployments.get(0);
    mapToDeployment(deployment, this);

    hostname = bson.getString("hostname");
    environment = bson.getString("environment");
  }

  public ServerDeployment(Server server, Deployment deployment) {
    super(deployment);
    hostname = server.hostname;
    environment = server.environment;
    applicationLink = null;
  }

  public static ServerDeployment fromBson(Document bson) {
    return new ServerDeployment(bson);
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

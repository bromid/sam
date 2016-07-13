package se.atg.cmdb.model;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.annotations.ApiModel;
import se.atg.cmdb.ui.rest.Defaults;

@ApiModel(description = "Information about a deployed application.")
@JsonPropertyOrder({ "applicationLink", "version", "releaseNotes", "description", "attributes" })
public class Deployment extends Base {

  @NotNull
  @Valid
  @JsonView(View.Api.class)
  public ApplicationLink applicationLink;
  public String version;
  public String releaseNotes;

  public Deployment() {
  }

  @JsonCreator
  public Deployment(String applicationId) {
    applicationLink = new ApplicationLink(applicationId);
  }

  @JsonCreator
  public Deployment(
    @JsonProperty("applicationId") String applicationId,
    @JsonProperty("version") String version,
    @JsonProperty("releaseNotes") String releaseNotes,
    @JsonProperty("description") String description,
    @JsonProperty("attributes") Map<String, Object> attributes
  ) {
    applicationLink = new ApplicationLink(applicationId);
    this.version = version;
    this.releaseNotes = releaseNotes;
    this.description = description;
    this.attributes = attributes;
  }

  // Copy constructor
  public Deployment(Deployment deployment) {
    applicationLink = deployment.applicationLink;
    version = deployment.version;
    releaseNotes = deployment.releaseNotes;
    description = deployment.description;
    attributes = deployment.attributes;
  }

  private Deployment(Document deployment, Document application) {
    mapToDeployment(deployment, this);

    final String applicationId = deployment.getString("applicationId");
    final String applicationName = (application == null) ? null : application.getString("name");
    applicationLink = new ApplicationLink(applicationId, applicationName);
  }

  public static void mapToDeployment(Document bson, Deployment deployment) {
    mapToBase(bson, deployment);
    deployment.version = bson.getString("version");
    deployment.releaseNotes = bson.getString("releaseNotes");
  }

  public static Deployment fromBson(Document deployment, Document application) {
    return new Deployment(deployment, application);
  }

  public static Deployment fromDeploymentBson(Document deployment) {
    return new Deployment(deployment, null);
  }

  @JsonProperty
  @JsonView(View.Db.class)
  public String getApplicationId() {
    return applicationLink.id;
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

  public static boolean sameApplicationId(Document first, Document second) {
    return first.getString("applicationId").equals(second.getString("applicationId"));
  }
}

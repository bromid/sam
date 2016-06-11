package se.atg.cmdb.model;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import se.atg.cmdb.ui.rest.Defaults;

public class Deployment extends Base {

	@NotNull @JsonView(View.API.class)
	public ApplicationLink applicationLink;
	public String version;
	public String releaseNotes;

	public Deployment() {}

	@JsonCreator
	public Deployment(
		String applicationId
	) {
		applicationLink = new ApplicationLink(applicationId);
	}

	@JsonCreator
	public Deployment(
		@JsonProperty("applicationId") String applicationId,
		@JsonProperty("version") String version,
		@JsonProperty("releaseNotes") String releaseNotes
	) {
		applicationLink = new ApplicationLink(applicationId);
		this.version = version;
		this.releaseNotes = releaseNotes;
	}

	private Deployment(Document deployment, Document application) {
		version = deployment.getString("version");
		releaseNotes = deployment.getString("releaseNotes");

		final String applicationId = deployment.getString("applicationId");
		final String applicationName = (application == null) ? null : application.getString("name");
		applicationLink = new ApplicationLink(applicationId, applicationName);
	}

	public static Deployment fromBson(Document deployment, Document application) {
		return new Deployment(deployment, application);
	}

	public static Deployment fromDeploymentBson(Document deployment) {
		return new Deployment(deployment, null);
	}

	@JsonProperty @JsonView(View.DB.class)
	public String getApplicationId() {
		return applicationLink.id;
	}

	@JsonProperty @JsonView(View.DB.class)
	public String getApplicationName() {
		return applicationLink.name;
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
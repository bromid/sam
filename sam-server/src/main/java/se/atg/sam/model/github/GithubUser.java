package se.atg.sam.model.github;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import se.atg.sam.ui.rest.Defaults;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubUser {

  @JsonProperty
  public String login;

  @JsonProperty
  public String id;

  @JsonProperty("avatar_url")
  public String avatarUrl;

  @JsonProperty
  public String email;

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

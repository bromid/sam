package se.atg.cmdb.model.auth;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import se.atg.cmdb.ui.rest.Defaults;

@ApiModel(description = "OAuth2 access token")
public class OAuth2AccessToken {

  @JsonProperty("access_token")
  public String token;

  @JsonProperty("token_type")
  public String type;

  @JsonProperty("scope")
  public String scope;

  public OAuth2AccessToken() {}

  public OAuth2AccessToken(String token, String type) {
    this.token = token;
    this.type = type;
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

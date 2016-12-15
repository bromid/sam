package se.atg.sam.model.auth;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import se.atg.sam.ui.rest.Defaults;

@ApiModel(description = "OAuth2 id token")
public class OAuth2IdToken {

  @JsonProperty("id_token")
  public String token;

  @JsonProperty("token_type")
  public String type = "Bearer";

  public OAuth2IdToken() {}

  public OAuth2IdToken(String token) {
    this.token = token;
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

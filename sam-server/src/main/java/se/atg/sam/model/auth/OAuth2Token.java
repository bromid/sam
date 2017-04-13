package se.atg.sam.model.auth;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import se.atg.sam.ui.rest.Defaults;

@ApiModel(description = "OAuth2 access token")
public class OAuth2Token {

  @JsonProperty("access_token")
  public String accessToken;

  @JsonProperty("refresh_token")
  public String refreshToken;

  @JsonProperty("id_token")
  public String idToken;

  @JsonProperty("token_type")
  public String type;

  @JsonProperty("expires_in")
  public Integer expires;

  @JsonProperty("scope")
  public String scope;

  @JsonProperty("error")
  public String error;

  public OAuth2Token() {}

  public OAuth2Token(String accessToken, String type) {
    this.accessToken = accessToken;
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

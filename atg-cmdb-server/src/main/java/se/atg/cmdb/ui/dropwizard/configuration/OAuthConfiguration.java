package se.atg.cmdb.ui.dropwizard.configuration;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthConfiguration {

  @NotNull
  @JsonProperty
  private String clientId;

  @NotNull
  @JsonProperty
  private String clientSecret;

  @NotNull
  @JsonProperty
  private String authorizeEndpoint;

  @NotNull
  @JsonProperty
  private String accessTokenEndpoint;

  @NotNull
  @JsonProperty
  private String userEndpoint;

  @NotNull
  @JsonProperty
  private String idTokenIssuer;

  @NotNull
  @JsonProperty
  private String idTokenSignKey;

  @NotNull
  @JsonProperty
  private String origin;

  public String getClientId() {
    return clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public String getAuthorizeEndpoint() {
    return authorizeEndpoint;
  }

  public String getAccessTokenEndpoint() {
    return accessTokenEndpoint;
  }

  public String getUserEndpoint() {
    return userEndpoint;
  }

  public String getIdTokenIssuer() {
    return idTokenIssuer;
  }

  public String getIdTokenSignKey() {
    return idTokenSignKey;
  }

  public String getOrigin() {
    return origin;
  }
}

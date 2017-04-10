package se.atg.sam.ui.dropwizard.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthConfiguration {

  @NotNull
  @JsonProperty
  protected String clientId;

  @NotNull
  @JsonProperty
  protected String clientSecret;

  @NotNull
  @JsonProperty
  protected String scopes;

  @NotNull
  @JsonProperty
  protected String authorizeEndpoint;

  @NotNull
  @JsonProperty
  protected String accessTokenEndpoint;

  @NotNull
  @JsonProperty
  protected String idTokenIssuer;

  @NotNull
  @JsonProperty
  protected String idTokenSignKey;

  @NotNull
  @JsonProperty
  protected String origin;

  @Valid
  @NotNull
  @JsonProperty
  protected OAuthUserConfiguration userConfig = new OAuthUserConfiguration();

  public String getClientId() {
    return clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public String getScopes() {
    return scopes;
  }

  public String getAuthorizeEndpoint() {
    return authorizeEndpoint;
  }

  public String getAccessTokenEndpoint() {
    return accessTokenEndpoint;
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

  public OAuthUserConfiguration getUserConfig() {
    return userConfig;
  }
}

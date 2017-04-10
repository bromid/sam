package se.atg.sam.ui.dropwizard.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthUserConfiguration {

  @JsonProperty
  protected String githubEndpoint;

  @JsonProperty
  protected String idTokenPublicKeyModulus;

  @JsonProperty
  protected String idTokenPublicKeyExponent;

  @JsonProperty
  protected String idTokenIssuer;

  @JsonProperty
  protected String idTokenAudience;

  public String getGithubEndpoint() {
    return githubEndpoint;
  }

  public String getIdTokenPublicKeyModulus() {
    return idTokenPublicKeyModulus;
  }

  public String getIdTokenPublicKeyExponent() {
    return idTokenPublicKeyExponent;
  }

  public String getIdTokenIssuer() {
    return idTokenIssuer;
  }

  public String getIdTokenAudience() {
    return idTokenAudience;
  }
}

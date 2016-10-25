package se.atg.cmdb.ui.dropwizard.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.CacheBuilderSpec;

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import se.atg.cmdb.ui.dropwizard.db.MongoDatabaseConnectionFactory;

public class CmdbConfiguration extends Configuration {

  @Valid
  @NotNull
  @JsonProperty("mongoDatabase")
  protected MongoDatabaseConnectionFactory dbConnectionFactory = new MongoDatabaseConnectionFactory();

  @Valid
  @NotNull
  @JsonProperty
  protected JerseyClientConfiguration jerseyClientConfig = new JerseyClientConfiguration();

  @Valid
  @NotNull
  @JsonProperty
  protected OAuthConfiguration oauthConfig = new OAuthConfiguration();

  @JsonProperty
  protected String testEndpoint;

  @JsonProperty
  protected boolean logRequests = false;

  @JsonProperty
  protected String authenticationCachePolicy;

  public MongoDatabaseConnectionFactory getDbConnectionFactory() {
    return this.dbConnectionFactory;
  }

  public JerseyClientConfiguration getJerseyClientConfiguration() {
    return jerseyClientConfig;
  }

  public OAuthConfiguration getOAuthConfiguration() {
    return oauthConfig;
  }

  public String getTestEndpoint() {
    return testEndpoint;
  }

  public boolean isLogRequests() {
    return logRequests;
  }

  public CacheBuilderSpec getAuthenticationCachePolicy() {
    return CacheBuilderSpec.parse(authenticationCachePolicy);
  }
}

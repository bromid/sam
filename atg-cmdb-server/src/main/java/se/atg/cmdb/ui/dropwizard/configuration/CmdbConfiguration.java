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
  private MongoDatabaseConnectionFactory dbConnectionFactory = new MongoDatabaseConnectionFactory();

  @Valid
  @NotNull
  @JsonProperty
  private JerseyClientConfiguration jerseyClientConfig = new JerseyClientConfiguration();

  @Valid
  @NotNull
  @JsonProperty
  private OAuthConfiguration oauthConfig = new OAuthConfiguration();

  @JsonProperty
  private String testEndpoint;

  @JsonProperty
  private boolean logRequests = false;

  @JsonProperty
  private String authenticationCachePolicy;

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

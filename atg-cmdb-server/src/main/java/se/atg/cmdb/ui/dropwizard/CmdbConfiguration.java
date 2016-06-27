package se.atg.cmdb.ui.dropwizard;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

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

  @JsonProperty
  private String testEndpoint;

  public MongoDatabaseConnectionFactory getDbConnectionFactory() {
    return this.dbConnectionFactory;
  }

  public JerseyClientConfiguration getJerseyClientConfiguration() {
    return jerseyClientConfig;
  }

  public String getTestEndpoint() {
    return testEndpoint;
  }
}

package se.atg.cmdb.ui.dropwizard;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import se.atg.cmdb.ui.dropwizard.db.MongoDBConnectionFactory;

public class CMDBConfiguration extends Configuration {

	@Valid
	@NotNull
	@JsonProperty("mongoDB")
	private MongoDBConnectionFactory dbConnectionFactory = new MongoDBConnectionFactory();

    @Valid
    @NotNull
    @JsonProperty
    private JerseyClientConfiguration jerseyClientConfig = new JerseyClientConfiguration();

    @JsonProperty
    private String testEndpoint;

	public MongoDBConnectionFactory getDBConnectionFactory() {
	    return this.dbConnectionFactory;
	}

    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return jerseyClientConfig;
    }

    public String getTestEndpoint() {
    	return testEndpoint;
    }
}
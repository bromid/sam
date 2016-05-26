package se.atg.cmdb.ui.dropwizard;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import se.atg.cmdb.ui.dropwizard.db.MongoDBConnectionFactory;

public class CMDBConfiguration extends Configuration {

	@Valid
	@NotNull
	private MongoDBConnectionFactory dbConnectionFactory = new MongoDBConnectionFactory();

	@JsonProperty("mongoDB")
	public MongoDBConnectionFactory getDBConnectionFactory() {
	    return this.dbConnectionFactory;
	}

	@JsonProperty("mongoDB")
	public void setDBConnectionFactory(MongoDBConnectionFactory dbConnectionFactory) {
	    this.dbConnectionFactory = dbConnectionFactory;
	}
}
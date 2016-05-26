package se.atg.cmdb.ui.dropwizard.db;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MongoDBAddress {

	@NotNull
	@JsonProperty
	public String host;

	@Min(1)
	@Max(65535)
	@JsonProperty
	public int port;
}
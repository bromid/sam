package se.atg.cmdb.ui.dropwizard.db;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;

public class MongoDBConnectionFactory {

	final static Logger logger = LoggerFactory.getLogger(MongoDBConnectionFactory.class);

	@NotNull
	@JsonProperty
	private String dbName;
	@JsonProperty
	private String username;
	@JsonProperty
	private String password;
	@NotNull
	@JsonProperty
	private List<MongoDBAddress> connections;
	private MongoClient mongoClient;

	public MongoDatabase getDatabase(Environment environment) {

		logger.debug("Create database {}", dbName);
		final MongoClient client = getClient(environment);
		return client.getDatabase(dbName);
	}

	private MongoClient getClient(Environment environment) {
		synchronized (this) {

			if (mongoClient != null) {
				return mongoClient;
			}

			logger.debug("Create client {}", connections);
			//final MongoCredential credential = MongoCredential.createCredential(username, dbName, password.toCharArray());
			//mongoClient = new MongoClient(getServerAddresses(), Arrays.asList(credential));
			mongoClient = new MongoClient(getServerAddresses());
			environment.lifecycle().manage(new Managed() {

                @Override
                public void start() throws Exception {}

                @Override
                public void stop() throws Exception {
                	mongoClient.close();
                }
            });
			return mongoClient;
		}
	}

	private List<ServerAddress> getServerAddresses() {
		return connections
			.stream()
			.map(t -> new ServerAddress(t.host, t.port))
			.collect(Collectors.toList());
	}
}

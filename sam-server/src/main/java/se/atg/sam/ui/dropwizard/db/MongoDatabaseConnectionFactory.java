package se.atg.sam.ui.dropwizard.db;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import io.dropwizard.lifecycle.Managed;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;

public class MongoDatabaseConnectionFactory {

  private static final Logger logger = LoggerFactory.getLogger(MongoDatabaseConnectionFactory.class);

  @NotNull
  @JsonProperty
  private String name;
  @JsonProperty
  private String username;
  @JsonProperty
  private String password;
  @NotNull
  @JsonProperty
  private List<MongoDatabaseAddress> connections;
  private MongoClient mongoClient;

  public MongoDatabase getDatabase(LifecycleEnvironment lifecycle) {

    logger.debug("Create database {}", name);
    final MongoClient client = getClient(lifecycle);
    return client.getDatabase(name);
  }

  private MongoClient getClient(LifecycleEnvironment lifecycle) {
    synchronized (this) {

      if (mongoClient != null) {
        return mongoClient;
      }

      logger.debug("Create client {}", connections);

      //final MongoCredential credential = MongoCredential.createCredential(username, dbName, password.toCharArray());
      //mongoClient = new MongoClient(getServerAddresses(), Arrays.asList(credential));

      final MongoClientOptions options = MongoClientOptions.builder()
        .connectTimeout(1000)
        .serverSelectionTimeout(5000)
        .maxWaitTime(5000)
        .build();
      mongoClient = new MongoClient(getServerAddresses(), options);
      lifecycle.manage(new Managed() {

        @Override
        public void start() throws Exception {
        }

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

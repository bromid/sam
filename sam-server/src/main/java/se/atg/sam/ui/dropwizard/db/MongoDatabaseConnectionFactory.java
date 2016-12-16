package se.atg.sam.ui.dropwizard.db;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
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
  private String authDb;
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

      final MongoClientOptions options = MongoClientOptions.builder()
        .connectTimeout(5000)
        .serverSelectionTimeout(10000)
        .maxWaitTime(5000)
        .build();

      mongoClient = createClient(options);

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

  private MongoClient createClient(MongoClientOptions options) {

    final List<ServerAddress> serverAddresses = getServerAddresses();
    logger.info("Connecting to database {}", serverAddresses);

    if (username != null) {
      Preconditions.checkNotNull(password);
      final List<MongoCredential> credentials = Arrays.asList(
          MongoCredential.createCredential(username, (authDb != null) ? authDb : name, password.toCharArray())
       );
      return new MongoClient(serverAddresses, credentials, options);
    }
    return new MongoClient(serverAddresses, options);
  }

  private List<ServerAddress> getServerAddresses() {
    return connections
      .stream()
      .map(t -> new ServerAddress(t.host, t.port))
      .collect(Collectors.toList());
  }
}

package se.atg.sam.ui.dropwizard.db;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;
import com.mongodb.MongoClientException;
import com.mongodb.client.MongoDatabase;

public class MongoDatabaseHealthCheck extends HealthCheck {

  private static final Logger logger = LoggerFactory.getLogger(MongoDatabaseHealthCheck.class);

  private final MongoDatabase mongoDatabase;

  public MongoDatabaseHealthCheck(MongoDatabase database) {
    this.mongoDatabase = database;
  }

  /**
   * Checks if the system database, available in all MongoDB instances can be
   * reached.
   */
  @Override
  protected Result check() throws Exception {
    try {
      final Document result = mongoDatabase.runCommand(new Document("dbStats", 1));
      return Result.healthy(result.toJson());
    } catch (MongoClientException exc) {
      logger.warn("Unhealthy database", exc);
      return Result.unhealthy(exc);
    }
  }
}

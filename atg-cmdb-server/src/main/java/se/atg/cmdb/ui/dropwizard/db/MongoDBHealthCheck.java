package se.atg.cmdb.ui.dropwizard.db;

import org.bson.Document;

import com.codahale.metrics.health.HealthCheck;
import com.mongodb.MongoClientException;
import com.mongodb.client.MongoDatabase;

public class MongoDBHealthCheck extends HealthCheck {

	private final MongoDatabase mongoDatabase;

	public MongoDBHealthCheck(MongoDatabase database) {
        this.mongoDatabase = database;
    }

	/**
	 * Checks if the system database, which exists in all MongoDB instances can
	 * be reached.
	 * 
	 * @return A Result object
	 * @throws Exception
	 */
	@Override
	protected Result check() throws Exception {

		try {
			final Document result = mongoDatabase.runCommand(new Document("dbStats", 1));
			return Result.healthy(result.toJson());
		} catch (MongoClientException ex) {
			return Result.unhealthy(ex.getMessage());
		}
	}
}

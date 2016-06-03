package se.atg.cmdb.ui.text;

import static com.mongodb.client.model.Indexes.ascending;
import static com.mongodb.client.model.Indexes.compoundIndex;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import static com.mongodb.client.model.Indexes.*;

public class CreateDatabase {

	final static Logger logger = LoggerFactory.getLogger(CreateDatabase.class);

	public static void main(String[] args) {

		logger.info("Start");

		try (final MongoClient mongoClient = new MongoClient()) {

			final MongoDatabase database = mongoClient.getDatabase("test");

			final MongoCollection<Document> servers = database.getCollection("servers");
			servers.createIndexes(Lists.newArrayList(
				new IndexModel(
					ascending("fqdn"),
					new IndexOptions().unique(true).sparse(true)
				),
				new IndexModel(
					compoundIndex(ascending("hostname"), ascending("environment")),
					new IndexOptions().unique(true)
				),
				new IndexModel(
					compoundIndex(text("hostname"), text("environment"), text("fqdn"), text("description")),
					new IndexOptions().weights(new Document().append("hostname", 10).append("fqdn", 10).append("environment", 5)).defaultLanguage("sv")
				)
			));

			final MongoCollection<Document> applications = database.getCollection("applications");
			applications.createIndex(ascending("id"), new IndexOptions().unique(true));

			final MongoCollection<Document> groups = database.getCollection("groups");
			groups.createIndex(ascending("id"), new IndexOptions().unique(true));

			final MongoCollection<Document> assets = database.getCollection("assets");
			assets.createIndex(ascending("id"), new IndexOptions().unique(true));			
		}
	}
}

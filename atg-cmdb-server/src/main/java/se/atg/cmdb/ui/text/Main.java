package se.atg.cmdb.ui.text;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import se.atg.cmdb.model.Server;

public class Main {

	final static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {

		logger.info("Start");

		try (final MongoClient mongoClient = new MongoClient()) {

			final MongoDatabase database = mongoClient.getDatabase("test");
			final MongoCollection<Document> servers = database.getCollection("servers");
			servers.drop();
			servers.createIndex(new Document().append("fqdn", 1), new IndexOptions().unique(true).sparse(true));
			servers.createIndex(new Document().append("hostname", 1).append("environment", 1), new IndexOptions().unique(true));

			for (int server=1; server<9; ++server) {
				for (int env=1; env<3; ++env) {
					servers.insertOne(Document.parse(
					"{\"hostname\": \"vltma" + server + "\"," +
						"\"fqdn\": \"vltma" + server + ".test" + env + ".hh.atg.se\"," +
						"\"environment\": \"test" + env + "\"," +
						"\"os\": {" +
						 "\"name\": \"RedHat Enterprise Linux\"," +
						 "\"type\": " + ((server%2==0) ? "\"Linux\"" : "\"Windows\",") +
						 "\"version\": \"6.2\"" +
					"}}")
					.append("applications", Lists.newArrayList("atg-service", "tillsammans-service"))
					.append("attributes", new Document().append("Param1","Value1").append("Param2",new Document().append("Param3", "Value3")))
					);
				}
			}

			final MongoCollection<Document> applications = database.getCollection("applications");
			applications.drop();
			applications.createIndex(new Document().append("id", 1), new IndexOptions().unique(true));

			applications.insertOne(new Document()
				.append("id", "atg-web")
				.append("name", "atg.se Web")
				.append("version", "1.0.0")
				.append("group", "atg-web")
				.append("attributes", new Document().append("server", new Document().append("name", "dropwizard").append("version", "0.9.1")))
			);
			applications.insertOne(new Document()
				.append("id", "atg-service-info")
				.append("name", "ATG Service Info")
				.append("version", "2.1.0-feature1")
				.append("group", "atg-service-info")
			);
			applications.insertOne(new Document()
				.append("id", "atg-service-info-cache")
				.append("name", "ATG Service Info Cache")
				.append("version", "2.4.0-feature1")
				.append("group", "atg-service-info")
			);
			applications.insertOne(new Document()
				.append("id", "atg-service-betting")
				.append("name", "ATG Service Betting")
				.append("version", "2.3.0-feature1")
				.append("group", "atg-service-betting")
			);
			applications.insertOne(new Document()
				.append("id", "tillsammans-service")
				.append("name", "Tillsammans Service")
				.append("version", "5.0")
				.append("group", "tillsammans")
			);
			applications.insertOne(new Document()
				.append("id", "tillsammans-web")
				.append("name", "Tillsammans Web")
				.append("version", "1.73.0")
				.append("group", "tillsammans")
			);

			final MongoCollection<Document> groups = database.getCollection("groups");
			groups.drop();

			groups.insertOne(new Document()
				.append("id", "atg-se")
				.append("name", "atg.se")
				.append("description", "www.atg.se")
				.append("groups", Lists.newArrayList("atg-web", "atg-service","atg-virtual-racing"))
			);
			groups.insertOne(new Document()
				.append("id", "atg-virtual-racing")
				.append("name", "ATG Virtual Racing")
				.append("description", "Atg.se Spel på virtuella hästar")
				.append("groups", Lists.newArrayList("atg-service-betting", "atg-vr-video"))
			);
			groups.insertOne(new Document()
				.append("id", "atg-vr-video")
				.append("name", "ATG Virtual Racing games generators")
			);
			groups.insertOne(new Document()
				.append("id", "atg-web")
				.append("name", "atg.se")
				.append("description", "Atg.se statiska webbresurser")
			);
			groups.insertOne(new Document()
				.append("id", "atg-service")
				.append("name", "atg.se")
				.append("description", "Atg.se Tjänstelager")
				.append("groups", Lists.newArrayList("atg-service-info", "atg-service-betting"))
			);
			groups.insertOne(new Document()
				.append("id", "atg-service-info")
				.append("name", "atg.se")
				.append("description", "Atg.se Info Tjänstelager")
			);
			groups.insertOne(new Document()
				.append("id", "atg-service-betting")
				.append("name", "atg.se")
				.append("description", "Atg.se Betting Tjänstelager")
			);

			groups.insertOne(new Document()
				.append("id", "tillsammans")
				.append("name", "Tillsammans")
				.append("description", "tillsammans.atg.se")
			);

			groups.insertOne(new Document()
				.append("id", "org-it")
				.append("name", "ATG IT")
			);
			groups.insertOne(new Document()
				.append("id", "org-it-spel")
				.append("name", "ATG IT Spelsektionen")
				.append("groups", Lists.newArrayList("atg-se"))
			);
			groups.insertOne(new Document()
				.append("id", "org-it-sport")
				.append("name", "ATG IT Sportsektionen")
			);			
			groups.insertOne(new Document()
				.append("id", "org-it-prod")
				.append("name", "ATG IT Produktionssektionen")
				.append("groups", Lists.newArrayList("org-netman", "org-sysman"))
			);
			groups.insertOne(new Document()
				.append("id", "org-netman")
				.append("name", "ATG IT Netman")
				.append("groups", Lists.newArrayList("track-infra"))
			);
			groups.insertOne(new Document()
				.append("id", "org-sysman")
				.append("name", "ATG IT Sysman")
			);

			logger.info("Number of servers: {}", servers.count());
			logger.info("Server: {}", servers.find().first().toJson());

			final MongoCursor<Document> cursor = servers.find().iterator();
			try {
			    while (cursor.hasNext()) {
			        System.out.println(cursor.next().toJson());
			    }
			} finally {
			    cursor.close();
			}

			System.out.println("Windows i Test2");
			servers.find(Filters.and(
							Filters.eq("environment", "test2"),
							Filters.eq("os.type", "Windows")
						)
					).projection(Projections.excludeId()
					).sort(Sorts.descending("hostname")
					).forEach((Consumer<Document>) System.out::println);

			System.out.println("Aggregate");
			servers.aggregate(Lists.newArrayList(
						Aggregates.match(Filters.and(
							Filters.eq("environment", "test2"),
							Filters.eq("os.type", "Windows")
						)),
						Aggregates.unwind("$applications"),
						Aggregates.lookup("applications", "applications", "name", "application"),
						Aggregates.project(new Document()
							.append("_id", 0)
							.append("fqdn", 1)
							.append("environment", 1)
							.append("application.group", 1))
					)).forEach((Consumer<Document>) System.out::println);

			System.out.println("Linux i Test1");
			final ArrayList<Server> serverDocs = Lists.newArrayList();
			servers.find(
				Filters.and(
					Filters.eq("environment", "test1"),
					Filters.eq("os.type", "Linux"))
			).map(Server::new
			).into(serverDocs);

			for (Server server: serverDocs) {
				System.out.println(server);
			}
		}
		logger.info("End");
	}
}

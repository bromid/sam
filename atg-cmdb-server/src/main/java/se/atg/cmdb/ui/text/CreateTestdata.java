package se.atg.cmdb.ui.text;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class CreateTestdata {

	final static Logger logger = LoggerFactory.getLogger(CreateTestdata.class);

	public static void main(String[] args) {

		logger.info("Start");

		try (final MongoClient mongoClient = new MongoClient()) {

			final MongoDatabase database = mongoClient.getDatabase("test");

			final MongoCollection<Document> servers = database.getCollection("servers");
			servers.drop();

			final MongoCollection<Document> applications = database.getCollection("applications");
			applications.drop();

			final MongoCollection<Document> groups = database.getCollection("groups");
			groups.drop();

			final MongoCollection<Document> assets = database.getCollection("assets");
			assets.drop();

			// Add indexes
			CreateDatabase.initDatabase(database);

			createServers(servers);
			createApplications(applications);
			createGroups(groups);
			createAssets(assets);
		}
		logger.info("End");
	}

	private static void createAssets(MongoCollection<Document> assets) {

		assets.insertOne(new Document()
			.append("id", "core-switch-hh-1")
			.append("name", "Core switch hästsportenshus 1")
			.append("description", "Nexus 3423 ACI ver2")
			.append("group", "netman-infrastructure-hh")
		);
		assets.insertOne(new Document()
			.append("id", "core-switch-hh-2")
			.append("name", "Core switch hästsportenshus 2")
			.append("description", "Nexus 3423 ACI ver2")
			.append("group", "netman-infrastructure-hh")
		);
	}

	private static void createGroups(MongoCollection<Document> groups) {

		groups.insertOne(new Document()
			.append("id", "empty-root")
			.append("name", "Tom root grupp")
			.append("tags", Lists.newArrayList("empty"))
		);
		groups.insertOne(new Document()
			.append("id", "atg-se")
			.append("name", "atg.se")
			.append("description", "www.atg.se")
			.append("groups", Lists.newArrayList(new Document("id", "atg-web"), new Document("id", "atg-service"), new Document("id", "atg-virtual-racing")))
			.append("tags", Lists.newArrayList("webapp"))
		);
		groups.insertOne(new Document()
			.append("id", "atg-virtual-racing")
			.append("name", "ATG Virtual Racing")
			.append("description", "Atg.se Spel på virtuella hästar")
			.append("groups", Lists.newArrayList(new Document("id", "atg-service-betting"), new Document("id", "atg-vr-video")))
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
			.append("groups", Lists.newArrayList(new Document("id", "atg-service-info"), new Document("id", "atg-service-betting")))
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
			.append("id", "webbappar")
			.append("name", "Alla webbappar")
			.append("groups", Lists.newArrayList(new Document("id", "tillsammans"), new Document("id", "atg-se")))
			.append("tags", Lists.newArrayList("webapp"))
		);
		groups.insertOne(new Document()
			.append("id", "tillsammans")
			.append("name", "Tillsammans")
			.append("description", "tillsammans.atg.se")
			.append("tags", Lists.newArrayList("webapp", "weblogic"))
		);
		groups.insertOne(new Document()
			.append("id", "travsport")
			.append("tags", Lists.newArrayList("webapp", "weblogic", "sportsystem"))
		);

		groups.insertOne(new Document()
			.append("id", "org-it")
			.append("name", "ATG IT")
			.append("groups", Lists.newArrayList(new Document("id", "org-it-spel"), new Document("id", "org-it-sport"), new Document("id", "org-it-prod")))
			.append("tags", Lists.newArrayList("webapp"))
		);
		groups.insertOne(new Document()
			.append("id", "org-it-spel")
			.append("name", "ATG IT Spelsektionen")
			.append("groups", Lists.newArrayList(new Document("id", "tillsammans"), new Document("id", "atg-se")))
		);
		groups.insertOne(new Document()
			.append("id", "org-it-sport")
			.append("name", "ATG IT Sportsektionen")
			.append("groups", Lists.newArrayList(new Document("id", "travsport")))
		);			
		groups.insertOne(new Document()
			.append("id", "org-it-prod")
			.append("name", "ATG IT Produktionssektionen")
			.append("groups", Lists.newArrayList(new Document("id", "org-netman"), new Document("id", "org-sysman")))
		);
		groups.insertOne(new Document()
			.append("id", "org-netman")
			.append("name", "ATG IT Netman")
			.append("groups", Lists.newArrayList(new Document("id", "netman-infrastructure-tracks"), new Document("id", "netman-infrastructure-hh")))
			.append("tags", Lists.newArrayList("netman"))
		);
		groups.insertOne(new Document()
			.append("id", "netman-infrastructure-tracks")
			.append("name", "Netman nätverksutrustning på banor")
			.append("tags", Lists.newArrayList("switches", "routers"))
		);
		groups.insertOne(new Document()
			.append("id", "netman-infrastructure-hh")
			.append("name", "Netman nätverksutrustning hästsportenshus")
			.append("tags", Lists.newArrayList("switches", "routers"))
		);
		groups.insertOne(new Document()
			.append("id", "org-sysman")
			.append("name", "ATG IT Sysman")
		);
	}

	private static void createApplications(MongoCollection<Document> applications) {

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
	}

	private static void createServers(MongoCollection<Document> servers) {

		for (int server=1; server<9; ++server) {
			for (int env=1; env<3; ++env) {
				servers.insertOne(Document.parse(
				"{\"hostname\": \"vltma" + server + "\"," +
					"\"fqdn\": \"vltma" + server + ".test" + env + ".hh.atg.se\"," +
					"\"environment\": \"test" + env + "\"," +
					"\"description\": \"Runs all important applications\"" +
					"\"os\": {" +
					  "\"name\": \"RedHat Enterprise Linux\"," +
					  "\"type\": " + ((server%2==0) ? "\"Linux\"" : "\"Windows\",") +
					  "\"version\": \"6.2\"" +
					"}," +
					"\"network\": {" +
					  "\"ipv4Address\": \"10.0.0.1\"," +
					  "\"attributes\": {" +
					    "\"test\": 1" +
					  "}" +
				"}}")
				.append("applications", Lists.newArrayList(new Document().append("id", "atg-service-betting"), new Document().append("id", "tillsammans-service")))
				.append("attributes", new Document().append("Param1","Value1").append("Param2",new Document().append("Param3", "Value3")))
				);
			}
		}
		servers.updateOne(Filters.eq("fqdn", "vltma1.test1.hh.atg.se"), Updates.set("description", "Denna server tillhör Tillsammans."));
		servers.updateOne(Filters.eq("fqdn", "vltma2.test1.hh.atg.se"), Updates.set("description", "Kör både atg.se och atg-service precis som vltma1"));
		servers.updateOne(Filters.eq("fqdn", "vltma1.test2.hh.atg.se"), Updates.set("description", "POB, Jira och Tillsammans i test2 och test1"));
		servers.updateOne(Filters.eq("fqdn", "vltma2.test2.hh.atg.se"), Updates.set("description", "Go-servern är en del av leveranssystemet."));
	}
}

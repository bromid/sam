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

import se.atg.cmdb.dao.Collections;
import se.atg.cmdb.helpers.JsonHelper;

public final class CreateTestdata {

  private static final Logger logger = LoggerFactory.getLogger(CreateTestdata.class);

  private CreateTestdata() {}

  public static void main(String[] args) {

    logger.info("Start");

    try (final MongoClient mongoClient = new MongoClient()) {

      final MongoDatabase database = mongoClient.getDatabase("test");

      final MongoCollection<Document> servers = database.getCollection(Collections.SERVERS);
      servers.drop();

      final MongoCollection<Document> applications = database.getCollection(Collections.APPLICATIONS);
      applications.drop();

      final MongoCollection<Document> groups = database.getCollection(Collections.GROUPS);
      groups.drop();

      final MongoCollection<Document> assets = database.getCollection(Collections.ASSETS);
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
      .append("meta", defaultMeta()));
    assets.insertOne(new Document()
      .append("id", "core-switch-hh-2")
      .append("name", "Core switch hästsportenshus 2")
      .append("description", "Nexus 3423 ACI ver2")
      .append("group", "netman-infrastructure-hh")
      .append("meta", defaultMeta()));
  }

  private static void createGroups(MongoCollection<Document> groups) {

    groups.insertOne(new Document()
      .append("id", "webbappar")
      .append("name", "Alla webbappar")
      .append("meta", defaultMeta())
      .append("groups", Lists.newArrayList(
        new Document("id", "tillsammans"),
        new Document("id", "atg-se"),
        new Document("id", "travsport"),
        new Document("id", "galoppsport")
      ))
      .append("tags", Lists.newArrayList("webapp")));

    groups.insertOne(new Document()
      .append("id", "tillsammans")
      .append("name", "Tillsammans")
      .append("meta", defaultMeta())
      .append("description", "tillsammans.atg.se")
      .append("tags", Lists.newArrayList("webapp", "weblogic")));

    groups.insertOne(new Document()
      .append("id", "travsport")
      .append("name", "Travsport")
      .append("meta", defaultMeta())
      .append("description", "www.travsport.se")
      .append("tags", Lists.newArrayList("webapp", "weblogic", "sportsystem")));

    groups.insertOne(new Document()
      .append("id", "galoppsport")
      .append("name", "Galoppsport")
      .append("meta", defaultMeta())
      .append("description", "www.svenskgalopp.se")
      .append("tags", Lists.newArrayList("webapp", "weblogic", "sportsystem")));

    groups.insertOne(new Document()
      .append("id", "atg-se")
      .append("name", "Atg.se")
      .append("meta", defaultMeta())
      .append("description", "www.atg.se")
      .append("groups", Lists.newArrayList(
        new Document("id", "atg-web"),
        new Document("id", "atg-service"),
        new Document("id", "atg-virtual-racing")
      ))
      .append("tags", Lists.newArrayList("webapp")));
    groups.insertOne(new Document()
      .append("id", "atg-virtual-racing")
      .append("name", "ATG Virtual Racing")
      .append("meta", defaultMeta())
      .append("description", "Atg.se Spel på virtuella hästar")
      .append("groups", Lists.newArrayList(new Document("id", "atg-service-betting"), new Document("id", "atg-vr-video"))));
    groups.insertOne(new Document()
      .append("id", "atg-vr-video")
      .append("name", "ATG Virtual Racing games generators")
      .append("meta", defaultMeta()));
    groups.insertOne(new Document()
      .append("id", "atg-web")
      .append("name", "Atg.se frontend")
      .append("meta", defaultMeta())
      .append("description", "Atg.se statiska webbresurser"));
    groups.insertOne(new Document()
      .append("id", "atg-service")
      .append("name", "Atg.se tjänstelager")
      .append("meta", defaultMeta())
      .append("description", "Atg.se tjänstelager")
      .append("groups", Lists.newArrayList(new Document("id", "atg-service-info"), new Document("id", "atg-service-betting"))));
    groups.insertOne(new Document()
      .append("id", "atg-service-info")
      .append("name", "Atg.se info tjänstelager")
      .append("meta", defaultMeta())
      .append("description", "Atg.se info tjänstelager"));
    groups.insertOne(new Document()
      .append("id", "atg-service-betting")
      .append("name", "Atg.se betting tjänstelager")
      .append("meta", defaultMeta())
      .append("description", "Atg.se betting tjänstelager"));

    groups.insertOne(new Document()
      .append("id", "org-it")
      .append("name", "ATG IT")
      .append("meta", defaultMeta())
      .append("groups", Lists.newArrayList(
        new Document("id", "org-it-spel"),
        new Document("id", "org-it-sport"),
        new Document("id", "org-it-prod")))
      .append("tags", Lists.newArrayList("webapp")));
    groups.insertOne(new Document()
      .append("id", "org-it-spel")
      .append("name", "ATG IT Spelsektionen")
      .append("meta", defaultMeta())
      .append("groups", Lists.newArrayList(new Document("id", "tillsammans"), new Document("id", "atg-se"))));
    groups.insertOne(new Document()
      .append("id", "org-it-sport")
      .append("name", "ATG IT Sportsektionen")
      .append("meta", defaultMeta())
      .append("groups", Lists.newArrayList(new Document("id", "travsport"), new Document("id", "galoppsport"))));
    groups.insertOne(new Document()
      .append("id", "org-it-prod")
      .append("name", "ATG IT Produktionssektionen")
      .append("meta", defaultMeta())
      .append("groups", Lists.newArrayList(new Document("id", "org-netman"), new Document("id", "org-sysman-linux"))));
    groups.insertOne(new Document()
      .append("id", "org-netman")
      .append("name", "Netman")
      .append("meta", defaultMeta())
      .append("groups", Lists.newArrayList(
        new Document("id", "netman-infrastructure-tracks"),
        new Document("id", "netman-infrastructure-hh")
      ))
      .append("tags", Lists.newArrayList("netman")));
    groups.insertOne(new Document()
      .append("id", "netman-infrastructure-tracks")
      .append("name", "Netman nätverksutrustning på banor")
      .append("meta", defaultMeta())
      .append("tags", Lists.newArrayList("switches", "routers")));
    groups.insertOne(new Document()
      .append("id", "netman-infrastructure-hh")
      .append("name", "Netman nätverksutrustning hästsportenshus")
      .append("meta", defaultMeta())
      .append("tags", Lists.newArrayList("switches", "routers")));
    groups.insertOne(new Document()
      .append("id", "org-sysman-linux")
      .append("name", "Sysman Linux")
      .append("meta", defaultMeta()));
  }

  private static void createApplications(MongoCollection<Document> applications) {

    applications.insertOne(new Document()
      .append("id", "atg-web")
      .append("name", "atg.se Web")
      .append("version", "1.0.0")
      .append("group", "atg-web")
      .append("description", "This application runs the atg.se website")
      .append("meta", defaultMeta())
      .append("attributes", new Document().append("server", new Document().append("name", "dropwizard").append("version", "0.9.1"))));
    applications.insertOne(new Document()
      .append("id", "atg-service-info")
      .append("name", "ATG Service Info")
      .append("version", "2.1.0-feature1")
      .append("group", "atg-service-info")
      .append("description", "The info service layer.")
      .append("meta", defaultMeta()));
    applications.insertOne(new Document()
      .append("id", "atg-service-info-cache")
      .append("name", "ATG Service Info Cache")
      .append("version", "2.4.0-feature1")
      .append("group", "atg-service-info")
      .append("description", "The info cache supporting the info service layer with cache service.")
      .append("meta", defaultMeta()));
    applications.insertOne(new Document()
      .append("id", "atg-service-betting")
      .append("name", "ATG Service Betting")
      .append("version", "2.3.0-feature1")
      .append("group", "atg-service-betting")
      .append("description", "The betting service layer.")
      .append("meta", defaultMeta()));
    applications.insertOne(new Document()
      .append("id", "tillsammans-service")
      .append("name", "Tillsammans Service")
      .append("version", "5.0")
      .append("group", "tillsammans")
      .append("description", "The service layer supporting the Tillsammans website")
      .append("meta", defaultMeta()));
    applications.insertOne(new Document()
      .append("id", "tillsammans-web")
      .append("name", "Tillsammans Web")
      .append("version", "1.73.0")
      .append("group", "tillsammans")
      .append("description", "This application runs the Tillsammans website")
      .append("meta", defaultMeta()));
  }

  private static void createServers(MongoCollection<Document> servers) {

    for (int server = 1; server < 9; ++server) {
      for (int env = 1; env < 5; ++env) {

        final Document serverDoc = Document.parse(
          "{\"hostname\": \"vltma" + server + "\"," +
            "\"fqdn\": \"vltma" + server + ".test" + env + ".hh.atg.se\"," +
            "\"environment\": \"test" + env + "\"," +
            "\"description\": \"Runs all important applications.\n1. Atg service layer betting\n2. Atg service layer info\"" +
            "\"os\": {" +
              "\"name\": \"RedHat Enterprise Linux\"," +
              "\"type\": " + ((server % 2 == 0) ? "\"Linux\"" : "\"Windows\",") +
              "\"version\": \"6.2\"" +
            "}," +
            "\"network\": {" +
              "\"ipv4Address\": \"10.0.0.1\"," +
              "\"attributes\": {" +
              "\"test\": 1" +
            "}" +
          "}}")
          .append("deployments", Lists.newArrayList(
            new Document().append("applicationId", "atg-service-betting").append("version", "1.0.0"),
            new Document().append("applicationId", "tillsammans-service").append("version", "0.0.1-patch2")
          )).append("attributes", new Document()
            .append("Param1", "Value1")
            .append("Param2", new Document()
              .append("Param3", "Value3")
            ).append("env-id", env)
            .append("server-id", server)
          ).append("meta", defaultMeta());

          final int version = server % 3 + 1;
          if (server % 2 == 0 && env % 2 != 0) {
            serverDoc.append("deployments", Lists.newArrayList(
              new Document().append("applicationId", "atg-service-betting").append("version", version + ".0.0"),
              new Document().append("applicationId", "tillsammans-service").append("version", "0.0.1-patch" + version)
            ));
          } else if (env % 2 == 0) {
            serverDoc.append("deployments", Lists.newArrayList(
              new Document().append("applicationId", "atg-web").append("version", version + ".0.0"),
              new Document().append("applicationId", "tillsammans-web").append("version", "0.0.1-patch" + version)
            ));
          }
        servers.insertOne(serverDoc);
      }
    }

    final Document qaServer = createServer("qa");
    qaServer.append("deployments", Lists.newArrayList(
      new Document().append("applicationId", "tillsammans-web").append("version", "1.0.0"),
      new Document().append("applicationId", "tillsammans-service").append("version", "2.0.0")
    ));
    servers.insertOne(qaServer);

    final Document stageServer = createServer("stage");
    stageServer.append("deployments", Lists.newArrayList(
      new Document().append("applicationId", "tillsammans-web").append("version", "1.0.0"),
      new Document().append("applicationId", "tillsammans-service").append("version", "2.0.0")
    ));
    servers.insertOne(stageServer);

    final Document prodServer = createServer("prod");
    prodServer.append("deployments", Lists.newArrayList(
      new Document().append("applicationId", "tillsammans-web").append("version", "1.0.0"),
      new Document().append("applicationId", "tillsammans-service").append("version", "2.0.0")
    ));
    servers.insertOne(prodServer);

    final Document internalprodServer = createServer("internalprod");
    internalprodServer.append("deployments", Lists.newArrayList(
      new Document().append("applicationId", "tillsammans-web").append("version", "1.0.0"),
      new Document().append("applicationId", "tillsammans-service").append("version", "2.0.0")
    ));
    servers.insertOne(internalprodServer);

    servers.updateOne(Filters.eq("fqdn", "vltma1.test1.hh.atg.se"), Updates.set("description", "Denna server tillhör Tillsammans."));
    servers.updateOne(Filters.eq("fqdn", "vltma2.test1.hh.atg.se"), Updates.set("description", "Kör både atg.se och atg-service precis som vltma1"));
    servers.updateOne(Filters.eq("fqdn", "vltma1.test2.hh.atg.se"), Updates.set("description", "POB, Jira och Tillsammans i test2 och test1"));
    servers.updateOne(Filters.eq("fqdn", "vltma2.test2.hh.atg.se"), Updates.set("description", "Go-servern är en del av leveranssystemet."));
  }

  private static Document createServer(String env) {
    return Document.parse(
      "{\"hostname\": \"vltma1\"," +
        "\"fqdn\": \"vltma1." + env + ".hh.atg.se\"," +
        "\"environment\": \"" + env + "\"," +
        "\"os\": {" +
          "\"name\": \"RedHat Enterprise Linux\"," +
          "\"type\": \"Linux\"" +
          "\"version\": \"6.2\"" +
        "}," +
        "\"network\": {" +
          "\"ipv4Address\": \"10.0.0.1\"," +
      "}}")
      .append("meta", defaultMeta());
  }

  private static Document defaultMeta() {
    return JsonHelper.addMetaForCreate("{}", "testdata").get("meta", Document.class);
  }
}

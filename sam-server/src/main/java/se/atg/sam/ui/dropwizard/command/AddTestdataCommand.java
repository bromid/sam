package se.atg.sam.ui.dropwizard.command;

import org.bson.Document;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import se.atg.sam.dao.Collections;
import se.atg.sam.helpers.JsonHelper;
import se.atg.sam.ui.dropwizard.configuration.SamConfiguration;

public class AddTestdataCommand extends EnvironmentCommand<SamConfiguration> {

  private Application<SamConfiguration> application;

  public AddTestdataCommand(Application<SamConfiguration> application) {
    super(application, "dbtestdata", "Add testdata to the database");
    this.application = application;
  }

  @Override
  protected void run(Environment environment, Namespace namespace, SamConfiguration configuration) throws Exception {

    final MongoDatabase database = configuration.getDbConnectionFactory().getDatabase(environment.lifecycle());

    final MongoCollection<Document> servers = database.getCollection(Collections.SERVERS);
    servers.drop();

    final MongoCollection<Document> applications = database.getCollection(Collections.APPLICATIONS);
    applications.drop();

    final MongoCollection<Document> groups = database.getCollection(Collections.GROUPS);
    groups.drop();

    final MongoCollection<Document> assets = database.getCollection(Collections.ASSETS);
    assets.drop();

    // Add indexes
    new CreateDatabaseCommand(application).run(environment, namespace, configuration);

    createServers(servers);
    createApplications(applications);
    createGroups(groups);
    createAssets(assets);
  }

  private static void createAssets(MongoCollection<Document> assets) {

    assets.insertOne(new Document()
      .append("id", "core-switch-hh-1")
      .append("name", "Core switch 1")
      .append("description", "Nexus 3423 ACI ver2")
      .append("group", "netman-infrastructure")
      .append("meta", defaultMeta()));
    assets.insertOne(new Document()
      .append("id", "core-switch-hh-2")
      .append("name", "Core switch 2")
      .append("description", "Nexus 3423 ACI ver2")
      .append("group", "netman-infrastructure")
      .append("meta", defaultMeta()));
  }

  private static void createGroups(MongoCollection<Document> groups) {

    groups.insertOne(new Document()
      .append("id", "webapps")
      .append("name", "All web applications")
      .append("meta", defaultMeta())
      .append("groups", Lists.newArrayList(
        new Document("id", "webapp1"),
        new Document("id", "webapp2"),
        new Document("id", "webapp3"),
        new Document("id", "webapp4")
      ))
      .append("tags", Lists.newArrayList("webapp")));

    groups.insertOne(new Document()
      .append("id", "webapp1")
      .append("name", "Webapp1")
      .append("meta", defaultMeta())
      .append("description", "www.webapp1.com")
      .append("tags", Lists.newArrayList("webapp", "weblogic")));

    groups.insertOne(new Document()
      .append("id", "webapp2")
      .append("name", "Webapp2")
      .append("meta", defaultMeta())
      .append("description", "www.webapp2.com")
      .append("tags", Lists.newArrayList("webapp", "weblogic", "system1")));

    groups.insertOne(new Document()
      .append("id", "webapp3")
      .append("name", "Webapp3")
      .append("meta", defaultMeta())
      .append("description", "www.webapp3.com")
      .append("tags", Lists.newArrayList("webapp", "weblogic", "system1")));

    groups.insertOne(new Document()
      .append("id", "webapp4")
      .append("name", "Webapp4")
      .append("meta", defaultMeta())
      .append("description", "www.webapp4.com")
      .append("groups", Lists.newArrayList(
        new Document("id", "webapp4-service"),
        new Document("id", "webapp4-web")
      ))
      .append("tags", Lists.newArrayList("webapp")));
    groups.insertOne(new Document()
      .append("id", "webapp4-service")
      .append("name", "Webapp4 Service")
      .append("meta", defaultMeta()));
    groups.insertOne(new Document()
      .append("id", "webapp4-web")
      .append("name", "Webapp4 Web")
      .append("meta", defaultMeta()));

    groups.insertOne(new Document()
      .append("id", "org-it")
      .append("name", "IT")
      .append("meta", defaultMeta())
      .append("groups", Lists.newArrayList(
        new Document("id", "org-it-dev"),
        new Document("id", "org-it-prod")))
      .append("tags", Lists.newArrayList("webapp")));
    groups.insertOne(new Document()
      .append("id", "org-it-dev")
      .append("name", "IT Development")
      .append("meta", defaultMeta())
      .append("groups", Lists.newArrayList(new Document("id", "webapp1"), new Document("id", "webapp2"))));
    groups.insertOne(new Document()
      .append("id", "org-it-prod")
      .append("name", "IT Production operations")
      .append("meta", defaultMeta())
      .append("groups", Lists.newArrayList(new Document("id", "org-netman"), new Document("id", "org-sysman-linux"))));
    groups.insertOne(new Document()
      .append("id", "org-netman")
      .append("name", "Network manager")
      .append("meta", defaultMeta())
      .append("groups", Lists.newArrayList(
        new Document("id", "netman-infrastructure")
      ))
      .append("tags", Lists.newArrayList("netman")));
    groups.insertOne(new Document()
      .append("id", "netman-infrastructure")
      .append("name", "Network manager network gear")
      .append("meta", defaultMeta())
      .append("tags", Lists.newArrayList("switches", "routers")));
    groups.insertOne(new Document()
      .append("id", "org-sysman-linux")
      .append("name", "System manager Linux")
      .append("meta", defaultMeta()));
  }

  private static void createApplications(MongoCollection<Document> applications) {

    applications.insertOne(new Document()
      .append("id", "webapp4-web")
      .append("name", "Webapp4 Web")
      .append("version", "1.0.0")
      .append("group", "webapp4-web")
      .append("description", "This application runs the most important website")
      .append("meta", defaultMeta())
      .append("attributes", new Document().append("server", new Document().append("name", "dropwizard").append("version", "0.9.1"))));
    applications.insertOne(new Document()
      .append("id", "webapp4-service")
      .append("name", "Webapp4")
      .append("version", "2.1.0-feature1")
      .append("group", "webapp4-service")
      .append("description", "Another service layer.")
      .append("meta", defaultMeta()));
    applications.insertOne(new Document()
      .append("id", "webapp3-service")
      .append("name", "Webapp3 Service")
      .append("version", "2.4.0-feature1")
      .append("group", "webapp3")
      .append("description", "Another service layer.")
      .append("meta", defaultMeta()));
    applications.insertOne(new Document()
      .append("id", "webapp2-service")
      .append("name", "Webapp2 Service")
      .append("version", "2.3.0-feature1")
      .append("group", "webapp2")
      .append("description", "Another service layer.")
      .append("meta", defaultMeta()));
    applications.insertOne(new Document()
      .append("id", "webapp1-service")
      .append("name", "Webapp1 Service")
      .append("version", "5.0")
      .append("group", "webapp1")
      .append("description", "The service layer supporting the Webapp1 website")
      .append("meta", defaultMeta()));
    applications.insertOne(new Document()
      .append("id", "webapp1-web")
      .append("name", "Webapp1 Web")
      .append("version", "1.73.0")
      .append("group", "webapp1")
      .append("description", "This application runs the Webapp1 website")
      .append("meta", defaultMeta()));
  }

  private static void createServers(MongoCollection<Document> servers) {

    for (int server = 1; server < 9; ++server) {
      for (int env = 1; env < 5; ++env) {

        final Document serverDoc = Document.parse(
          "{\"hostname\": \"vltweb" + server + "\"," +
            "\"fqdn\": \"vltweb" + server + ".test" + env + ".se\"," +
            "\"environment\": \"test" + env + "\"," +
            "\"description\": \"Runs all important applications.\n\"" +
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
            new Document().append("applicationId", "webapp3-service").append("version", "1.0.0"),
            new Document().append("applicationId", "webapp1-service").append("version", "0.0.1-patch2")
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
              new Document().append("applicationId", "webapp3-service").append("version", version + ".0.0"),
              new Document().append("applicationId", "webapp1-service").append("version", "0.0.1-patch" + version)
            ));
          } else if (env % 2 == 0) {
            serverDoc.append("deployments", Lists.newArrayList(
              new Document().append("applicationId", "webapp2-web").append("version", version + ".0.0"),
              new Document().append("applicationId", "webapp1-web").append("version", "0.0.1-patch" + version)
            ));
          }
        servers.insertOne(serverDoc);
      }
    }

    final Document qaServer = createServer("qa");
    qaServer.append("deployments", Lists.newArrayList(
      new Document().append("applicationId", "webapp1-web").append("version", "1.0.0"),
      new Document().append("applicationId", "webapp1-service").append("version", "2.0.0")
    ));
    servers.insertOne(qaServer);

    final Document stageServer = createServer("stage");
    stageServer.append("deployments", Lists.newArrayList(
      new Document().append("applicationId", "webapp1-web").append("version", "1.0.0"),
      new Document().append("applicationId", "webapp1-service").append("version", "2.0.0")
    ));
    servers.insertOne(stageServer);

    final Document prodServer = createServer("prod");
    prodServer.append("deployments", Lists.newArrayList(
      new Document().append("applicationId", "webapp1-web").append("version", "1.0.0"),
      new Document().append("applicationId", "webapp1-service").append("version", "2.0.0")
    ));
    servers.insertOne(prodServer);

    final Document internalprodServer = createServer("internalprod");
    internalprodServer.append("deployments", Lists.newArrayList(
      new Document().append("applicationId", "webapp1-web").append("version", "1.0.0"),
      new Document().append("applicationId", "webapp1-service").append("version", "2.0.0")
    ));
    servers.insertOne(internalprodServer);

    servers.updateOne(Filters.eq("fqdn", "vltweb1.test1.se"), Updates.set("description", "This server runs Webapp1."));
    servers.updateOne(Filters.eq("fqdn", "vltweb2.test1.se"), Updates.set("description", "This server runs Webapp1 and Webapp2"));
    servers.updateOne(Filters.eq("fqdn", "vltweb1.test2.se"), Updates.set("description", "This server runs Webapp3 and Webapp4"));
    servers.updateOne(Filters.eq("fqdn", "vltweb2.test2.se"), Updates.set("description", "This server runs Webapp3 and Webapp4"));
  }

  private static Document createServer(String env) {
    return Document.parse(
      "{\"hostname\": \"vltweb1\"," +
        "\"fqdn\": \"vltweb1." + env + ".se\"," +
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

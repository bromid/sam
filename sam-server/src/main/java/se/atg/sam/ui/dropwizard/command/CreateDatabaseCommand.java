package se.atg.sam.ui.dropwizard.command;

import static com.mongodb.client.model.Indexes.ascending;
import static com.mongodb.client.model.Indexes.compoundIndex;

import org.bson.Document;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import se.atg.sam.dao.Collections;
import se.atg.sam.ui.dropwizard.configuration.SamConfiguration;

public class CreateDatabaseCommand extends EnvironmentCommand<SamConfiguration> {

  public CreateDatabaseCommand(Application<SamConfiguration> application) {
    super(application, "dbcreate", "Create or update the db structure");
  }

  @Override
  public void run(Environment environment, Namespace namespace, SamConfiguration configuration) throws Exception {

    final MongoDatabase database = configuration.getDbConnectionFactory().getDatabase(environment.lifecycle());

    final MongoCollection<Document> servers = database.getCollection(Collections.SERVERS);
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
        compoundIndex(Indexes.text("hostname"), Indexes.text("environment"), Indexes.text("fqdn"), Indexes.text("description")),
        new IndexOptions().weights(new Document().append("hostname", 10).append("fqdn", 10).append("environment", 5)).defaultLanguage("sv")
      )
    ));

    final MongoCollection<Document> applications = database.getCollection(Collections.APPLICATIONS);
    applications.createIndexes(Lists.newArrayList(
      new IndexModel(
        ascending("id"),
        new IndexOptions().unique(true)
      ),
      new IndexModel(
        compoundIndex(Indexes.text("id"), Indexes.text("name"), Indexes.text("description")),
        new IndexOptions().weights(new Document().append("id", 10).append("name", 10)).defaultLanguage("sv")
      )
    ));

    final MongoCollection<Document> groups = database.getCollection(Collections.GROUPS);
    groups.createIndexes(Lists.newArrayList(
      new IndexModel(
        ascending("id"),
        new IndexOptions().unique(true)
      ),
      new IndexModel(
        ascending("tags")
      ),
      new IndexModel(
        compoundIndex(Indexes.text("id"), Indexes.text("name"), Indexes.text("description"), Indexes.text("tags")),
        new IndexOptions().weights(new Document().append("id", 10).append("name", 10).append("tags", 5)).defaultLanguage("sv")
      )
    ));

    final MongoCollection<Document> assets = database.getCollection(Collections.ASSETS);
    assets.createIndexes(Lists.newArrayList(
      new IndexModel(
        ascending("id"),
        new IndexOptions().unique(true)
      ),
      new IndexModel(
        compoundIndex(Indexes.text("id"), Indexes.text("name"), Indexes.text("description")),
        new IndexOptions().weights(new Document().append("id", 10).append("name", 10)).defaultLanguage("sv")
      )
    ));
  }
}

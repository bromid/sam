package se.atg.cmdb.ui.rest.integrationtest;

import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.client.MongoDatabase;

import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import se.atg.cmdb.helpers.JsonHelper;
import se.atg.cmdb.model.View;
import se.atg.cmdb.ui.dropwizard.CmdbConfiguration;
import se.atg.cmdb.ui.dropwizard.db.MongoDatabaseHealthCheck;
import se.atg.cmdb.ui.text.CreateDatabase;

public class TestCommand extends EnvironmentCommand<CmdbConfiguration> {

  static final java.util.logging.Logger HTTP_LOGGER = java.util.logging.Logger.getLogger(LoggingFilter.class.getName());
  static final Logger LOGGER = LoggerFactory.getLogger("integration-test");

  private Class<?>[] testClasses;
  private Optional<Description> testFilter;

  public TestCommand(Application<CmdbConfiguration> application, Optional<Description> testFilter, Class<?>... testClasses) {
    super(application, "test", "Runs junit tests");
    this.testFilter = testFilter;
    this.testClasses = testClasses;
  }

  @Override
  protected Class<CmdbConfiguration> getConfigurationClass() {
    return CmdbConfiguration.class;
  }

  @Override
  protected void run(Environment environment, Namespace namespace, CmdbConfiguration configuration) throws Exception {

    // Jackson configuration
    final ObjectMapper objectMapper = JsonHelper.configureObjectMapper(environment.getObjectMapper(), View.Api.class);

    // Jersey client configuration
    final Client client = new JerseyClientBuilder(environment)
      .using(configuration.getJerseyClientConfiguration())
      .withProvider(new LoggingFilter(HTTP_LOGGER, true))
      .withProvider(HttpAuthenticationFeature.basic("integration-test", "secret"))
      .build("atg_cmdb_integrationtest");

    // Guice injection
    final MongoDatabase database = configuration.getDbConnectionFactory().getDatabase(environment.lifecycle());
    final Injector injector = Guice.createInjector(new AbstractModule() {
      protected void configure() {
        bind(ObjectMapper.class).toInstance(objectMapper);
        bind(MongoDatabase.class).toInstance(database);
        bind(Client.class).toInstance(client);
        bind(WebTarget.class).toInstance(client.target(configuration.getTestEndpoint()));
      }
    });

    // Init database
    verifyDatabaseHealth(database);
    CreateDatabase.initDatabase(database);

    // Run junit tests
    final Result result = runTests(injector, testClasses, testFilter);
    if (!result.wasSuccessful()) {
      throw new RuntimeException("The tests were unsuccessful!");
    }
  }

  private static void verifyDatabaseHealth(MongoDatabase database) {

    final MongoDatabaseHealthCheck dbHealthCheck = new MongoDatabaseHealthCheck(database);
    final com.codahale.metrics.health.HealthCheck.Result dbHealth = dbHealthCheck.execute();
    if (!dbHealth.isHealthy()) {
      throw new RuntimeException("Failed to connect to database", dbHealth.getError());
    }
  }

  private static Result runTests(Injector injector, Class<?>[] testClasses, Optional<Description> testFilter) throws InitializationError {

    final JUnitCore junit = new JUnitCore();
    junit.addListener(new JUnitRunListener(LOGGER));

    final Request testRequest = Request.runner(new Suite(new GuiceInjectionJUnitRunner(injector), testClasses));
    if (testFilter.isPresent()) {
      return junit.run(testRequest.filterWith(testFilter.get()));
    } else {
      return junit.run(testRequest);
    }
  }
}

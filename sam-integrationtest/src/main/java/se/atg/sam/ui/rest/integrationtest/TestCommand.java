package se.atg.sam.ui.rest.integrationtest;

import java.util.Optional;
import java.util.logging.Level;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.logging.LoggingFeature.Verbosity;
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
import com.google.inject.name.Names;
import com.mongodb.client.MongoDatabase;

import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import se.atg.sam.helpers.JsonHelper;
import se.atg.sam.model.View;
import se.atg.sam.ui.dropwizard.auth.ClientTokenFilter;
import se.atg.sam.ui.dropwizard.command.CreateDatabaseCommand;
import se.atg.sam.ui.dropwizard.configuration.SamConfiguration;
import se.atg.sam.ui.dropwizard.db.MongoDatabaseHealthCheck;

public class TestCommand extends EnvironmentCommand<SamConfiguration> {

  private static final java.util.logging.Logger HTTP_LOGGER = java.util.logging.Logger.getLogger(LoggingFeature.class.getName());
  private static final Logger LOGGER = LoggerFactory.getLogger("integration-test");

  private Class<?>[] testClasses;
  private Optional<Description> testFilter;
  private Application<SamConfiguration> application;

  public TestCommand(Application<SamConfiguration> application, Optional<Description> testFilter, Class<?>... testClasses) {
    super(application, "test", "Runs junit tests");
    this.testFilter = testFilter;
    this.testClasses = testClasses;
    this.application = application;
  }

  @Override
  protected Class<SamConfiguration> getConfigurationClass() {
    return SamConfiguration.class;
  }

  @Override
  protected void run(Environment environment, Namespace namespace, SamConfiguration configuration) throws Exception {

    // Jackson configuration
    final ObjectMapper objectMapper = JsonHelper.configureObjectMapper(environment.getObjectMapper(), View.Api.class);

    // Jersey clients
    final String username = "integration-test";
    final Client anonymousClient = createRestClientBuilder(environment, configuration)
      .build("integrationtest-anonymous-client");
    final Client tokenAuthClient = createRestClientBuilder(environment, configuration)
      .withProvider(ClientTokenFilter.feature(username, configuration.getOAuthConfiguration()))
      .build("integrationtest-token-client");
    final Client basicAuthClient = createRestClientBuilder(environment, configuration)
      .withProvider(HttpAuthenticationFeature.basic(username, "secret"))
      .build("integrationtest-basic-client");

    // Guice injection
    final MongoDatabase database = configuration.getDbConnectionFactory().getDatabase(environment.lifecycle());
    final Injector injector = Guice.createInjector(new AbstractModule() {
      protected void configure() {
        bind(ObjectMapper.class).toInstance(objectMapper);
        bind(MongoDatabase.class).toInstance(database);
        bind(Client.class).toInstance(tokenAuthClient);
        bind(Client.class).annotatedWith(Names.named("basic")).toInstance(basicAuthClient);
        bind(Client.class).annotatedWith(Names.named("anonymous")).toInstance(anonymousClient);
        bind(WebTarget.class).toInstance(tokenAuthClient.target(configuration.getTestEndpoint()));
      }
    });

    // Init database
    database.drop();
    verifyDatabaseHealth(database);
    new CreateDatabaseCommand(application).run(environment, namespace, configuration);

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

  private static JerseyClientBuilder createRestClientBuilder(Environment environment, SamConfiguration configuration) {

    final JerseyClientBuilder builder = new JerseyClientBuilder(environment).using(configuration.getJerseyClientConfiguration());
    if (configuration.isLogRequests()) {
      builder.withProvider(new LoggingFeature(HTTP_LOGGER, Level.INFO, Verbosity.PAYLOAD_ANY, LoggingFeature.DEFAULT_MAX_ENTITY_SIZE));
    }
    return builder;
  }
}

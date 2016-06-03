package se.atg.cmdb.ui.rest.integrationtest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runners.Suite;
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
import se.atg.cmdb.helpers.JSONHelper;
import se.atg.cmdb.ui.dropwizard.CMDBConfiguration;

public class TestCommand extends EnvironmentCommand<CMDBConfiguration> {

	static final java.util.logging.Logger HTTP_LOGGER = java.util.logging.Logger.getLogger(LoggingFilter.class.getName());
	static final Logger LOGGER = LoggerFactory.getLogger(TestCommand.class);

	private Class<?>[] testClasses;

	public TestCommand(Application<CMDBConfiguration> application, Class<?>... testClasses) {
		super(application, "test", "Runs junit tests");
		this.testClasses = testClasses;
	}

	@Override
	protected Class<CMDBConfiguration> getConfigurationClass() {
		return CMDBConfiguration.class;
	}

	@Override
	protected void run(Environment environment, Namespace namespace, CMDBConfiguration configuration) throws Exception {

		// Jackson configuration
		final ObjectMapper objectMapper = JSONHelper.configureObjectMapper(environment.getObjectMapper());

		// Jersey client configuration
		final Client client = new JerseyClientBuilder(environment)
			.using(configuration.getJerseyClientConfiguration())
			.withProvider(new LoggingFilter(HTTP_LOGGER, true))
			.withProvider(HttpAuthenticationFeature.basic("integration-test", "secret"))
			.build("atg_cmdb_integrationtest");

		// Guice injection
		final MongoDatabase database = configuration.getDBConnectionFactory().getDatabase(environment.lifecycle());
		final Injector injector = Guice.createInjector(new AbstractModule() {
			protected void configure() {
				bind(ObjectMapper.class).toInstance(objectMapper);
				bind(MongoDatabase.class).toInstance(database);
				bind(Client.class).toInstance(client);
				bind(WebTarget.class).toInstance(
					client.target(configuration.getTestEndpoint())
				);
			}
		});

		// Run junit tests
		final JUnitCore junit = new JUnitCore();
		junit.addListener(new JUnitRunListener(LOGGER));

		final Suite testSuite = new Suite(new GuiceInjectionJUnitRunner(injector), testClasses);
		final Result result = junit.run(Request.runner(testSuite));

		if (!result.wasSuccessful()) {
			throw new RuntimeException("The tests were unsuccessful!");
		}
	}
}

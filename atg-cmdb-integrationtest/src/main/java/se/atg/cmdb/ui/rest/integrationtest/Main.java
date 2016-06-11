package se.atg.cmdb.ui.rest.integrationtest;

import java.util.Optional;

import org.junit.runner.Description;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import se.atg.cmdb.ui.dropwizard.CMDBConfiguration;
import se.atg.cmdb.ui.rest.ApplicationIntegrationTest;
import se.atg.cmdb.ui.rest.AssetIntegrationTest;
import se.atg.cmdb.ui.rest.GroupIntegrationTest;
import se.atg.cmdb.ui.rest.SearchIntegrationTest;
import se.atg.cmdb.ui.rest.ServerIntegrationTest;

public class Main extends Application<CMDBConfiguration> {

	public void run(CMDBConfiguration configuration, Environment environment) throws Exception {}
	public static void main(String[] args) throws Exception {
		new Main().run(args);
	}

	@Override
	public void initialize(Bootstrap<CMDBConfiguration> bootstrap) {
		bootstrap.addCommand(new TestCommand(this,
			Optional.empty(),
			ServerIntegrationTest.class,
			GroupIntegrationTest.class,
			ApplicationIntegrationTest.class,
			AssetIntegrationTest.class,
			SearchIntegrationTest.class
		));
	}

	public static Optional<Description> testDescription(Class<?> testClass, String testMethod) {
		return Optional.of(Description.createTestDescription(testClass, testMethod));
	}
}

package se.atg.cmdb.ui.rest.integrationtest;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import se.atg.cmdb.ui.dropwizard.CMDBConfiguration;
import se.atg.cmdb.ui.rest.GroupIntegrationTest;
import se.atg.cmdb.ui.rest.ServerIntegrationTest;

public class Main extends Application<CMDBConfiguration> {

	public void run(CMDBConfiguration configuration, Environment environment) throws Exception {}
	public static void main(String[] args) throws Exception {
		new Main().run(args);
	}

	@Override
	public void initialize(Bootstrap<CMDBConfiguration> bootstrap) {
		bootstrap.addCommand(new TestCommand(this,
			ServerIntegrationTest.class,
			GroupIntegrationTest.class
		));
	}
}

package se.atg.sam.ui.rest.integrationtest;

import java.util.Optional;

import org.junit.runner.Description;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import se.atg.sam.ui.dropwizard.configuration.SamConfiguration;
import se.atg.sam.ui.rest.ApplicationIntegrationTest;
import se.atg.sam.ui.rest.AssetIntegrationTest;
import se.atg.sam.ui.rest.GroupIntegrationTest;
import se.atg.sam.ui.rest.SearchIntegrationTest;
import se.atg.sam.ui.rest.ServerIntegrationTest;
import se.atg.sam.ui.rest.SmokeTest;

public class Main extends Application<SamConfiguration> {

  @Override
  public void run(SamConfiguration configuration, Environment environment) throws Exception {
  }

  public static void main(String[] args) throws Exception {
    new Main().run(args);
  }

  @Override
  public void initialize(Bootstrap<SamConfiguration> bootstrap) {

    bootstrap.setConfigurationSourceProvider(
      new SubstitutingSourceProvider(
        bootstrap.getConfigurationSourceProvider(),
        new EnvironmentVariableSubstitutor()
      )
    );

    bootstrap.addCommand(new TestCommand(this,
      Optional.empty(),
      //testDescription(AssetIntegrationTest.class, "newAssetMustHaveId"),
      SmokeTest.class,
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

package se.atg.cmdb.ui.rest.integrationtest;

import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.RunnerBuilder;

import com.google.inject.Injector;

public class GuiceInjectionJUnitRunner extends RunnerBuilder {

  private final Injector injector;

  public GuiceInjectionJUnitRunner(Injector injector) {
    this.injector = injector;
  }

  @Override
  public Runner runnerForClass(Class<?> testClass) throws Throwable {
    return new BlockJUnit4ClassRunner(testClass) {
        public Object createTest() throws Exception {
            final Object obj = super.createTest();
            injector.injectMembers(obj);
            return obj;
        }
    };
  }
}

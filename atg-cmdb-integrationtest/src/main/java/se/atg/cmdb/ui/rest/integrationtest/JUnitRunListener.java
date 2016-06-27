package se.atg.cmdb.ui.rest.integrationtest;

import java.text.NumberFormat;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;

public class JUnitRunListener extends RunListener {

  private Logger logger;

  public JUnitRunListener(Logger logger) {
    this.logger = logger;
  }

    public void testStarted(Description desc) throws Exception {
      logger.info("Started " + desc);
    }

    public void testRunFinished(Result result) {

      logger.info("Time: " + elapsedTimeAsString(result.getRunTime()));

      if (result.wasSuccessful()) {
        logger.info("OK (" + result.getRunCount() + " test" + (result.getRunCount() == 1 ? "" : "s") + ")");
      } else {

        final List<Failure> failures = result.getFailures();
        if (failures.size() == 1) {
          logger.error("There was " + failures.size() + " failure:");
        } else {
          logger.error("There were " + failures.size() + " failures:");
        }

        for (Failure failure : failures) {
          logger.error(failure.getTestHeader());
          logger.error(failure.getTrace());
        }
        logger.info("FAILURE Tests run: " + result.getRunCount() + ",  Failures: " + result.getFailureCount());
      }
    }

    /**
     * Returns the formatted string of the elapsed time. Duplicated from
     * BaseTestRunner. Fix it.
     */
    protected String elapsedTimeAsString(long runTime) {
        return NumberFormat.getInstance().format((double) runTime / 1000);
    }
}

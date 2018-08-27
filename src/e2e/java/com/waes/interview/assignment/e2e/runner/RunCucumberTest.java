package com.waes.interview.assignment.e2e.runner;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.runner.RunWith;

import static com.waes.interview.assignment.e2e.TestContext.CONTEXT;

/**
 * Cucumber's JUnit based tests runner definition.
 * <p>
 * It is the entry point for E2E tests.
 *
 * @author Juan Krzemien
 */

@RunWith(Cucumber.class)
@CucumberOptions(
    glue = "com.waes.interview.assignment.e2e",
    features = "src/e2e/resources/features",
    plugin = {
        "pretty",
        "json:build/reports/cucumber-report.json",
        "html:build/reports/cucumber-html"
    }
)
public class RunCucumberTest {

  @AfterClass
  public static void afterSuite() {
    CONTEXT.remove();
  }

}
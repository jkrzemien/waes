package com.waes.interview.assignment.e2e.steps;

import com.waes.interview.assignment.e2e.models.Difference;
import com.waes.interview.assignment.e2e.models.DifferencesRequest;
import com.waes.interview.assignment.e2e.models.DifferencesResponse;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

import static com.waes.interview.assignment.e2e.TestContext.CONTEXT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Cucumber step definitions for E2E tests written in Gherkin syntax in resources/features folder.
 *
 * @author Juan Krzemien
 */
public class DifferencesSteps {

  @Given("^I define file '(.+?)' as input for '(/.+?)' endpoint with ID (.+?)$")
  public void iDefineFileAsInputForEndpointWithID(String fileName, String endpoint, String id) throws Throwable {
    String base64Data = getBase64FromFile(fileName);
    DifferencesRequest request = new DifferencesRequest(base64Data);
    CONTEXT.setResponse(CONTEXT.getSpec().given().body(request).pathParam("id", id).post(endpoint));
  }

  @When("^I request differences to endpoint '(/.+?)' for provided files with ID (.+?)$")
  public void iRequestDifferencesToEndpointFromProvidedFilesWithID(String endpoint, String id) {
    CONTEXT.setResponse(CONTEXT.getSpec().given().pathParam("id", id).get(endpoint));
  }

  @Then("^Response should have a (\\d+) code$")
  public void responseShouldHaveCode(int statusCode) {
    CONTEXT.getResponse().then().statusCode(statusCode);
  }

  @Then("^Response should have message '(.+?)'$")
  public void responseShouldHaveMessage(String message) {
    DifferencesResponse response = getResponseBody();
    assertThat("Differences should match expectation", response.getMessage(), is(message));
  }

  @And("^Response should have the following differences:$")
  public void responseShouldHaveTheFollowingDifferences(DataTable table) {
    List<Object> expectedDifferences = table.asList(Difference.class);
    DifferencesResponse response = getResponseBody();
    List<Difference> differences = response.getDifferences();
    assertThat("Differences should match expectation", differences, is(expectedDifferences));
  }

  @And("^Response should have no differences$")
  public void responseShouldHaveNoDifferences() {
    DifferencesResponse response = getResponseBody();
    assertThat("Differences should be empty", response.getDifferences().isEmpty(), is(true));
  }

  /**
   * Helper method to convert file contents into Base64 strings.
   * <p>
   * This kind of reusable methods should be part of the test automation framework used to perform testing.
   *
   * @param fileName File to convert to Base64
   * @return A Base64 representation of provided file's content
   */
  private String getBase64FromFile(String fileName) throws URISyntaxException, IOException {
    byte[] data = Files.readAllBytes(Paths.get(getClass().getResource("/" + fileName).toURI()));
    return Base64.getEncoder().encodeToString(data);
  }

  /**
   * Helper method to retrieve current RestAssured response from test context.
   * Deserialize it and asserts that it is present (not null), since it required for every step.
   *
   * @return a deserialized instance of RestAssured response from test context.
   */
  private DifferencesResponse getResponseBody() {
    DifferencesResponse response = CONTEXT.getResponse().getBody().as(DifferencesResponse.class);
    assertThat("Response is present", response, is(notNullValue()));
    return response;
  }

}

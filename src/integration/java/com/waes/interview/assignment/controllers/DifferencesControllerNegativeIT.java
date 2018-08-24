package com.waes.interview.assignment.controllers;

import com.waes.interview.assignment.models.DifferencesResponse;
import org.junit.Before;
import org.junit.Test;

import static com.waes.interview.assignment.controllers.AbstractControllerIntegrationTest.DiffEndpoint.*;
import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This integration test suite deals with negative test cases of {@link DifferencesController DifferencesController} endpoints.
 *
 * @author Juan Krzemien
 */
public class DifferencesControllerNegativeIT extends AbstractControllerIntegrationTest {

  private static final DifferencesResponse DONE = new DifferencesResponse("Done");
  private static final DifferencesResponse INVALID = new DifferencesResponse("Invalid Base64 payload!");
  private static final DifferencesResponse WRONG_ORDER = new DifferencesResponse("Must call endpoint /left before calling endpoint /right");

  private String id;

  /**
   * Generate a random ID per test
   */
  @Before
  public void setUp() {
    this.id = randomUUID().toString();
  }

  @Test
  public void setLeftContentNoSize() throws Exception {
    String noData = createBase64JsonData(new byte[0]);

    DifferencesResponse response = doPostAndReturn(ENDPOINT_LEFT.with(id), noData, status().isBadRequest(), DifferencesResponse.class);

    assertThat("Response matches expectation", response, is(INVALID));

  }

  @Test
  public void setRightContentWithoutLeft() throws Exception {

    DifferencesResponse response = doPostAndReturn(ENDPOINT_RIGHT.with(id), createBase64JsonData(), status().isBadRequest(), DifferencesResponse.class);

    assertThat("Response matches expectation", response, is(WRONG_ORDER));

  }

  @Test
  public void setRightContentNoSize() throws Exception {

    DifferencesResponse response = doPostAndReturn(ENDPOINT_LEFT.with(id), createBase64JsonData(), DifferencesResponse.class);

    assertThat("Response matches expectation", response, is(DONE));

    String noData = createBase64JsonData(new byte[0]);

    response = doPostAndReturn(ENDPOINT_LEFT.with(id), noData, status().isBadRequest(), DifferencesResponse.class);

    assertThat("Response matches expectation", response, is(INVALID));

  }

  @Test
  public void doDiffNoComparison() throws Exception {

    doGet(ENDPOINT_DIFF.with(id))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(format("No comparison pending for ID [%s]", id))))
        .andExpect(jsonPath("$", not(hasKey("differences"))));

  }

  @Test
  public void doDiffNoRightOperand() throws Exception {

    DifferencesResponse response = doPostAndReturn(ENDPOINT_LEFT.with(id), createBase64JsonData(), DifferencesResponse.class);

    assertThat("Response matches expectation", response, is(DONE));

    doGet(ENDPOINT_DIFF.with(id))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Cannot operate with either operand (Left/Right) missing!")))
        .andExpect(jsonPath("$", not(hasKey("differences"))));

  }

}
package com.waes.interview.assignment.controllers;

import com.waes.interview.assignment.models.DifferencesResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static com.waes.interview.assignment.controllers.AbstractControllerIntegrationTest.DiffEndpoint.*;
import static java.lang.String.format;
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
  private static final DifferencesResponse DATA_INTEGRITY = new DifferencesResponse("Payload cannot exceed 1 MB in size!");

  private Long id;

  /**
   * Generate a random ID per test
   */
  @Before
  public void setUp() {
    this.id = new Random().nextLong();
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
        .andExpect(jsonPath("$.message", is(format("No comparison pending for ID [%s]", id))))
        .andExpect(jsonPath("$", not(hasKey("differences"))));

  }

  @Test
  public void payloadExceeds1MBLeftOperand() throws Exception {

    final byte[] LARGE_BYTE_ARRAY = new byte[1024 * 1024 + 1];

    final String data = createBase64JsonData(LARGE_BYTE_ARRAY);

    DifferencesResponse response = doPostAndReturn(ENDPOINT_LEFT.with(id), data, status().isBadRequest(), DifferencesResponse.class);

    assertThat("Response matches expectation", response, is(DATA_INTEGRITY));

  }

  @Test
  public void payloadExceeds1MBRightOperand() throws Exception {

    final byte[] LARGE_BYTE_ARRAY = new byte[1024 * 1024 + 1];

    final String data = createBase64JsonData(LARGE_BYTE_ARRAY);

    DifferencesResponse response = doPostAndReturn(ENDPOINT_LEFT.with(id), createBase64JsonData(), DifferencesResponse.class);

    assertThat("Response matches expectation", response, is(DONE));

    response = doPostAndReturn(ENDPOINT_RIGHT.with(id), data, status().isBadRequest(), DifferencesResponse.class);

    assertThat("Response matches expectation", response, is(DATA_INTEGRITY));

  }

}
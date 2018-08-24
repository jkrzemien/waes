package com.waes.interview.assignment.controllers;

import com.waes.interview.assignment.models.Difference;
import com.waes.interview.assignment.models.DifferencesResponse;
import org.junit.Before;
import org.junit.Test;

import static com.waes.interview.assignment.controllers.AbstractControllerIntegrationTest.DiffEndpoint.*;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * This integration test suite deals with test cases for {@link DifferencesController DifferencesController} endpoints.
 *
 * @author Juan Krzemien
 */
public class DifferencesControllerIT extends AbstractControllerIntegrationTest {

  private static final DifferencesResponse DONE = new DifferencesResponse("Done");
  private static final DifferencesResponse EQUALS = new DifferencesResponse("Byte arrays are equal!");
  private static final DifferencesResponse NOT_EQUALS = new DifferencesResponse("Byte arrays are NOT equal!");

  private String id;

  @Before
  public void setUp() {
    this.id = randomUUID().toString();
  }

  @Test
  public void doDiffEquals() throws Exception {


    String data = createBase64JsonData();

    DifferencesResponse response = doPostAndReturn(ENDPOINT_LEFT.with(id), data, DifferencesResponse.class);

    assertThat("Response matches expectation", response, is(DONE));

    response = doPostAndReturn(ENDPOINT_RIGHT.with(id), data, DifferencesResponse.class);

    assertThat("Response matches expectation", response, is(DONE));

    DifferencesResponse differences = doGetAndReturn(ENDPOINT_DIFF.with(id), DifferencesResponse.class);

    assertThat("Response matches expectation", differences, is(EQUALS));

  }

  @Test
  public void doDiffNoEqualsDueToSize() throws Exception {

    DifferencesResponse response = doPostAndReturn(ENDPOINT_LEFT.with(id), createBase64JsonData(), DifferencesResponse.class);

    assertThat("Response matches expectation", response, is(DONE));

    response = doPostAndReturn(ENDPOINT_RIGHT.with(id), createBase64JsonData(new byte[10]), DifferencesResponse.class);

    assertThat("Response matches expectation", response, is(DONE));

    DifferencesResponse differences = doGetAndReturn(ENDPOINT_DIFF.with(id), DifferencesResponse.class);

    assertThat("Message is as expected", differences, is(NOT_EQUALS));

  }

  @Test
  public void doDiffNoEquals() throws Exception {

    final byte[] LEFT_KNOWN_BYTE_ARRAY = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes();
    final byte[] RIGHT_KNOWN_BYTE_ARRAY = "ABCDZFGHXJKLMNWPQRSTUVOIYE".getBytes();

    final String leftData = createBase64JsonData(LEFT_KNOWN_BYTE_ARRAY);
    final String rightData = createBase64JsonData(RIGHT_KNOWN_BYTE_ARRAY);

    DifferencesResponse response = doPostAndReturn(ENDPOINT_LEFT.with(id), leftData, DifferencesResponse.class);

    assertThat("Response matches expectation", response, is(DONE));

    response = doPostAndReturn(ENDPOINT_RIGHT.with(id), rightData, DifferencesResponse.class);

    assertThat("Response matches expectation", response, is(DONE));

    DifferencesResponse expectation = new DifferencesResponse("Byte arrays are NOT equal!", asList(
        new Difference(4, 1),
        new Difference(8, 1),
        new Difference(14, 1),
        new Difference(22, 2),
        new Difference(25, 1)
    ));

    DifferencesResponse differences = doGetAndReturn(ENDPOINT_DIFF.with(id), DifferencesResponse.class);

    assertThat("Message is as expected", differences, is(expectation));

  }

}
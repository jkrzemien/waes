package com.waes.interview.assignment.controllers;

import com.waes.interview.assignment.differentiator.Differentiable;
import com.waes.interview.assignment.models.*;
import com.waes.interview.assignment.repositories.LeftOperandsRepository;
import com.waes.interview.assignment.repositories.RightOperandsRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Base64;
import java.util.List;
import java.util.Random;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

/**
 * Unit test suite for {@link DifferencesController DifferencesController} methods.
 * <p>
 * Most of {@link DifferencesController DifferencesController} code gets tested during integration tests already
 * but I wanted to write this class also to be able to show usage of mock dependencies injection.
 *
 * @author Juan Krzemien
 */
@RunWith(MockitoJUnitRunner.class)
public class DifferencesControllerTest {

  /**
   * Mock dependencies
   */
  @Mock
  private LeftOperandsRepository leftRepository;

  @Mock
  private RightOperandsRepository rightRepository;

  @Mock
  private Differentiable<byte[]> differentiable;

  /**
   * Class members
   */
  private Long id;
  private DifferencesRequest request;

  /**
   * Class under test
   */
  private DifferencesController differencesController;

  /**
   * Initializes before each test:
   * - {@link DifferencesController DifferencesController} with mock dependencies
   * - ID
   * - Data
   */
  @Before
  public void setUp() {
    // Reset mocks state
    reset(leftRepository, rightRepository, differentiable);

    this.differencesController = new DifferencesController(leftRepository, rightRepository, differentiable);
    this.id = 1L;
    this.request = new DifferencesRequest(createBase64Data());

  }

  /**
   * Resets mock dependencies state after each test
   */
  @After
  public void tearDown() {
    // Verify that no other dependencies were called.
    verifyNoMoreInteractions(leftRepository, rightRepository, differentiable);
  }

  @Test
  public void setLeftOperand() {

    // Invoke method to test
    ResponseEntity<DifferencesResponse> response = differencesController.leftOperand(id, request);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is OK (200)", response.getStatusCode(), is(OK));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Done"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

    // Verify that storage.set() was called exactly once. Uses captured argument.
    verify(leftRepository, times(1)).save(any(LeftOperand.class));

  }

  @Test
  public void setNullLeftId() {

    // Invoke method to test
    ResponseEntity<DifferencesResponse> response = differencesController.leftOperand(null, null);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is BAD REQUEST (400)", response.getStatusCode(), is(BAD_REQUEST));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Invalid ID"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

  }

  @Test
  public void setNullLeftOperand() {

    // Invoke method to test
    ResponseEntity<DifferencesResponse> response = differencesController.leftOperand(id, null);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is BAD REQUEST (400)", response.getStatusCode(), is(BAD_REQUEST));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Invalid Base64 payload!"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

  }

  @Test
  public void setEmptyLeftOperand() {

    // Invoke method to test
    ResponseEntity<DifferencesResponse> response = differencesController.leftOperand(id, new DifferencesRequest(null));

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is BAD REQUEST (400)", response.getStatusCode(), is(BAD_REQUEST));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Invalid Base64 payload!"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

  }

  @Test
  public void setRightOperand() {

    // Invoke method to test
    ResponseEntity<DifferencesResponse> response = differencesController.rightOperand(id, request);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is OK (200)", response.getStatusCode(), is(OK));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Done"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

    // Verify that storage.set() was called exactly once. Uses captured argument.
    verify(rightRepository, times(1)).save(any(RightOperand.class));

  }

  @Test
  public void setNullRightOperand() {

    // Invoke method to test
    ResponseEntity<DifferencesResponse> response = differencesController.rightOperand(id, null);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is BAD REQUEST (400)", response.getStatusCode(), is(BAD_REQUEST));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Invalid Base64 payload!"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

  }

  @Test
  public void setEmptyRightOperand() {

    // Invoke method to test
    ResponseEntity<DifferencesResponse> response = differencesController.rightOperand(id, new DifferencesRequest(null));

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is BAD REQUEST (400)", response.getStatusCode(), is(BAD_REQUEST));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Invalid Base64 payload!"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

  }

  @Test
  public void setNullRightId() {

    // Invoke method to test
    ResponseEntity<DifferencesResponse> response = differencesController.rightOperand(null, null);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is BAD REQUEST (400)", response.getStatusCode(), is(BAD_REQUEST));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Invalid ID"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

  }

  @Test
  public void diffOperationSuccessfulEquals() {
    List<LeftOperand> leftOperand = singletonList(LeftOperand.from(id, request.getPayload()));
    List<RightOperand> rightOperand = singletonList(RightOperand.from(id, request.getPayload()));

    // Set expectations
    when(leftRepository.findTopByOperationIdOrderByCreatedDesc(id)).thenReturn(leftOperand);
    when(rightRepository.findTopByOperationIdOrderByCreatedDesc(id)).thenReturn(rightOperand);

    // Invoke method to test
    ResponseEntity<DifferencesResponse> response = differencesController.diffOperation(id);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is OK (200)", response.getStatusCode(), is(OK));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Byte arrays are equal!"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

    // Verify mocks invocations
    verify(leftRepository, times(1)).findTopByOperationIdOrderByCreatedDesc(eq(id));
    verify(rightRepository, times(1)).findTopByOperationIdOrderByCreatedDesc(eq(id));
    verify(differentiable, times(1)).diff(any(byte[].class), any(byte[].class));

  }

  @Test
  public void diffOperationSuccessfulNotEqualSize() {
    List<LeftOperand> leftOperand = singletonList(LeftOperand.from(id, request.getPayload()));
    List<RightOperand> rightOperand = singletonList(RightOperand.from(id, createBase64Data("SOMETHING".getBytes())));

    // Set expectations
    when(leftRepository.findTopByOperationIdOrderByCreatedDesc(id)).thenReturn(leftOperand);
    when(rightRepository.findTopByOperationIdOrderByCreatedDesc(id)).thenReturn(rightOperand);

    // Invoke method to test
    ResponseEntity<DifferencesResponse> response = differencesController.diffOperation(id);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is OK (200)", response.getStatusCode(), is(OK));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Byte arrays are NOT equal!"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

    // Verify mocks invocations
    verify(leftRepository, times(1)).findTopByOperationIdOrderByCreatedDesc(eq(id));
    verify(rightRepository, times(1)).findTopByOperationIdOrderByCreatedDesc(eq(id));

  }

  @Test
  public void diffOperationSuccessfulNotEquals() {
    List<LeftOperand> leftOperand = singletonList(LeftOperand.from(id, request.getPayload()));
    List<RightOperand> rightOperand = singletonList(RightOperand.from(id, request.getPayload()));

    // Set expectations
    when(leftRepository.findTopByOperationIdOrderByCreatedDesc(id)).thenReturn(leftOperand);
    when(rightRepository.findTopByOperationIdOrderByCreatedDesc(id)).thenReturn(rightOperand);

    Difference difference = new Difference(1, 1);
    List<Difference> differenceList = singletonList(difference);

    when(differentiable.diff(any(byte[].class), any(byte[].class))).thenReturn(differenceList);

    // Invoke method to test
    ResponseEntity<DifferencesResponse> response = differencesController.diffOperation(id);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is OK (200)", response.getStatusCode(), is(OK));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Byte arrays are NOT equal!"));
    assertThat("There are no differences", differences.getDifferences(), is(differenceList));

    // Verify mocks invocations
    verify(leftRepository, times(1)).findTopByOperationIdOrderByCreatedDesc(eq(id));
    verify(rightRepository, times(1)).findTopByOperationIdOrderByCreatedDesc(eq(id));
    verify(differentiable, times(1)).diff(any(byte[].class), any(byte[].class));

  }

  @Test
  public void diffOperationWithoutOneOperand() {

    List<LeftOperand> leftOperand = singletonList(LeftOperand.from(id, request.getPayload()));

    // Set expectations
    when(leftRepository.findTopByOperationIdOrderByCreatedDesc(id)).thenReturn(leftOperand);
    when(rightRepository.findTopByOperationIdOrderByCreatedDesc(id)).thenReturn(emptyList());

    ResponseEntity<DifferencesResponse> response = differencesController.diffOperation(id);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is BAD REQUEST (400)", response.getStatusCode(), is(BAD_REQUEST));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is(format("No comparison pending for ID [%s]", id)));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

    // Verify mocks invocations
    verify(leftRepository, times(1)).findTopByOperationIdOrderByCreatedDesc(eq(id));
    verify(rightRepository, times(1)).findTopByOperationIdOrderByCreatedDesc(eq(id));

  }

  @Test
  public void diffOperationWithNullId() {

    ResponseEntity<DifferencesResponse> response = differencesController.diffOperation(null);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is BAD REQUEST (400)", response.getStatusCode(), is(BAD_REQUEST));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Invalid ID"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

  }

  @Test
  public void diffOperationInvalidOperandsAreTreatedAsEmptyByteArrays() {
    List<LeftOperand> leftOperand = singletonList(LeftOperand.from(id, null));
    List<RightOperand> rightOperand = singletonList(RightOperand.from(id, null));

    // Set expectations
    when(leftRepository.findTopByOperationIdOrderByCreatedDesc(id)).thenReturn(leftOperand);
    when(rightRepository.findTopByOperationIdOrderByCreatedDesc(id)).thenReturn(rightOperand);

    ResponseEntity<DifferencesResponse> response = differencesController.diffOperation(id);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is OK (200)", response.getStatusCode(), is(OK));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Byte arrays are equal!"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

    // Verify mocks invocations
    verify(leftRepository, times(1)).findTopByOperationIdOrderByCreatedDesc(eq(id));
    verify(rightRepository, times(1)).findTopByOperationIdOrderByCreatedDesc(eq(id));
    verify(differentiable, times(1)).diff(eq(new byte[0]), eq(new byte[0]));

  }

  private String createBase64Data() {
    byte[] buffer = new byte[1024];
    return createBase64Data(buffer);
  }

  private String createBase64Data(byte[] buffer) {
    new Random().nextBytes(buffer);
    return Base64.getEncoder().encodeToString(buffer);
  }

}
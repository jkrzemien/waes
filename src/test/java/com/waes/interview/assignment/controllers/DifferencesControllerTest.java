package com.waes.interview.assignment.controllers;

import com.waes.interview.assignment.differentiator.Differentiable;
import com.waes.interview.assignment.models.DiffOperands;
import com.waes.interview.assignment.models.DifferencesRequest;
import com.waes.interview.assignment.models.DifferencesResponse;
import com.waes.interview.assignment.storage.Storage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Base64;
import java.util.Random;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
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
  private Storage<DiffOperands<byte[]>> storage;

  @Mock
  private Differentiable<byte[]> differentiable;

  @Captor
  private ArgumentCaptor<DiffOperands<byte[]>> captor;

  /**
   * Class members
   */
  private String id;
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
    reset(storage, differentiable);

    this.differencesController = new DifferencesController(storage, differentiable);
    this.id = randomUUID().toString();
    this.request = new DifferencesRequest(createBase64Data());

  }

  /**
   * Resets mock dependencies state after each test
   */
  @After
  public void tearDown() {
    // Verify that no other dependencies were called.
    verifyNoMoreInteractions(storage, differentiable);
  }

  @Test
  public void setLeftOperand() {

    ResponseEntity<DifferencesResponse> response = differencesController.leftDiffData(id, request);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is OK (200)", response.getStatusCode(), is(OK));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Done"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

    // Verify that storage.set() was called exactly once. Uses captured argument.
    verify(storage, times(1)).set(eq(id), captor.capture());

  }

  @Test
  public void setNullLeftOperand() {

    ResponseEntity<DifferencesResponse> response = differencesController.leftDiffData(id, null);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is BAD REQUEST (400)", response.getStatusCode(), is(BAD_REQUEST));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Invalid Base64 payload!"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

  }

  @Test
  public void setEmptyLeftOperand() {

    ResponseEntity<DifferencesResponse> response = differencesController.leftDiffData(id, new DifferencesRequest(null));

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is BAD REQUEST (400)", response.getStatusCode(), is(BAD_REQUEST));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Invalid Base64 payload!"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

  }

  @Test
  public void setRightOperandWithExistingLeftOperand() {
    final DiffOperands<byte[]> operands = new DiffOperands<>(new byte[10], null);

    // Set expectations
    when(storage.hasEntry(id)).thenReturn(true);
    when(storage.remove(id)).thenReturn(operands);

    ResponseEntity<DifferencesResponse> response = differencesController.rightDiffData(id, request);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is OK (200)", response.getStatusCode(), is(OK));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Done"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

    // Verify mocks invocations
    verify(storage, times(1)).hasEntry(eq(id));
    verify(storage, times(1)).set(eq(id), captor.capture());
    verify(storage, times(1)).remove(eq(id));

  }

  @Test
  public void setRightOperandWithoutExistingLeftOperand() {

    // Set expectations
    when(storage.hasEntry(id)).thenReturn(false);

    ResponseEntity<DifferencesResponse> response = differencesController.rightDiffData(id, request);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is BAD REQUEST (400)", response.getStatusCode(), is(BAD_REQUEST));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Must call endpoint /left before calling endpoint /right"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

    // Verify mocks invocations
    verify(storage, times(1)).hasEntry(eq(id));

  }

  @Test
  public void setNullRightOperand() {

    // Set expectations
    when(storage.hasEntry(id)).thenReturn(true);

    ResponseEntity<DifferencesResponse> response = differencesController.rightDiffData(id, null);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is BAD REQUEST (400)", response.getStatusCode(), is(BAD_REQUEST));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Invalid Base64 payload!"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

    // Verify mocks invocations
    verify(storage, times(1)).hasEntry(eq(id));

  }

  @Test
  public void setEmptyRightOperand() {

    ResponseEntity<DifferencesResponse> response = differencesController.rightDiffData(id, new DifferencesRequest(null));

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is BAD REQUEST (400)", response.getStatusCode(), is(BAD_REQUEST));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Must call endpoint /left before calling endpoint /right"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

    // Verify mocks invocations
    verify(storage, times(1)).hasEntry(eq(id));

  }

  @Test
  public void diffOperationSuccessful() {

    DiffOperands<byte[]> operands = new DiffOperands<>(new byte[10], new byte[10]);

    // Set expectations
    when(storage.remove(id)).thenReturn(operands);
    when(differentiable.diff(eq(operands.getLeft()), eq(operands.getRight()))).thenReturn(emptyList());

    ResponseEntity<DifferencesResponse> response = differencesController.diffOperation(id);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is OK (200)", response.getStatusCode(), is(OK));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is("Byte arrays are equal!"));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

    // Verify mocks invocations
    verify(storage, times(1)).remove(eq(id));
    verify(differentiable, times(1)).diff(eq(operands.getLeft()), eq(operands.getRight()));

  }

  @Test
  public void diffOperationUnsuccessful() {

    DiffOperands<byte[]> operands = new DiffOperands<>(new byte[10], new byte[10]);

    // Set expectations
    when(storage.remove(id)).thenReturn(null);

    ResponseEntity<DifferencesResponse> response = differencesController.diffOperation(id);

    assertThat("There is a result", response, is(notNullValue()));
    assertThat("HTTP return code is BAD REQUEST (400)", response.getStatusCode(), is(BAD_REQUEST));

    DifferencesResponse differences = response.getBody();

    assertThat("Message matches expected value", differences.getMessage(), is(format("No comparison pending for ID [%s]", id)));
    assertThat("There are no differences", differences.getDifferences().isEmpty(), is(true));

    // Verify mocks invocations
    verify(storage, times(1)).remove(id);
    verify(differentiable, never()).diff(eq(operands.getLeft()), eq(operands.getRight()));

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

  private String createBase64Data() {
    byte[] buffer = new byte[1024];
    new Random().nextBytes(buffer);
    return Base64.getEncoder().encodeToString(buffer);
  }

}
package com.waes.interview.assignment.controllers;

import com.waes.interview.assignment.differentiator.Differentiable;
import com.waes.interview.assignment.models.Difference;
import com.waes.interview.assignment.models.DifferenceOperand;
import com.waes.interview.assignment.models.DifferencesRequest;
import com.waes.interview.assignment.models.DifferencesResponse;
import com.waes.interview.assignment.repositories.OperandsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.String.format;
import static java.util.Base64.getDecoder;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * Spring REST controller for diff-ing two operands (Left and Right).
 * <p>
 * Exposes the 3 endpoints required for this assignment.
 * <p>
 * POST /v1/diff/{id}/left
 * POST /v1/diff/{id}/right
 * GET /v1/diff/{id}
 *
 * @author Juan Krzemien
 */
@RestController
public class DifferencesController {

  /**
   * Constant definitions
   */
  private static final String INVALID_ID = "Invalid ID";
  private static final String NO_COMPARISON_PENDING_FOR_ID = "No comparison pending for ID [%s]";
  private static final String INVALID_OPERANDS = "Cannot operate with either operand (Left/Right) missing!";
  private static final String BYTE_ARRAYS_ARE_NOT_EQUAL = "Byte arrays are NOT equal!";
  private static final String BYTE_ARRAYS_ARE_EQUAL = "Byte arrays are equal!";
  private static final String INVALID_BASE64_PAYLOAD = "Invalid Base64 payload!";
  private static final String DUPLICATE_TRANSACTION_ID = "The transaction ID has pending operations. Please, specify a different one.";
  private static final String WRONG_INVOCATION_ORDER = "Must call endpoint /left before calling endpoint /right";

  /**
   * Class members
   */
  private final OperandsRepository repository;
  private final Differentiable<byte[]> differentiable;

  /**
   * Constructor
   *
   * @param repository     Implementation of a storage for operands between endpoint invocations.
   * @param differentiable Implementation of a differentiable for diff-ing /left and /right endpoints
   */
  public DifferencesController(@Autowired OperandsRepository repository, @Autowired Differentiable<byte[]> differentiable) {
    this.repository = repository;
    this.differentiable = differentiable;
  }

  /**
   * Endpoint for setting the Left operand of diff operation.
   *
   * @param id      ID for the operation
   * @param request Request with the Base64 payload to set as Left operand
   * @return {@link DifferencesResponse DifferencesResponse} with message indicating the status of the operation
   */
  @PostMapping(value = "/v1/diff/{id}/left", produces = APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<DifferencesResponse> leftOperand(@PathVariable Long id, @RequestBody DifferencesRequest request) {

    if (id == null) {
      return badRequest().body(new DifferencesResponse(INVALID_ID));
    }

    if (request == null || request.getPayload().isEmpty()) {
      return badRequest().body(new DifferencesResponse(INVALID_BASE64_PAYLOAD));
    }

    if (repository.existsByOperationIdAndProcessed(id, false)) {
      return badRequest().body(new DifferencesResponse(DUPLICATE_TRANSACTION_ID));
    }

    DifferenceOperand operand = DifferenceOperand.from(id, request.getPayload(), false);

    repository.save(operand);

    return ok(new DifferencesResponse("Done"));
  }

  /**
   * Endpoint for setting the Right operand of diff operation.
   *
   * @param id      ID for the operation
   * @param request Request with the Base64 payload to set as Right operand
   * @return {@link DifferencesResponse DifferencesResponse} with message indicating the status of the operation
   */
  @PostMapping(value = "/v1/diff/{id}/right", produces = APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<DifferencesResponse> rightOperand(@PathVariable Long id, @RequestBody DifferencesRequest request) {

    if (id == null) {
      return badRequest().body(new DifferencesResponse(INVALID_ID));
    }

    if (request == null || request.getPayload().isEmpty()) {
      return badRequest().body(new DifferencesResponse(INVALID_BASE64_PAYLOAD));
    }

    List<DifferenceOperand> transactions = repository.findByOperationIdAndProcessed(id, false);

    if (transactions.isEmpty()) {
      return badRequest().body(new DifferencesResponse(WRONG_INVOCATION_ORDER));
    } else if (transactions.size() > 1) {
      return badRequest().body(new DifferencesResponse(DUPLICATE_TRANSACTION_ID));
    }

    DifferenceOperand operand = DifferenceOperand.from(id, request.getPayload(), false);

    repository.save(operand);

    return ok(new DifferencesResponse("Done"));
  }

  /**
   * Endpoint for getting the results of diff operation.
   *
   * @param id ID for the operation
   * @return {@link DifferencesResponse DifferencesResponse} with message indicating the result of the operation
   */
  @GetMapping(value = "/v1/diff/{id}", produces = APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<DifferencesResponse> diffOperation(@PathVariable Long id) {

    if (id == null) {
      return badRequest().body(new DifferencesResponse(INVALID_ID));
    }

    List<DifferenceOperand> operands = repository.findByOperationIdAndProcessed(id, false);

    if (operands == null || operands.size() != 2) {
      return badRequest().body(new DifferencesResponse(format(NO_COMPARISON_PENDING_FOR_ID, id)));
    }

    // Do not operate on invalid operands
    if (operands.stream().anyMatch(operand -> !operand.isValid())) {
      return badRequest().body(new DifferencesResponse(INVALID_OPERANDS));
    }

    byte[] left = decode(operands.get(0).getData());
    byte[] right = decode(operands.get(1).getData());

    // Do not operate on different length arrays
    if (left.length != right.length) {
      markOperandsAsProcessed(operands);
      return ok().body(new DifferencesResponse(BYTE_ARRAYS_ARE_NOT_EQUAL));
    }

    final List<Difference> differences = differentiable.diff(left, right);

    markOperandsAsProcessed(operands);

    // If we noticed differences, then arrays were not equal
    if (!differences.isEmpty()) {
      return ok(new DifferencesResponse(BYTE_ARRAYS_ARE_NOT_EQUAL, differences));
    }

    // Otherwise, arrays were equals
    return ok(new DifferencesResponse(BYTE_ARRAYS_ARE_EQUAL, differences));
  }

  /**
   * Marks the operands as processed by {@link DifferencesController DifferencesController}.
   *
   * @param operands List of operands to set as processed
   */
  private void markOperandsAsProcessed(List<DifferenceOperand> operands) {
    operands.forEach(operand -> operand.setProcessed(true));
    repository.saveAll(operands);
  }

  /**
   * Decodes the incoming request payload from Base64 into a byte array.
   * <p>
   * Any failure during attempting to do so will result in a zero length byte array returning.
   *
   * @param base64Data Incoming Base64 data from {@link DifferencesRequest DifferencesRequest}
   * @return a byte array with the decoding of the Base64 payload present in the incoming {@link DifferencesRequest DifferencesRequest}
   */
  private byte[] decode(String base64Data) {
    try {
      return getDecoder().decode(base64Data);
    } catch (Exception e) {
      return new byte[0];
    }
  }

}

package com.waes.interview.assignment.controllers;

import com.waes.interview.assignment.differentiator.Differentiable;
import com.waes.interview.assignment.models.*;
import com.waes.interview.assignment.repositories.LeftOperandsRepository;
import com.waes.interview.assignment.repositories.RightOperandsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
  private static final String BYTE_ARRAYS_ARE_NOT_EQUAL = "Byte arrays are NOT equal!";
  private static final String BYTE_ARRAYS_ARE_EQUAL = "Byte arrays are equal!";
  private static final String INVALID_BASE64_PAYLOAD = "Invalid Base64 payload!";
  private static final String DATA_INTEGRITY = "Payload cannot exceed 1 MB in size!";

  /**
   * Class members
   */
  private final LeftOperandsRepository leftRepository;
  private final RightOperandsRepository rightRepository;
  private final Differentiable<byte[]> differentiable;

  /**
   * Constructor
   *
   * @param leftRepository  Implementation of a storage for operands between endpoint invocations.
   * @param rightRepository Implementation of a storage for operands between endpoint invocations.
   * @param differentiable  Implementation of a differentiable for diff-ing /left and /right endpoints
   */
  public DifferencesController(@Autowired LeftOperandsRepository leftRepository, @Autowired RightOperandsRepository rightRepository, @Autowired Differentiable<byte[]> differentiable) {
    this.leftRepository = leftRepository;
    this.rightRepository = rightRepository;
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

    // Fail upon invalid IDs
    if (id == null) {
      return badRequest().body(new DifferencesResponse(INVALID_ID));
    }

    // Fail upon invalid requests
    if (request == null || request.getPayload().isEmpty()) {
      return badRequest().body(new DifferencesResponse(INVALID_BASE64_PAYLOAD));
    }

    LeftOperand operand = LeftOperand.from(id, request.getPayload());

    try {
      leftRepository.save(operand);
    } catch (DataIntegrityViolationException e) {
      return badRequest().body(new DifferencesResponse(DATA_INTEGRITY));
    }

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

    // Fail upon invalid IDs
    if (id == null) {
      return badRequest().body(new DifferencesResponse(INVALID_ID));
    }

    // Fail upon invalid requests
    if (request == null || request.getPayload().isEmpty()) {
      return badRequest().body(new DifferencesResponse(INVALID_BASE64_PAYLOAD));
    }

    RightOperand operand = RightOperand.from(id, request.getPayload());

    try {
      rightRepository.save(operand);
    } catch (DataIntegrityViolationException e) {
      return badRequest().body(new DifferencesResponse(DATA_INTEGRITY));
    }

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

    // Fail upon invalid IDs
    if (id == null) {
      return badRequest().body(new DifferencesResponse(INVALID_ID));
    }

    final List<LeftOperand> leftOperands = leftRepository.findTopByOperationIdOrderByCreatedDesc(id);
    final List<RightOperand> rightOperands = rightRepository.findTopByOperationIdOrderByCreatedDesc(id);

    // Fail upon operands count mismatch
    if (leftOperands.size() != 1 || rightOperands.size() != 1) {
      return badRequest().body(new DifferencesResponse(format(NO_COMPARISON_PENDING_FOR_ID, id)));
    }

    byte[] left = decode(leftOperands.get(0).getData());
    byte[] right = decode(rightOperands.get(0).getData());

    // Do not operate on different length arrays, just indicate they are not equal
    if (left.length != right.length) {
      return ok().body(new DifferencesResponse(BYTE_ARRAYS_ARE_NOT_EQUAL));
    }

    // Process operands
    final List<Difference> differences = differentiable.diff(left, right);

    // If we noticed differences, then arrays were not equal
    if (!differences.isEmpty()) {
      return ok(new DifferencesResponse(BYTE_ARRAYS_ARE_NOT_EQUAL, differences));
    }

    // Otherwise, arrays were equals
    return ok(new DifferencesResponse(BYTE_ARRAYS_ARE_EQUAL, differences));
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
      // Should never happen
      return new byte[0];
    }
  }

}

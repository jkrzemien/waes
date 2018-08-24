package com.waes.interview.assignment.controllers;

import com.waes.interview.assignment.differentiator.Differentiable;
import com.waes.interview.assignment.models.DiffOperands;
import com.waes.interview.assignment.models.Difference;
import com.waes.interview.assignment.models.DifferencesRequest;
import com.waes.interview.assignment.models.DifferencesResponse;
import com.waes.interview.assignment.storage.Storage;
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
  private static final String WRONG_INVOCATION_ORDER = "Must call endpoint /left before calling endpoint /right";

  /**
   * Class members
   */
  private final Storage<DiffOperands<byte[]>> storage;
  private final Differentiable<byte[]> differentiable;

  /**
   * Constructor
   *
   * @param storage        Implementation of a storage for operands between endpoint invocations.
   * @param differentiable Implementation of a differentiable for diff-ing /left and /right endpoints
   */
  public DifferencesController(@Autowired Storage<DiffOperands<byte[]>> storage, @Autowired Differentiable<byte[]> differentiable) {
    this.storage = storage;
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
  public ResponseEntity<DifferencesResponse> leftDiffData(@PathVariable String id, @RequestBody DifferencesRequest request) {
    byte[] bytes = decode(request);

    if (bytes.length == 0) {
      return badRequest().body(new DifferencesResponse(INVALID_BASE64_PAYLOAD));
    }

    DiffOperands<byte[]> operands = new DiffOperands<>(bytes, null);

    storage.set(id, operands);

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
  public ResponseEntity<DifferencesResponse> rightDiffData(@PathVariable String id, @RequestBody DifferencesRequest request) {
    if (!storage.hasEntry(id)) {
      return badRequest().body(new DifferencesResponse(WRONG_INVOCATION_ORDER));
    }

    byte[] bytes = decode(request);

    if (bytes.length == 0) {
      return badRequest().body(new DifferencesResponse(INVALID_BASE64_PAYLOAD));
    }

    DiffOperands<byte[]> operands = storage.remove(id);
    storage.set(id, new DiffOperands<>(operands.getLeft(), bytes));

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
  public ResponseEntity<DifferencesResponse> diffOperation(@PathVariable String id) {

    if (id == null) {
      return badRequest().body(new DifferencesResponse(INVALID_ID));
    }

    DiffOperands<byte[]> operands = storage.remove(id);

    if (operands == null) {
      return badRequest().body(new DifferencesResponse(format(NO_COMPARISON_PENDING_FOR_ID, id)));
    }
    // Do not operate on invalid operands
    if (!operands.areValid()) {
      return badRequest().body(new DifferencesResponse(INVALID_OPERANDS));
    }

    byte[] left = operands.getLeft();
    byte[] right = operands.getRight();

    // Do not operate on different length arrays
    if (left.length != right.length) {
      return ok().body(new DifferencesResponse(BYTE_ARRAYS_ARE_NOT_EQUAL));
    }

    final List<Difference> differences = differentiable.diff(operands.getLeft(), operands.getRight());

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
   * @param request Incoming {@link DifferencesRequest DifferencesRequest} from {@link DifferencesController DifferencesController}
   * @return a byte array with the decoding of the Base64 payload present in the incoming {@link DifferencesRequest DifferencesRequest}
   */
  private byte[] decode(DifferencesRequest request) {
    try {
      return getDecoder().decode(request.getPayload());
    } catch (Exception e) {
      return new byte[0];
    }
  }

}

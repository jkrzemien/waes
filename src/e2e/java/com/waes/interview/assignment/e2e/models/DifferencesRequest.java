package com.waes.interview.assignment.e2e.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Immutable class to store request for E2E tests.
 * <p>
 * NOTE: I am not reusing the models from actual application because if there is an error in it
 * I'd be replicating it in the E2E tests. Both projects should be totally isolated from each other.
 *
 * @author Juan Krzemien
 */
public final class DifferencesRequest {

  /**
   * Base64 encoded payload
   */
  @JsonProperty("payload")
  private final String payload;

  /**
   * Constructor
   *
   * @param payload String containing the payload for this request
   */
  @JsonCreator
  public DifferencesRequest(@JsonProperty("payload") String payload) {
    if (payload == null) {
      payload = "";
    }
    this.payload = payload;
  }

  /**
   * Retrieves the payload attached to this request
   *
   * @return String containing the payload for this request
   */
  public String getPayload() {
    return payload;
  }

}

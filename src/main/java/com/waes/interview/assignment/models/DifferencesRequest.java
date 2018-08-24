package com.waes.interview.assignment.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Immutable class to store incoming request to {@link com.waes.interview.assignment.controllers.DifferencesController DifferencesController}
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

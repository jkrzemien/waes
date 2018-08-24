package com.waes.interview.assignment.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

/**
 * Immutable structure to store responses from {@link com.waes.interview.assignment.controllers.DifferencesController DifferencesController} endpoints.
 * <p>
 * Holds a message with the result of the operation performed and a list of {@link Difference Difference} objects found, if any.
 *
 * @author Juan Krzemien
 */

@JsonInclude(NON_EMPTY)
public final class DifferencesResponse {

  @JsonProperty(value = "message", required = true)
  private final String message;

  @JsonProperty("differences")
  private final List<Difference> differences;

  /**
   * Constructor. This structure is expected to, at least, provide a message. List of differences is initialized to an
   * unmodifiable empty list.
   *
   * @param message Message to return from {@link com.waes.interview.assignment.controllers.DifferencesController DifferencesController} endpoints
   */
  public DifferencesResponse(@JsonProperty("message") String message) {
    this(message, emptyList());
  }

  /**
   * Constructor. Requires message to return and list of {@link Difference Difference}s.
   *
   * @param message     Message to return from {@link com.waes.interview.assignment.controllers.DifferencesController DifferencesController} endpoints
   * @param differences List of {@link Difference Difference} found
   */
  @JsonCreator
  public DifferencesResponse(@JsonProperty("message") String message, @JsonProperty("differences") List<Difference> differences) {
    this.message = message;
    this.differences = unmodifiableList(differences != null ? differences : emptyList());
  }

  /**
   * Retrieves the message stored in this response
   *
   * @return Message stored in this response
   */
  public String getMessage() {
    return message;
  }

  /**
   * Retrieves the list of {@link Difference Difference} objects found, if any
   *
   * @return List with {@link Difference Difference} objects, or an immutable empty list if no {@link Difference Difference}
   * was found
   */
  public List<Difference> getDifferences() {
    return differences;
  }

  /**
   * Overriding equals will allow for easier instances comparison during assertions in tests.
   *
   * @param o Object instance to compare this instance against
   * @return true if instances equal, false otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DifferencesResponse that = (DifferencesResponse) o;
    return Objects.equals(message, that.message) &&
        Objects.equals(differences, that.differences);
  }

  /**
   * One must override hashCode() in every class that overrides equals().
   * Failure to do so will result in a violation of the general contract for Object.hashCode(), which will prevent
   * class from functioning properly in conjunction with all hash-based collections.
   *
   * @return hash code for this instance
   */
  @Override
  public int hashCode() {
    return Objects.hash(message, differences);
  }

}

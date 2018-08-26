package com.waes.interview.assignment.e2e.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Immutable structure to store individual differences in responses of E2E tests.
 * <p>
 * The difference is composed by:
 * - An Integer denoting the starting position of the difference
 * - An Integer denoting the length of difference (offset)
 * <p>
 * NOTE: I am not reusing the models from actual application because if there is an error in it
 * I'd be replicating it in the E2E tests. Both projects should be totally isolated from each other.
 *
 * @author Juan Krzemien
 */
public final class Difference {

  @JsonProperty("position")
  private final Integer position;

  @JsonProperty("offset")
  private final Integer offset;

  /**
   * Constructor.
   *
   * @param position Starting index position of the difference
   * @param offset   Length of the difference
   */
  @JsonCreator
  public Difference(@JsonProperty("position") Integer position, @JsonProperty("offset") Integer offset) {
    this.position = position;
    this.offset = offset;
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
    Difference that = (Difference) o;
    return Objects.equals(position, that.position) &&
        Objects.equals(offset, that.offset);
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
    return Objects.hash(position, offset);
  }

  /**
   * Retrieves the stored position for this difference
   *
   * @return Starting position for this difference
   */
  public Integer getPosition() {
    return position;
  }

  /**
   * Retrieves the stored offset for this difference
   *
   * @return Offset length for this difference
   */
  public Integer getOffset() {
    return offset;
  }

}

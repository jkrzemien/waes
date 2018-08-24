package com.waes.interview.assignment.differentiator;

import com.waes.interview.assignment.models.Difference;

import java.util.List;

/**
 * A class implements <code>Differentiable</code> interface to
 * indicate that their state can be traversed looking for
 * positional differences (position + offset) in its data structures.
 * <p>
 * Examples: Strings, arrays, lists, etc
 *
 * @param <T> Type of the instances to look differences in
 * @author Juan Krzemien
 */
public interface Differentiable<T> {

  /**
   * Compares two instances looking for differences
   *
   * @param left  First instance to compare
   * @param right Second instance to compare
   * @return List of differences found between the two, if any.
   */
  List<Difference> diff(T left, T right);

}

package com.waes.interview.assignment.differentiator;

import com.waes.interview.assignment.models.Difference;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

/**
 * Implementation of Differentiable for binary data.
 * <p>
 * Takes two byte arrays of equal length and compares them looking for differences.
 * Differences will be stored as a list of Difference types ({ position=difference start , offSet = difference length })
 * <p>
 * This algorithm performs in O(n) complexity, as a full traversal of the arrays is required.
 * <p>
 * Deals with the portion of the WAES exercise:
 * <p>
 * - If equal return that
 * - If not of equal size just return that
 * - If of same size provide insight in where the diffs are, actual diffs are not needed.
 * - So mainly offsets + length in the data
 *
 * @author Juan Krzemien
 */

public class ByteArrayDiffer implements Differentiable<byte[]> {

  /**
   * Compares two byte arrays looking for differences
   *
   * @param left  First byte array to compare
   * @param right Second byte array to compare
   * @return Instance holding a text message with comparison outcome and the list of differences found between the two
   * provided byte arrays, if any.
   */
  @Override
  public List<Difference> diff(byte[] left, byte[] right) {

    // Do not operate on null arrays
    if (left == null || right == null) {
      return unmodifiableList(emptyList());
    }

    // Save one size for later re-usability, as both arrays to operated on should be equal
    int leftSize = left.length;

    // Do not operate on different length arrays
    if (leftSize != right.length) {
      return unmodifiableList(emptyList());
    }

    // Set the initial capacity of the array backed list to our arrays size
    final List<Difference> differences = new ArrayList<>(leftSize);
    int offSetStartMark = -1;
    int offSet = 1;

    // Traverse array once, using left size (since both are equal in length)
    for (int i = 0; i < leftSize; i++) {
      // Compare value for current position between arrays

      // If difference...
      if (left[i] != right[i]) {
        // ...and we are not counting offsets
        if (offSetStartMark == -1) {
          // Mark beginning of offset
          offSetStartMark = i;
        } else {
          // If we are already counting offset, increase counter
          offSet++;
        }
      } else {
        // If there is no difference...
        if (offSetStartMark != -1) { // ... and we were counting offsets
          // Store the difference offSetStartMark/offSet
          differences.add(new Difference(offSetStartMark, offSet));
          // Stop counting
          offSet = 1;
          offSetStartMark = -1;
        }
      }
    }

    // We may have reached the end of the arrays while counting offSets
    if (offSetStartMark != -1) {
      differences.add(new Difference(offSetStartMark, offSet));
    }

    return unmodifiableList(differences);
  }

}

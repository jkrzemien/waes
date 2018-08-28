package com.waes.interview.assignment.differentiator;

import com.waes.interview.assignment.models.Difference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Data driven JUnit 4 test suite for {@link ByteArrayDiffer ByteArrayDiffer} class.
 *
 * @author Juan Krzemien
 */
@RunWith(Parameterized.class)
public class ByteArrayDifferTest {

  /**
   * Constant data requirements for the different cases
   */
  private static final byte[] BASE_BYTE_ARRAY = new byte[5];
  private static final byte[] CASE_1_NO_DIFFERENCES = BASE_BYTE_ARRAY;
  private static final byte[] CASE_2_POS_0_WHOLE_OFFSET = new byte[]{4, 3, 2, 1, 0};
  private static final byte[] CASE_3_POS_1_OFFSET_4 = new byte[]{0, 1, 2, 3, 4};
  private static final byte[] CASE_4_MULTIPLE_OFFSETS_1 = new byte[]{1, 0, 0, 0, 3};
  private static final byte[] CASE_5_MULTIPLE_OFFSETS_2 = new byte[]{1, 2, 0, 3, 4};
  private static final byte[] CASE_6_SIZE_DIFFERENCES = new byte[6];

  /**
   * Constant expectations for some cases
   */
  private static final List<Difference> NO_DIFFERENCES = emptyList();

  /**
   * Attributes to store constructor arguments
   */
  private final byte[] left;
  private final byte[] right;
  private final List<Difference> expected;

  /**
   * Class under test
   */
  private final ByteArrayDiffer byteArrayDiffer = new ByteArrayDiffer();

  /**
   * Constructor for test suite. As this test suite is a parameterized data driven one, JUnit 4 runner requires
   * parameters to be passed as constructor arguments.
   *
   * @param explanation Just a string explaining the case being executed.
   *                    It is not stored anywhere as its purpose it to provide fancier test names.
   * @param left        Left side operand for {@link ByteArrayDiffer ByteArrayDiffer}'s diff() method
   * @param right       Right side operand for {@link ByteArrayDiffer ByteArrayDiffer}'s diff() method
   * @param differences Expected difference responses to compare
   */
  public ByteArrayDifferTest(String explanation, byte[] left, byte[] right, List<Difference> differences) {
    this.left = left;
    this.right = right;
    this.expected = differences;
  }

  @Parameters(name = "{0}")
  public static Object[][] data() {
    return new Object[][]{
        {"Both arguments are null", null, null, NO_DIFFERENCES},
        {"Left argument is null", null, BASE_BYTE_ARRAY, NO_DIFFERENCES},
        {"Right argument is null", BASE_BYTE_ARRAY, null, NO_DIFFERENCES},
        {"No differences between operands", BASE_BYTE_ARRAY, CASE_1_NO_DIFFERENCES, NO_DIFFERENCES},
        {
            "Operands are completely different",
            BASE_BYTE_ARRAY,
            CASE_2_POS_0_WHOLE_OFFSET,
            singletonList(new Difference(0, 4))
        },
        {
            "A single long difference after first position",
            BASE_BYTE_ARRAY,
            CASE_3_POS_1_OFFSET_4,
            singletonList(new Difference(1, 4))
        },

        {
            "Differences apart from each other with offset 1",
            BASE_BYTE_ARRAY,
            CASE_4_MULTIPLE_OFFSETS_1,
            asList(new Difference(0, 1), new Difference(4, 1))

        },
        {
            "Differences apart from each other with offset 2",
            BASE_BYTE_ARRAY,
            CASE_5_MULTIPLE_OFFSETS_2,
            asList(new Difference(0, 2), new Difference(3, 2))
        },
        {
            "Differences in size between operands",
            BASE_BYTE_ARRAY,
            CASE_6_SIZE_DIFFERENCES,
            NO_DIFFERENCES
        }
    };
  }

  @Test
  public void validateDifferentiable() {
    List<Difference> differences = byteArrayDiffer.diff(left, right);

    assertThat("Differences match expectations", differences, is(expected));

    /*
     * Left this code commented on purpose as it denotes the validations that would be required to perform in
     * case one does not override equals() and, consequently, hashCode() methods in {@link Difference Difference} class.
     *
     * Same notion applies for Integration Tests.
     */

    /*
    assertThat("Response is not null", differences, is(notNullValue()));

    int size = differences.size();
    assertThat("Differences have expected length", size, is(expected.size()));

    for (int i = 0; i < size; i++) {
      Difference difference = differences.get(i);
      Difference expectedDifference = expected.get(i);
      assertThat("Difference position matches expectation", difference.getPosition(), is(expectedDifference.getPosition()));
      assertThat("Difference offset matches expectation", difference.getOffset(), is(expectedDifference.getOffset()));
    }
    */
  }
}

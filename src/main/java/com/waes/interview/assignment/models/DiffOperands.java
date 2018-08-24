package com.waes.interview.assignment.models;

/**
 * Immutable class to hold pair of instances (left and right) to compare.
 *
 * @param <T> Type of the objects to hold for later comparison.
 * @author Juan Krzemien
 */
public final class DiffOperands<T> {

  private final T left;
  private final T right;

  /**
   * Constructor. Takes the two instances (left and right) to hold for later comparison.
   *
   * @param left  Left operand to compare against Right one
   * @param right Right operand to compare against Left one
   */
  public DiffOperands(T left, T right) {
    this.left = left;
    this.right = right;
  }

  /**
   * Returns the Left operand stored in this holder.
   *
   * @return Returns the Left operand
   */
  public T getLeft() {
    return left;
  }

  /**
   * Returns the Right operand stored in this holder.
   *
   * @return Returns the Right operand
   */
  public T getRight() {
    return right;
  }

  /**
   * Checks if both operands are present
   *
   * @return true if both operands are present, false otherwise.
   */
  public boolean areValid() {
    return left != null && right != null;
  }

}

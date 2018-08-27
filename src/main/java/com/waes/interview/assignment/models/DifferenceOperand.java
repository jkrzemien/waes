package com.waes.interview.assignment.models;

import javax.persistence.*;
import java.util.Objects;

/**
 * Entity class to hold ID and Base64 data of an operand for difference operation in persistence layer.
 * <p>
 * This class serves as DTO for Spring JPA repository ({@link com.waes.interview.assignment.repositories.OperandsRepository OperandsRepository})
 * <p>
 * Assumption: It allows to store up to 1 MB of Base64 data
 *
 * @author Juan Krzemien
 */
@Entity
public final class DifferenceOperand {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(name = "operationId", nullable = false)
  private Long operationId;

  // Assumption: Store up to 1 MB of data for this assignment
  @Column(name = "operand", nullable = false, length = 1024 * 1024)
  private String base64Data;

  @Column(name = "processed", nullable = false)
  private boolean processed;

  /**
   * Factory method for instances. Avoid duplicating several lines in code.
   *
   * @param id        Transaction ID
   * @param payload   Base64 data to store
   * @param processed Flag to indicate whether operand has been processed by controller or not
   * @return An {@link DifferenceOperand DifferenceOperand} instance populated with provided values
   */
  public static DifferenceOperand from(Long id, String payload, boolean processed) {
    DifferenceOperand operand = new DifferenceOperand();
    operand.setOperationId(id);
    operand.setBase64Data(payload);
    operand.setProcessed(processed);
    return operand;
  }

  /**
   * Sets the Base64 data to hold
   *
   * @param base64Data String in Base64 format of the data to hold
   */
  public void setBase64Data(String base64Data) {
    this.base64Data = base64Data;
  }

  /**
   * Returns the ID of the entity from persistence layer.
   *
   * @return the ID assigned to this entity by persistence layer
   */
  public Long getId() {
    return id;
  }

  /**
   * Returns ID of difference operation
   *
   * @return the difference operation ID
   */
  public Long getOperationId() {
    return operationId;
  }

  /**
   * Sets the difference operation ID for this entity.
   *
   * @param operationId The difference operation ID
   */
  public void setOperationId(Long operationId) {
    this.operationId = operationId;
  }

  /**
   * Returns the operand stored in this holder.
   *
   * @return the operand value
   */
  public String getData() {
    return base64Data;
  }

  /**
   * Checks if entity contains workable data
   *
   * @return true if none of the entity attributes are null or Base64 data is empty, false otherwise.
   */
  public boolean isValid() {
    return operationId != null && base64Data != null && !base64Data.isEmpty();
  }

  /**
   * Flag indicating whether this entity has been already processed to avoid considering it in future operations, while keeping historical data.
   *
   * @return true if operand has been used in a difference operation already, false otherwise.
   */
  public boolean isProcessed() {
    return processed;
  }

  /**
   * Marks the operand as already processed to avoid considering it in future operations, while keeping historical data.
   *
   * @param processed true if operand has been used in a difference operation already, false otherwise.
   */
  public void setProcessed(boolean processed) {
    this.processed = processed;
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
    DifferenceOperand that = (DifferenceOperand) o;
    return processed == that.processed &&
        Objects.equals(id, that.id) &&
        Objects.equals(operationId, that.operationId) &&
        Objects.equals(base64Data, that.base64Data);
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
    return Objects.hash(id, operationId, base64Data, processed);
  }
}

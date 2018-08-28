package com.waes.interview.assignment.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

/**
 * Abstract superclass for LEFT & RIGHT entity classes to hold ID and Base64 data of an operand for
 * difference operation in persistence layer.
 * <p>
 * This class serves as DTO for Spring JPA repositories
 * <p>
 * Assumption: It allows to store up to 1 MB of Base64 data
 *
 * @author Juan Krzemien
 */
@MappedSuperclass
public abstract class AbstractOperand {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(name = "operationId", nullable = false)
  private Long operationId;

  // Assumption: Store up to 1 MB of data for this assignment
  @Column(name = "operand", nullable = false, length = 1024 * 1024)
  private String base64Data;

  @Column(name = "created", nullable = false)
  private final Timestamp created = Timestamp.from(Instant.now());

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
   * Retrieves creation timestamp of entity.
   *
   * @return Entity's creation date time
   */
  public Timestamp getCreated() {
    return created;
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
    AbstractOperand that = (AbstractOperand) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(operationId, that.operationId) &&
        Objects.equals(base64Data, that.base64Data) &&
        Objects.equals(created, that.created);
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
    return Objects.hash(id, operationId, base64Data, created);
  }
}

package com.waes.interview.assignment.models;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity class to hold ID and Base64 data of RIGHT operands for a difference operation in persistence layer.
 * <p>
 * This class serves as DTO for Spring JPA repositories
 * <p>
 * Assumption: It allows to store up to 1 MB of Base64 data
 *
 * @author Juan Krzemien
 */
@Entity
@Table(name = "RIGHT")
public class RightOperand extends AbstractOperand {

  /**
   * Factory method for instances. Avoid duplicating several lines in code.
   *
   * @param id      Transaction ID
   * @param payload Base64 data to store
   * @return An {@link LeftOperand DifferenceOperand} instance populated with provided values
   */
  public static RightOperand from(Long id, String payload) {
    RightOperand operand = new RightOperand();
    operand.setOperationId(id);
    operand.setBase64Data(payload);
    return operand;
  }

}

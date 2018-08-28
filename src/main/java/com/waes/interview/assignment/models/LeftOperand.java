package com.waes.interview.assignment.models;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity class to hold ID and Base64 data of LEFT operands for a difference operation in persistence layer.
 * <p>
 * This class serves as DTO for Spring JPA repositories
 * <p>
 * Assumption: It allows to store up to 1 MB of Base64 data
 *
 * @author Juan Krzemien
 */
@Entity
@Table(name = "LEFT")
public class LeftOperand extends AbstractOperand {

  /**
   * Factory method for instances. Avoid duplicating several lines in code.
   *
   * @param id      Transaction ID
   * @param payload Base64 data to store
   * @return An {@link LeftOperand DifferenceOperand} instance populated with provided values
   */
  public static LeftOperand from(Long id, String payload) {
    LeftOperand operand = new LeftOperand();
    operand.setOperationId(id);
    operand.setBase64Data(payload);
    return operand;
  }

}

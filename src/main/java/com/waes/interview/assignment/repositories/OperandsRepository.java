package com.waes.interview.assignment.repositories;

import com.waes.interview.assignment.models.DifferenceOperand;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Interface to interact against SQL database (in-memory H2 in this case) via Spring JPA.
 * <p>
 * Allows CRUD operations on {@link DifferenceOperand DifferenceOperand} instances and defines two more methods
 * for filtering further.
 *
 * @author Juan Krzemien
 */

// Annotation for debugging purposes only, as Spring REST repository is not exported by default
@RepositoryRestResource(collectionResourceRel = "operands", path = "operands", exported = false)
public interface OperandsRepository extends CrudRepository<DifferenceOperand, Long> {

  /**
   * Checks existence of operands given operation ID and processed mark.
   *
   * @param operationId The ID of the operation of the operand we want to check
   * @param processed   The mark status of the operand we want to check
   * @return true if operand exists, false otherwise
   */
  boolean existsByOperationIdAndProcessed(@Param("operationId") Long operationId, @Param("processed") boolean processed);

  /**
   * Retrieves all operands matching given operation ID and processed mark.
   *
   * @param operationId The ID of the operation of the operand we want to retrieve
   * @param processed   The mark status of the operand we want to check
   * @return List of {@link DifferenceOperand DifferenceOperand} matching criteria
   */
  List<DifferenceOperand> findByOperationIdAndProcessed(@Param("operationId") Long operationId, @Param("processed") boolean processed);

}

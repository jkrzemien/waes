package com.waes.interview.assignment.repositories;

import com.waes.interview.assignment.models.LeftOperand;
import com.waes.interview.assignment.models.RightOperand;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Interface to interact against SQL database (in-memory H2 in this case) via Spring JPA.
 * <p>
 * Allows CRUD operations on {@link RightOperand RightOperand} instances and defines two more methods
 * for further filtering.
 *
 * @author Juan Krzemien
 */

// Annotation for debugging purposes only, as Spring REST repository is not exported by default
@RepositoryRestResource(collectionResourceRel = "operands", path = "operands", exported = false)
public interface RightOperandsRepository extends CrudRepository<RightOperand, Long> {

  /**
   * Retrieves newest operand matching given operation ID.
   *
   * @param operationId The ID of the operation of the operand we want to retrieve
   * @return List of {@link LeftOperand DifferenceOperand} matching criteria
   */
  List<RightOperand> findTopByOperationIdOrderByCreatedDesc(@Param("operationId") Long operationId);

}

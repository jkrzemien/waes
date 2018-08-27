package com.waes.interview.assignment.repositories;

import com.waes.interview.assignment.models.DifferenceOperand;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "operands", path = "operands", exported = false)
public interface OperandsRepository extends CrudRepository<DifferenceOperand, Long> {

  boolean existsByOperationIdAndProcessed(@Param("operationId") Long operationId, @Param("processed") boolean processed);

  List<DifferenceOperand> findByOperationIdAndProcessed(@Param("operationId") Long operationId, @Param("processed") boolean processed);

  // Turn off Spring REST endpoints auto export for some "dangerous" entries

  @Override
  @RestResource(exported = false)
  void deleteById(Long id);

  @Override
  @RestResource(exported = false)
  void delete(DifferenceOperand entity);

  @Override
  @RestResource(exported = false)
  void deleteAll();

  @Override
  @RestResource(exported = false)
  void deleteAll(Iterable<? extends DifferenceOperand> entities);

}

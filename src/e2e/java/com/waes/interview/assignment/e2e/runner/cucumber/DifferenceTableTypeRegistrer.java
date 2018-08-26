package com.waes.interview.assignment.e2e.runner.cucumber;

import com.waes.interview.assignment.e2e.models.Difference;
import cucumber.api.TypeRegistry;
import cucumber.api.TypeRegistryConfigurer;
import io.cucumber.datatable.DataTableType;

import java.util.Locale;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.util.Locale.ENGLISH;

/**
 * Type registry for Cucumber's DataTable.
 * <p>
 * Allows Cucumber to parse tables into list of {@link Difference Difference} to make tests more readable.
 * <p>
 * Will be auto detected and loaded by Cucumber at runtime because it is defined in it's "glue" path.
 *
 * @author Juan Krzemien
 */
public class DifferenceTableTypeRegistrer implements TypeRegistryConfigurer {

  @Override
  public Locale locale() {
    return ENGLISH;
  }

  @Override
  public void configureTypeRegistry(TypeRegistry typeRegistry) {
    typeRegistry.defineDataTableType(new DataTableType(
        Difference.class,
        (Map<String, String> row) -> new Difference(parseInt(row.get("position")), parseInt(row.get("offset")))
    ));
  }
}
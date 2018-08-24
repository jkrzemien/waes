# WAES

## Assignment

* Provide 2 http endpoints that accepts JSON base64 encoded binary data on both
endpoints
  * _host_/v1/diff/_ID_/left 
  * _host_/v1/diff/_ID_/right
* The provided data needs to be diff-ed and the results shall be available on a third end
point
  * _host_/v1/diff/_ID_

* The results shall provide the following info in JSON format
  * If equal return that
  * If not of equal size just return that
  * If of same size provide insight in where the diffs are, actual diffs are not needed.
     * So mainly offsets + length in the data

* Make assumptions in the implementation explicit, choices are good but need to be
communicated

* Must haves
  * Solution written in Java
  * Internal logic shall be under unit test
  * Functionality shall be under integration test
  * Documentation in code
  * Clear and to the point readme on usage

* Nice to haves
  * Suggestions for improvement

## Testing

### How do I execute unit tests only?

```bash
gradle clean test
```

### How do I execute integration tests only?

```bash
gradle clean integrationTest
```

### How do I execute all tests?

```bash
gradle clean test integrationTest
```

### How do I execute everything in here?

```bash
gradle clean test integrationTest jacocoTestReport Javadoc
```

### Reports

#### HTML execution results

Executing any of the following tasks will generate an HTML report inside `build/reports/tests` folder.

 * `test`
 * `integrationTest`

#### Code coverage

Appending `jacocoTestReport` task to any testing related task will generate a code coverage report at `build/reports/jacoco/index.html`.

* Unit tests coverage: `test jacocoTestReport`.

* Integration tests coverage: `integrationTest jacocoTestReport`: will do the same just for integration tests.

* A _unified_ HTML report for both unit and integration tests.

### Documentation

JavaDocs can be generated via `gradle Javadoc` task and located at `build/docs/javadoc`.
# WAES - Alternative implementation

**NOTE:** There are *two different implementations* of this assignment:

* `master` branch contains one solution, which restricts invocation order in REST API.
* `alternative` branch contains another solution, which allows to work with REST APIs in any order.

The following documentation remains true for any solution branch.

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

## Running the REST application

To immediately run the application execute: `gradle clean bootRun`

In case you need to package the application first to run it somewhere else, you can build and package the application:

`gradle clean bootJar`

and then:

`java -jar build\libs\waes-assignment-0.0.1.jar`

## Testing

### How do I execute unit tests only?

```bash
gradle clean test
```

### How do I execute integration tests only?

```bash
gradle clean integrationTest
```

### How do I execute End to End tests only?

```bash
gradle clean e2eTest
```

**IMPORTANT:** In order to execute End to End tests, the application **must be running**.

To do so, execute `gradle clean bootRun` in a _different_ terminal or command prompt console.

By default, End to End tests run against `http://localhost:8080` but this can be changed by setting the _ENVIRONMENT_ variable `SUT_ENV`.

Windows example: 

```
set SUT_ENV=http://www.somehost.com
```

*nix example: 

```bash
export SUT_ENV=http://www.somehost.com
```


### Reports

#### HTML execution results

Executing any of the test tasks will generate an HTML report inside `build/reports/tests` folder.

 * `test`
 * `integrationTest`
 * `e2eTest`
 
 Additionally, End to End tests will also generate Cucumber specific reports inside `build/reports/cucumber-html` folder and `build/reports/cucumber-report.json`.

#### Code coverage

Appending `jacocoTestReport` task to any testing related task will generate a code coverage report at `build/reports/jacoco/index.html`.

* Unit tests coverage: `test jacocoTestReport`.

* Integration tests coverage: `integrationTest jacocoTestReport`: will do the same just for integration tests.

* A _unified_ HTML report for both unit and integration tests.

### Documentation

JavaDocs can be generated via `gradle javadoc` task and located at `build/docs/javadoc`.
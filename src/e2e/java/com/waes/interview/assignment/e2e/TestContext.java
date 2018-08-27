package com.waes.interview.assignment.e2e;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.config.LogConfig.logConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static io.restassured.http.ContentType.JSON;
import static java.lang.ThreadLocal.withInitial;
import static java.util.Optional.ofNullable;

/**
 * Rudimentary Test Context for holding information between steps across potentially different threads
 * without polluting step definition classes.
 *
 * @author Juan Krzemien
 */
public enum TestContext {

  CONTEXT;

  private static final String SUT_ENV = "SUT_ENV";
  private static final String DEFAULT_URL = "http://localhost:8080";
  private static final String BASE_URI = ofNullable(System.getenv(SUT_ENV)).orElse(DEFAULT_URL);

  private static final String WORKING_ENTITY = "WORKING_ENTITY";
  private static final String REQUEST_SPECIFICATION = "REQUEST_SPECIFICATION";
  private static final String RESPONSE = "RESPONSE";

  private static final ThreadLocal<Map<String, Object>> testContexts = withInitial(HashMap::new);

  /**
   * Private constructor
   */
  TestContext() {
    RestAssured.config = config()
        .logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails());
  }

  /**
   * Retrieves a RestAssured specification instance from current thread test context.
   * Creates one if there isn't any.
   * <p>
   * Executes tests against http://localhost:8080 by default.
   * <p>
   * Define environment variable SUT_ENV to a different host if you need to change the base URL.
   *
   * @return Current thread's RestAssured specification instance
   */
  public RequestSpecification getSpec() {
    RequestSpecification spec = CONTEXT.getRequest();
    if (spec == null) {
      spec = RestAssured.with()
          .baseUri(BASE_URI)
          .contentType(JSON);
      CONTEXT.setRequest(spec);
    }
    return spec;
  }

  /**
   * Shortcut method for retrieving the current thread's RestAssured specification instance.
   *
   * @return Current thread's RestAssured specification instance
   */
  private RequestSpecification getRequest() {
    return get(REQUEST_SPECIFICATION);
  }

  /**
   * Shortcut method for setting the current thread's RestAssured specification instance.
   */
  private void setRequest(RequestSpecification request) {
    set(REQUEST_SPECIFICATION, request);
  }

  /**
   * Retrieves current thread's RestAssured response instance.
   *
   * @return Current thread's RestAssured response instance
   */
  public Response getResponse() {
    return get(RESPONSE);
  }

  /**
   * Sets RestAssured response instance into current thread test context.
   *
   * @param response A RestAssured response instance
   * @return The same RestAssured response instance that was set into current thread test context
   */
  public Response setResponse(Response response) {
    return set(RESPONSE, response);
  }

  /**
   * Shortcut method for retrieving an entity without providing a key.
   *
   * @return Working entity retrieves from current thread test context
   */
  public <T> T getWorkingEntity() {
    return get(WORKING_ENTITY);
  }

  /**
   * Shortcut method for storing an entity without providing a key.
   *
   * @param <T> Working entity to store for current thread test context
   */
  public <T> void setWorkingEntity(T object) {
    set(WORKING_ENTITY, object);
  }

  /**
   * Gets a value stored under a key/name from current thread test context.
   *
   * @param name Name/key used to store the value in current thread test context
   * @return Value stored under name/key from current thread test context
   */
  @SuppressWarnings("unchecked")
  public <T> T get(String name) {
    return (T) testContexts.get().get(name);
  }

  /**
   * Sets a value under a key/name into current thread test context.
   *
   * @param name   Name/key to store the value in current thread test context
   * @param object Value to store in current thread test context
   * @return Value stored
   */
  public <T> T set(String name, T object) {
    testContexts.get().put(name, object);
    return object;
  }

  /**
   * Clears all key/values stored for the current thread test context
   */
  public void reset() {
    testContexts.get().clear();
  }

  /**
   * Clears all key/values stored for the current thread test context, then removes the current thread test context.
   */
  public void remove() {
    reset();
    testContexts.remove();
  }

}

package com.waes.interview.assignment.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waes.interview.assignment.App;
import com.waes.interview.assignment.models.DifferencesRequest;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.IOException;
import java.util.Base64;
import java.util.Random;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Abstract class to hide away from integration test suites annotations and common methods
 * reusable throughout controller tests.
 *
 * @author Juan Krzemien
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
abstract class AbstractControllerIntegrationTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final Random RANDOM = new Random();

  @Autowired
  private MockMvc mvc;

  ResultActions doPost(String uri, String data) throws Exception {
    return mvc.perform(post(uri).content(data).contentType(APPLICATION_JSON));
  }

  <T> T doPostAndReturn(String uri, String data, Class<T> type) throws Exception {
    return doPostAndReturn(uri, data, status().isOk(), type);
  }

  <T> T doPostAndReturn(String uri, String data, ResultMatcher status, Class<T> type) throws Exception {
    MvcResult result = doPost(uri, data)
        .andExpect(status)
        .andReturn();
    return fromJson(result.getResponse().getContentAsString(), type);
  }

  ResultActions doGet(String uri) throws Exception {
    return mvc.perform(get(uri).contentType(APPLICATION_JSON));
  }

  <T> T doGetAndReturn(String uri, Class<T> type) throws Exception {
    return doGetAndReturn(uri, status().isOk(), type);
  }

  <T> T doGetAndReturn(String uri, ResultMatcher status, Class<T> type) throws Exception {
    MvcResult result = doGet(uri)
        .andExpect(status)
        .andReturn();
    return fromJson(result.getResponse().getContentAsString(), type);
  }

  String createBase64JsonData() throws JsonProcessingException {
    byte[] buffer = new byte[1024];
    RANDOM.nextBytes(buffer);
    return createBase64JsonData(buffer);
  }

  String createBase64JsonData(byte[] bytes) throws JsonProcessingException {
    String base64 = Base64.getEncoder().encodeToString(bytes);
    return mapper.writeValueAsString(new DifferencesRequest(base64));
  }

  private <T> T fromJson(String data, Class<T> type) throws IOException {
    return mapper.readValue(data, type);
  }

  enum DiffEndpoint {

    ENDPOINT_LEFT("/v1/diff/%s/left"),
    ENDPOINT_RIGHT("/v1/diff/%s/right"),
    ENDPOINT_DIFF("/v1/diff/%s");

    private final String endpoint;

    DiffEndpoint(String endpoint) {
      this.endpoint = endpoint;
    }

    @Override
    public String toString() {
      return endpoint;
    }

    public String with(Long id) {
      return format(endpoint, id);
    }

  }
}
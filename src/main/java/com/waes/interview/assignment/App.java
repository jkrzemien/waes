package com.waes.interview.assignment;

import com.waes.interview.assignment.differentiator.ByteArrayDiffer;
import com.waes.interview.assignment.differentiator.Differentiable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Main application class
 *
 * @author Juan Krzemien
 */
@SpringBootApplication
public class App {

  /**
   * Application entry point
   *
   * @param args command line arguments to pass to Spring Boot application
   */
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

  /**
   * Spring context configuration.
   * <p>
   * Contains manual definitions for the instances required for autowiring the application.
   * <p>
   * Alternatively, I could have annotated the implementations used in this assignment with @Component and to let
   * Spring recognize them as candidates for autowiring (via @ComponentScan annotation present in @SpringBootApplication).
   * <p>
   * I did not do that because, since I'm not using Spring Profiles, there is a chance of someone adding an alternative
   * implementations for any of the interfaces in this project as a @Component rendering Spring unaware of which
   * implementation to wire, resulting in app crashes when initializing the context.
   */
  @Configuration
  static class AppConfiguration {

    @Bean
    public Differentiable<byte[]> differentiable() {
      return new ByteArrayDiffer();
    }

  }

}

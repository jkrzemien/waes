Feature: Binary differences highlighter REST API

  Scenario: As an JSON REST API consumer, I want to know the differences between two binary files of same size.
    Given I define file 'files/binary.dat' as input for '/v1/diff/{id}/left' endpoint with ID 1
    And Response should have a 200 code
    Given I define file 'files/binaryModified.dat' as input for '/v1/diff/{id}/right' endpoint with ID 1
    And Response should have a 200 code
    When I request differences to endpoint '/v1/diff/{id}' for provided files with ID 1
    Then Response should have a 200 code
    And Response should have message 'Byte arrays are NOT equal!'
    And Response should have the following differences:
      | position | offset |
      | 0        | 2      |
      | 2328     | 3      |

  Scenario: As an JSON REST API consumer, I want to know if two completely different binary files are equals.
    Given I define file 'files/binary.dat' as input for '/v1/diff/{id}/left' endpoint with ID 1
    And Response should have a 200 code
    Given I define file 'files/binary2.dat' as input for '/v1/diff/{id}/right' endpoint with ID 1
    And Response should have a 200 code
    When I request differences to endpoint '/v1/diff/{id}' for provided files with ID 1
    Then Response should have a 200 code
    And Response should have message 'Byte arrays are NOT equal!'
    And Response should have no differences

  Scenario: As an JSON REST API consumer, I want to know if a binary file is equals with itself.
    Given I define file 'files/binary.dat' as input for '/v1/diff/{id}/left' endpoint with ID 1
    And Response should have a 200 code
    Given I define file 'files/binary.dat' as input for '/v1/diff/{id}/right' endpoint with ID 1
    And Response should have a 200 code
    When I request differences to endpoint '/v1/diff/{id}' for provided files with ID 1
    Then Response should have a 200 code
    And Response should have message 'Byte arrays are equal!'
    And Response should have no differences
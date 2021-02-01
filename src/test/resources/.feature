#Author: your.email@your.domain.com
#Keywords Summary :
#Feature: List of scenarios.
#Scenario: Business rule through list of steps with arguments.
#Given: Some precondition step
#When: Some key actions
#Then: To observe outcomes or validation
#And,But: To enumerate more Given,When,Then steps
#Scenario Outline: List of steps for data-driven as an Examples and <placeholder>
#Examples: Container for s table
#Background: List of steps run before each of the scenarios
#""" (Doc Strings)
#| (Data Tables)
#@ (Tags/Labels):To group Scenarios
#<> (placeholder)
#""
## (Comments)
#Sample Feature Definition Template
@tag
Feature: To Test the Weather Data API using Postal Code

  @tag1
  Scenario: As a choosey surfer
    Given I like to surf in any 2 beaches <Out of top ten> of Sydney
    And some other precondition
    And I only like to surf on any 2 days specifically <Thursday & Friday> in next 16 Days
    When I look up the the weather forecast for the next 16 days using POSTAL CODES 
    Then I check to if see the temperature is between <20℃ and 30℃>
    And I check to see if UV index is <= 3
    And I Pick two spots based on suitable weather forecast for the day

    Examples: 
      | name  						| value 							| status  |
      | Out of top ten 		| BONDI AND MANLY     | success |
      | Thursday & Friday | Thursday & Friday 	| success |
      | 20℃ and 30℃ 				| 20℃ and 30℃ 					| success |

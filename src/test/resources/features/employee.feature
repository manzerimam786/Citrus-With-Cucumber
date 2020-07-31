Feature: Employee app

  Scenario: Add employee entry
    Given Employee list is empty
    When I add entry "Code something"
    Then the number of employee entries should be 1

  Scenario: Remove employee entry
    Given Employee list is empty
    When I add entry "1001"
    Then the number of employee entries should be 1
    When I remove entry "1032"
    Then the employee list should be empty

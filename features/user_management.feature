Feature: User Registration

  # BO-1: User Management
  Scenario: A user registers an account and creates a ride group
    Given the user is on the registration page
    When the user fills in the registration form
    And submits the form
    Then the user should be redirected to the dashboard
    And the ride group should be created

  Scenario: A Group Admin invites users to the group
    Given the Group Admin is on the group management page
    When the Group Admin enters the email of the user to invite
    And sends the invitation
    Then the user should receive an invitation email
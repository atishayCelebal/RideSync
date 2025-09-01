Feature: User & Group Management

  # This feature covers user registration, group creation, and user invitation scenarios.
  
  Scenario: Successful user registration
    Given a user provides valid registration details
    When the user submits the registration form
    Then the user should be registered successfully
    And the system should send a confirmation email

  Scenario: Failed user registration due to missing fields
    Given a user provides incomplete registration details
    When the user submits the registration form
    Then the registration should fail
    And the system should display error messages for missing fields

  Scenario: Successful group creation
    Given a registered user is logged in
    When the user creates a new ride group
    Then the group should be created successfully
    And the user should become the group admin

  Scenario: Failed group creation due to insufficient permissions
    Given a guest user is on the platform
    When the user attempts to create a new ride group
    Then the group creation should fail
    And the system should display an access denied message

  Scenario: Inviting a user to a group
    Given a group admin is logged in
    When the admin invites another user via email
    Then the invited user should receive an invitation
    And the user should be able to accept the invitation to join the group

  # Future scenarios can include group management features like removing a user.

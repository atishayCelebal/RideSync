Feature: Device Registration

  # BO-2: Device Management
  Scenario: A Group Admin registers a GPS device
    Given the Group Admin is on the device management page
    When the Group Admin enters the GPS device ID
    And submits the registration form
    Then the GPS device should be registered successfully
    And the device should be associated with the group
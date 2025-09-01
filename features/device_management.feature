Feature: Device Management

  # This feature covers GPS device registration and data ingestion scenarios.

  Scenario: Successful GPS device registration
    Given a user is logged in as a group admin
    When the user provides valid device registration details
    Then the device should be registered successfully
    And the system should associate the device with the user

  Scenario: Failed GPS device registration due to invalid device ID
    Given a user is logged in as a group admin
    When the user provides an invalid device ID
    Then the device registration should fail
    And the system should display an error message

  Scenario: Successful data streaming from the GPS device
    Given a registered GPS device is active
    When the device streams valid location data to the endpoint
    Then the system should acknowledge the data receipt
    And the data should be processed for real-time updates

  Scenario: Handling invalid data from the GPS device
    Given a registered GPS device is active
    When the device streams invalid location data to the endpoint
    Then the system should reject the data
    And the system should log an error for invalid data

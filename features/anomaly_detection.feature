Feature: Anomaly Detection

  # This feature covers the functionality of real-time anomaly detection during a ride session.

  Scenario: Detecting stationary behavior
    Given a member is participating in a ride session
    When the member remains stationary for over 3 minutes
    Then the system should detect the stationary behavior
    And generate an alert for the group admin

  Scenario: Detecting significant deviation from the group path
    Given a member is participating in a ride session
    When the member deviates significantly from the group path
    Then the system should detect the deviation
    And generate an alert for the group admin

  Scenario: Alerting group admin upon anomaly detection
    Given an anomaly has been detected
    When the alert is generated
    Then the group admin should receive a notification
    And the alert message should specify the nature of the anomaly

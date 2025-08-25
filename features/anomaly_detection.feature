Feature: Detect Anomalies

  # BO-5: Anomaly Detection
  Scenario: The system detects and alerts the Group Admin of anomalous behavior
    Given a ride session is active
    When a group member remains stationary for more than 3 minutes
    Then the system should generate an alert
    And the alert should be sent to the Group Admin's dashboard
    And the alert message should specify the member's name and the nature of the anomaly
Feature: View Real-Time Dashboard

  # BO-4: Real-Time Dashboard
  Scenario: A Group Admin views the real-time dashboard during an active ride session
    Given the Group Admin has started a ride session
    When the Group Admin navigates to the dashboard
    Then the dashboard should display the real-time location of all group members
    And the map should update continuously with new location data
Feature: Real-Time Dashboard

  # This feature covers the functionality of the real-time map dashboard and WebSocket connections.

  Scenario: Successful connection to the WebSocket
    Given a user is logged in during an active ride session
    When the dashboard attempts to connect to the WebSocket
    Then the connection should be established successfully
    And the dashboard should be updated with real-time data

  Scenario: Displaying markers for active ride session participants
    Given a user is logged in during an active ride session
    When real-time location data is received via WebSocket
    Then the map should display markers for all active participants
    And the markers should update as new data is received

  Scenario: Handling WebSocket disconnection
    Given a user is logged in during an active ride session
    When the WebSocket connection is lost
    Then the dashboard should display a disconnection message
    And attempt to reconnect to the WebSocket after a delay

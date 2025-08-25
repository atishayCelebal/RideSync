Feature: Ingest Location Data

  # BO-3: Data Ingestion
  Scenario: The system ingests real-time location data from GPS devices
    Given a GPS device is registered and active
    When the device sends location data to the backend
    Then the location data should be ingested successfully
    And the data should be processed for real-time updates
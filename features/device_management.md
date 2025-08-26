# Feature: Device Management

## Scenario: Device Registration
- Given the Group Admin is on the device management page
- When the Group Admin enters a valid GPS device ID
- And clicks the "Register Device" button
- Then the user should see a confirmation message "Device registered successfully."

## Scenario: Device Association
- Given the Group Admin is on the device management page
- When the Group Admin selects a device from the list
- And associates it with a user or vehicle
- Then the user should see a confirmation message "Device associated successfully."

## Scenario: Device Data Streaming
- Given the GPS device is registered and associated with a group
- When the device streams location data
- Then the backend should successfully ingest the location data.

## Scenario: Device Management Error Handling
- Given the Group Admin is on the device management page
- When the Group Admin enters an invalid GPS device ID
- And clicks the "Register Device" button
- Then the user should see an error message "Invalid device ID."
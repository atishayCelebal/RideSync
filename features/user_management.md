# Feature: User Management

## Scenario: User Registration
- Given the user is on the registration page
- When the user enters a valid username, password, and email
- And clicks the "Register" button
- Then the user should see a confirmation message "User registered successfully."

## Scenario: Group Creation
- Given the user is logged in as a Group Admin
- When the user navigates to the group management page
- And enters a group name
- And clicks the "Create Group" button
- Then the user should see a confirmation message "Group created successfully."

## Scenario: Inviting Users
- Given the Group Admin is on the group management page
- When the Group Admin enters an email address of a user to invite
- And clicks the "Invite" button
- Then the invited user should receive an email invitation.

## Scenario: Device Registration
- Given the Group Admin is on the device management page
- When the Group Admin enters a valid GPS device ID
- And clicks the "Register Device" button
- Then the user should see a confirmation message "Device registered successfully."
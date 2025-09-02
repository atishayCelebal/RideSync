package org.celebal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.celebal.api.ApiResponse;
import org.celebal.model.User;
import org.celebal.repository.UserRepository;
import org.celebal.service.SmartcarOAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/smartcar")
@RequiredArgsConstructor
@Slf4j
public class SmartcarOAuthController {

    private final SmartcarOAuthService smartcarOAuthService;
    private final UserRepository userRepository;
    
    @Value("${smartcar.client.id}")
    private String clientId;
    
    @Value("${smartcar.redirect.uri}")
    private String redirectUri;
    
    @Value("${smartcar.scope}")
    private String scope;

    /**
     * Initiate Smartcar OAuth flow
     * Redirects user to Smartcar authorization page
     */
    @GetMapping("/auth")
    public ResponseEntity<ApiResponse<String>> initiateAuth() {
        try {
            String state = UUID.randomUUID().toString();
            String authUrl = smartcarOAuthService.generateAuthorizationUrl(state);
            
            log.info("Initiating Smartcar OAuth flow with state: {}", state);
            
            return ResponseEntity.ok(ApiResponse.ok(
                "Smartcar authorization URL generated",
                authUrl
            ));
            
        } catch (Exception e) {
            log.error("Failed to generate Smartcar authorization URL: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to initiate OAuth flow: " + e.getMessage()
            ));
        }
    }

    /**
     * Handle OAuth callback from Smartcar
     * Exchange authorization code for access token
     * Automatically fetch user's vehicles and create mappings
     * 
     * Note: This endpoint requires proper authentication context.
     * The user ID is automatically extracted from the JWT token.
     */
    @GetMapping("/callback")
    public ResponseEntity<ApiResponse<String>> handleCallback(
            @RequestParam String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            Principal principal) {
        
        try {
            if (error != null) {
                log.error("Smartcar OAuth error: {}", error);
                return ResponseEntity.badRequest().body(ApiResponse.fail(
                    "OAuth authorization failed: " + error
                ));
            }
            
            if (code == null) {
                return ResponseEntity.badRequest().body(ApiResponse.fail(
                    "Authorization code is required"
                ));
            }
            
            if (principal == null || principal.getName() == null || principal.getName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.fail(
                    "Authentication required for OAuth callback"
                ));
            }
            
            String username = principal.getName();
            
            // Get the actual User entity to extract the UUID
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found for username: " + username));
            
            String userId = user.getUserId().toString();
            log.info("Received OAuth callback with code: {}, state: {}, username: {}, userId: {}", code, state, username, userId);
            
            // Exchange code for token
            SmartcarOAuthService.SmartcarTokenResponse tokenResponse = 
                smartcarOAuthService.exchangeCodeForToken(code);
            
            // Store tokens for the user using the UUID
            smartcarOAuthService.storeUserTokens(userId, tokenResponse);
            
            log.info("Successfully obtained and stored Smartcar tokens for user: {}", userId);
            
            // NEW: Automatically fetch vehicles and create mappings
            try {
                log.info("Automatically fetching vehicles for user: {}", userId);
                smartcarOAuthService.autoMapUserVehicles(userId, tokenResponse.getAccessToken());
                log.info("Successfully created vehicle mappings for user: {}", userId);
            } catch (Exception vehicleMappingError) {
                log.warn("Failed to auto-map vehicles for user {}: {}", userId, vehicleMappingError.getMessage());
                // Don't fail the OAuth flow if vehicle mapping fails
            }
            
            return ResponseEntity.ok(ApiResponse.ok(
                "Smartcar OAuth completed successfully. Vehicle mappings created automatically.",
                "Tokens stored and vehicles mapped for user: " + userId
            ));
            
        } catch (Exception e) {
            log.error("Failed to handle OAuth callback: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to complete OAuth flow: " + e.getMessage()
            ));
        }
    }

    /**
     * Check if user has valid Smartcar tokens
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Boolean>> checkTokenStatus(Principal principal) {
        try {
            if (principal == null || principal.getName() == null || principal.getName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.fail(
                    "Authentication required to check token status"
                ));
            }
            
            String username = principal.getName();
            
            // Get the actual User entity to extract the UUID
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found for username: " + username));
            
            String userId = user.getUserId().toString();
            boolean hasValidTokens = smartcarOAuthService.hasValidTokens(userId);
            
            return ResponseEntity.ok(ApiResponse.ok(
                "Token status checked successfully",
                hasValidTokens
            ));
            
        } catch (Exception e) {
            log.error("Failed to check token status for user {}: {}", principal.getName(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to check token status: " + e.getMessage()
            ));
        }
    }

    /**
     * Revoke user's Smartcar tokens
     */
    @PostMapping("/revoke")
    public ResponseEntity<ApiResponse<String>> revokeTokens(Principal principal) {
        try {
            if (principal == null || principal.getName() == null || principal.getName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.fail(
                    "Authentication required to revoke tokens"
                ));
            }
            
            String username = principal.getName();
            
            // Get the actual User entity to extract the UUID
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found for username: " + username));
            
            String userId = user.getUserId().toString();
            smartcarOAuthService.revokeUserTokens(userId);
            
            log.info("Successfully revoked Smartcar tokens for user: {}", userId);
            
            return ResponseEntity.ok(ApiResponse.ok(
                "Tokens revoked successfully",
                "Tokens revoked for user: " + userId
            ));
            
        } catch (Exception e) {
            log.error("Failed to revoke tokens for user {}: {}", principal.getName(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to revoke tokens: " + e.getMessage()
            ));
        }
    }

    /**
     * Handle OAuth callback with automatic user ID extraction from JWT token
     * This is the preferred method for production use
     */
    @GetMapping("/callback/secure")
    public ResponseEntity<ApiResponse<String>> handleCallbackSecure(
            @RequestParam String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            Principal principal) {
        
        try {
            if (error != null) {
                log.error("Smartcar OAuth error: {}", error);
                return ResponseEntity.badRequest().body(ApiResponse.fail(
                    "OAuth authorization failed: " + error
                ));
            }
            
            if (code == null) {
                return ResponseEntity.badRequest().body(ApiResponse.fail(
                    "Authorization code is required"
                ));
            }
            
            if (principal == null || principal.getName() == null || principal.getName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.fail(
                    "Authentication required for OAuth callback"
                ));
            }
            
            String username = principal.getName();
            
            // Get the actual User entity to extract the UUID
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found for username: " + username));
            
            String userId = user.getUserId().toString();
            log.info("Received secure OAuth callback with code: {}, state: {}, username: {}, userId: {}", code, state, username, userId);
            
            // Exchange code for token
            SmartcarOAuthService.SmartcarTokenResponse tokenResponse = 
                smartcarOAuthService.exchangeCodeForToken(code);
            
            // Store tokens for the user using the UUID
            smartcarOAuthService.storeUserTokens(userId, tokenResponse);
            
            log.info("Successfully obtained and stored Smartcar tokens for user: {}", userId);
            
            return ResponseEntity.ok(ApiResponse.ok(
                "Smartcar OAuth completed successfully",
                "Tokens stored for user: " + userId
            ));
            
        } catch (Exception e) {
            log.error("Failed to handle secure OAuth callback: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to complete OAuth flow: " + e.getMessage()
            ));
        }
    }

    // ===== TEST ENDPOINTS FOR DEVELOPMENT =====
    
    /**
     * Test endpoint to simulate OAuth callback with a specific user ID
     * This is for development/testing purposes only
     */
    @GetMapping("/test/callback")
    public ResponseEntity<ApiResponse<String>> testCallback(
            @RequestParam String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            @RequestParam String testUserId) {
        
        try {
            if (error != null) {
                log.error("Smartcar OAuth error: {}", error);
                return ResponseEntity.badRequest().body(ApiResponse.fail(
                    "OAuth authorization failed: " + error
                ));
            }
            
            if (code == null) {
                return ResponseEntity.badRequest().body(ApiResponse.fail(
                    "Authorization code is required"
                ));
            }
            
            if (testUserId == null || testUserId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.fail(
                    "Test user ID is required for testing OAuth callback"
                ));
            }
            
            log.info("Testing OAuth callback with code: {}, state: {}, testUserId: {}", code, state, testUserId);
            
            // Exchange code for token
            SmartcarOAuthService.SmartcarTokenResponse tokenResponse = 
                smartcarOAuthService.exchangeCodeForToken(code);
            
            // Store tokens for the test user
            smartcarOAuthService.storeUserTokens(testUserId, tokenResponse);
            
            log.info("Successfully obtained and stored Smartcar tokens for test user: {}", testUserId);
            
            return ResponseEntity.ok(ApiResponse.ok(
                "Smartcar OAuth test completed successfully",
                "Tokens stored for test user: " + testUserId
            ));
            
        } catch (Exception e) {
            log.error("Failed to handle test OAuth callback: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(
                "Failed to complete test OAuth flow: " + e.getMessage()
            ));
        }
    }
}

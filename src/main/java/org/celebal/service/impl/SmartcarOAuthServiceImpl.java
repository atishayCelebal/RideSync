package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.celebal.model.UserSmartcarToken;
import org.celebal.repository.UserSmartcarTokenRepository;
import org.celebal.service.SmartcarOAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import org.celebal.repository.VehicleSmartcarMappingRepository;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmartcarOAuthServiceImpl implements SmartcarOAuthService {

    private final RestTemplate restTemplate;
    private final UserSmartcarTokenRepository tokenRepository;
    private final VehicleSmartcarMappingRepository vehicleSmartcarMappingRepository;
    
    @Value("${smartcar.client.id}")
    private String clientId;
    
    @Value("${smartcar.client.secret}")
    private String clientSecret;
    
    @Value("${smartcar.redirect.uri}")
    private String redirectUri;
    
    @Value("${smartcar.scope}")
    private String scope;
    
    @Value("${smartcar.auth.url}")
    private String authUrl;
    
    @Value("${smartcar.token.url}")
    private String tokenUrl;

    @Override
    public String generateAuthorizationUrl(String state) {
        try {
            log.debug("Generating Smartcar authorization URL with state: {}", state);
            
            // Build OAuth 2.0 authorization URL
            String authorizationUrl = String.format("%s?response_type=code&client_id=%s&scope=%s&redirect_uri=%s&state=%s&mode=simulated",
                    authUrl,
                    clientId,
                    scope,
                    redirectUri,
                    state);
            
            log.debug("Generated authorization URL: {}", authorizationUrl);
            return authorizationUrl;
            
        } catch (Exception e) {
            log.error("Failed to generate authorization URL: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate Smartcar authorization URL", e);
        }
    }

    @Override
    public SmartcarTokenResponse exchangeCodeForToken(String authorizationCode) {
        try {
            log.info("Exchanging authorization code for access token");
            
            // Prepare HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(clientId, clientSecret);
            
            // Prepare form data
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "authorization_code");
            formData.add("code", authorizationCode);
            formData.add("redirect_uri", redirectUri);
            
            // Create HTTP entity
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
            
            log.debug("Making token exchange request to: {}", tokenUrl);
            
            // First get the raw response to debug
            ResponseEntity<String> rawResponse = restTemplate.postForEntity(
                    tokenUrl, 
                    request, 
                    String.class);
            
            log.info("Smartcar API Response Status: {}", rawResponse.getStatusCode());
            log.info("Smartcar API Response Headers: {}", rawResponse.getHeaders());
            log.info("Raw Smartcar API Response Body: {}", rawResponse.getBody());
            
            if (!rawResponse.getStatusCode().is2xxSuccessful() || rawResponse.getBody() == null) {
                log.error("Token exchange failed with status: {}", rawResponse.getStatusCode());
                throw new RuntimeException("Failed to exchange authorization code for token");
            }
            
            // Now try to parse the response
            try {
                // Use ObjectMapper to parse the JSON manually
                com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                SmartcarTokenResponse tokenResponse = objectMapper.readValue(rawResponse.getBody(), SmartcarTokenResponse.class);
                
                log.info("Successfully parsed token response: {}", tokenResponse);
                
                // Calculate expiration time
                if (tokenResponse.getExpiresIn() != null) {
                    Instant expiresAt = Instant.now().plusSeconds(tokenResponse.getExpiresIn());
                    tokenResponse.setExpiresAt(expiresAt.toString());
                }
                
                log.info("Successfully exchanged authorization code for access token");
                log.debug("Token response: accessToken={}, expiresIn={}, scope={}", 
                    maskToken(tokenResponse.getAccessToken()), 
                    tokenResponse.getExpiresIn(), 
                    tokenResponse.getScope());
                
                return tokenResponse;
                
            } catch (Exception parseError) {
                log.error("Failed to parse token response: {}", parseError.getMessage(), parseError);
                log.error("Raw response body was: {}", rawResponse.getBody());
                throw new RuntimeException("Failed to parse Smartcar token response", parseError);
            }
            

            
        } catch (Exception e) {
            log.error("Failed to exchange authorization code for token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to exchange authorization code for token", e);
        }
    }

    @Override
    public SmartcarTokenResponse refreshAccessToken(String refreshToken) {
        try {
            log.info("Refreshing access token using refresh token");
            
            // Prepare HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(clientId, clientSecret);
            
            // Prepare form data
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "refresh_token");
            formData.add("refresh_token", refreshToken);
            
            // Create HTTP entity
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
            
            log.debug("Making token refresh request to: {}", tokenUrl);
            
            // Make HTTP request to Smartcar token endpoint
            ResponseEntity<SmartcarTokenResponse> response = restTemplate.postForEntity(
                    tokenUrl, 
                    request, 
                    SmartcarTokenResponse.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                SmartcarTokenResponse tokenResponse = response.getBody();
                
                // Calculate expiration time
                if (tokenResponse.getExpiresIn() != null) {
                    Instant expiresAt = Instant.now().plusSeconds(tokenResponse.getExpiresIn());
                    tokenResponse.setExpiresAt(expiresAt.toString());
                }
                
                log.info("Successfully refreshed access token");
                log.debug("Refreshed token: accessToken={}, expiresIn={}", 
                    maskToken(tokenResponse.getAccessToken()), 
                    tokenResponse.getExpiresIn());
                
                return tokenResponse;
            } else {
                log.error("Token refresh failed with status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to refresh access token");
            }
            
        } catch (Exception e) {
            log.error("Failed to refresh access token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to refresh access token", e);
        }
    }

    @Override
    public void storeUserTokens(String userId, SmartcarTokenResponse tokenResponse) {
        try {
            log.info("Storing Smartcar tokens for user: {}", userId);
            
            UUID userUuid = UUID.fromString(userId);
            
            // Parse expiration time
            Instant expiresAt = null;
            if (tokenResponse.getExpiresAt() != null) {
                expiresAt = Instant.parse(tokenResponse.getExpiresAt());
            }
            
            // Create or update token entity
            UserSmartcarToken tokenEntity = UserSmartcarToken.builder()
                    .userId(userUuid)
                    .accessToken(tokenResponse.getAccessToken())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .expiresAt(expiresAt)
                    .build();
            
            // Check if user already has tokens
            Optional<UserSmartcarToken> existingToken = tokenRepository.findByUserId(userUuid);
            if (existingToken.isPresent()) {
                // Update existing token
                UserSmartcarToken existing = existingToken.get();
                existing.setAccessToken(tokenResponse.getAccessToken());
                existing.setRefreshToken(tokenResponse.getRefreshToken());
                existing.setExpiresAt(expiresAt);
                tokenRepository.save(existing);
                log.debug("Updated existing tokens for user: {}", userId);
            } else {
                // Save new token
                tokenRepository.save(tokenEntity);
                log.debug("Saved new tokens for user: {}", userId);
            }
            
            log.info("Successfully stored tokens for user: {}", userId);
            log.debug("Stored token details: accessToken={}, expiresAt={}, scope={}", 
                maskToken(tokenResponse.getAccessToken()), 
                tokenResponse.getExpiresAt(), 
                tokenResponse.getScope());
            
        } catch (Exception e) {
            log.error("Failed to store tokens for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to store user tokens", e);
        }
    }

    @Override
    public String getStoredAccessToken(String userId) {
        try {
            log.debug("Retrieving stored access token for user: {}", userId);
            
            UUID userUuid = UUID.fromString(userId);
            
            // Query database for user's tokens
            Optional<UserSmartcarToken> tokenOpt = tokenRepository.findByUserId(userUuid);
            if (tokenOpt.isEmpty()) {
                log.debug("No tokens found for user: {}", userId);
                return null;
            }
            
            UserSmartcarToken token = tokenOpt.get();
            
            // Check if token is expired
            if (token.isExpired()) {
                log.debug("Token expired for user: {}", userId);
                
                // Try to refresh token if refresh token exists
                if (token.getRefreshToken() != null) {
                    try {
                        log.debug("Attempting to refresh expired token for user: {}", userId);
                        SmartcarTokenResponse refreshedToken = refreshAccessToken(token.getRefreshToken());
                        
                        // Store the refreshed token
                        storeUserTokens(userId, refreshedToken);
                        
                        // Return the new access token
                        return refreshedToken.getAccessToken();
                        
                    } catch (Exception refreshError) {
                        log.warn("Failed to refresh token for user {}: {}", userId, refreshError.getMessage());
                        // Remove expired token
                        tokenRepository.delete(token);
                        return null;
                    }
                } else {
                    // No refresh token, remove expired token
                    tokenRepository.delete(token);
                    return null;
                }
            }
            
            log.debug("Retrieved valid access token for user: {}", userId);
            return token.getAccessToken();
            
        } catch (Exception e) {
            log.error("Failed to retrieve access token for user {}: {}", userId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean hasValidTokens(String userId) {
        try {
            log.debug("Checking if user {} has valid Smartcar tokens", userId);
            
            UUID userUuid = UUID.fromString(userId);
            
            // Use repository method to check token validity
            boolean hasValid = tokenRepository.hasValidTokens(userUuid, Instant.now());
            
            log.debug("User {} has valid Smartcar tokens: {}", userId, hasValid);
            return hasValid;
            
        } catch (Exception e) {
            log.error("Failed to check token validity for user {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void revokeUserTokens(String userId) {
        try {
            log.info("Revoking Smartcar tokens for user: {}", userId);
            
            UUID userUuid = UUID.fromString(userId);
            
            // Find and remove user's tokens from database
            Optional<UserSmartcarToken> tokenOpt = tokenRepository.findByUserId(userUuid);
            if (tokenOpt.isPresent()) {
                UserSmartcarToken token = tokenOpt.get();
                
                // TODO: Call Smartcar token revocation endpoint
                // For now, just remove from database
                
                tokenRepository.delete(token);
                log.info("Successfully revoked tokens for user: {}", userId);
            } else {
                log.debug("No tokens found to revoke for user: {}", userId);
            }
            
        } catch (Exception e) {
            log.error("Failed to revoke tokens for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to revoke user tokens", e);
        }
    }

    @Override
    public void autoMapUserVehicles(String userId, String accessToken) {
        try {
            log.info("Automatically mapping vehicles for user: {}", userId);
            
            // Get user's vehicles from Smartcar
            List<SmartcarOAuthService.SmartcarVehicleInfo> vehicles = getSmartcarVehicles(accessToken);
            
            if (vehicles.isEmpty()) {
                log.info("No vehicles found for user: {}", userId);
                return;
            }
            
            log.info("Found {} vehicles for user: {}", vehicles.size(), userId);
            
            // Create vehicle mappings for each vehicle
            for (SmartcarOAuthService.SmartcarVehicleInfo vehicle : vehicles) {
                try {
                    createVehicleMapping(userId, vehicle, accessToken);
                } catch (Exception vehicleError) {
                    log.warn("Failed to create mapping for vehicle {}: {}", vehicle.getId(), vehicleError.getMessage());
                    // Continue with other vehicles
                }
            }
            
            log.info("Successfully created vehicle mappings for user: {}", userId);
            
        } catch (Exception e) {
            log.error("Failed to auto-map vehicles for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to auto-map user vehicles", e);
        }
    }

    // ===== PRIVATE HELPER METHODS =====

    /**
     * Mask sensitive token data for logging
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 8) {
            return "***";
        }
        return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
    }

    /**
     * Check if a token is expired
     */
    private boolean isTokenExpired(String expiresAt) {
        if (expiresAt == null) {
            return false;
        }
        
        try {
            Instant expirationTime = Instant.parse(expiresAt);
            return Instant.now().isAfter(expirationTime);
        } catch (Exception e) {
            log.warn("Failed to parse token expiration time: {}", expiresAt);
            return false;
        }
    }

    /**
     * Validate token format
     */
    private boolean isValidTokenFormat(String token) {
        return token != null && token.length() > 10 && token.matches("^[A-Za-z0-9._-]+$");
    }

    /**
     * Get user's vehicles from Smartcar API
     */
    private List<SmartcarOAuthService.SmartcarVehicleInfo> getSmartcarVehicles(String accessToken) {
        try {
            log.info("Making request to Smartcar vehicles endpoint");
            log.info("Using access token: {}", maskToken(accessToken));
            
            // Make HTTP request to Smartcar vehicles endpoint
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            log.info("Making HTTP request to: https://api.smartcar.com/v2.0/vehicles");
            
            ResponseEntity<SmartcarOAuthService.SmartcarVehiclesResponse> response = restTemplate.exchange(
                "https://api.smartcar.com/v2.0/vehicles",
                org.springframework.http.HttpMethod.GET,
                request,
                SmartcarOAuthService.SmartcarVehiclesResponse.class
            );
            
            log.info("Smartcar API response status: {}", response.getStatusCode());
            log.info("Smartcar API response headers: {}", response.getHeaders());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<String> vehicleIds = response.getBody().getVehicles();
                log.info("Successfully retrieved {} vehicle IDs from Smartcar API", vehicleIds != null ? vehicleIds.size() : 0);
                
                if (vehicleIds == null || vehicleIds.isEmpty()) {
                    return new ArrayList<>();
                }
                
                // Fetch detailed information for each vehicle
                List<SmartcarOAuthService.SmartcarVehicleInfo> vehicleDetails = new ArrayList<>();
                for (String vehicleId : vehicleIds) {
                    try {
                        SmartcarOAuthService.SmartcarVehicleInfo vehicleInfo = getVehicleDetails(accessToken, vehicleId);
                        if (vehicleInfo != null) {
                            vehicleDetails.add(vehicleInfo);
                        }
                    } catch (Exception e) {
                        log.warn("Failed to get details for vehicle {}: {}", vehicleId, e.getMessage());
                    }
                }
                
                log.info("Successfully retrieved details for {} out of {} vehicles", vehicleDetails.size(), vehicleIds.size());
                return vehicleDetails;
            } else {
                log.warn("Failed to get vehicles from Smartcar API: {}", response.getStatusCode());
                return new ArrayList<>();
            }
            
        } catch (Exception e) {
            log.error("Failed to get vehicles from Smartcar: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get detailed information for a specific vehicle
     */
    private SmartcarOAuthService.SmartcarVehicleInfo getVehicleDetails(String accessToken, String vehicleId) {
        try {
            log.debug("Getting details for vehicle: {}", vehicleId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<SmartcarOAuthService.SmartcarVehicleInfo> response = restTemplate.exchange(
                "https://api.smartcar.com/v2.0/vehicles/" + vehicleId,
                org.springframework.http.HttpMethod.GET,
                request,
                SmartcarOAuthService.SmartcarVehicleInfo.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                SmartcarOAuthService.SmartcarVehicleInfo vehicleInfo = response.getBody();
                vehicleInfo.setId(vehicleId); // Ensure the ID is set
                log.debug("Successfully retrieved details for vehicle: {} - Make: {}, Model: {}, Year: {}", 
                    vehicleId, vehicleInfo.getMake(), vehicleInfo.getModel(), vehicleInfo.getYear());
                return vehicleInfo;
            } else {
                log.warn("Failed to get details for vehicle {}: {}", vehicleId, response.getStatusCode());
                return null;
            }
            
        } catch (Exception e) {
            log.error("Failed to get details for vehicle {}: {}", vehicleId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Create vehicle mapping in database
     */
    private void createVehicleMapping(String userId, SmartcarOAuthService.SmartcarVehicleInfo vehicle, String accessToken) {
        try {
            // Use Smartcar vehicle ID as the internal vehicle ID for simplicity
            // This makes the mapping more meaningful and easier to understand
            UUID internalVehicleId = UUID.fromString(vehicle.getId());
            
            // Create vehicle mapping entity
            org.celebal.model.VehicleSmartcarMapping mapping = org.celebal.model.VehicleSmartcarMapping.builder()
                .vehicleId(internalVehicleId)
                .smartcarVehicleId(vehicle.getId())
                .userId(UUID.fromString(userId))
                .vehicleInfo(String.format("{\"make\":\"%s\",\"model\":\"%s\",\"year\":%d}", 
                    vehicle.getMake(), vehicle.getModel(), vehicle.getYear()))
                .isActive(true)
                .build();
            
            // Save to database
            vehicleSmartcarMappingRepository.save(mapping);
            log.info("Created vehicle mapping: internalId={}, smartcarId={}, make={}, model={}", 
                internalVehicleId, vehicle.getId(), vehicle.getMake(), vehicle.getModel());
            
        } catch (Exception e) {
            log.error("Failed to create vehicle mapping for vehicle {}: {}", vehicle.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to create vehicle mapping", e);
        }
    }
}

package org.celebal.service;

import java.util.UUID;
import java.util.List;

public interface SmartcarOAuthService {
    
    /**
     * Generate OAuth authorization URL for Smartcar
     */
    String generateAuthorizationUrl(String state);
    
    /**
     * Exchange authorization code for access token
     */
    SmartcarTokenResponse exchangeCodeForToken(String authorizationCode);
    
    /**
     * Refresh access token using refresh token
     */
    SmartcarTokenResponse refreshAccessToken(String refreshToken);
    
    /**
     * Store user tokens in database
     */
    void storeUserTokens(String userId, SmartcarTokenResponse tokenResponse);
    
    /**
     * Get stored access token for user
     */
    String getStoredAccessToken(String userId);
    
    /**
     * Check if user has valid tokens
     */
    boolean hasValidTokens(String userId);
    
    /**
     * Revoke user tokens
     */
    void revokeUserTokens(String userId);
    
    /**
     * Automatically fetch user's vehicles from Smartcar and create mappings
     */
    void autoMapUserVehicles(String userId, String accessToken);
    
    // ===== DTOs =====
    
    class SmartcarTokenResponse {
        @com.fasterxml.jackson.annotation.JsonProperty("access_token")
        private String accessToken;
        
        @com.fasterxml.jackson.annotation.JsonProperty("refresh_token")
        private String refreshToken;
        
        @com.fasterxml.jackson.annotation.JsonProperty("token_type")
        private String tokenType;
        
        @com.fasterxml.jackson.annotation.JsonProperty("expires_in")
        private Integer expiresIn;
        
        @com.fasterxml.jackson.annotation.JsonProperty("scope")
        private String scope;
        
        private String expiresAt; // This is calculated, not from API
        
        // Getters and Setters
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
        
        public Integer getExpiresIn() { return expiresIn; }
        public void setExpiresIn(Integer expiresIn) { this.expiresIn = expiresIn; }
        
        public String getScope() { return scope; }
        public void setScope(String scope) { this.scope = scope; }
        
        public String getExpiresAt() { return expiresAt; }
        public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }
        
        @Override
        public String toString() {
            return "SmartcarTokenResponse{" +
                    "accessToken='" + (accessToken != null ? accessToken.substring(0, Math.min(4, accessToken.length())) + "..." : "null") + '\'' +
                    ", refreshToken='" + (refreshToken != null ? refreshToken.substring(0, Math.min(4, refreshToken.length())) + "..." : "null") + '\'' +
                    ", tokenType='" + tokenType + '\'' +
                    ", expiresIn=" + expiresIn +
                    ", scope='" + scope + '\'' +
                    ", expiresAt='" + expiresAt + '\'' +
                    '}';
        }
    }
    
    class SmartcarVehiclesResponse {
        private SmartcarPaging paging;
        private List<String> vehicles;
        
        public SmartcarPaging getPaging() { return paging; }
        public void setPaging(SmartcarPaging paging) { this.paging = paging; }
        
        public List<String> getVehicles() { return vehicles; }
        public void setVehicles(List<String> vehicles) { this.vehicles = vehicles; }
    }
    
    class SmartcarPaging {
        private Integer count;
        private Integer offset;
        
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
        
        public Integer getOffset() { return offset; }
        public void setOffset(Integer offset) { this.offset = offset; }
    }
    
    class SmartcarVehicleInfo {
        private String id;
        private String make;
        private String model;
        private Integer year;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getMake() { return make; }
        public void setMake(String make) { this.make = make; }
        
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        
        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }
    }
}

package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.celebal.config.SmartcarConfig;
import org.celebal.dto.SmartcarDtos.*;
import org.celebal.service.SmartcarService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmartcarServiceImpl implements SmartcarService {

    private final SmartcarConfig smartcarConfig;
    private final WebClient smartcarWebClient;

    @Override
    public AuthUrlResponse generateAuthUrl(String userId) {
        String state = UUID.randomUUID().toString();
        String authUrl = String.format(
            "https://connect.smartcar.com/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=%s&state=%s&mode=simulated",
            smartcarConfig.getClientId(),
            smartcarConfig.getRedirectUri(),
            smartcarConfig.getScope(),
            state
        );
        
        return AuthUrlResponse.builder()
                .authUrl(authUrl)
                .state(state)
                .build();
    }

    @Override
    public TokenResponse exchangeCodeForToken(String code, String state) {
        try {
            TokenExchangeRequest request = TokenExchangeRequest.builder()
                    .grantType("authorization_code")
                    .code(code)
                    .redirectUri(smartcarConfig.getRedirectUri())
                    .clientId(smartcarConfig.getClientId())
                    .clientSecret(smartcarConfig.getClientSecret())
                    .build();

            TokenExchangeResponse response = smartcarWebClient.post()
                    .uri("https://auth.smartcar.com/oauth/token&mode=simulated")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(TokenExchangeResponse.class)
                    .block();

            return TokenResponse.builder()
                    .accessToken(response.getAccessToken())
                    .refreshToken(response.getRefreshToken())
                    .expiresIn(response.getExpiresIn())
                    .tokenType(response.getTokenType())
                    .expiresAt(OffsetDateTime.now().plusSeconds(response.getExpiresIn()))
                    .build();
        } catch (WebClientResponseException e) {
            log.error("Failed to exchange code for token: {}", e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to exchange authorization code for token", e);
        }
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        try {
            TokenExchangeRequest request = TokenExchangeRequest.builder()
                    .grantType("refresh_token")
                    .refreshToken(refreshToken)
                    .clientId(smartcarConfig.getClientId())
                    .clientSecret(smartcarConfig.getClientSecret())
                    .build();

            TokenExchangeResponse response = smartcarWebClient.post()
                    .uri("https://auth.smartcar.com/oauth/token&mode=simulated")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(TokenExchangeResponse.class)
                    .block();

            return TokenResponse.builder()
                    .accessToken(response.getAccessToken())
                    .refreshToken(response.getRefreshToken())
                    .expiresIn(response.getExpiresIn())
                    .tokenType(response.getTokenType())
                    .expiresAt(OffsetDateTime.now().plusSeconds(response.getExpiresIn()))
                    .build();
        } catch (WebClientResponseException e) {
            log.error("Failed to refresh token: {}", e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to refresh token", e);
        }
    }

    @Override
    public List<VehicleInfo> getUserVehicles(String accessToken) {
        try {
            String fullUrl = smartcarConfig.getApiBaseUrl() + "/vehicles";
            log.info("Calling Smartcar API with full URL: {}", fullUrl);
            
            VehicleListResponse response = smartcarWebClient.get()
                    .uri(fullUrl)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(VehicleListResponse.class)
                    .block();
            
            return response.getVehicles();
        } catch (WebClientResponseException e) {
            log.error("Failed to get user vehicles: {}", e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to get user vehicles", e);
        }
    }

    @Override
    public VehicleInfo getVehicleInfo(String accessToken, String vehicleId) {
        try {
            String fullUrl = smartcarConfig.getApiBaseUrl() + "/vehicles/" + vehicleId;
            log.info("Calling Smartcar API with full URL: {}", fullUrl);
            
            return smartcarWebClient.get()
                    .uri(fullUrl)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(VehicleInfo.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Failed to get vehicle info for {}: {}", vehicleId, e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to get vehicle info", e);
        }
    }

    @Override
    public VehicleLocation getVehicleLocation(String accessToken, String vehicleId) {
        try {
            String baseUrl = smartcarConfig.getApiBaseUrl();
            if (baseUrl == null) {
                log.warn("API Base URL is null, using fallback URL");
                baseUrl = "https://api.smartcar.com/v2.0";
            }
            String fullUrl = baseUrl + "/vehicles/" + vehicleId + "/location";
            log.info("Calling Smartcar API with full URL: {}", fullUrl);
            
            return smartcarWebClient.get()
                    .uri(fullUrl)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(VehicleLocation.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Failed to get vehicle location for {}: {}", vehicleId, e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to get vehicle location", e);
        } catch (Exception e) {
            log.error("Unexpected error getting vehicle location for {}: {}", vehicleId, e.getMessage(), e);
            throw new RuntimeException("Failed to get vehicle location", e);
        }
    }

    @Override
    public Double getBatteryLevel(String accessToken, String vehicleId) {
        try {
            BatteryResponse response = smartcarWebClient.get()
                    .uri("/vehicles/{vehicleId}/battery", vehicleId)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(BatteryResponse.class)
                    .block();
            
            return response.getPercentRemaining();
        } catch (WebClientResponseException e) {
            log.error("Failed to get battery level for {}: {}", vehicleId, e.getResponseBodyAsString(), e);
            return null; // Not all vehicles support battery
        }
    }

    @Override
    public Double getFuelLevel(String accessToken, String vehicleId) {
        try {
            FuelResponse response = smartcarWebClient.get()
                    .uri("/vehicles/{vehicleId}/fuel", vehicleId)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(FuelResponse.class)
                    .block();
            
            return response.getPercentRemaining();
        } catch (WebClientResponseException e) {
            log.error("Failed to get fuel level for {}: {}", vehicleId, e.getResponseBodyAsString(), e);
            return null; // Not all vehicles support fuel
        }
    }

    @Override
    public Double getOdometer(String accessToken, String vehicleId) {
        try {
            OdometerResponse response = smartcarWebClient.get()
                    .uri("/vehicles/{vehicleId}/odometer", vehicleId)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(OdometerResponse.class)
                    .block();
            
            return response.getDistance();
        } catch (WebClientResponseException e) {
            log.error("Failed to get odometer for {}: {}", vehicleId, e.getResponseBodyAsString(), e);
            return null; // Not all vehicles support odometer
        }
    }

    @Override
    public Boolean isVehicleLocked(String accessToken, String vehicleId) {
        try {
            LockStatusResponse response = smartcarWebClient.get()
                    .uri("/vehicles/{vehicleId}/security", vehicleId)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(LockStatusResponse.class)
                    .block();
            
            return response.getLocked();
        } catch (WebClientResponseException e) {
            log.error("Failed to get lock status for {}: {}", vehicleId, e.getResponseBodyAsString(), e);
            return null; // Not all vehicles support lock status
        }
    }

    @Override
    public Boolean lockVehicle(String accessToken, String vehicleId) {
        try {
            LockActionRequest request = LockActionRequest.builder()
                    .action("LOCK")
                    .build();
            
            ActionResponse response = smartcarWebClient.post()
                    .uri("/vehicles/{vehicleId}/security", vehicleId)
                    .header("Authorization", "Bearer " + accessToken)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ActionResponse.class)
                    .block();
            
            return response.getStatus().equals("success");
        } catch (WebClientResponseException e) {
            log.error("Failed to lock vehicle {}: {}", vehicleId, e.getResponseBodyAsString(), e);
            return false;
        }
    }

    @Override
    public Boolean unlockVehicle(String accessToken, String vehicleId) {
        try {
            LockActionRequest request = LockActionRequest.builder()
                    .action("UNLOCK")
                    .build();
            
            ActionResponse response = smartcarWebClient.post()
                    .uri("/vehicles/{vehicleId}/security", vehicleId)
                    .header("Authorization", "Bearer " + accessToken)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ActionResponse.class)
                    .block();
            
            return response.getStatus().equals("success");
        } catch (WebClientResponseException e) {
            log.error("Failed to unlock vehicle {}: {}", vehicleId, e.getResponseBodyAsString(), e);
            return false;
        }
    }

    // Additional DTOs for Smartcar API responses
    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    private static class TokenExchangeRequest {
        private String grantType;
        private String code;
        private String redirectUri;
        private String clientId;
        private String clientSecret;
        private String refreshToken;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    private static class TokenExchangeResponse {
        private String accessToken;
        private String refreshToken;
        private Long expiresIn;
        private String tokenType;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    private static class BatteryResponse {
        private Double percentRemaining;
        private Double range;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    private static class FuelResponse {
        private Double percentRemaining;
        private Double range;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    private static class OdometerResponse {
        private Double distance;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    private static class LockStatusResponse {
        private Boolean locked;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    private static class LockActionRequest {
        private String action;
    }

    @lombok.Getter @lombok.Setter @lombok.Builder @lombok.AllArgsConstructor @lombok.NoArgsConstructor
    private static class ActionResponse {
        private String status;
    }
}

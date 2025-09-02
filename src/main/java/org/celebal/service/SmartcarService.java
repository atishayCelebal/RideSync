package org.celebal.service;

import org.celebal.dto.SmartcarDtos.*;

import java.util.List;

public interface SmartcarService {
    
    /**
     * Generate OAuth authorization URL for Smartcar
     */
    AuthUrlResponse generateAuthUrl(String userId);
    
    /**
     * Exchange authorization code for access token
     */
    TokenResponse exchangeCodeForToken(String code, String state);
    
    /**
     * Refresh access token using refresh token
     */
    TokenResponse refreshToken(String refreshToken);
    
    /**
     * Get list of user's vehicles
     */
    List<VehicleInfo> getUserVehicles(String accessToken);
    
    /**
     * Get specific vehicle information
     */
    VehicleInfo getVehicleInfo(String accessToken, String vehicleId);
    
    /**
     * Get vehicle location
     */
    VehicleLocation getVehicleLocation(String accessToken, String vehicleId);
    
    /**
     * Get vehicle battery level (for electric vehicles)
     */
    Double getBatteryLevel(String accessToken, String vehicleId);
    
    /**
     * Get vehicle fuel level (for gas vehicles)
     */
    Double getFuelLevel(String accessToken, String vehicleId);
    
    /**
     * Get vehicle odometer reading
     */
    Double getOdometer(String accessToken, String vehicleId);
    
    /**
     * Check if vehicle is locked
     */
    Boolean isVehicleLocked(String accessToken, String vehicleId);
    
    /**
     * Lock vehicle (if supported)
     */
    Boolean lockVehicle(String accessToken, String vehicleId);
    
    /**
     * Unlock vehicle (if supported)
     */
    Boolean unlockVehicle(String accessToken, String vehicleId);
}

package org.celebal.service;

import org.celebal.dto.VehicleMappingDtos;
import java.util.List;
import java.util.UUID;

public interface VehicleMappingService {
    
    /**
     * Create a new vehicle-Smartcar mapping
     */
    VehicleMappingDtos.VehicleMappingResponse createVehicleMapping(VehicleMappingDtos.CreateVehicleMappingRequest request);
    
    /**
     * Update an existing vehicle mapping
     */
    VehicleMappingDtos.VehicleMappingResponse updateVehicleMapping(UUID mappingId, VehicleMappingDtos.UpdateVehicleMappingRequest request);
    
    /**
     * Get vehicle mapping by ID
     */
    VehicleMappingDtos.VehicleMappingResponse getVehicleMapping(UUID mappingId);
    
    /**
     * Get vehicle mapping by internal vehicle ID
     */
    VehicleMappingDtos.VehicleMappingResponse getVehicleMappingByVehicleId(UUID vehicleId);
    
    /**
     * Get all active mappings for a user
     */
    List<VehicleMappingDtos.VehicleMappingResponse> getUserVehicleMappings(UUID userId);
    
    /**
     * Get all active vehicle mappings
     */
    List<VehicleMappingDtos.VehicleMappingResponse> getAllActiveMappings();
    
    /**
     * Deactivate a vehicle mapping
     */
    void deactivateVehicleMapping(UUID mappingId);
    
    /**
     * Activate a vehicle mapping
     */
    void activateVehicleMapping(UUID mappingId);
    
    /**
     * Delete a vehicle mapping
     */
    void deleteVehicleMapping(UUID mappingId);
    
    /**
     * Fetch vehicle information from Smartcar and update mapping
     */
    VehicleMappingDtos.VehicleMappingResponse refreshVehicleInfo(UUID mappingId);
    
    /**
     * Bulk create vehicle mappings for a user
     */
    List<VehicleMappingDtos.VehicleMappingResponse> bulkCreateMappings(VehicleMappingDtos.BulkCreateMappingsRequest request);
    
    /**
     * Get mapping statistics
     */
    VehicleMappingDtos.MappingStatistics getMappingStatistics();
    
    /**
     * Check if a vehicle has an active Smartcar mapping
     */
    boolean hasActiveMapping(UUID vehicleId);
    
    /**
     * Get Smartcar vehicle ID for internal vehicle
     */
    String getSmartcarVehicleId(UUID vehicleId);
}

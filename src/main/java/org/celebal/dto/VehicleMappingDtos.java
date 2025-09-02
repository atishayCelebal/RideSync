package org.celebal.dto;

import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class VehicleMappingDtos {

    // ===== CREATE VEHICLE MAPPING REQUEST =====
    
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateVehicleMappingRequest {
        private UUID vehicleId;
        private String smartcarVehicleId;
        private UUID userId;
        private String vehicleInfo; // JSON string with make, model, year
    }

    // ===== UPDATE VEHICLE MAPPING REQUEST =====
    
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdateVehicleMappingRequest {
        private String smartcarVehicleId;
        private String vehicleInfo;
        private Boolean isActive;
    }

    // ===== VEHICLE MAPPING RESPONSE =====
    
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class VehicleMappingResponse {
        private UUID id;
        private UUID vehicleId;
        private String smartcarVehicleId;
        private UUID userId;
        private String vehicleInfo;
        private Boolean isActive;
        private Instant lastLocationFetch;
        private Double lastLocationLatitude;
        private Double lastLocationLongitude;
        private Instant createdAt;
        private Instant updatedAt;
        
        // Additional vehicle details
        private String vehicleMake;
        private String vehicleModel;
        private Integer vehicleYear;
        private String vehicleLicensePlate;
    }

    // ===== BULK CREATE MAPPINGS REQUEST =====
    
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BulkCreateMappingsRequest {
        private UUID userId;
        private List<CreateVehicleMappingRequest> mappings;
    }

    // ===== MAPPING STATISTICS =====
    
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MappingStatistics {
        private long totalMappings;
        private long activeMappings;
        private long inactiveMappings;
        private long mappingsWithRecentLocation;
        private long mappingsNeedingRefresh;
        private Instant lastUpdated;
    }

    // ===== VEHICLE INFO FROM SMARTCAR =====
    
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SmartcarVehicleInfo {
        private String id;
        private String make;
        private String model;
        private Integer year;
        private String style;
        private String color;
        private String vin;
        private String fuelType;
        private String transmission;
        private String engine;
        private String trim;
    }

    // ===== VEHICLE MAPPING SUMMARY =====
    
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class VehicleMappingSummary {
        private UUID vehicleId;
        private String smartcarVehicleId;
        private String vehicleDisplayName;
        private Boolean isActive;
        private Instant lastLocationFetch;
        private Boolean hasRecentLocation;
        private String status; // "ACTIVE", "INACTIVE", "NEEDS_REFRESH", "NO_MAPPING"
    }
}

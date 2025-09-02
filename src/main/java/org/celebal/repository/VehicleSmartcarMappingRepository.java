package org.celebal.repository;

import org.celebal.model.VehicleSmartcarMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleSmartcarMappingRepository extends JpaRepository<VehicleSmartcarMapping, UUID> {
    
    /**
     * Find mapping by internal vehicle ID
     */
    Optional<VehicleSmartcarMapping> findByVehicleId(UUID vehicleId);
    
    /**
     * Find mapping by Smartcar vehicle ID
     */
    Optional<VehicleSmartcarMapping> findBySmartcarVehicleId(String smartcarVehicleId);
    
    /**
     * Find all active mappings for a user
     */
    List<VehicleSmartcarMapping> findByUserIdAndIsActiveTrue(UUID userId);
    
    /**
     * Find all active mappings
     */
    List<VehicleSmartcarMapping> findByIsActiveTrue();
    
    /**
     * Find mappings that haven't been updated recently
     */
    @Query("SELECT v FROM VehicleSmartcarMapping v WHERE v.isActive = true AND (v.lastLocationFetch IS NULL OR v.lastLocationFetch < :threshold)")
    List<VehicleSmartcarMapping> findMappingsNeedingLocationUpdate(@Param("threshold") Instant threshold);
    
    /**
     * Find mappings by vehicle IDs
     */
    @Query("SELECT v FROM VehicleSmartcarMapping v WHERE v.vehicleId IN :vehicleIds AND v.isActive = true")
    List<VehicleSmartcarMapping> findByVehicleIds(@Param("vehicleIds") List<UUID> vehicleIds);
    
    /**
     * Check if vehicle has active mapping
     */
    @Query("SELECT COUNT(v) > 0 FROM VehicleSmartcarMapping v WHERE v.vehicleId = :vehicleId AND v.isActive = true")
    boolean hasActiveMapping(@Param("vehicleId") UUID vehicleId);
    
    /**
     * Find mappings with recent location data
     */
    @Query("SELECT v FROM VehicleSmartcarMapping v WHERE v.isActive = true AND v.lastLocationFetch > :since")
    List<VehicleSmartcarMapping> findMappingsWithRecentLocation(@Param("since") Instant since);
    
    /**
     * Count active mappings for a user
     */
    @Query("SELECT COUNT(v) FROM VehicleSmartcarMapping v WHERE v.userId = :userId AND v.isActive = true")
    long countActiveMappingsByUser(@Param("userId") UUID userId);
    
    /**
     * Count all active mappings
     */
    long countByIsActiveTrue();
    
    /**
     * Find mappings that need location refresh (older than specified minutes)
     */
    @Query("SELECT v FROM VehicleSmartcarMapping v WHERE v.isActive = true AND (v.lastLocationFetch IS NULL OR v.lastLocationFetch < :refreshThreshold)")
    List<VehicleSmartcarMapping> findMappingsNeedingRefresh(@Param("refreshThreshold") Instant refreshThreshold);
}

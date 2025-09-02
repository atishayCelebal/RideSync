package org.celebal.service;

import org.celebal.dto.SmartcarDtos.VehicleLocation;

import java.util.List;

public interface LocationSimulatorService {
    
    /**
     * Generate simulated location for a specific vehicle
     */
    VehicleLocation generateSimulatedLocation(String vehicleId);
    
    /**
     * Generate simulated locations for all vehicles in a group
     */
    List<VehicleLocation> generateGroupSimulatedLocations(String groupId);
    
    /**
     * Start continuous simulation (generates locations every few seconds)
     */
    void startSimulation();
    
    /**
     * Stop continuous simulation
     */
    void stopSimulation();
    
    /**
     * Check if simulation is active
     */
    boolean isSimulationActive();
    
    /**
     * Set simulation speed (how often to generate locations)
     */
    void setSimulationSpeed(int intervalSeconds);
    
    /**
     * Generate realistic route simulation between two points
     */
    List<VehicleLocation> generateRouteSimulation(String vehicleId, 
                                                 double startLat, double startLng,
                                                 double endLat, double endLng,
                                                 int numberOfPoints);
}

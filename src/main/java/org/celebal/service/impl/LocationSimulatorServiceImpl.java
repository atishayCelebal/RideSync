package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.celebal.dto.SmartcarDtos.VehicleLocation;
import org.celebal.model.Vehicle;
import org.celebal.repository.VehicleRepository;
import org.celebal.service.GroupLocationService;
import org.celebal.service.LocationService;
import org.celebal.service.LocationSimulatorService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationSimulatorServiceImpl implements LocationSimulatorService {

    private final LocationService locationService;
    private final GroupLocationService groupLocationService;
    private final VehicleRepository vehicleRepository;
    
    private final AtomicBoolean isSimulationActive = new AtomicBoolean(false);
    private final Map<String, SimulatedVehicleState> vehicleStates = new ConcurrentHashMap<>();
    private int simulationIntervalSeconds = 10; // Default: generate location every 10 seconds
    
    // Simulation boundaries (San Francisco area)
    private static final double MIN_LAT = 37.7;
    private static final double MAX_LAT = 37.8;
    private static final double MIN_LNG = -122.5;
    private static final double MAX_LNG = -122.4;

    @Override
    public VehicleLocation generateSimulatedLocation(String vehicleId) {
        try {
            SimulatedVehicleState state = getOrCreateVehicleState(vehicleId);
            
            // Generate realistic movement
            double newLat = state.currentLat + (Math.random() - 0.5) * 0.001; // Small random movement
            double newLng = state.currentLng + (Math.random() - 0.5) * 0.001;
            
            // Keep within bounds
            newLat = Math.max(MIN_LAT, Math.min(MAX_LAT, newLat));
            newLng = Math.max(MIN_LNG, Math.min(MAX_LNG, newLng));
            
            // Calculate speed based on distance moved
            double distance = calculateDistance(state.currentLat, state.currentLng, newLat, newLng);
            double speed = distance * 111000; // Convert to meters per second (roughly)
            
            // Update state
            state.currentLat = newLat;
            state.currentLng = newLng;
            state.lastUpdate = OffsetDateTime.now();
            
            // Create location object
            VehicleLocation location = VehicleLocation.builder()
                    .vehicleId(vehicleId)
                    .latitude(newLat)
                    .longitude(newLng)
                    .timestamp(OffsetDateTime.now())
                    .speed(speed)
                    .heading(calculateHeading(state.currentLat, state.currentLng, newLat, newLng))
                    .altitude(10.0 + Math.random() * 20) // Random altitude between 10-30m
                    .build();
            
            log.debug("Generated simulated location for vehicle {}: ({}, {})", 
                vehicleId, newLat, newLng);
            
            return location;
            
        } catch (Exception e) {
            log.error("Failed to generate simulated location for vehicle {}: {}", vehicleId, e.getMessage());
            return null;
        }
    }

    @Override
    public List<VehicleLocation> generateGroupSimulatedLocations(String groupId) {
        try {
            log.debug("Generating simulated locations for group: {}", groupId);
            
            // Get all vehicles in the group through GroupLocationService
            List<VehicleLocation> existingLocations = groupLocationService.getGroupLocations(groupId);
            
            if (existingLocations.isEmpty()) {
                log.debug("No vehicles found for group: {}", groupId);
                return List.of();
            }
            
            // Generate simulated locations for each vehicle in the group
            List<VehicleLocation> simulatedLocations = existingLocations.stream()
                    .map(existingLocation -> {
                        if (existingLocation != null) {
                            return generateSimulatedLocation(existingLocation.getVehicleId());
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .toList();
            
            log.debug("Generated {} simulated locations for group: {}", simulatedLocations.size(), groupId);
            return simulatedLocations;
                    
        } catch (Exception e) {
            log.error("Failed to generate group simulated locations for group {}: {}", groupId, e.getMessage());
            return List.of();
        }
    }

    @Override
    public void startSimulation() {
        if (isSimulationActive.compareAndSet(false, true)) {
            log.info("Starting location simulation service");
        } else {
            log.warn("Location simulation is already active");
        }
    }

    @Override
    public void stopSimulation() {
        if (isSimulationActive.compareAndSet(true, false)) {
            log.info("Stopping location simulation service");
        } else {
            log.warn("Location simulation is already stopped");
        }
    }

    @Override
    public boolean isSimulationActive() {
        return isSimulationActive.get();
    }

    @Override
    public void setSimulationSpeed(int intervalSeconds) {
        this.simulationIntervalSeconds = Math.max(1, intervalSeconds); // Minimum 1 second
        log.info("Simulation speed set to {} seconds", this.simulationIntervalSeconds);
    }

    @Override
    public List<VehicleLocation> generateRouteSimulation(String vehicleId, 
                                                        double startLat, double startLng,
                                                        double endLat, double endLng, 
                                                        int numberOfPoints) {
        try {
            log.debug("Generating route simulation for vehicle {} from ({}, {}) to ({}, {})", 
                vehicleId, startLat, startLng, endLat, endLng);
            
            List<VehicleLocation> route = new ArrayList<>();
            
            for (int i = 0; i < numberOfPoints; i++) {
                double progress = (double) i / (numberOfPoints - 1);
                
                // Linear interpolation between start and end points
                double lat = startLat + (endLat - startLat) * progress;
                double lng = startLng + (endLng - startLng) * progress;
                
                // Add some realistic variation
                lat += (Math.random() - 0.5) * 0.0001;
                lng += (Math.random() - 0.5) * 0.0001;
                
                VehicleLocation location = VehicleLocation.builder()
                        .vehicleId(vehicleId)
                        .latitude(lat)
                        .longitude(lng)
                        .timestamp(OffsetDateTime.now().plusSeconds(i * 30)) // 30 seconds apart
                        .speed(15.0 + Math.random() * 10) // 15-25 m/s (roughly 35-55 mph)
                        .heading(calculateHeading(startLat, startLng, endLat, endLng))
                        .altitude(15.0 + Math.random() * 10)
                        .build();
                
                route.add(location);
            }
            
            log.debug("Generated {} route points for vehicle {}", route.size(), vehicleId);
            return route;
            
        } catch (Exception e) {
            log.error("Failed to generate route simulation for vehicle {}: {}", vehicleId, e.getMessage());
            return List.of();
        }
    }

    /**
     * Scheduled task that generates simulated locations for all vehicles
     */
    @Scheduled(fixedRate = 10000) // Run every 10 seconds
    public void scheduledLocationGeneration() {
        if (!isSimulationActive.get()) {
            return;
        }
        
        try {
            log.debug("Running scheduled location simulation...");
            
            // Get all vehicles
            List<Vehicle> allVehicles = vehicleRepository.findAll();
            
            if (allVehicles.isEmpty()) {
                log.debug("No vehicles found, skipping simulation");
                return;
            }
            
            log.info("Generating simulated locations for {} vehicles", allVehicles.size());
            
            // Generate locations for each vehicle
            for (Vehicle vehicle : allVehicles) {
                try {
                    VehicleLocation location = generateSimulatedLocation(vehicle.getVehicleId().toString());
                    
                    if (location != null) {
                        // Send to Kafka pipeline and WebSocket for real-time updates
                        locationService.ingestSmartcarLocation(location);
                    }
                    
                } catch (Exception e) {
                    log.error("Failed to generate location for vehicle {}: {}", vehicle.getVehicleId(), e.getMessage());
                }
            }
            
        } catch (Exception e) {
            log.error("Error in scheduled location simulation: {}", e.getMessage(), e);
        }
    }

    /**
     * Get or create a simulated vehicle state
     */
    private SimulatedVehicleState getOrCreateVehicleState(String vehicleId) {
        return vehicleStates.computeIfAbsent(vehicleId, id -> {
            // Generate random starting position
            double startLat = MIN_LAT + Math.random() * (MAX_LAT - MIN_LAT);
            double startLng = MIN_LNG + Math.random() * (MAX_LNG - MIN_LNG);
            
            return new SimulatedVehicleState(startLat, startLng);
        });
    }

    /**
     * Calculate distance between two points in degrees
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double deltaLat = lat2 - lat1;
        double deltaLng = lng2 - lng1;
        return Math.sqrt(deltaLat * deltaLat + deltaLng * deltaLng);
    }

    /**
     * Calculate heading between two points
     */
    private double calculateHeading(double lat1, double lng1, double lat2, double lng2) {
        double deltaLng = lng2 - lng1;
        double y = Math.sin(deltaLng) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLng);
        double heading = Math.atan2(y, x);
        return Math.toDegrees(heading);
    }

    /**
     * Inner class to track vehicle state for simulation
     */
    private static class SimulatedVehicleState {
        double currentLat;
        double currentLng;
        OffsetDateTime lastUpdate;
        
        SimulatedVehicleState(double lat, double lng) {
            this.currentLat = lat;
            this.currentLng = lng;
            this.lastUpdate = OffsetDateTime.now();
        }
    }
}

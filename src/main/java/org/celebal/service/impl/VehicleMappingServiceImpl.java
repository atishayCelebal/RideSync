package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.celebal.dto.VehicleMappingDtos;
import org.celebal.model.Vehicle;
import org.celebal.model.VehicleSmartcarMapping;
import org.celebal.repository.VehicleRepository;
import org.celebal.repository.VehicleSmartcarMappingRepository;
import org.celebal.service.SmartcarOAuthService;
import org.celebal.service.VehicleMappingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleMappingServiceImpl implements VehicleMappingService {

    private final VehicleSmartcarMappingRepository mappingRepository;
    private final VehicleRepository vehicleRepository;
    private final SmartcarOAuthService smartcarOAuthService;
    private final RestTemplate restTemplate;

    @Value("${smartcar.api.base.url}")
    private String smartcarApiBaseUrl;

    @Override
    public VehicleMappingDtos.VehicleMappingResponse createVehicleMapping(VehicleMappingDtos.CreateVehicleMappingRequest request) {
        try {
            log.info("Creating vehicle mapping for vehicle: {} with Smartcar ID: {}", 
                request.getVehicleId(), request.getSmartcarVehicleId());

            // Validate vehicle exists
            Optional<Vehicle> vehicleOpt = vehicleRepository.findById(request.getVehicleId());
            if (vehicleOpt.isEmpty()) {
                throw new IllegalArgumentException("Vehicle not found with ID: " + request.getVehicleId());
            }

            // Check if mapping already exists
            Optional<VehicleSmartcarMapping> existingMapping = mappingRepository.findByVehicleId(request.getVehicleId());
            if (existingMapping.isPresent()) {
                throw new IllegalArgumentException("Vehicle already has a Smartcar mapping");
            }

            // Check if Smartcar vehicle ID is already mapped
            Optional<VehicleSmartcarMapping> existingSmartcarMapping = mappingRepository.findBySmartcarVehicleId(request.getSmartcarVehicleId());
            if (existingSmartcarMapping.isPresent()) {
                throw new IllegalArgumentException("Smartcar vehicle ID already mapped to another vehicle");
            }

            // Create new mapping
            VehicleSmartcarMapping mapping = VehicleSmartcarMapping.builder()
                    .vehicleId(request.getVehicleId())
                    .smartcarVehicleId(request.getSmartcarVehicleId())
                    .userId(request.getUserId())
                    .vehicleInfo(request.getVehicleInfo())
                    .isActive(true)
                    .build();

            // Fetch vehicle info from Smartcar if not provided
            if (request.getVehicleInfo() == null || request.getVehicleInfo().trim().isEmpty()) {
                try {
                    String vehicleInfo = fetchVehicleInfoFromSmartcar(request.getSmartcarVehicleId(), request.getUserId());
                    mapping.setVehicleInfo(vehicleInfo);
                } catch (Exception e) {
                    log.warn("Failed to fetch vehicle info from Smartcar: {}", e.getMessage());
                }
            }

            VehicleSmartcarMapping savedMapping = mappingRepository.save(mapping);
            log.info("Successfully created vehicle mapping with ID: {}", savedMapping.getId());

            return mapToResponse(savedMapping);

        } catch (Exception e) {
            log.error("Failed to create vehicle mapping: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create vehicle mapping", e);
        }
    }

    @Override
    public VehicleMappingDtos.VehicleMappingResponse updateVehicleMapping(UUID mappingId, VehicleMappingDtos.UpdateVehicleMappingRequest request) {
        try {
            log.info("Updating vehicle mapping: {}", mappingId);

            Optional<VehicleSmartcarMapping> mappingOpt = mappingRepository.findById(mappingId);
            if (mappingOpt.isEmpty()) {
                throw new IllegalArgumentException("Vehicle mapping not found with ID: " + mappingId);
            }

            VehicleSmartcarMapping mapping = mappingOpt.get();

            // Update fields if provided
            if (request.getSmartcarVehicleId() != null) {
                // Check if new Smartcar ID is already mapped to another vehicle
                Optional<VehicleSmartcarMapping> existingMapping = mappingRepository.findBySmartcarVehicleId(request.getSmartcarVehicleId());
                if (existingMapping.isPresent() && !existingMapping.get().getId().equals(mappingId)) {
                    throw new IllegalArgumentException("Smartcar vehicle ID already mapped to another vehicle");
                }
                mapping.setSmartcarVehicleId(request.getSmartcarVehicleId());
            }

            if (request.getVehicleInfo() != null) {
                mapping.setVehicleInfo(request.getVehicleInfo());
            }

            if (request.getIsActive() != null) {
                mapping.setIsActive(request.getIsActive());
            }

            VehicleSmartcarMapping updatedMapping = mappingRepository.save(mapping);
            log.info("Successfully updated vehicle mapping: {}", mappingId);

            return mapToResponse(updatedMapping);

        } catch (Exception e) {
            log.error("Failed to update vehicle mapping {}: {}", mappingId, e.getMessage(), e);
            throw new RuntimeException("Failed to update vehicle mapping", e);
        }
    }

    @Override
    public VehicleMappingDtos.VehicleMappingResponse getVehicleMapping(UUID mappingId) {
        try {
            Optional<VehicleSmartcarMapping> mappingOpt = mappingRepository.findById(mappingId);
            if (mappingOpt.isEmpty()) {
                throw new IllegalArgumentException("Vehicle mapping not found with ID: " + mappingId);
            }

            return mapToResponse(mappingOpt.get());

        } catch (Exception e) {
            log.error("Failed to get vehicle mapping {}: {}", mappingId, e.getMessage(), e);
            throw new RuntimeException("Failed to get vehicle mapping", e);
        }
    }

    @Override
    public VehicleMappingDtos.VehicleMappingResponse getVehicleMappingByVehicleId(UUID vehicleId) {
        try {
            Optional<VehicleSmartcarMapping> mappingOpt = mappingRepository.findByVehicleId(vehicleId);
            if (mappingOpt.isEmpty()) {
                throw new IllegalArgumentException("No Smartcar mapping found for vehicle: " + vehicleId);
            }

            return mapToResponse(mappingOpt.get());

        } catch (Exception e) {
            log.error("Failed to get vehicle mapping for vehicle {}: {}", vehicleId, e.getMessage(), e);
            throw new RuntimeException("Failed to get vehicle mapping", e);
        }
    }

    @Override
    public List<VehicleMappingDtos.VehicleMappingResponse> getUserVehicleMappings(UUID userId) {
        try {
            List<VehicleSmartcarMapping> mappings = mappingRepository.findByUserIdAndIsActiveTrue(userId);
            return mappings.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to get vehicle mappings for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to get user vehicle mappings", e);
        }
    }

    @Override
    public List<VehicleMappingDtos.VehicleMappingResponse> getAllActiveMappings() {
        try {
            List<VehicleSmartcarMapping> mappings = mappingRepository.findByIsActiveTrue();
            return mappings.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to get all active vehicle mappings: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get all active vehicle mappings", e);
        }
    }

    @Override
    public void deactivateVehicleMapping(UUID mappingId) {
        try {
            Optional<VehicleSmartcarMapping> mappingOpt = mappingRepository.findById(mappingId);
            if (mappingOpt.isPresent()) {
                VehicleSmartcarMapping mapping = mappingOpt.get();
                mapping.setIsActive(false);
                mappingRepository.save(mapping);
                log.info("Deactivated vehicle mapping: {}", mappingId);
            }

        } catch (Exception e) {
            log.error("Failed to deactivate vehicle mapping {}: {}", mappingId, e.getMessage(), e);
            throw new RuntimeException("Failed to deactivate vehicle mapping", e);
        }
    }

    @Override
    public void activateVehicleMapping(UUID mappingId) {
        try {
            Optional<VehicleSmartcarMapping> mappingOpt = mappingRepository.findById(mappingId);
            if (mappingOpt.isPresent()) {
                VehicleSmartcarMapping mapping = mappingOpt.get();
                mapping.setIsActive(true);
                mappingRepository.save(mapping);
                log.info("Activated vehicle mapping: {}", mappingId);
            }

        } catch (Exception e) {
            log.error("Failed to activate vehicle mapping {}: {}", mappingId, e.getMessage(), e);
            throw new RuntimeException("Failed to activate vehicle mapping", e);
        }
    }

    @Override
    public void deleteVehicleMapping(UUID mappingId) {
        try {
            mappingRepository.deleteById(mappingId);
            log.info("Deleted vehicle mapping: {}", mappingId);

        } catch (Exception e) {
            log.error("Failed to delete vehicle mapping {}: {}", mappingId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete vehicle mapping", e);
        }
    }

    @Override
    public VehicleMappingDtos.VehicleMappingResponse refreshVehicleInfo(UUID mappingId) {
        try {
            log.info("Refreshing vehicle info for mapping: {}", mappingId);

            Optional<VehicleSmartcarMapping> mappingOpt = mappingRepository.findById(mappingId);
            if (mappingOpt.isEmpty()) {
                throw new IllegalArgumentException("Vehicle mapping not found with ID: " + mappingId);
            }

            VehicleSmartcarMapping mapping = mappingOpt.get();

            // Fetch fresh vehicle info from Smartcar
            String vehicleInfo = fetchVehicleInfoFromSmartcar(mapping.getSmartcarVehicleId(), mapping.getUserId());
            mapping.setVehicleInfo(vehicleInfo);

            VehicleSmartcarMapping updatedMapping = mappingRepository.save(mapping);
            log.info("Successfully refreshed vehicle info for mapping: {}", mappingId);

            return mapToResponse(updatedMapping);

        } catch (Exception e) {
            log.error("Failed to refresh vehicle info for mapping {}: {}", mappingId, e.getMessage(), e);
            throw new RuntimeException("Failed to refresh vehicle info", e);
        }
    }

    @Override
    public List<VehicleMappingDtos.VehicleMappingResponse> bulkCreateMappings(VehicleMappingDtos.BulkCreateMappingsRequest request) {
        try {
            log.info("Bulk creating {} vehicle mappings for user: {}", 
                request.getMappings().size(), request.getUserId());

            List<VehicleMappingDtos.VehicleMappingResponse> responses = request.getMappings().stream()
                    .map(mappingRequest -> {
                        try {
                            return createVehicleMapping(mappingRequest);
                        } catch (Exception e) {
                            log.error("Failed to create mapping for vehicle {}: {}", 
                                mappingRequest.getVehicleId(), e.getMessage());
                            return null;
                        }
                    })
                    .filter(response -> response != null)
                    .collect(Collectors.toList());

            log.info("Successfully created {} out of {} vehicle mappings", 
                responses.size(), request.getMappings().size());

            return responses;

        } catch (Exception e) {
            log.error("Failed to bulk create vehicle mappings: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to bulk create vehicle mappings", e);
        }
    }

    @Override
    public VehicleMappingDtos.MappingStatistics getMappingStatistics() {
        try {
            long totalMappings = mappingRepository.count();
            long activeMappings = mappingRepository.countByIsActiveTrue();
            long inactiveMappings = totalMappings - activeMappings;

            Instant recentThreshold = Instant.now().minusSeconds(30 * 60);
            long mappingsWithRecentLocation = mappingRepository.findMappingsWithRecentLocation(recentThreshold).size();

            Instant refreshThreshold = Instant.now().minusSeconds(60 * 60);
            long mappingsNeedingRefresh = mappingRepository.findMappingsNeedingRefresh(refreshThreshold).size();

            return VehicleMappingDtos.MappingStatistics.builder()
                    .totalMappings(totalMappings)
                    .activeMappings(activeMappings)
                    .inactiveMappings(inactiveMappings)
                    .mappingsWithRecentLocation(mappingsWithRecentLocation)
                    .mappingsNeedingRefresh(mappingsNeedingRefresh)
                    .lastUpdated(Instant.now())
                    .build();

        } catch (Exception e) {
            log.error("Failed to get mapping statistics: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get mapping statistics", e);
        }
    }

    @Override
    public boolean hasActiveMapping(UUID vehicleId) {
        try {
            return mappingRepository.hasActiveMapping(vehicleId);
        } catch (Exception e) {
            log.error("Failed to check active mapping for vehicle {}: {}", vehicleId, e.getMessage());
            return false;
        }
    }

    @Override
    public String getSmartcarVehicleId(UUID vehicleId) {
        try {
            Optional<VehicleSmartcarMapping> mappingOpt = mappingRepository.findByVehicleId(vehicleId);
            if (mappingOpt.isPresent() && mappingOpt.get().getIsActive()) {
                return mappingOpt.get().getSmartcarVehicleId();
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to get Smartcar vehicle ID for vehicle {}: {}", vehicleId, e.getMessage());
            return null;
        }
    }

    // ===== PRIVATE HELPER METHODS =====

    /**
     * Fetch vehicle information from Smartcar API
     */
    private String fetchVehicleInfoFromSmartcar(String smartcarVehicleId, UUID userId) {
        try {
            // Get user's access token
            String accessToken = smartcarOAuthService.getStoredAccessToken(userId.toString());
            if (accessToken == null) {
                throw new RuntimeException("No access token found for user: " + userId);
            }

            // Build Smartcar API URL
            String vehicleInfoUrl = String.format("%s/vehicles/%s", smartcarApiBaseUrl, smartcarVehicleId);

            // Prepare headers with access token
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            // Make request to Smartcar API
            ResponseEntity<String> response = restTemplate.exchange(
                    vehicleInfoUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.debug("Successfully fetched vehicle info from Smartcar for vehicle: {}", smartcarVehicleId);
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to fetch vehicle info from Smartcar. Status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Failed to fetch vehicle info from Smartcar for vehicle {}: {}", smartcarVehicleId, e.getMessage());
            throw new RuntimeException("Failed to fetch vehicle info from Smartcar", e);
        }
    }

    /**
     * Map entity to response DTO
     */
    private VehicleMappingDtos.VehicleMappingResponse mapToResponse(VehicleSmartcarMapping mapping) {
        return VehicleMappingDtos.VehicleMappingResponse.builder()
                .id(mapping.getId())
                .vehicleId(mapping.getVehicleId())
                .smartcarVehicleId(mapping.getSmartcarVehicleId())
                .userId(mapping.getUserId())
                .vehicleInfo(mapping.getVehicleInfo())
                .isActive(mapping.getIsActive())
                .lastLocationFetch(mapping.getLastLocationFetch())
                .lastLocationLatitude(mapping.getLastLocationLatitude())
                .lastLocationLongitude(mapping.getLastLocationLongitude())
                .createdAt(mapping.getCreatedAt())
                .updatedAt(mapping.getUpdatedAt())
                .build();
    }
}

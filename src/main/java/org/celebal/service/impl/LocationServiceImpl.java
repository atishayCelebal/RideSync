package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.celebal.dto.LocationDtos;
import org.celebal.dto.SmartcarDtos.VehicleLocation;
import org.celebal.model.Device;
import org.celebal.model.LocationData;
import org.celebal.repository.DeviceRepository;
import org.celebal.repository.LocationDataRepository;
import org.celebal.service.LocationService;
import org.celebal.service.WebSocketService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {

    private final DeviceRepository deviceRepository;
    private final LocationDataRepository locationDataRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final WebSocketService webSocketService;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "ridesync.location";

    @Override
    @Transactional
    public void ingest(LocationDtos.IngestRequest request) {
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new IllegalArgumentException("Unknown device"));

        LocationData entity = LocationData.builder()
                .device(device)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .timestamp(request.getTimestamp())
                .build();
        locationDataRepository.save(entity);

        String payload = String.format("{\"deviceId\":\"%s\",\"latitude\":%s,\"longitude\":%s,\"timestamp\":\"%s\"}",
                request.getDeviceId(), request.getLatitude(), request.getLongitude(), request.getTimestamp());
        kafkaTemplate.send(TOPIC, request.getDeviceId(), payload);
    }

    @Override
    public void ingestSmartcarLocation(VehicleLocation vehicleLocation) {
        try {
            log.info("Ingesting Smartcar location for vehicle: {} at ({}, {})", 
                vehicleLocation.getVehicleId(), vehicleLocation.getLatitude(), vehicleLocation.getLongitude());
            
            // Send to Kafka topic
            String kafkaPayload = objectMapper.writeValueAsString(vehicleLocation);
            kafkaTemplate.send(TOPIC, vehicleLocation.getVehicleId(), kafkaPayload);
            
            // Broadcast to WebSocket for real-time frontend updates
            webSocketService.broadcastLocationUpdate(vehicleLocation);
            
            log.debug("Successfully sent Smartcar location to Kafka and WebSocket for vehicle: {}", 
                vehicleLocation.getVehicleId());
            
        } catch (Exception e) {
            log.error("Failed to ingest Smartcar location for vehicle {}: {}", 
                vehicleLocation.getVehicleId(), e.getMessage(), e);
        }
    }

    @Override
    public void ingestAndBroadcast(LocationDtos.IngestRequest request) {
        try {
            // First ingest to database and Kafka (existing logic)
            ingest(request);
            
            // Then broadcast to WebSocket for real-time updates
            webSocketService.broadcastLocationUpdate(request);
            
            log.debug("Successfully ingested and broadcasted location for device: {}", request.getDeviceId());
            
        } catch (Exception e) {
            log.error("Failed to ingest and broadcast location for device {}: {}", 
                request.getDeviceId(), e.getMessage(), e);
        }
    }
}



package org.celebal.service.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.celebal.model.Device;
import org.celebal.model.LocationData;
import org.celebal.repository.DeviceRepository;
import org.celebal.repository.LocationDataRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationStreamConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DeviceRepository deviceRepository;
    private final LocationDataRepository locationDataRepository;

    @KafkaListener(topics = "ridesync.location", groupId = "ridesync-consumers")
    @Transactional
    public void onMessage(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String deviceId = root.path("deviceId").asText(null);
            Double latitude = root.path("latitude").asDouble();
            Double longitude = root.path("longitude").asDouble();
            String ts = root.path("timestamp").asText(null);

            if (deviceId == null || ts == null) {
                log.warn("Invalid location message: {}", message);
                return;
            }

            Device device = deviceRepository.findById(deviceId).orElse(null);
            if (device == null) {
                log.warn("Unknown device in stream: {}", deviceId);
                return;
            }

            LocationData ld = LocationData.builder()
                    .device(device)
                    .latitude(latitude)
                    .longitude(longitude)
                    .timestamp(OffsetDateTime.parse(ts))
                    .build();
            locationDataRepository.save(ld);
        } catch (Exception ex) {
            log.error("Failed to process location message: {}", message, ex);
        }
    }
}

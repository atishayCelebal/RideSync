package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import org.celebal.dto.LocationDtos;
import org.celebal.model.Device;
import org.celebal.model.LocationData;
import org.celebal.repository.DeviceRepository;
import org.celebal.repository.LocationDataRepository;
import org.celebal.service.LocationService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final DeviceRepository deviceRepository;
    private final LocationDataRepository locationDataRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

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
}



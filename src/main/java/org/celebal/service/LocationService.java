package org.celebal.service;

import org.celebal.dto.LocationDtos;
import org.celebal.dto.SmartcarDtos.VehicleLocation;

public interface LocationService {
    void ingest(LocationDtos.IngestRequest request);
    
    /**
     * Ingest Smartcar vehicle location and send to Kafka + WebSocket
     */
    void ingestSmartcarLocation(VehicleLocation vehicleLocation);
    
    /**
     * Ingest location data and broadcast to frontend
     */
    void ingestAndBroadcast(LocationDtos.IngestRequest request);
}



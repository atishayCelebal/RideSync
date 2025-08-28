package org.celebal.service;

import org.celebal.dto.LocationDtos;

public interface LocationService {
    void ingest(LocationDtos.IngestRequest request);
}



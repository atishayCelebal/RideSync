package org.celebal.repository;

import org.celebal.model.Device;
import org.celebal.model.LocationData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface LocationDataRepository extends JpaRepository<LocationData, Long> {
    List<LocationData> findByDeviceAndTimestampBetweenOrderByTimestampAsc(Device device, OffsetDateTime from, OffsetDateTime to);
}



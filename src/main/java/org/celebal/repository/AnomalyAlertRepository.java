package org.celebal.repository;

import org.celebal.model.AnomalyAlert;
import org.celebal.model.AlertStatus;
import org.celebal.model.Device;
import org.celebal.model.RideSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnomalyAlertRepository extends JpaRepository<AnomalyAlert, UUID> {
    List<AnomalyAlert> findBySession(RideSession session);
    List<AnomalyAlert> findByDeviceAndStatus(Device device, AlertStatus status);
}



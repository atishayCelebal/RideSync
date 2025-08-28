package org.celebal.repository;

import org.celebal.model.Group;
import org.celebal.model.RideSession;
import org.celebal.model.RideSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RideSessionRepository extends JpaRepository<RideSession, UUID> {
    List<RideSession> findByGroup(Group group);
    Optional<RideSession> findFirstByGroupAndStatusOrderByRideStartDesc(Group group, RideSessionStatus status);
}



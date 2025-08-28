package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import org.celebal.model.*;
import org.celebal.repository.RideSessionRepository;
import org.celebal.service.RideSessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RideSessionServiceImpl implements RideSessionService {

    private final RideSessionRepository sessionRepository;

    @Override
    @Transactional
    public RideSession startSession(Group group, User admin) {
        // Check if there's already an active session
        Optional<RideSession> active = getActiveSession(group);
        if (active.isPresent()) {
            throw new IllegalArgumentException("Group already has an active session");
        }

        RideSession session = RideSession.builder()
                .group(group)
                .rideStart(OffsetDateTime.now())
                .status(RideSessionStatus.ACTIVE)
                .build();
        return sessionRepository.save(session);
    }

    @Override
    @Transactional
    public RideSession endSession(Group group, User admin) {
        RideSession session = getActiveSession(group)
                .orElseThrow(() -> new IllegalArgumentException("No active session found"));
        
        session.setRideEnd(OffsetDateTime.now());
        session.setStatus(RideSessionStatus.ENDED);
        return sessionRepository.save(session);
    }

    @Override
    public Optional<RideSession> getActiveSession(Group group) {
        return sessionRepository.findFirstByGroupAndStatusOrderByRideStartDesc(group, RideSessionStatus.ACTIVE);
    }

    @Override
    public List<RideSession> getGroupSessions(Group group) {
        return sessionRepository.findByGroup(group);
    }
}

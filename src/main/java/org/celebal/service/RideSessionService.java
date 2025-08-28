package org.celebal.service;

import org.celebal.model.Group;
import org.celebal.model.RideSession;
import org.celebal.model.User;

import java.util.List;
import java.util.Optional;

public interface RideSessionService {
    RideSession startSession(Group group, User admin);
    RideSession endSession(Group group, User admin);
    Optional<RideSession> getActiveSession(Group group);
    List<RideSession> getGroupSessions(Group group);
}

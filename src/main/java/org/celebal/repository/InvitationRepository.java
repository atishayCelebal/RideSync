package org.celebal.repository;

import org.celebal.model.Group;
import org.celebal.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    List<Invitation> findByGroup(Group group);
    Optional<Invitation> findByToken(String token);
}



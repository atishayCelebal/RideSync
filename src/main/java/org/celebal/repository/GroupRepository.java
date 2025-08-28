package org.celebal.repository;

import org.celebal.model.Group;
import org.celebal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {
    Optional<Group> findByGroupName(String groupName);
    List<Group> findByAdmin(User admin);
    boolean existsByGroupName(String groupName);
}



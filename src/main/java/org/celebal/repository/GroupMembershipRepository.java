package org.celebal.repository;

import org.celebal.model.Group;
import org.celebal.model.GroupMembership;
import org.celebal.model.MembershipStatus;
import org.celebal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, UUID> {
    Optional<GroupMembership> findByGroupAndUser(Group group, User user);
    List<GroupMembership> findByGroup(Group group);
    List<GroupMembership> findByUser(User user);
    List<GroupMembership> findByGroupAndStatus(Group group, MembershipStatus status);
}



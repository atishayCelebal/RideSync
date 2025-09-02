package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import org.celebal.model.*;
import org.celebal.repository.GroupMembershipRepository;
import org.celebal.service.GroupMembershipService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupMembershipServiceImpl implements GroupMembershipService {

    private final GroupMembershipRepository membershipRepository;

    @Override
    @Transactional
    public GroupMembership addMember(Group group, User user) {
        membershipRepository.findByGroupAndUser(group, user).ifPresent(m -> {
            throw new IllegalArgumentException("User already in group");
        });
        GroupMembership m = GroupMembership.builder()
                .group(group)
                .user(user)
                .role(MembershipRole.MEMBER)
                .status(MembershipStatus.ACTIVE)
                .build();
        return membershipRepository.save(m);
    }

    @Override
    public List<GroupMembership> listMembers(Group group) {
        return membershipRepository.findByGroup(group);
    }
}



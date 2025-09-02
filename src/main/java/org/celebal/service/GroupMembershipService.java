package org.celebal.service;

import org.celebal.dto.MembershipDtos;
import org.celebal.model.Group;
import org.celebal.model.GroupMembership;
import org.celebal.model.User;

import java.util.List;

public interface GroupMembershipService {
    GroupMembership addMember(Group group, User user);
    List<GroupMembership> listMembers(Group group);
}



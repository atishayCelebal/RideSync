package org.celebal.service;

import org.celebal.dto.GroupDtos;
import org.celebal.model.Group;
import org.celebal.model.User;

import java.util.List;
import java.util.UUID;

public interface GroupService {
    Group createGroup(User admin, GroupDtos.CreateRequest request);
    List<Group> findGroupsByAdmin(User admin);
    Group getById(UUID groupId);
}



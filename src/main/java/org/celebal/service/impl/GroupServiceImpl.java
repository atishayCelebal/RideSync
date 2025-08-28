package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import org.celebal.dto.GroupDtos;
import org.celebal.model.Group;
import org.celebal.model.User;
import org.celebal.repository.GroupRepository;
import org.celebal.service.GroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    @Override
    @Transactional
    public Group createGroup(User admin, GroupDtos.CreateRequest request) {
        if (groupRepository.existsByGroupName(request.getGroupName())) {
            throw new IllegalArgumentException("Group name already exists");
        }
        Group group = Group.builder()
                .groupName(request.getGroupName())
                .admin(admin)
                .build();
        return groupRepository.save(group);
    }

    @Override
    public List<Group> findGroupsByAdmin(User admin) {
        return groupRepository.findByAdmin(admin);
    }

    @Override
    public Group getById(UUID groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
    }
}



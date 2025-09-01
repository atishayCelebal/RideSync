package com.example.demo.service;

import com.example.demo.entity.Group;
import com.example.demo.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;

    @Transactional
    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }

    public Group findGroupById(UUID groupId) {
        return groupRepository.findById(groupId).orElse(null);
    }

    public void deleteGroup(UUID groupId) {
        groupRepository.deleteById(groupId);
    }
}
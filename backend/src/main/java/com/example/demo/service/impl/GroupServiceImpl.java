package com.example.demo.service.impl;

import com.example.demo.dto.requestsDto.GroupCreationDto;
import com.example.demo.dto.requestsDto.AddUserToGroupDto;
import com.example.demo.service.GroupService;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl implements GroupService {
    @Override
    public String createGroup(GroupCreationDto groupCreationDto) {
        // Logic to create group
        return "Group created successfully.";
    }

    @Override
    public String addUserToGroup(AddUserToGroupDto addUserToGroupDto) {
        // Logic to add user to group
        return "User added successfully.";
    }
}
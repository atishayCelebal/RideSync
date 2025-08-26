package com.example.demo.service;

import com.example.demo.dto.requestsDto.GroupCreationDto;
import com.example.demo.dto.requestsDto.AddUserToGroupDto;

public interface GroupService {
    String createGroup(GroupCreationDto groupCreationDto);
    String addUserToGroup(AddUserToGroupDto addUserToGroupDto);
}
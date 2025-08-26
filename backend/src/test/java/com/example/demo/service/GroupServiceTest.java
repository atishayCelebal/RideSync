package com.example.demo.service;

import com.example.demo.dto.requestsDto.GroupCreationDto;
import com.example.demo.dto.requestsDto.AddUserToGroupDto;
import com.example.demo.repositories.GroupRepository;
import com.example.demo.service.impl.GroupServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class GroupServiceTest {

    @InjectMocks
    private GroupServiceImpl groupService;

    @Mock
    private GroupRepository groupRepository;

    public GroupServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateGroup() {
        GroupCreationDto groupDto = new GroupCreationDto();
        groupDto.setGroupName("Test Group");
        groupDto.setAdminId("admin-id");

        String response = groupService.createGroup(groupDto);
        assertEquals("Group created successfully.", response);
    }

    // @Test
    // public void testAddUserToGroup() {
    //     AddUserToGroupDto addUserDto = new AddUserToGroupDto();
    //     addUserDto.setGroupId("group-id");
    //     addUserDto.setUsername(List.of("testuser"));

    //     String response = groupService.addUserToGroup(addUserDto);
    //     assertEquals("User added successfully.", response);
    // }
}
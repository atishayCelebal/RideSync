package com.example.demo.controllers;

import com.example.demo.dto.requestsDto.GroupCreationDto;
import com.example.demo.dto.requestsDto.AddUserToGroupDto;
import com.example.demo.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping
    public ResponseEntity<String> createGroup(@RequestBody GroupCreationDto groupCreationDto) {
        String response = groupService.createGroup(groupCreationDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addUser")
    public ResponseEntity<String> addUserToGroup(@RequestBody AddUserToGroupDto addUserToGroupDto) {
        String response = groupService.addUserToGroup(addUserToGroupDto);
        return ResponseEntity.ok(response);
    }
}
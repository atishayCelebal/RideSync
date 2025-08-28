package org.celebal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.celebal.api.ApiResponse;
import org.celebal.dto.GroupDtos;
import org.celebal.mapper.GroupMapper;
import org.celebal.model.Group;
import org.celebal.model.User;
import org.celebal.service.GroupService;
import org.celebal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final UserService userService;
    private final GroupMapper groupMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<GroupDtos.GroupResponse>> create(@Valid @RequestBody GroupDtos.CreateRequest request,
                                                                       Principal principal) {
        User admin = userService.findByUsername(principal.getName()).orElseThrow();
        Group group = groupService.createGroup(admin, request);
        return ResponseEntity.ok(ApiResponse.ok("Group created successfully.", groupMapper.toResponse(group)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<GroupDtos.GroupResponse>>> myAdminGroups(Principal principal) {
        User admin = userService.findByUsername(principal.getName()).orElseThrow();
        List<GroupDtos.GroupResponse> list = groupService.findGroupsByAdmin(admin).stream().map(groupMapper::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok("Groups fetched.", list));
    }
}



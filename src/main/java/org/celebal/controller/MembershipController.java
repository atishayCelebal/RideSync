package org.celebal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.celebal.api.ApiResponse;
import org.celebal.dto.MembershipDtos;
import org.celebal.mapper.MembershipMapper;
import org.celebal.model.Group;
import org.celebal.model.GroupMembership;
import org.celebal.model.User;
import org.celebal.service.GroupMembershipService;
import org.celebal.service.GroupService;
import org.celebal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/members")
@RequiredArgsConstructor
public class MembershipController {

    private final GroupService groupService;
    private final GroupMembershipService membershipService;
    private final UserService userService;
    private final MembershipMapper membershipMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<MembershipDtos.MemberResponse>> addMember(@PathVariable("groupId") String groupId,
                                                                               @Valid @RequestBody MembershipDtos.AddMemberRequest request,
                                                                               Principal principal) {
        Group group = groupService.getById(java.util.UUID.fromString(groupId));
        User user = userService.findById(request.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        GroupMembership m = membershipService.addMember(group, user);
        return ResponseEntity.ok(ApiResponse.ok("Member added.", membershipMapper.toResponse(m)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MembershipDtos.MemberResponse>>> list(@PathVariable("groupId") String groupId) {
        Group group = groupService.getById(java.util.UUID.fromString(groupId));
        List<MembershipDtos.MemberResponse> list = membershipService.listMembers(group).stream().map(membershipMapper::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok("Members fetched.", list));
    }
}



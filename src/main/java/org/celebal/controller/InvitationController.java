package org.celebal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.celebal.api.ApiResponse;
import org.celebal.dto.InvitationDtos;
import org.celebal.mapper.InvitationMapper;
import org.celebal.model.Group;
import org.celebal.service.GroupService;
import org.celebal.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;
    private final GroupService groupService;
    private final InvitationMapper invitationMapper;

    @PostMapping("/{groupId}")
    public ResponseEntity<ApiResponse<InvitationDtos.InvitationResponse>> create(@PathVariable("groupId") String groupId,
                                                                                @Valid @RequestBody InvitationDtos.CreateInviteRequest request) {
        Group group = groupService.getById(java.util.UUID.fromString(groupId));
        var inv = invitationService.createInvite(group, request.getInvitedUsername());
        return ResponseEntity.ok(ApiResponse.ok("Invitation created.", invitationMapper.toResponse(inv)));
    }

    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<Void>> accept(@Valid @RequestBody InvitationDtos.AcceptInviteRequest request,
                                                    @RequestHeader("X-Username") String username) {
        invitationService.accept(request.getToken(), username);
        return ResponseEntity.ok(ApiResponse.ok("Invitation accepted.", null));
    }
}



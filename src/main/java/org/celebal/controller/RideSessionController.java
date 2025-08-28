package org.celebal.controller;

import lombok.RequiredArgsConstructor;
import org.celebal.api.ApiResponse;
import org.celebal.dto.RideSessionDtos;
import org.celebal.mapper.RideSessionMapper;
import org.celebal.model.Group;
import org.celebal.model.RideSession;
import org.celebal.model.User;
import org.celebal.service.GroupService;
import org.celebal.service.RideSessionService;
import org.celebal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class RideSessionController {

    private final RideSessionService sessionService;
    private final GroupService groupService;
    private final UserService userService;
    private final RideSessionMapper sessionMapper;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<RideSessionDtos.SessionResponse>> startSession(@RequestParam String groupId,
                                                                                   Principal principal) {
        User admin = userService.findByUsername(principal.getName()).orElseThrow();
        Group group = groupService.getById(java.util.UUID.fromString(groupId));
        RideSession session = sessionService.startSession(group, admin);
        return ResponseEntity.ok(ApiResponse.ok("Ride session started.", sessionMapper.toResponse(session)));
    }

    @PostMapping("/end")
    public ResponseEntity<ApiResponse<RideSessionDtos.SessionResponse>> endSession(@RequestParam String groupId,
                                                                                 Principal principal) {
        User admin = userService.findByUsername(principal.getName()).orElseThrow();
        Group group = groupService.getById(java.util.UUID.fromString(groupId));
        RideSession session = sessionService.endSession(group, admin);
        return ResponseEntity.ok(ApiResponse.ok("Ride session ended.", sessionMapper.toResponse(session)));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<RideSessionDtos.SessionResponse>> getActiveSession(@RequestParam String groupId) {
        Group group = groupService.getById(java.util.UUID.fromString(groupId));
        RideSession session = sessionService.getActiveSession(group)
                .orElseThrow(() -> new IllegalArgumentException("No active session found"));
        return ResponseEntity.ok(ApiResponse.ok("Active session fetched.", sessionMapper.toResponse(session)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<RideSessionDtos.SessionResponse>>> getGroupSessions(@RequestParam String groupId) {
        Group group = groupService.getById(java.util.UUID.fromString(groupId));
        List<RideSessionDtos.SessionResponse> sessions = sessionService.getGroupSessions(group)
                .stream()
                .map(sessionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Session history fetched.", sessions));
    }
}

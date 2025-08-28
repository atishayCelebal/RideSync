package org.celebal.service.impl;

import lombok.RequiredArgsConstructor;
import org.celebal.model.*;
import org.celebal.repository.GroupMembershipRepository;
import org.celebal.repository.InvitationRepository;
import org.celebal.repository.UserRepository;
import org.celebal.service.InvitationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final GroupMembershipRepository membershipRepository;

    @Override
    @Transactional
    public Invitation createInvite(Group group, String invitedUsername) {
        String token = UUID.randomUUID().toString();
        Invitation invitation = Invitation.builder()
                .group(group)
                .invitedUsername(invitedUsername)
                .token(token)
                .status("PENDING")
                .expiresAt(OffsetDateTime.now().plusDays(7))
                .build();
        return invitationRepository.save(invitation);
    }

    @Override
    @Transactional
    public void accept(String token, String username) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        if (invitation.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("Invitation expired");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // add to group as active member
        membershipRepository.findByGroupAndUser(invitation.getGroup(), user).ifPresent(m -> {
            throw new IllegalArgumentException("User already in group");
        });
        GroupMembership m = GroupMembership.builder()
                .group(invitation.getGroup())
                .user(user)
                .role(MembershipRole.MEMBER)
                .status(MembershipStatus.ACTIVE)
                .build();
        membershipRepository.save(m);

        invitation.setStatus("ACCEPTED");
        invitationRepository.save(invitation);
    }
}



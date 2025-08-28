package org.celebal.service;

import org.celebal.dto.InvitationDtos;
import org.celebal.model.Group;
import org.celebal.model.Invitation;

public interface InvitationService {
    Invitation createInvite(Group group, String invitedUsername);
    void accept(String token, String username);
}



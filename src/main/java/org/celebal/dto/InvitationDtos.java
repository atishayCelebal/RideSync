package org.celebal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

public class InvitationDtos {
    @Getter
    @Setter
    public static class CreateInviteRequest {
        @NotBlank
        private String invitedUsername;
    }

    @Getter
    @Setter
    public static class AcceptInviteRequest {
        @NotBlank
        private String token;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InvitationResponse {
        private UUID invitationId;
        private UUID groupId;
        private String invitedUsername;
        private String status;
        private String token;
    }
}



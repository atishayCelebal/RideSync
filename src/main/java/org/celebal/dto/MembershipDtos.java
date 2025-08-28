package org.celebal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

public class MembershipDtos {

    @Getter
    @Setter
    public static class AddMemberRequest {
        @NotNull
        private UUID userId;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberResponse {
        private UUID membershipId;
        private UUID userId;
        private String username;
        private String role;
        private String status;
    }
}



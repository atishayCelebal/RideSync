package org.celebal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

public class GroupDtos {

    @Getter
    @Setter
    public static class CreateRequest {
        @NotBlank
        @Size(max = 150)
        private String groupName;
    }

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
    public static class GroupResponse {
        private UUID groupId;
        private String groupName;
        private UUID adminUserId;
    }
}



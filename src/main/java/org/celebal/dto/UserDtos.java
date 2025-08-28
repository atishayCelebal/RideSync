package org.celebal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

public class UserDtos {

    @Getter
    @Setter
    public static class RegisterRequest {
        @NotBlank
        @Size(min = 3, max = 100)
        private String username;

        @NotBlank
        @Size(min = 2, max = 150)
        private String name;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Size(min = 6, max = 100)
        private String password;

        @Size(max = 150)
        private String vehicleName;

        @Size(max = 50)
        private String vehicleRegNo;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserResponse {
        private UUID userId;
        private String username;
        private String name;
        private String email;
    }
}



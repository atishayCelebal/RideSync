package org.celebal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "invitations")
public class Invitation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "invitation_id", nullable = false, updatable = false)
    private UUID invitationId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "invited_username", nullable = false, length = 100)
    private String invitedUsername;

    @Column(name = "token", nullable = false, length = 200)
    private String token;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;
}



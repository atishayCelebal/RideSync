package org.celebal.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_smartcar_tokens")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSmartcarToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "access_token", nullable = false, length = 500)
    private String accessToken;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ===== BUSINESS LOGIC METHODS =====

    /**
     * Check if the token is expired
     */
    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    /**
     * Check if the token expires soon (within specified minutes)
     */
    public boolean expiresSoon(int minutes) {
        if (expiresAt == null) {
            return false;
        }
        Instant threshold = Instant.now().plusSeconds(minutes * 60);
        return expiresAt.isBefore(threshold);
    }

    /**
     * Check if the token is valid (not expired)
     */
    public boolean isValid() {
        return !isExpired();
    }

    /**
     * Get time until expiration in seconds
     */
    public long getSecondsUntilExpiration() {
        if (expiresAt == null) {
            return -1;
        }
        return Instant.now().until(expiresAt, java.time.temporal.ChronoUnit.SECONDS);
    }

    // ===== BUILDER METHODS =====

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

package org.celebal.repository;

import org.celebal.model.UserSmartcarToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSmartcarTokenRepository extends JpaRepository<UserSmartcarToken, UUID> {
    
    /**
     * Find token by user ID
     */
    Optional<UserSmartcarToken> findByUserId(UUID userId);
    
    /**
     * Find token by user ID (string version)
     */
    @Query("SELECT t FROM UserSmartcarToken t WHERE t.userId = :userId")
    Optional<UserSmartcarToken> findByUserIdString(@Param("userId") String userId);
    
    /**
     * Find all expired tokens
     */
    @Query("SELECT t FROM UserSmartcarToken t WHERE t.expiresAt < :currentTime")
    List<UserSmartcarToken> findExpiredTokens(@Param("currentTime") Instant currentTime);
    
    /**
     * Find tokens expiring soon (within specified minutes)
     */
    @Query("SELECT t FROM UserSmartcarToken t WHERE t.expiresAt BETWEEN :currentTime AND :expiryThreshold")
    List<UserSmartcarToken> findTokensExpiringSoon(
            @Param("currentTime") Instant currentTime,
            @Param("expiryThreshold") Instant expiryThreshold);
    
    /**
     * Check if user has valid tokens
     */
    @Query("SELECT COUNT(t) > 0 FROM UserSmartcarToken t WHERE t.userId = :userId AND t.expiresAt > :currentTime")
    boolean hasValidTokens(@Param("userId") UUID userId, @Param("currentTime") Instant currentTime);
    
    /**
     * Delete expired tokens
     */
    @Query("DELETE FROM UserSmartcarToken t WHERE t.expiresAt < :currentTime")
    void deleteExpiredTokens(@Param("currentTime") Instant currentTime);
}

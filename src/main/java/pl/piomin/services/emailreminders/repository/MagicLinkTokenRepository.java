package pl.piomin.services.emailreminders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.piomin.services.emailreminders.model.MagicLinkToken;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MagicLinkTokenRepository extends JpaRepository<MagicLinkToken, Long> {

    Optional<MagicLinkToken> findByTokenAndUsedFalseAndExpiresAtAfter(String token, LocalDateTime now);

    @Modifying
    @Query("DELETE FROM MagicLinkToken t WHERE t.expiresAt < :threshold OR t.used = true")
    int deleteExpiredOrUsedTokens(@Param("threshold") LocalDateTime threshold);
}

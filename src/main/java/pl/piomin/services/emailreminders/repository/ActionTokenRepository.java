package pl.piomin.services.emailreminders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.piomin.services.emailreminders.model.ActionToken;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ActionTokenRepository extends JpaRepository<ActionToken, Long> {

    Optional<ActionToken> findByTokenAndUsedFalseAndExpiresAtAfter(String token, LocalDateTime now);

    @Modifying
    @Query("DELETE FROM ActionToken t WHERE t.expiresAt < :threshold")
    int deleteExpiredTokens(@Param("threshold") LocalDateTime threshold);
}

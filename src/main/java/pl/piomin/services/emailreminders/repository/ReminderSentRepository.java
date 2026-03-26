package pl.piomin.services.emailreminders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.piomin.services.emailreminders.model.ReminderSent;

import java.util.Optional;

@Repository
public interface ReminderSentRepository extends JpaRepository<ReminderSent, Long> {

    Optional<ReminderSent> findByUserIdAndEventInstanceId(Long userId, Long eventInstanceId);

    boolean existsByUserIdAndEventInstanceId(Long userId, Long eventInstanceId);
}

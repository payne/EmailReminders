package pl.piomin.services.emailreminders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.piomin.services.emailreminders.model.ReminderPreference;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderPreferenceRepository extends JpaRepository<ReminderPreference, Long> {

    Optional<ReminderPreference> findByUserIdAndEventInstanceId(Long userId, Long eventInstanceId);

    Optional<ReminderPreference> findByUserIdAndEventIdAndAllEventInstancesTrue(Long userId, Long eventId);

    @Query("SELECT rp FROM ReminderPreference rp " +
           "WHERE rp.user.id = :userId " +
           "AND (rp.eventInstance.id = :instanceId " +
           "     OR (rp.event.id = :eventId AND rp.allEventInstances = true))")
    List<ReminderPreference> findUserPreferencesForInstance(
            @Param("userId") Long userId,
            @Param("instanceId") Long instanceId,
            @Param("eventId") Long eventId);

    List<ReminderPreference> findByUserId(Long userId);

    List<ReminderPreference> findByEventId(Long eventId);
}

package pl.piomin.services.emailreminders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.piomin.services.emailreminders.model.EventInstance;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventInstanceRepository extends JpaRepository<EventInstance, Long> {

    @Query("SELECT ei FROM EventInstance ei " +
           "JOIN FETCH ei.event e " +
           "WHERE ei.instanceTime BETWEEN :start AND :end " +
           "AND ei.cancelled = false")
    List<EventInstance> findInstancesInTimeRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<EventInstance> findByEventIdAndCancelledFalseOrderByInstanceTimeAsc(Long eventId);

    @Query("SELECT ei FROM EventInstance ei " +
           "JOIN FETCH ei.event e " +
           "LEFT JOIN FETCH e.groups g " +
           "LEFT JOIN FETCH g.memberships m " +
           "LEFT JOIN FETCH m.user " +
           "WHERE ei.id = :id")
    Optional<EventInstance> findByIdWithEventAndGroupMembers(@Param("id") Long id);

    @Query("SELECT ei FROM EventInstance ei " +
           "JOIN FETCH ei.event e " +
           "WHERE ei.instanceTime BETWEEN :start AND :end " +
           "AND ei.cancelled = false " +
           "ORDER BY ei.instanceTime ASC")
    List<EventInstance> findUpcomingInstances(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}

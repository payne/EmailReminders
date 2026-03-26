package pl.piomin.services.emailreminders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.piomin.services.emailreminders.model.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT DISTINCT e FROM Event e " +
           "JOIN e.groups g " +
           "JOIN g.memberships m " +
           "WHERE m.user.id = :userId")
    List<Event> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT e FROM Event e " +
           "LEFT JOIN FETCH e.groups " +
           "LEFT JOIN FETCH e.instances " +
           "WHERE e.id = :id")
    Optional<Event> findByIdWithGroupsAndInstances(@Param("id") Long id);

    @Query("SELECT DISTINCT e FROM Event e " +
           "LEFT JOIN FETCH e.groups " +
           "WHERE e.createdBy.id = :userId")
    List<Event> findByCreatedByIdWithGroups(@Param("userId") Long userId);

    List<Event> findByCreatedById(Long userId);
}

package pl.piomin.services.emailreminders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.piomin.services.emailreminders.model.UserGroup;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    @Query("SELECT DISTINCT g FROM UserGroup g " +
           "LEFT JOIN FETCH g.memberships m " +
           "LEFT JOIN FETCH m.user " +
           "WHERE g.id = :id")
    Optional<UserGroup> findByIdWithMembers(@Param("id") Long id);

    @Query("SELECT DISTINCT g FROM UserGroup g " +
           "JOIN g.memberships m " +
           "WHERE m.user.id = :userId")
    List<UserGroup> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT g FROM UserGroup g " +
           "LEFT JOIN FETCH g.memberships m " +
           "LEFT JOIN FETCH m.user " +
           "JOIN g.memberships m2 " +
           "WHERE m2.user.id = :userId")
    List<UserGroup> findAllByUserIdWithMembers(@Param("userId") Long userId);

    List<UserGroup> findByOwnerId(Long ownerId);
}

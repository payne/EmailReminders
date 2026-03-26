package pl.piomin.services.emailreminders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.piomin.services.emailreminders.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.groupMemberships gm " +
           "LEFT JOIN FETCH gm.group WHERE u.email = :email")
    Optional<User> findByEmailWithGroups(@Param("email") String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.groupMemberships gm " +
           "LEFT JOIN FETCH gm.group WHERE u.id = :id")
    Optional<User> findByIdWithGroups(@Param("id") Long id);
}

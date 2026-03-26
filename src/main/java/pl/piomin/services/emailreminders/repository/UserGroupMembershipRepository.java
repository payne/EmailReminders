package pl.piomin.services.emailreminders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.piomin.services.emailreminders.model.UserGroupMembership;
import pl.piomin.services.emailreminders.model.UserGroupMembershipId;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGroupMembershipRepository extends JpaRepository<UserGroupMembership, UserGroupMembershipId> {

    Optional<UserGroupMembership> findByUserIdAndGroupId(Long userId, Long groupId);

    List<UserGroupMembership> findByGroupId(Long groupId);

    List<UserGroupMembership> findByUserId(Long userId);

    boolean existsByUserIdAndGroupId(Long userId, Long groupId);

    void deleteByUserIdAndGroupId(Long userId, Long groupId);
}

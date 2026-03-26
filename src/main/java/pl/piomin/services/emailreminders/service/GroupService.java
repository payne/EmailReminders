package pl.piomin.services.emailreminders.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.piomin.services.emailreminders.dto.request.AddMembersRequest;
import pl.piomin.services.emailreminders.dto.request.GroupCreateRequest;
import pl.piomin.services.emailreminders.dto.request.GroupUpdateRequest;
import pl.piomin.services.emailreminders.dto.response.GroupResponse;
import pl.piomin.services.emailreminders.exception.ResourceNotFoundException;
import pl.piomin.services.emailreminders.exception.UnauthorizedException;
import pl.piomin.services.emailreminders.model.GroupRole;
import pl.piomin.services.emailreminders.model.User;
import pl.piomin.services.emailreminders.model.UserGroup;
import pl.piomin.services.emailreminders.model.UserGroupMembership;
import pl.piomin.services.emailreminders.model.UserGroupMembershipId;
import pl.piomin.services.emailreminders.repository.UserGroupMembershipRepository;
import pl.piomin.services.emailreminders.repository.UserGroupRepository;
import pl.piomin.services.emailreminders.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GroupService {

    private final UserGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupMembershipRepository membershipRepository;

    public GroupService(UserGroupRepository groupRepository,
                        UserRepository userRepository,
                        UserGroupMembershipRepository membershipRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional
    public GroupResponse create(Long ownerId, GroupCreateRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", ownerId));

        UserGroup group = new UserGroup(request.getName(), owner);
        group.setDescription(request.getDescription());

        UserGroup saved = groupRepository.save(group);

        // Add owner as member with OWNER role
        UserGroupMembership ownerMembership = new UserGroupMembership();
        ownerMembership.setId(new UserGroupMembershipId(owner.getId(), saved.getId()));
        ownerMembership.setUser(owner);
        ownerMembership.setGroup(saved);
        ownerMembership.setRole(GroupRole.OWNER);
        ownerMembership.setJoinedAt(LocalDateTime.now());
        membershipRepository.save(ownerMembership);

        return findById(saved.getId());
    }

    public GroupResponse findById(Long id) {
        UserGroup group = groupRepository.findByIdWithMembers(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", id));
        return GroupResponse.fromEntity(group);
    }

    public List<GroupResponse> findByUserId(Long userId) {
        List<UserGroup> groups = groupRepository.findAllByUserIdWithMembers(userId);
        return groups.stream()
                .map(GroupResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupResponse update(Long id, Long userId, GroupUpdateRequest request) {
        UserGroup group = groupRepository.findByIdWithMembers(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", id));

        checkModifyPermission(group, userId);

        if (request.getName() != null && !request.getName().isBlank()) {
            group.setName(request.getName());
        }
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }

        groupRepository.save(group);
        return GroupResponse.fromEntity(group);
    }

    @Transactional
    public void addMembers(Long groupId, Long userId, AddMembersRequest request) {
        UserGroup group = groupRepository.findByIdWithMembers(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", groupId));

        checkModifyPermission(group, userId);

        for (String email : request.getEmails()) {
            User user = userRepository.findByEmail(email.toLowerCase().trim()).orElse(null);
            if (user != null && !membershipRepository.existsByUserIdAndGroupId(user.getId(), groupId)) {
                UserGroupMembership membership = new UserGroupMembership();
                membership.setId(new UserGroupMembershipId(user.getId(), groupId));
                membership.setUser(user);
                membership.setGroup(group);
                membership.setRole(request.getRole() != null ? request.getRole() : GroupRole.MEMBER);
                membership.setJoinedAt(LocalDateTime.now());
                membershipRepository.save(membership);
            }
        }
    }

    @Transactional
    public void removeMember(Long groupId, Long targetUserId, Long requestingUserId) {
        UserGroup group = groupRepository.findByIdWithMembers(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", groupId));

        // Check if requesting user has permission
        if (!requestingUserId.equals(targetUserId)) {
            checkModifyPermission(group, requestingUserId);
        }

        // Cannot remove owner
        if (group.getOwner().getId().equals(targetUserId)) {
            throw new UnauthorizedException("Cannot remove the group owner");
        }

        membershipRepository.deleteByUserIdAndGroupId(targetUserId, groupId);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        UserGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", id));

        // Only owner can delete
        if (!group.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Only the owner can delete this group");
        }

        groupRepository.delete(group);
    }

    public boolean isMember(Long groupId, Long userId) {
        return membershipRepository.existsByUserIdAndGroupId(userId, groupId);
    }

    private void checkModifyPermission(UserGroup group, Long userId) {
        UserGroupMembership membership = membershipRepository
                .findByUserIdAndGroupId(userId, group.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this group"));

        if (membership.getRole() != GroupRole.OWNER && membership.getRole() != GroupRole.ADMIN) {
            throw new UnauthorizedException("You don't have permission to modify this group");
        }
    }
}

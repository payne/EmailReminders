package pl.piomin.services.emailreminders.dto.response;

import pl.piomin.services.emailreminders.model.GroupRole;
import pl.piomin.services.emailreminders.model.UserGroupMembership;

import java.time.LocalDateTime;

public class MemberResponse {

    private Long userId;
    private String email;
    private String displayName;
    private GroupRole role;
    private LocalDateTime joinedAt;

    public MemberResponse() {
    }

    public MemberResponse(Long userId, String email, String displayName, GroupRole role, LocalDateTime joinedAt) {
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public static MemberResponse fromMembership(UserGroupMembership membership) {
        return new MemberResponse(
                membership.getUser().getId(),
                membership.getUser().getEmail(),
                membership.getUser().getDisplayName(),
                membership.getRole(),
                membership.getJoinedAt()
        );
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public GroupRole getRole() {
        return role;
    }

    public void setRole(GroupRole role) {
        this.role = role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}

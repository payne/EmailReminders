package pl.piomin.services.emailreminders.dto.response;

import pl.piomin.services.emailreminders.model.UserGroup;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class GroupResponse {

    private Long id;
    private String name;
    private String description;
    private UserResponse owner;
    private List<MemberResponse> members;
    private LocalDateTime createdAt;

    public GroupResponse() {
    }

    public GroupResponse(Long id, String name, String description, UserResponse owner,
                         List<MemberResponse> members, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.members = members;
        this.createdAt = createdAt;
    }

    public static GroupResponse fromEntity(UserGroup group) {
        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                UserResponse.fromEntity(group.getOwner()),
                group.getMemberships().stream()
                        .map(MemberResponse::fromMembership)
                        .collect(Collectors.toList()),
                group.getCreatedAt()
        );
    }

    public static GroupResponse fromEntityWithoutMembers(UserGroup group) {
        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                UserResponse.fromEntity(group.getOwner()),
                null,
                group.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserResponse getOwner() {
        return owner;
    }

    public void setOwner(UserResponse owner) {
        this.owner = owner;
    }

    public List<MemberResponse> getMembers() {
        return members;
    }

    public void setMembers(List<MemberResponse> members) {
        this.members = members;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

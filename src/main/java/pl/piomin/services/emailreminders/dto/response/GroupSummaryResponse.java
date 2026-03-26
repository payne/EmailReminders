package pl.piomin.services.emailreminders.dto.response;

import pl.piomin.services.emailreminders.model.UserGroup;

public class GroupSummaryResponse {

    private Long id;
    private String name;

    public GroupSummaryResponse() {
    }

    public GroupSummaryResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static GroupSummaryResponse fromEntity(UserGroup group) {
        return new GroupSummaryResponse(group.getId(), group.getName());
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
}

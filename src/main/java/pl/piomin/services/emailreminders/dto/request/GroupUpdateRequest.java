package pl.piomin.services.emailreminders.dto.request;

import jakarta.validation.constraints.Size;

public class GroupUpdateRequest {

    @Size(max = 100, message = "Group name must be at most 100 characters")
    private String name;

    private String description;

    public GroupUpdateRequest() {
    }

    public GroupUpdateRequest(String name, String description) {
        this.name = name;
        this.description = description;
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
}

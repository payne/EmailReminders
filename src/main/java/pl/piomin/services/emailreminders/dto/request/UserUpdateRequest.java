package pl.piomin.services.emailreminders.dto.request;

import jakarta.validation.constraints.Size;

public class UserUpdateRequest {

    @Size(max = 100, message = "Display name must be at most 100 characters")
    private String displayName;

    public UserUpdateRequest() {
    }

    public UserUpdateRequest(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

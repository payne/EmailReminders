package pl.piomin.services.emailreminders.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import pl.piomin.services.emailreminders.model.GroupRole;

import java.util.List;

public class AddMembersRequest {

    @NotEmpty(message = "At least one email is required")
    private List<@Email(message = "Invalid email format") String> emails;

    private GroupRole role = GroupRole.MEMBER;

    public AddMembersRequest() {
    }

    public AddMembersRequest(List<String> emails, GroupRole role) {
        this.emails = emails;
        this.role = role;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public GroupRole getRole() {
        return role;
    }

    public void setRole(GroupRole role) {
        this.role = role;
    }
}

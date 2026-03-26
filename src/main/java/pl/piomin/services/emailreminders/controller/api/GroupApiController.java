package pl.piomin.services.emailreminders.controller.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.piomin.services.emailreminders.dto.request.AddMembersRequest;
import pl.piomin.services.emailreminders.dto.request.GroupCreateRequest;
import pl.piomin.services.emailreminders.dto.request.GroupUpdateRequest;
import pl.piomin.services.emailreminders.dto.response.GroupResponse;
import pl.piomin.services.emailreminders.security.UserPrincipal;
import pl.piomin.services.emailreminders.service.GroupService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupApiController {

    private final GroupService groupService;

    public GroupApiController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody GroupCreateRequest request) {
        GroupResponse group = groupService.create(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getMyGroups(@AuthenticationPrincipal UserPrincipal principal) {
        List<GroupResponse> groups = groupService.findByUserId(principal.getId());
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable Long id) {
        GroupResponse group = groupService.findById(id);
        return ResponseEntity.ok(group);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupResponse> updateGroup(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody GroupUpdateRequest request) {
        GroupResponse group = groupService.update(id, principal.getId(), request);
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        groupService.delete(id, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<Void> addMembers(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AddMembersRequest request) {
        groupService.addMembers(id, principal.getId(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long id,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal principal) {
        groupService.removeMember(id, userId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}

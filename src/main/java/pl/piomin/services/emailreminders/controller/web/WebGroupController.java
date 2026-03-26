package pl.piomin.services.emailreminders.controller.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.piomin.services.emailreminders.dto.request.AddMembersRequest;
import pl.piomin.services.emailreminders.dto.request.GroupCreateRequest;
import pl.piomin.services.emailreminders.dto.request.GroupUpdateRequest;
import pl.piomin.services.emailreminders.dto.response.GroupResponse;
import pl.piomin.services.emailreminders.model.GroupRole;
import pl.piomin.services.emailreminders.security.UserPrincipal;
import pl.piomin.services.emailreminders.service.GroupService;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/groups")
public class WebGroupController {

    private final GroupService groupService;

    public WebGroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public String listGroups(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        List<GroupResponse> groups = groupService.findByUserId(principal.getId());
        model.addAttribute("groups", groups);
        model.addAttribute("currentPage", "groups");
        model.addAttribute("pageTitle", "My Groups");
        return "groups/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("currentPage", "groups");
        model.addAttribute("pageTitle", "Create Group");
        return "groups/create";
    }

    @PostMapping
    public String createGroup(@AuthenticationPrincipal UserPrincipal principal,
                              @RequestParam String name,
                              @RequestParam(required = false) String description,
                              RedirectAttributes redirectAttributes) {
        GroupCreateRequest request = new GroupCreateRequest(name, description);
        GroupResponse group = groupService.create(principal.getId(), request);
        redirectAttributes.addFlashAttribute("successMessage", "Group created successfully");
        return "redirect:/groups/" + group.getId();
    }

    @GetMapping("/{id}")
    public String viewGroup(@PathVariable Long id,
                            @AuthenticationPrincipal UserPrincipal principal,
                            Model model) {
        GroupResponse group = groupService.findById(id);
        boolean isOwnerOrAdmin = group.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(principal.getId()) &&
                        (m.getRole() == GroupRole.OWNER || m.getRole() == GroupRole.ADMIN));

        model.addAttribute("group", group);
        model.addAttribute("isOwnerOrAdmin", isOwnerOrAdmin);
        model.addAttribute("currentUserId", principal.getId());
        model.addAttribute("currentPage", "groups");
        model.addAttribute("pageTitle", group.getName());
        return "groups/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        GroupResponse group = groupService.findById(id);
        model.addAttribute("group", group);
        model.addAttribute("currentPage", "groups");
        model.addAttribute("pageTitle", "Edit Group");
        return "groups/edit";
    }

    @PostMapping("/{id}")
    public String updateGroup(@PathVariable Long id,
                              @AuthenticationPrincipal UserPrincipal principal,
                              @RequestParam String name,
                              @RequestParam(required = false) String description,
                              RedirectAttributes redirectAttributes) {
        GroupUpdateRequest request = new GroupUpdateRequest(name, description);
        groupService.update(id, principal.getId(), request);
        redirectAttributes.addFlashAttribute("successMessage", "Group updated successfully");
        return "redirect:/groups/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteGroup(@PathVariable Long id,
                              @AuthenticationPrincipal UserPrincipal principal,
                              RedirectAttributes redirectAttributes) {
        groupService.delete(id, principal.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Group deleted successfully");
        return "redirect:/groups";
    }

    @PostMapping("/{id}/members")
    public String addMembers(@PathVariable Long id,
                             @AuthenticationPrincipal UserPrincipal principal,
                             @RequestParam String emails,
                             RedirectAttributes redirectAttributes) {
        List<String> emailList = Arrays.asList(emails.split("[,\\s]+"));
        AddMembersRequest request = new AddMembersRequest(emailList, GroupRole.MEMBER);
        groupService.addMembers(id, principal.getId(), request);
        redirectAttributes.addFlashAttribute("successMessage", "Members added successfully");
        return "redirect:/groups/" + id;
    }

    @PostMapping("/{id}/members/{userId}/remove")
    public String removeMember(@PathVariable Long id,
                               @PathVariable Long userId,
                               @AuthenticationPrincipal UserPrincipal principal,
                               RedirectAttributes redirectAttributes) {
        groupService.removeMember(id, userId, principal.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Member removed successfully");
        return "redirect:/groups/" + id;
    }
}

package pl.piomin.services.emailreminders.controller.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.piomin.services.emailreminders.dto.request.UserUpdateRequest;
import pl.piomin.services.emailreminders.dto.response.EventInstanceResponse;
import pl.piomin.services.emailreminders.dto.response.GroupResponse;
import pl.piomin.services.emailreminders.model.EventInstance;
import pl.piomin.services.emailreminders.repository.EventInstanceRepository;
import pl.piomin.services.emailreminders.security.UserPrincipal;
import pl.piomin.services.emailreminders.service.GroupService;
import pl.piomin.services.emailreminders.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final GroupService groupService;
    private final UserService userService;
    private final EventInstanceRepository instanceRepository;

    public HomeController(GroupService groupService,
                          UserService userService,
                          EventInstanceRepository instanceRepository) {
        this.groupService = groupService;
        this.userService = userService;
        this.instanceRepository = instanceRepository;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal != null) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        List<GroupResponse> groups = groupService.findByUserId(principal.getId());

        // Get upcoming events for the next 7 days
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekFromNow = now.plusDays(7);
        List<EventInstance> instances = instanceRepository.findUpcomingInstances(now, weekFromNow);

        // Filter to only show events the user is part of (via groups)
        List<EventInstanceResponse> upcomingEvents = instances.stream()
                .filter(i -> i.getEvent().getGroups().stream()
                        .anyMatch(g -> groupService.isMember(g.getId(), principal.getId())))
                .map(EventInstanceResponse::fromEntity)
                .limit(10)
                .collect(Collectors.toList());

        model.addAttribute("user", principal.getUser());
        model.addAttribute("groups", groups);
        model.addAttribute("upcomingEvents", upcomingEvents);
        model.addAttribute("currentPage", "dashboard");
        model.addAttribute("pageTitle", "Dashboard");
        return "dashboard";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        model.addAttribute("user", principal.getUser());
        model.addAttribute("currentPage", "profile");
        model.addAttribute("pageTitle", "Profile");
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserPrincipal principal,
                                @RequestParam(required = false) String displayName,
                                RedirectAttributes redirectAttributes) {
        UserUpdateRequest request = new UserUpdateRequest(displayName);
        userService.update(principal.getId(), request);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
        return "redirect:/profile";
    }
}

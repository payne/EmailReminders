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
import pl.piomin.services.emailreminders.dto.request.EventCreateRequest;
import pl.piomin.services.emailreminders.dto.request.EventUpdateRequest;
import pl.piomin.services.emailreminders.dto.response.EventInstanceResponse;
import pl.piomin.services.emailreminders.dto.response.EventResponse;
import pl.piomin.services.emailreminders.dto.response.GroupResponse;
import pl.piomin.services.emailreminders.model.RecurrencePattern;
import pl.piomin.services.emailreminders.security.UserPrincipal;
import pl.piomin.services.emailreminders.service.EventInstanceService;
import pl.piomin.services.emailreminders.service.EventService;
import pl.piomin.services.emailreminders.service.GroupService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/events")
public class WebEventController {

    private final EventService eventService;
    private final EventInstanceService instanceService;
    private final GroupService groupService;

    public WebEventController(EventService eventService,
                              EventInstanceService instanceService,
                              GroupService groupService) {
        this.eventService = eventService;
        this.instanceService = instanceService;
        this.groupService = groupService;
    }

    @GetMapping
    public String listEvents(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        List<EventResponse> events = eventService.findByUserGroups(principal.getId());
        model.addAttribute("events", events);
        model.addAttribute("currentPage", "events");
        model.addAttribute("pageTitle", "My Events");
        return "events/list";
    }

    @GetMapping("/new")
    public String showCreateForm(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        List<GroupResponse> groups = groupService.findByUserId(principal.getId());
        model.addAttribute("groups", groups);
        model.addAttribute("recurrencePatterns", RecurrencePattern.values());
        model.addAttribute("currentPage", "events");
        model.addAttribute("pageTitle", "Create Event");
        return "events/create";
    }

    @PostMapping
    public String createEvent(@AuthenticationPrincipal UserPrincipal principal,
                              @RequestParam String title,
                              @RequestParam(required = false) String description,
                              @RequestParam String eventTime,
                              @RequestParam String timezone,
                              @RequestParam(required = false) Boolean isRecurring,
                              @RequestParam(required = false) RecurrencePattern recurrencePattern,
                              @RequestParam(required = false) Integer recurrenceInterval,
                              @RequestParam(required = false) String recurrenceEndDate,
                              @RequestParam(required = false) Integer reminderMinutesBefore,
                              @RequestParam(required = false) List<Long> groupIds,
                              RedirectAttributes redirectAttributes) {
        EventCreateRequest request = new EventCreateRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setEventTime(LocalDateTime.parse(eventTime));
        request.setTimezone(timezone);
        request.setIsRecurring(isRecurring != null && isRecurring);
        request.setRecurrencePattern(recurrencePattern);
        request.setRecurrenceInterval(recurrenceInterval != null ? recurrenceInterval : 1);
        if (recurrenceEndDate != null && !recurrenceEndDate.isBlank()) {
            request.setRecurrenceEndDate(LocalDate.parse(recurrenceEndDate));
        }
        request.setReminderMinutesBefore(reminderMinutesBefore != null ? reminderMinutesBefore : 60);
        request.setGroupIds(groupIds);

        EventResponse event = eventService.create(principal.getId(), request);
        redirectAttributes.addFlashAttribute("successMessage", "Event created successfully");
        return "redirect:/events/" + event.getId();
    }

    @GetMapping("/{id}")
    public String viewEvent(@PathVariable Long id,
                            @AuthenticationPrincipal UserPrincipal principal,
                            Model model) {
        EventResponse event = eventService.findById(id);
        List<EventInstanceResponse> instances = instanceService.getUpcomingInstances(id);
        boolean isOwner = event.getCreatedBy().getId().equals(principal.getId());

        model.addAttribute("event", event);
        model.addAttribute("instances", instances);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("currentPage", "events");
        model.addAttribute("pageTitle", event.getTitle());
        return "events/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               @AuthenticationPrincipal UserPrincipal principal,
                               Model model) {
        EventResponse event = eventService.findById(id);
        List<GroupResponse> groups = groupService.findByUserId(principal.getId());

        model.addAttribute("event", event);
        model.addAttribute("groups", groups);
        model.addAttribute("recurrencePatterns", RecurrencePattern.values());
        model.addAttribute("currentPage", "events");
        model.addAttribute("pageTitle", "Edit Event");
        return "events/edit";
    }

    @PostMapping("/{id}")
    public String updateEvent(@PathVariable Long id,
                              @AuthenticationPrincipal UserPrincipal principal,
                              @RequestParam String title,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false) Integer reminderMinutesBefore,
                              RedirectAttributes redirectAttributes) {
        EventUpdateRequest request = new EventUpdateRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setReminderMinutesBefore(reminderMinutesBefore);

        eventService.update(id, principal.getId(), request);
        redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully");
        return "redirect:/events/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteEvent(@PathVariable Long id,
                              @AuthenticationPrincipal UserPrincipal principal,
                              RedirectAttributes redirectAttributes) {
        eventService.delete(id, principal.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully");
        return "redirect:/events";
    }

    @PostMapping("/{id}/cancel-instance/{instanceId}")
    public String cancelInstance(@PathVariable Long id,
                                 @PathVariable Long instanceId,
                                 @AuthenticationPrincipal UserPrincipal principal,
                                 RedirectAttributes redirectAttributes) {
        eventService.cancelInstance(id, instanceId, principal.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Instance cancelled successfully");
        return "redirect:/events/" + id;
    }
}

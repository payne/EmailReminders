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
import pl.piomin.services.emailreminders.dto.request.EventCreateRequest;
import pl.piomin.services.emailreminders.dto.request.EventUpdateRequest;
import pl.piomin.services.emailreminders.dto.response.EventInstanceResponse;
import pl.piomin.services.emailreminders.dto.response.EventResponse;
import pl.piomin.services.emailreminders.security.UserPrincipal;
import pl.piomin.services.emailreminders.service.EventInstanceService;
import pl.piomin.services.emailreminders.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventApiController {

    private final EventService eventService;
    private final EventInstanceService instanceService;

    public EventApiController(EventService eventService, EventInstanceService instanceService) {
        this.eventService = eventService;
        this.instanceService = instanceService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody EventCreateRequest request) {
        EventResponse event = eventService.create(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getMyEvents(@AuthenticationPrincipal UserPrincipal principal) {
        List<EventResponse> events = eventService.findByUserGroups(principal.getId());
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long id) {
        EventResponse event = eventService.findById(id);
        return ResponseEntity.ok(event);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody EventUpdateRequest request) {
        EventResponse event = eventService.update(id, principal.getId(), request);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        eventService.delete(id, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/groups/{groupId}")
    public ResponseEntity<Void> addEventToGroup(
            @PathVariable Long id,
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserPrincipal principal) {
        eventService.addToGroup(id, groupId, principal.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/groups/{groupId}")
    public ResponseEntity<Void> removeEventFromGroup(
            @PathVariable Long id,
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserPrincipal principal) {
        eventService.removeFromGroup(id, groupId, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/instances")
    public ResponseEntity<List<EventInstanceResponse>> getEventInstances(@PathVariable Long id) {
        List<EventInstanceResponse> instances = instanceService.getUpcomingInstances(id);
        return ResponseEntity.ok(instances);
    }

    @DeleteMapping("/{id}/instances/{instanceId}")
    public ResponseEntity<Void> cancelEventInstance(
            @PathVariable Long id,
            @PathVariable Long instanceId,
            @AuthenticationPrincipal UserPrincipal principal) {
        eventService.cancelInstance(id, instanceId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}

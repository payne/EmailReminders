package pl.piomin.services.emailreminders.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.piomin.services.emailreminders.dto.request.EventCreateRequest;
import pl.piomin.services.emailreminders.dto.request.EventUpdateRequest;
import pl.piomin.services.emailreminders.dto.response.EventResponse;
import pl.piomin.services.emailreminders.exception.ResourceNotFoundException;
import pl.piomin.services.emailreminders.exception.UnauthorizedException;
import pl.piomin.services.emailreminders.model.Event;
import pl.piomin.services.emailreminders.model.User;
import pl.piomin.services.emailreminders.model.UserGroup;
import pl.piomin.services.emailreminders.repository.EventRepository;
import pl.piomin.services.emailreminders.repository.UserGroupMembershipRepository;
import pl.piomin.services.emailreminders.repository.UserGroupRepository;
import pl.piomin.services.emailreminders.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository groupRepository;
    private final UserGroupMembershipRepository membershipRepository;
    private final EventInstanceService instanceService;

    public EventService(EventRepository eventRepository,
                        UserRepository userRepository,
                        UserGroupRepository groupRepository,
                        UserGroupMembershipRepository membershipRepository,
                        EventInstanceService instanceService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.instanceService = instanceService;
    }

    @Transactional
    public EventResponse create(Long creatorId, EventCreateRequest request) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", creatorId));

        Event event = new Event(request.getTitle(), request.getEventTime(),
                request.getTimezone(), creator);
        event.setDescription(request.getDescription());
        event.setIsRecurring(request.getIsRecurring() != null && request.getIsRecurring());
        event.setRecurrencePattern(request.getRecurrencePattern());
        event.setRecurrenceInterval(request.getRecurrenceInterval() != null
                ? request.getRecurrenceInterval() : 1);
        event.setRecurrenceEndDate(request.getRecurrenceEndDate());
        event.setReminderMinutesBefore(request.getReminderMinutesBefore() != null
                ? request.getReminderMinutesBefore() : 60);

        Event saved = eventRepository.save(event);

        // Add to groups if specified
        if (request.getGroupIds() != null && !request.getGroupIds().isEmpty()) {
            for (Long groupId : request.getGroupIds()) {
                UserGroup group = groupRepository.findById(groupId).orElse(null);
                if (group != null && membershipRepository.existsByUserIdAndGroupId(creatorId, groupId)) {
                    group.addEvent(saved);
                    groupRepository.save(group);
                }
            }
        }

        // Generate event instances (up to 1 year ahead for recurring events)
        instanceService.generateInstances(saved, LocalDateTime.now().plusYears(1));

        return findById(saved.getId());
    }

    public EventResponse findById(Long id) {
        Event event = eventRepository.findByIdWithGroupsAndInstances(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));
        return EventResponse.fromEntity(event);
    }

    public List<EventResponse> findByUserId(Long userId) {
        List<Event> events = eventRepository.findByCreatedByIdWithGroups(userId);
        return events.stream()
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<EventResponse> findByUserGroups(Long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        return events.stream()
                .map(e -> {
                    Event full = eventRepository.findByIdWithGroupsAndInstances(e.getId()).orElse(e);
                    return EventResponse.fromEntity(full);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public EventResponse update(Long id, Long userId, EventUpdateRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));

        checkEditPermission(event, userId);

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            event.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventTime() != null) {
            event.setEventTime(request.getEventTime());
        }
        if (request.getTimezone() != null) {
            event.setTimezone(request.getTimezone());
        }
        if (request.getIsRecurring() != null) {
            event.setIsRecurring(request.getIsRecurring());
        }
        if (request.getRecurrencePattern() != null) {
            event.setRecurrencePattern(request.getRecurrencePattern());
        }
        if (request.getRecurrenceInterval() != null) {
            event.setRecurrenceInterval(request.getRecurrenceInterval());
        }
        if (request.getRecurrenceEndDate() != null) {
            event.setRecurrenceEndDate(request.getRecurrenceEndDate());
        }
        if (request.getReminderMinutesBefore() != null) {
            event.setReminderMinutesBefore(request.getReminderMinutesBefore());
        }

        eventRepository.save(event);
        return findById(id);
    }

    @Transactional
    public void addToGroup(Long eventId, Long groupId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));
        UserGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", groupId));

        checkEditPermission(event, userId);

        if (!membershipRepository.existsByUserIdAndGroupId(userId, groupId)) {
            throw new UnauthorizedException("You are not a member of this group");
        }

        group.addEvent(event);
        groupRepository.save(group);
    }

    @Transactional
    public void removeFromGroup(Long eventId, Long groupId, Long userId) {
        Event event = eventRepository.findByIdWithGroupsAndInstances(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));
        UserGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", groupId));

        checkEditPermission(event, userId);

        group.removeEvent(event);
        groupRepository.save(group);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));

        checkEditPermission(event, userId);

        eventRepository.delete(event);
    }

    @Transactional
    public void cancelInstance(Long eventId, Long instanceId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));

        checkEditPermission(event, userId);

        instanceService.cancelInstance(instanceId);
    }

    private void checkEditPermission(Event event, Long userId) {
        if (!event.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You can only edit your own events");
        }
    }
}

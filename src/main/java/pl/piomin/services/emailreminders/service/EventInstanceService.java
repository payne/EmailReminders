package pl.piomin.services.emailreminders.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.piomin.services.emailreminders.dto.response.EventInstanceResponse;
import pl.piomin.services.emailreminders.exception.ResourceNotFoundException;
import pl.piomin.services.emailreminders.model.Event;
import pl.piomin.services.emailreminders.model.EventInstance;
import pl.piomin.services.emailreminders.model.RecurrencePattern;
import pl.piomin.services.emailreminders.repository.EventInstanceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventInstanceService {

    private final EventInstanceRepository instanceRepository;

    public EventInstanceService(EventInstanceRepository instanceRepository) {
        this.instanceRepository = instanceRepository;
    }

    public void generateInstances(Event event, LocalDateTime until) {
        List<EventInstance> instances = new ArrayList<>();

        if (!event.getIsRecurring()) {
            // Single event - one instance
            EventInstance instance = new EventInstance(event, event.getEventTime());
            instances.add(instance);
        } else {
            // Recurring event - generate instances
            LocalDateTime current = event.getEventTime();
            LocalDate endDate = event.getRecurrenceEndDate() != null
                    ? event.getRecurrenceEndDate()
                    : until.toLocalDate();

            while (!current.toLocalDate().isAfter(endDate) && !current.isAfter(until)) {
                EventInstance instance = new EventInstance(event, current);
                instances.add(instance);
                current = calculateNextOccurrence(current, event.getRecurrencePattern(),
                        event.getRecurrenceInterval());
            }
        }

        instanceRepository.saveAll(instances);
    }

    public void cancelInstance(Long instanceId) {
        EventInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("EventInstance", instanceId));
        instance.setCancelled(true);
        instanceRepository.save(instance);
    }

    public List<EventInstanceResponse> getUpcomingInstances(Long eventId) {
        List<EventInstance> instances = instanceRepository
                .findByEventIdAndCancelledFalseOrderByInstanceTimeAsc(eventId);
        return instances.stream()
                .filter(i -> i.getInstanceTime().isAfter(LocalDateTime.now()))
                .map(EventInstanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<EventInstance> findInstancesForReminder(LocalDateTime start, LocalDateTime end) {
        return instanceRepository.findInstancesInTimeRange(start, end);
    }

    private LocalDateTime calculateNextOccurrence(LocalDateTime current, RecurrencePattern pattern, int interval) {
        return switch (pattern) {
            case DAILY -> current.plusDays(interval);
            case WEEKLY -> current.plusWeeks(interval);
            case MONTHLY -> current.plusMonths(interval);
            case YEARLY -> current.plusYears(interval);
        };
    }
}

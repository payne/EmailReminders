package pl.piomin.services.emailreminders.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.piomin.services.emailreminders.exception.ResourceNotFoundException;
import pl.piomin.services.emailreminders.model.Event;
import pl.piomin.services.emailreminders.model.EventInstance;
import pl.piomin.services.emailreminders.model.ReminderPreference;
import pl.piomin.services.emailreminders.model.User;
import pl.piomin.services.emailreminders.repository.EventInstanceRepository;
import pl.piomin.services.emailreminders.repository.EventRepository;
import pl.piomin.services.emailreminders.repository.ReminderPreferenceRepository;
import pl.piomin.services.emailreminders.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReminderService {

    private final ReminderPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventInstanceRepository instanceRepository;

    public ReminderService(ReminderPreferenceRepository preferenceRepository,
                           UserRepository userRepository,
                           EventRepository eventRepository,
                           EventInstanceRepository instanceRepository) {
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.instanceRepository = instanceRepository;
    }

    public void snoozeReminder(Long userId, Long eventInstanceId, Duration duration) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        EventInstance instance = instanceRepository.findById(eventInstanceId)
                .orElseThrow(() -> new ResourceNotFoundException("EventInstance", eventInstanceId));

        ReminderPreference preference = preferenceRepository
                .findByUserIdAndEventInstanceId(userId, eventInstanceId)
                .orElseGet(() -> {
                    ReminderPreference newPref = new ReminderPreference(user, instance);
                    return newPref;
                });

        preference.setSnoozedUntil(LocalDateTime.now().plus(duration));
        preferenceRepository.save(preference);
    }

    public void turnOffSingleReminder(Long userId, Long eventInstanceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        EventInstance instance = instanceRepository.findById(eventInstanceId)
                .orElseThrow(() -> new ResourceNotFoundException("EventInstance", eventInstanceId));

        ReminderPreference preference = preferenceRepository
                .findByUserIdAndEventInstanceId(userId, eventInstanceId)
                .orElseGet(() -> {
                    ReminderPreference newPref = new ReminderPreference(user, instance);
                    return newPref;
                });

        preference.setReminderEnabled(false);
        preferenceRepository.save(preference);
    }

    public void cancelAllReminders(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));

        ReminderPreference preference = preferenceRepository
                .findByUserIdAndEventIdAndAllEventInstancesTrue(userId, eventId)
                .orElseGet(() -> {
                    ReminderPreference newPref = new ReminderPreference(user, event);
                    return newPref;
                });

        preference.setAllEventInstances(true);
        preference.setReminderEnabled(false);
        preferenceRepository.save(preference);
    }

    public boolean shouldSendReminder(Long userId, Long eventInstanceId, Long eventId) {
        List<ReminderPreference> preferences = preferenceRepository
                .findUserPreferencesForInstance(userId, eventInstanceId, eventId);

        for (ReminderPreference pref : preferences) {
            // If reminders are disabled
            if (!pref.getReminderEnabled()) {
                return false;
            }
            // If snoozed
            if (pref.isSnoozed()) {
                return false;
            }
        }

        return true;
    }
}

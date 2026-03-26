package pl.piomin.services.emailreminders.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.piomin.services.emailreminders.model.ActionType;
import pl.piomin.services.emailreminders.model.Event;
import pl.piomin.services.emailreminders.model.EventInstance;
import pl.piomin.services.emailreminders.model.ReminderSent;
import pl.piomin.services.emailreminders.model.User;
import pl.piomin.services.emailreminders.model.UserGroup;
import pl.piomin.services.emailreminders.model.UserGroupMembership;
import pl.piomin.services.emailreminders.repository.EventInstanceRepository;
import pl.piomin.services.emailreminders.repository.ReminderSentRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ReminderSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(ReminderSchedulerService.class);

    private final EventInstanceRepository instanceRepository;
    private final ReminderService reminderService;
    private final EmailService emailService;
    private final ActionTokenService actionTokenService;
    private final ReminderSentRepository reminderSentRepository;

    public ReminderSchedulerService(EventInstanceRepository instanceRepository,
                                    ReminderService reminderService,
                                    EmailService emailService,
                                    ActionTokenService actionTokenService,
                                    ReminderSentRepository reminderSentRepository) {
        this.instanceRepository = instanceRepository;
        this.reminderService = reminderService;
        this.emailService = emailService;
        this.actionTokenService = actionTokenService;
        this.reminderSentRepository = reminderSentRepository;
    }

    @Scheduled(fixedRateString = "${reminder.check-interval-ms:60000}")
    @Transactional
    public void processReminders() {
        logger.debug("Processing reminders...");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowEnd = now.plusMinutes(5); // Look ahead 5 minutes

        // Find instances where reminder time is now
        List<EventInstance> instances = instanceRepository.findUpcomingInstances(now, now.plusHours(24));

        for (EventInstance instance : instances) {
            Event event = instance.getEvent();
            int reminderMinutes = event.getReminderMinutesBefore();
            LocalDateTime reminderTime = instance.getInstanceTime().minusMinutes(reminderMinutes);

            // Check if it's time to send reminder (within 1 minute window)
            if (reminderTime.isAfter(now.minusMinutes(1)) && reminderTime.isBefore(now.plusMinutes(1))) {
                processInstanceReminder(instance);
            }
        }
    }

    private void processInstanceReminder(EventInstance instance) {
        Event event = instance.getEvent();
        Set<User> usersToNotify = getUsersForEventInstance(instance);

        for (User user : usersToNotify) {
            // Check if already sent
            if (reminderSentRepository.existsByUserIdAndEventInstanceId(user.getId(), instance.getId())) {
                continue;
            }

            // Check user preferences
            if (!reminderService.shouldSendReminder(user.getId(), instance.getId(), event.getId())) {
                continue;
            }

            try {
                // Generate action tokens
                Map<ActionType, String> actionTokens = actionTokenService.generateActionTokens(user, instance);

                // Send email
                emailService.sendReminderEmail(user, instance, actionTokens);

                // Record that reminder was sent
                ReminderSent sent = new ReminderSent(user, instance, "SENT");
                reminderSentRepository.save(sent);

                logger.info("Sent reminder to {} for event {}", user.getEmail(), event.getTitle());
            } catch (Exception e) {
                logger.error("Failed to send reminder to {} for event {}", user.getEmail(), event.getTitle(), e);

                // Record failure
                ReminderSent sent = new ReminderSent(user, instance, "FAILED");
                reminderSentRepository.save(sent);
            }
        }
    }

    private Set<User> getUsersForEventInstance(EventInstance instance) {
        Set<User> users = new HashSet<>();
        Event event = instance.getEvent();

        // Get all users from groups associated with this event
        for (UserGroup group : event.getGroups()) {
            for (UserGroupMembership membership : group.getMemberships()) {
                User user = membership.getUser();
                if (user.getActive()) {
                    users.add(user);
                }
            }
        }

        return users;
    }
}

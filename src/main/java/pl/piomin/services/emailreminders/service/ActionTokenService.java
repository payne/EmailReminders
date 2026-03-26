package pl.piomin.services.emailreminders.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.piomin.services.emailreminders.exception.InvalidTokenException;
import pl.piomin.services.emailreminders.model.ActionToken;
import pl.piomin.services.emailreminders.model.ActionType;
import pl.piomin.services.emailreminders.model.Event;
import pl.piomin.services.emailreminders.model.EventInstance;
import pl.piomin.services.emailreminders.model.User;
import pl.piomin.services.emailreminders.repository.ActionTokenRepository;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

@Service
@Transactional
public class ActionTokenService {

    private static final Logger logger = LoggerFactory.getLogger(ActionTokenService.class);

    private final ActionTokenRepository tokenRepository;
    private final TokenService tokenService;
    private final ReminderService reminderService;
    private final int tokenExpirationHours;

    public ActionTokenService(ActionTokenRepository tokenRepository,
                              TokenService tokenService,
                              ReminderService reminderService,
                              @Value("${action-token.expiration-hours}") int tokenExpirationHours) {
        this.tokenRepository = tokenRepository;
        this.tokenService = tokenService;
        this.reminderService = reminderService;
        this.tokenExpirationHours = tokenExpirationHours;
    }

    public Map<ActionType, String> generateActionTokens(User user, EventInstance instance) {
        Map<ActionType, String> tokens = new EnumMap<>(ActionType.class);
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(tokenExpirationHours);
        Event event = instance.getEvent();

        // Generate snooze tokens
        tokens.put(ActionType.SNOOZE_1H, createToken(user, ActionType.SNOOZE_1H, instance, event, expiresAt));
        tokens.put(ActionType.SNOOZE_1D, createToken(user, ActionType.SNOOZE_1D, instance, event, expiresAt));
        tokens.put(ActionType.SNOOZE_1W, createToken(user, ActionType.SNOOZE_1W, instance, event, expiresAt));

        // Turn off single instance
        tokens.put(ActionType.TURN_OFF_SINGLE, createToken(user, ActionType.TURN_OFF_SINGLE, instance, event, expiresAt));

        // Cancel all only for recurring events
        if (event.getIsRecurring()) {
            tokens.put(ActionType.CANCEL_ALL, createToken(user, ActionType.CANCEL_ALL, instance, event, expiresAt));
        }

        return tokens;
    }

    private String createToken(User user, ActionType actionType, EventInstance instance,
                               Event event, LocalDateTime expiresAt) {
        String token = tokenService.generateSecureToken();
        ActionToken actionToken = new ActionToken(token, user, actionType, instance, event, expiresAt);
        tokenRepository.save(actionToken);
        return token;
    }

    public ActionResult executeAction(String token) {
        ActionToken actionToken = tokenRepository
                .findByTokenAndUsedFalseAndExpiresAtAfter(token, LocalDateTime.now())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired action token"));

        // Mark as used
        actionToken.setUsed(true);
        tokenRepository.save(actionToken);

        User user = actionToken.getUser();
        EventInstance instance = actionToken.getEventInstance();
        Event event = actionToken.getEvent();
        ActionType actionType = actionToken.getActionType();

        String message;

        switch (actionType) {
            case SNOOZE_1H -> {
                reminderService.snoozeReminder(user.getId(), instance.getId(), java.time.Duration.ofHours(1));
                message = "Reminder snoozed for 1 hour";
            }
            case SNOOZE_1D -> {
                reminderService.snoozeReminder(user.getId(), instance.getId(), java.time.Duration.ofDays(1));
                message = "Reminder snoozed for 1 day";
            }
            case SNOOZE_1W -> {
                reminderService.snoozeReminder(user.getId(), instance.getId(), java.time.Duration.ofDays(7));
                message = "Reminder snoozed for 1 week";
            }
            case TURN_OFF_SINGLE -> {
                reminderService.turnOffSingleReminder(user.getId(), instance.getId());
                message = "Reminder turned off for this event";
            }
            case CANCEL_ALL -> {
                reminderService.cancelAllReminders(user.getId(), event.getId());
                message = "All future reminders cancelled for this recurring event";
            }
            default -> {
                message = "Unknown action";
            }
        }

        return new ActionResult(actionType, event.getTitle(), message);
    }

    @Scheduled(cron = "0 0 3 * * ?") // Daily at 3 AM
    public void cleanupExpiredTokens() {
        int deleted = tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        logger.info("Cleaned up {} expired action tokens", deleted);
    }

    public record ActionResult(ActionType actionType, String eventTitle, String message) {}
}

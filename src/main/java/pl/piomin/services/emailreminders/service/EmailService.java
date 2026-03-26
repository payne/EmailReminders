package pl.piomin.services.emailreminders.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pl.piomin.services.emailreminders.model.ActionType;
import pl.piomin.services.emailreminders.model.EventInstance;
import pl.piomin.services.emailreminders.model.User;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a");

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String baseUrl;
    private final String fromEmail;

    public EmailService(JavaMailSender mailSender,
                        TemplateEngine templateEngine,
                        @Value("${app.base-url}") String baseUrl,
                        @Value("${app.from-email}") String fromEmail) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.baseUrl = baseUrl;
        this.fromEmail = fromEmail;
    }

    @Async
    public void sendMagicLinkEmail(String to, String token) {
        String magicLinkUrl = baseUrl + "/auth/verify?token=" + token;

        Context context = new Context();
        context.setVariable("magicLinkUrl", magicLinkUrl);

        String htmlContent = templateEngine.process("email/magic-link", context);

        sendHtmlEmail(to, "Sign in to Email Reminders", htmlContent);
    }

    @Async
    public void sendReminderEmail(User user, EventInstance instance, Map<ActionType, String> actionTokens) {
        String eventTitle = instance.getEvent().getTitle();
        String eventDescription = instance.getEvent().getDescription();
        String eventTime = instance.getInstanceTime().format(DATE_TIME_FORMATTER);
        boolean isRecurring = instance.getEvent().getIsRecurring();

        Context context = new Context();
        context.setVariable("userName", user.getDisplayName() != null ? user.getDisplayName() : user.getEmail());
        context.setVariable("eventTitle", eventTitle);
        context.setVariable("eventDescription", eventDescription);
        context.setVariable("eventTime", eventTime);
        context.setVariable("isRecurring", isRecurring);
        context.setVariable("snooze1hUrl", baseUrl + "/actions/" + actionTokens.get(ActionType.SNOOZE_1H));
        context.setVariable("snooze1dUrl", baseUrl + "/actions/" + actionTokens.get(ActionType.SNOOZE_1D));
        context.setVariable("snooze1wUrl", baseUrl + "/actions/" + actionTokens.get(ActionType.SNOOZE_1W));
        context.setVariable("turnOffSingleUrl", baseUrl + "/actions/" + actionTokens.get(ActionType.TURN_OFF_SINGLE));
        if (isRecurring && actionTokens.containsKey(ActionType.CANCEL_ALL)) {
            context.setVariable("cancelAllUrl", baseUrl + "/actions/" + actionTokens.get(ActionType.CANCEL_ALL));
        }

        String htmlContent = templateEngine.process("email/reminder", context);

        sendHtmlEmail(user.getEmail(), "Reminder: " + eventTitle, htmlContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}

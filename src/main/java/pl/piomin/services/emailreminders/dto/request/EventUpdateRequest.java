package pl.piomin.services.emailreminders.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import pl.piomin.services.emailreminders.model.RecurrencePattern;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventUpdateRequest {

    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    private String description;

    private LocalDateTime eventTime;

    private String timezone;

    private Boolean isRecurring;

    private RecurrencePattern recurrencePattern;

    @Min(value = 1, message = "Recurrence interval must be at least 1")
    private Integer recurrenceInterval;

    private LocalDate recurrenceEndDate;

    @Min(value = 0, message = "Reminder minutes must be non-negative")
    private Integer reminderMinutesBefore;

    public EventUpdateRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Boolean getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public RecurrencePattern getRecurrencePattern() {
        return recurrencePattern;
    }

    public void setRecurrencePattern(RecurrencePattern recurrencePattern) {
        this.recurrencePattern = recurrencePattern;
    }

    public Integer getRecurrenceInterval() {
        return recurrenceInterval;
    }

    public void setRecurrenceInterval(Integer recurrenceInterval) {
        this.recurrenceInterval = recurrenceInterval;
    }

    public LocalDate getRecurrenceEndDate() {
        return recurrenceEndDate;
    }

    public void setRecurrenceEndDate(LocalDate recurrenceEndDate) {
        this.recurrenceEndDate = recurrenceEndDate;
    }

    public Integer getReminderMinutesBefore() {
        return reminderMinutesBefore;
    }

    public void setReminderMinutesBefore(Integer reminderMinutesBefore) {
        this.reminderMinutesBefore = reminderMinutesBefore;
    }
}

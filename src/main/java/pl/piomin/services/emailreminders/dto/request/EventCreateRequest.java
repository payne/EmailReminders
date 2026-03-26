package pl.piomin.services.emailreminders.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.piomin.services.emailreminders.model.RecurrencePattern;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class EventCreateRequest {

    @NotBlank(message = "Event title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    private String description;

    @NotNull(message = "Event time is required")
    private LocalDateTime eventTime;

    @NotBlank(message = "Timezone is required")
    private String timezone;

    private Boolean isRecurring = false;

    private RecurrencePattern recurrencePattern;

    @Min(value = 1, message = "Recurrence interval must be at least 1")
    private Integer recurrenceInterval = 1;

    private LocalDate recurrenceEndDate;

    @Min(value = 0, message = "Reminder minutes must be non-negative")
    private Integer reminderMinutesBefore = 60;

    private List<Long> groupIds;

    public EventCreateRequest() {
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

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }
}

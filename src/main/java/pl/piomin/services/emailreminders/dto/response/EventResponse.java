package pl.piomin.services.emailreminders.dto.response;

import pl.piomin.services.emailreminders.model.Event;
import pl.piomin.services.emailreminders.model.RecurrencePattern;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EventResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime eventTime;
    private String timezone;
    private Boolean isRecurring;
    private RecurrencePattern recurrencePattern;
    private Integer recurrenceInterval;
    private LocalDate recurrenceEndDate;
    private Integer reminderMinutesBefore;
    private UserResponse createdBy;
    private List<GroupSummaryResponse> groups;
    private LocalDateTime createdAt;

    public EventResponse() {
    }

    public static EventResponse fromEntity(Event event) {
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setTitle(event.getTitle());
        response.setDescription(event.getDescription());
        response.setEventTime(event.getEventTime());
        response.setTimezone(event.getTimezone());
        response.setIsRecurring(event.getIsRecurring());
        response.setRecurrencePattern(event.getRecurrencePattern());
        response.setRecurrenceInterval(event.getRecurrenceInterval());
        response.setRecurrenceEndDate(event.getRecurrenceEndDate());
        response.setReminderMinutesBefore(event.getReminderMinutesBefore());
        response.setCreatedBy(UserResponse.fromEntity(event.getCreatedBy()));
        response.setGroups(event.getGroups().stream()
                .map(GroupSummaryResponse::fromEntity)
                .collect(Collectors.toList()));
        response.setCreatedAt(event.getCreatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UserResponse getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserResponse createdBy) {
        this.createdBy = createdBy;
    }

    public List<GroupSummaryResponse> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupSummaryResponse> groups) {
        this.groups = groups;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

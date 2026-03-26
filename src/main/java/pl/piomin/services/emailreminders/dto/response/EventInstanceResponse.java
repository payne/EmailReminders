package pl.piomin.services.emailreminders.dto.response;

import pl.piomin.services.emailreminders.model.EventInstance;

import java.time.LocalDateTime;

public class EventInstanceResponse {

    private Long id;
    private Long eventId;
    private String eventTitle;
    private LocalDateTime instanceTime;
    private Boolean cancelled;

    public EventInstanceResponse() {
    }

    public EventInstanceResponse(Long id, Long eventId, String eventTitle,
                                  LocalDateTime instanceTime, Boolean cancelled) {
        this.id = id;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.instanceTime = instanceTime;
        this.cancelled = cancelled;
    }

    public static EventInstanceResponse fromEntity(EventInstance instance) {
        return new EventInstanceResponse(
                instance.getId(),
                instance.getEvent().getId(),
                instance.getEvent().getTitle(),
                instance.getInstanceTime(),
                instance.getCancelled()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public LocalDateTime getInstanceTime() {
        return instanceTime;
    }

    public void setInstanceTime(LocalDateTime instanceTime) {
        this.instanceTime = instanceTime;
    }

    public Boolean getCancelled() {
        return cancelled;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }
}

package pl.piomin.services.emailreminders.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reminder_preferences", indexes = {
    @Index(name = "idx_reminder_pref_user", columnList = "user_id"),
    @Index(name = "idx_reminder_pref_event", columnList = "event_id")
})
@EntityListeners(AuditingEntityListener.class)
public class ReminderPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_instance_id")
    private EventInstance eventInstance;

    @Column(name = "all_event_instances", nullable = false)
    private Boolean allEventInstances = false;

    @Column(name = "reminder_enabled", nullable = false)
    private Boolean reminderEnabled = true;

    @Column(name = "snoozed_until")
    private LocalDateTime snoozedUntil;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ReminderPreference() {
    }

    public ReminderPreference(User user, Event event) {
        this.user = user;
        this.event = event;
    }

    public ReminderPreference(User user, EventInstance eventInstance) {
        this.user = user;
        this.eventInstance = eventInstance;
        this.event = eventInstance.getEvent();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public EventInstance getEventInstance() {
        return eventInstance;
    }

    public void setEventInstance(EventInstance eventInstance) {
        this.eventInstance = eventInstance;
    }

    public Boolean getAllEventInstances() {
        return allEventInstances;
    }

    public void setAllEventInstances(Boolean allEventInstances) {
        this.allEventInstances = allEventInstances;
    }

    public Boolean getReminderEnabled() {
        return reminderEnabled;
    }

    public void setReminderEnabled(Boolean reminderEnabled) {
        this.reminderEnabled = reminderEnabled;
    }

    public LocalDateTime getSnoozedUntil() {
        return snoozedUntil;
    }

    public void setSnoozedUntil(LocalDateTime snoozedUntil) {
        this.snoozedUntil = snoozedUntil;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isSnoozed() {
        return snoozedUntil != null && LocalDateTime.now().isBefore(snoozedUntil);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReminderPreference that = (ReminderPreference) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

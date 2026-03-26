CREATE TABLE reminder_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    event_id BIGINT REFERENCES events(id) ON DELETE CASCADE,
    event_instance_id BIGINT REFERENCES event_instances(id) ON DELETE CASCADE,
    all_event_instances BOOLEAN NOT NULL DEFAULT false,
    reminder_enabled BOOLEAN NOT NULL DEFAULT true,
    snoozed_until TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_reminder_pref_user ON reminder_preferences(user_id);
CREATE INDEX idx_reminder_pref_event ON reminder_preferences(event_id);
CREATE INDEX idx_reminder_pref_instance ON reminder_preferences(event_instance_id);

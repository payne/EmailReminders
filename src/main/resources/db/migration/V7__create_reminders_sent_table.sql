CREATE TABLE reminders_sent (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    event_instance_id BIGINT NOT NULL REFERENCES event_instances(id) ON DELETE CASCADE,
    sent_at TIMESTAMP NOT NULL,
    email_status VARCHAR(20)
);

CREATE INDEX idx_reminders_sent_user_instance ON reminders_sent(user_id, event_instance_id);

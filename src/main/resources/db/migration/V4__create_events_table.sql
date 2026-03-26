CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    event_time TIMESTAMP NOT NULL,
    timezone VARCHAR(50) NOT NULL,
    is_recurring BOOLEAN NOT NULL DEFAULT false,
    recurrence_pattern VARCHAR(20),
    recurrence_interval INTEGER DEFAULT 1,
    recurrence_end_date DATE,
    reminder_minutes_before INTEGER NOT NULL DEFAULT 60,
    created_by_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE group_event (
    group_id BIGINT NOT NULL REFERENCES user_groups(id) ON DELETE CASCADE,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    PRIMARY KEY (group_id, event_id)
);

CREATE INDEX idx_events_created_by ON events(created_by_id);
CREATE INDEX idx_events_time ON events(event_time);

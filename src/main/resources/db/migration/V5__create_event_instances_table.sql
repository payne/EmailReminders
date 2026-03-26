CREATE TABLE event_instances (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    instance_time TIMESTAMP NOT NULL,
    cancelled BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_event_instance_time ON event_instances(instance_time);
CREATE INDEX idx_event_instance_event ON event_instances(event_id);

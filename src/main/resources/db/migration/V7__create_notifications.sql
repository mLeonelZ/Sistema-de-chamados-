CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    ticket_id UUID,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(20) NOT NULL,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT fk_notifications_ticket
        FOREIGN KEY (ticket_id)
        REFERENCES tickets(id)
        ON UPDATE NO ACTION
        ON DELETE SET NULL,
    CONSTRAINT chk_notifications_type CHECK (type IN ('ALERTA_SLA', 'VIOLACAO_SLA', 'INFO', 'SUCCESS'))
);

CREATE INDEX idx_notifications_user_read_created_at ON notifications(user_id, read, created_at);
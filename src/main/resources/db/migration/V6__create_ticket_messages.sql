CREATE TABLE ticket_messages (
    id UUID PRIMARY KEY,
    ticket_id UUID NOT NULL,
    author_id UUID NOT NULL,
    type VARCHAR(20) NOT NULL,
    text TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_ticket_messages_ticket
        FOREIGN KEY (ticket_id)
        REFERENCES tickets(id)
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT fk_ticket_messages_author
        FOREIGN KEY (author_id)
        REFERENCES users(id)
        ON UPDATE NO ACTION
        ON DELETE RESTRICT,
    CONSTRAINT chk_ticket_messages_type CHECK (type IN ('PUBLIC', 'INTERNAL'))
);

CREATE INDEX idx_ticket_messages_ticket_id_created_at ON ticket_messages(ticket_id, created_at);
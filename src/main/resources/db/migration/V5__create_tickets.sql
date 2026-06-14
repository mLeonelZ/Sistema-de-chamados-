CREATE TABLE tickets (
    id UUID PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    subject VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category_id UUID NOT NULL,
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    client_id UUID NOT NULL,
    assignee_id UUID,
    sla_first_response_deadline TIMESTAMP,
    sla_resolution_deadline TIMESTAMP,
    first_response_at TIMESTAMP,
    resolved_at TIMESTAMP,
    closed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_tickets_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON UPDATE NO ACTION
        ON DELETE RESTRICT,
    CONSTRAINT fk_tickets_client
        FOREIGN KEY (client_id)
        REFERENCES users(id)
        ON UPDATE NO ACTION
        ON DELETE RESTRICT,
    CONSTRAINT fk_tickets_assignee
        FOREIGN KEY (assignee_id)
        REFERENCES users(id)
        ON UPDATE NO ACTION
        ON DELETE SET NULL,
    CONSTRAINT chk_tickets_priority CHECK (priority IN ('CRITICA', 'ALTA', 'MEDIA', 'BAIXA')),
    CONSTRAINT chk_tickets_status CHECK (status IN ('ABERTO', 'EM_ANDAMENTO', 'RESOLVIDO', 'FECHADO')),
    CONSTRAINT chk_tickets_channel CHECK (channel IN ('EMAIL', 'TELEFONE', 'SLACK', 'PORTAL'))
);

CREATE INDEX idx_tickets_code ON tickets(code);
CREATE INDEX idx_tickets_status_priority_created_at ON tickets(status, priority, created_at);
CREATE INDEX idx_tickets_client_id ON tickets(client_id);
CREATE INDEX idx_tickets_assignee_id ON tickets(assignee_id);
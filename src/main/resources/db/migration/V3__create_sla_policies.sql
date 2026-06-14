CREATE TABLE sla_policies (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    response_time_minutes INTEGER NOT NULL,
    resolution_time_minutes INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_sla_response_positive CHECK (response_time_minutes > 0),
    CONSTRAINT chk_sla_resolution_positive CHECK (resolution_time_minutes > 0)
);
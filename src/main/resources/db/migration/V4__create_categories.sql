CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    sla_policy_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_categories_sla_policy
        FOREIGN KEY (sla_policy_id)
        REFERENCES sla_policies(id)
        ON UPDATE NO ACTION
        ON DELETE RESTRICT
);

CREATE INDEX idx_categories_sla_policy_id ON categories(sla_policy_id);
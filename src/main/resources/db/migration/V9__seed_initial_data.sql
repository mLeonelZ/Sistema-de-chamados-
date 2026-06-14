INSERT INTO departments (id, name, manager_name, status)
VALUES
    ('7a91f8f0-b6d9-4d7c-9fda-0ec7836fc101', 'Suporte N1', 'Coordenador N1', 'ACTIVE'),
    ('7a91f8f0-b6d9-4d7c-9fda-0ec7836fc102', 'Suporte N2', 'Coordenador N2', 'ACTIVE'),
    ('7a91f8f0-b6d9-4d7c-9fda-0ec7836fc103', 'Infraestrutura', 'Coordenador Infra', 'ACTIVE');

INSERT INTO sla_policies (id, name, response_time_minutes, resolution_time_minutes)
VALUES
    ('9b4b706f-3cc8-4f9e-a86e-09b89ac1c201', 'Crítico', 15, 240),
    ('9b4b706f-3cc8-4f9e-a86e-09b89ac1c202', 'Alto', 30, 480),
    ('9b4b706f-3cc8-4f9e-a86e-09b89ac1c203', 'Médio', 60, 1440),
    ('9b4b706f-3cc8-4f9e-a86e-09b89ac1c204', 'Baixo', 120, 2880);

INSERT INTO categories (id, name, sla_policy_id)
VALUES
    ('4fd3d73c-6e4d-45ce-9b0e-f27ec9ec7301', 'Hardware', '9b4b706f-3cc8-4f9e-a86e-09b89ac1c202'),
    ('4fd3d73c-6e4d-45ce-9b0e-f27ec9ec7302', 'Software', '9b4b706f-3cc8-4f9e-a86e-09b89ac1c203'),
    ('4fd3d73c-6e4d-45ce-9b0e-f27ec9ec7303', 'Rede', '9b4b706f-3cc8-4f9e-a86e-09b89ac1c201'),
    ('4fd3d73c-6e4d-45ce-9b0e-f27ec9ec7304', 'Acesso', '9b4b706f-3cc8-4f9e-a86e-09b89ac1c204');

INSERT INTO users (id, name, email, password_hash, role, status, department_id)
VALUES
    ('e8f6a593-f388-4d92-b4d0-8bfecf4f7101', 'Administrador', 'admin@helpdesk.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', 'ACTIVE', '7a91f8f0-b6d9-4d7c-9fda-0ec7836fc101');
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
    ('f1b2c3d4-1111-4d92-b4d0-8bfecf4f7201', 'Agente N2', 'agente.n2@helpdesk.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ATENDENTE', 'ACTIVE', '7a91f8f0-b6d9-4d7c-9fda-0ec7836fc102'),
    ('f1b2c3d4-1111-4d92-b4d0-8bfecf4f7202', 'Cliente QA', 'cliente.qa@helpdesk.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CLIENTE', 'ACTIVE', '7a91f8f0-b6d9-4d7c-9fda-0ec7836fc103');

INSERT INTO tickets (
    id,
    code,
    subject,
    description,
    category_id,
    priority,
    status,
    channel,
    client_id,
    assignee_id,
    sla_first_response_deadline,
    sla_resolution_deadline,
    first_response_at,
    resolved_at,
    closed_at
)
VALUES
    (
        '5fef7de8-d114-4f68-a257-8a5af6c8d811',
        'CHD-1101',
        'Notebook não liga',
        'Equipamento desligou e não inicia após atualização.',
        '4fd3d73c-6e4d-45ce-9b0e-f27ec9ec7301',
        'ALTA',
        'EM_ANDAMENTO',
        'PORTAL',
        'f1b2c3d4-1111-4d92-b4d0-8bfecf4f7202',
        'f1b2c3d4-1111-4d92-b4d0-8bfecf4f7201',
        NOW() + INTERVAL '30 minutes',
        NOW() + INTERVAL '8 hours',
        NOW() - INTERVAL '10 minutes',
        NULL,
        NULL
    ),
    (
        '5fef7de8-d114-4f68-a257-8a5af6c8d812',
        'CHD-1102',
        'Erro ao acessar VPN',
        'Conexão VPN retorna erro de autenticação.',
        '4fd3d73c-6e4d-45ce-9b0e-f27ec9ec7303',
        'MEDIA',
        'ABERTO',
        'EMAIL',
        'f1b2c3d4-1111-4d92-b4d0-8bfecf4f7202',
        NULL,
        NOW() + INTERVAL '1 hour',
        NOW() + INTERVAL '24 hours',
        NULL,
        NULL,
        NULL
    );

INSERT INTO ticket_messages (id, ticket_id, author_id, type, text)
VALUES
    ('6ab98787-1f62-4494-a635-dde63cc2f911', '5fef7de8-d114-4f68-a257-8a5af6c8d811', 'f1b2c3d4-1111-4d92-b4d0-8bfecf4f7201', 'PUBLIC', 'Recebemos o chamado e estamos em análise.'),
    ('6ab98787-1f62-4494-a635-dde63cc2f912', '5fef7de8-d114-4f68-a257-8a5af6c8d811', 'f1b2c3d4-1111-4d92-b4d0-8bfecf4f7201', 'INTERNAL', 'Suspeita de falha de fonte de alimentação.');

INSERT INTO notifications (id, user_id, ticket_id, title, message, type, read)
VALUES
    ('76d50a7a-d4d7-4596-a9ea-a2a58bdb1011', 'f1b2c3d4-1111-4d92-b4d0-8bfecf4f7202', '5fef7de8-d114-4f68-a257-8a5af6c8d811', 'Chamado em andamento', 'Seu chamado CHD-1101 está em andamento.', 'INFO', false),
    ('76d50a7a-d4d7-4596-a9ea-a2a58bdb1012', 'f1b2c3d4-1111-4d92-b4d0-8bfecf4f7201', '5fef7de8-d114-4f68-a257-8a5af6c8d811', 'Atenção SLA', 'Prazo de primeira resposta próximo do vencimento.', 'ALERTA_SLA', false);

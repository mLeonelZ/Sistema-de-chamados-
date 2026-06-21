UPDATE users 
SET password_hash = '$2a$10$HIV458bcFpbmwXnmUchD1OUM9VEC7zAHS4AdgNaD8GPhId1PJPAT6'
WHERE email IN ('admin@helpdesk.local', 'agente.n2@helpdesk.local', 'cliente.qa@helpdesk.local');

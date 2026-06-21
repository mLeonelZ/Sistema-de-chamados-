ALTER TABLE notifications DROP CONSTRAINT chk_notifications_type;

ALTER TABLE notifications ADD CONSTRAINT chk_notifications_type CHECK (type IN (
    'ALERTA_SLA', 
    'VIOLACAO_SLA', 
    'INFO', 
    'SUCCESS', 
    'NOVO_CHAMADO', 
    'ATRIBUICAO', 
    'NOVA_MENSAGEM'
));

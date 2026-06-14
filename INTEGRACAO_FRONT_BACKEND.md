# Integração Frontend x Backend (Helpdesk)

Este documento lista, de forma prática, o que deve ser alterado no frontend para substituir os dados mockados e consumir a API do backend.

## 1) Configurar URL base da API

No frontend, criar variável de ambiente:

- `VITE_API_BASE_URL=http://localhost:8080/api/v1`

Exemplo de uso:

- `const API_BASE_URL = import.meta.env.VITE_API_BASE_URL`

## 2) Criar camada HTTP única

Criar um helper único para requisições (`fetch`) com:

- `Content-Type: application/json`
- serialização de body (`JSON.stringify`)
- parse de resposta JSON
- tratamento padronizado de erro por status HTTP

Comportamento recomendado:

- `400`: erro de validação (mostrar mensagem amigável)
- `404`: recurso não encontrado
- `409`: conflito (ex.: e-mail já cadastrado)
- `500`: erro interno

## 3) Parar de usar mockData como fonte principal

O `mockData.js` deve ser removido do fluxo principal de telas.

Estratégia sugerida:

- manter mocks apenas para fallback local de desenvolvimento (opcional)
- trocar carregamentos iniciais para chamadas API
- trocar criação/edição/exclusão para POST/PUT/DELETE reais

## 4) Endpoints que o frontend deve consumir

Base: `/api/v1`

### Usuários

- `GET /users`
- `GET /users/{id}`
- `POST /users`
- `PUT /users/{id}`
- `DELETE /users/{id}`

### Tickets

- `GET /tickets`
- `GET /tickets/{id}`
- `POST /tickets`
- `PUT /tickets/{id}`
- `DELETE /tickets/{id}`

### Mensagens de ticket

- `GET /ticket-messages`
- `GET /ticket-messages/{id}`
- `POST /ticket-messages`
- `PUT /ticket-messages/{id}`
- `DELETE /ticket-messages/{id}`

### Notificações

- `GET /notifications`
- `GET /notifications/{id}`
- `POST /notifications`
- `PUT /notifications/{id}`
- `DELETE /notifications/{id}`

### Categorias

- `GET /categories`
- `GET /categories/{id}`
- `POST /categories`
- `PUT /categories/{id}`
- `DELETE /categories/{id}`

### Departamentos

- `GET /departments`
- `GET /departments/{id}`
- `POST /departments`
- `PUT /departments/{id}`
- `DELETE /departments/{id}`

### Políticas de SLA

- `GET /sla-policies`
- `GET /sla-policies/{id}`
- `POST /sla-policies`
- `PUT /sla-policies/{id}`
- `DELETE /sla-policies/{id}`

## 5) Ajustar payloads para IDs (não nomes)

O backend trabalha com relacionamentos por UUID. O frontend precisa enviar IDs nas relações.

Exemplos:

- Ticket: `categoryId`, `clientId`, `assigneeId`
- Mensagem: `ticketId`, `authorId`
- Notificação: `userId`, `ticketId`
- Usuário: `departmentId`
- Categoria: `slaPolicyId`

## 6) Mapear enums do frontend para enums do backend

O backend espera enums em maiúsculo, com nomes específicos.

### TicketPriority

- `critica` -> `CRITICA`
- `alta` -> `ALTA`
- `media` -> `MEDIA`
- `baixa` -> `BAIXA`

### TicketStatus

- `aberto` -> `ABERTO`
- `em_andamento` -> `EM_ANDAMENTO`
- `resolvido` -> `RESOLVIDO`
- `fechado` -> `FECHADO`

### TicketChannel

- `email` -> `EMAIL`
- `telefone` -> `TELEFONE`
- `slack` -> `SLACK`
- `portal` -> `PORTAL`

### MessageType

- `public` -> `PUBLIC`
- `internal` -> `INTERNAL`

### NotificationType

- `alerta_sla` -> `ALERTA_SLA`
- `violacao_sla` -> `VIOLACAO_SLA`
- `info` -> `INFO`
- `success` -> `SUCCESS`

### DepartmentStatus / UserStatus

- `active` -> `ACTIVE`
- `inactive` -> `INACTIVE`

### Role

- `admin` -> `ADMIN`
- `atendente` -> `ATENDENTE`
- `cliente` -> `CLIENTE`

## 7) Criar adapters (API -> UI e UI -> API)

Como o frontend atual usa estruturas próprias, criar adapters para evitar quebrar componentes.

Sugestão:

- `fromApiUser`, `toApiUser`
- `fromApiTicket`, `toApiTicket`
- `fromApiCategory`, `toApiCategory`
- etc.

Pontos importantes:

- converter enums backend -> formato exibido no frontend
- converter formato de datas ISO para o formato da UI
- garantir que campos opcionais (`assigneeId`, `ticketId`) possam ser `null`

## 8) Ajustar fluxo de autenticação

Situação atual:

- frontend espera login real
- backend atual não possui endpoint de autenticação (`POST /auth/login`)

No frontend, para integrar com o backend atual:

- manter login mock temporário, ou
- ocultar/neutralizar fluxo de token até backend expor autenticação

Quando o backend tiver auth:

- salvar token (preferencialmente em memória + refresh seguro)
- enviar `Authorization: Bearer <token>` no helper HTTP

## 9) Tratar CORS no ambiente local

Para o frontend em `http://localhost:5173`, o backend deve liberar CORS.

Impacto no frontend:

- nenhuma alteração de código além da base URL
- validar no navegador se erros de CORS desapareceram após configuração backend

## 10) Ordem recomendada de implementação no frontend

1. Configurar `VITE_API_BASE_URL`
2. Criar helper HTTP único
3. Criar módulos de API por recurso (`usersApi`, `ticketsApi`, etc.)
4. Criar mapeadores de enums
5. Criar adapters de DTO (API/UI)
6. Substituir carregamentos de lista por GET real
7. Substituir formulários de criação/edição por POST/PUT
8. Substituir exclusões por DELETE
9. Padronizar tratamento de erro e feedback visual
10. Remover dependência funcional de `mockData.js`

## 11) Checklist de pronto

- Todas as telas carregam dados da API
- CRUD funcionando para recursos usados pela UI
- Nenhuma tela depende de mock para fluxo principal
- Enums renderizam corretamente (sem erro de parse)
- Relacionamentos enviados por UUID
- Sem erro de CORS no browser
- Tratamento de erro por status HTTP implementado

---

Arquivo criado no backend por limitação de workspace da sessão. Conteúdo pronto para ser copiado para o repositório do frontend, se necessário.

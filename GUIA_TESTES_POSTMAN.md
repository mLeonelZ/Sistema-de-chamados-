# Guia completo da aplicação e testes no Postman

## 1) Visão geral
Aplicação backend Spring Boot para Help Desk com persistência em PostgreSQL e migrações Flyway.

Camadas implementadas:
- Controllers REST (`src/main/java/com/helpdesk/controller`)
- Services (`src/main/java/com/helpdesk/service`)
- Repositórios JPA (`src/main/java/com/helpdesk/repository`)
- Entidades JPA (`src/main/java/com/helpdesk/model`)
- DTOs de entrada/saída (`src/main/java/com/helpdesk/dto`)

Recursos atuais da API:
- Departments
- SlaPolicies
- Categories
- Users
- Tickets
- TicketMessages
- Notifications

## 2) Pré-requisitos
- Java 17
- Docker
- Postman

## 3) Subindo a aplicação do zero
### 3.1 Banco PostgreSQL
Use variáveis de ambiente para bater com `application.yaml` (`src/main/resources/application.yaml:1`):
- `DB_HOST` (default `localhost`)
- `DB_PORT` (default `5432`)
- `DB_NAME` (default `chamados`)
- `DB_USER` (default `admin`)
- `DB_PASSWORD` (default `admin123`)

No projeto, subir o banco:
```bash
docker compose up -d
```

### 3.2 Aplicação Spring
```bash
./mvnw spring-boot:run
```
No Windows:
```bash
mvnw.cmd spring-boot:run
```

API base URL: `http://localhost:8080`

Swagger (se ativo):
- `http://localhost:8080/swagger-ui/index.html`

## 4) Seed inicial disponível
A migration `src/main/resources/db/migration/V9__seed_initial_data.sql:1` já cria:
- 3 departamentos
- 4 políticas de SLA
- 4 categorias
- 1 usuário admin

Isso permite começar testes sem criar tudo manualmente.

## 5) Ordem recomendada no Postman (fluxo fim a fim)
Crie uma Collection e defina variável `baseUrl = http://localhost:8080`.

1. `GET {{baseUrl}}/api/v1/departments`
2. `GET {{baseUrl}}/api/v1/sla-policies`
3. `GET {{baseUrl}}/api/v1/categories`
4. `POST {{baseUrl}}/api/v1/users` (criar cliente e atendente)
5. `POST {{baseUrl}}/api/v1/tickets` (abrir chamado)
6. `POST {{baseUrl}}/api/v1/ticket-messages` (mensagem pública/interna)
7. `POST {{baseUrl}}/api/v1/notifications` (notificação)
8. `GET` por ID para validar vínculos
9. `DELETE` na ordem inversa para evitar erro de FK

## 6) Contratos de payload (POST)
### 6.1 Department
`POST /api/v1/departments`
```json
{"name":"Operações","managerName":"Maria","status":"ACTIVE"}
```

### 6.2 SlaPolicy
`POST /api/v1/sla-policies`
```json
{"name":"Urgente","responseTimeMinutes":10,"resolutionTimeMinutes":120}
```

### 6.3 Category
`POST /api/v1/categories`
```json
{"name":"ERP","slaPolicyId":"22222222-2222-2222-2222-222222222222"}
```

### 6.4 User
`POST /api/v1/users`
```json
{"name":"João Cliente","email":"joao@empresa.com","passwordHash":"hash-temporario","role":"CLIENTE","status":"ACTIVE","departmentId":"11111111-1111-1111-1111-111111111111"}
```

### 6.5 Ticket
`POST /api/v1/tickets`
```json
{"code":"CHM-9001","subject":"Erro no ERP","description":"Não consigo gerar relatório","categoryId":"33333333-3333-3333-3333-333333333333","priority":"ALTA","status":"ABERTO","channel":"PORTAL","clientId":"44444444-4444-4444-4444-444444444444","assigneeId":null,"slaFirstResponseDeadline":"2026-06-14T12:00:00","slaResolutionDeadline":"2026-06-15T12:00:00","firstResponseAt":null,"resolvedAt":null,"closedAt":null}
```

### 6.6 TicketMessage
`POST /api/v1/ticket-messages`
```json
{"ticketId":"55555555-5555-5555-5555-555555555555","authorId":"44444444-4444-4444-4444-444444444444","type":"PUBLIC","text":"Detalhando o problema com prints"}
```

### 6.7 Notification
`POST /api/v1/notifications`
```json
{"userId":"44444444-4444-4444-4444-444444444444","ticketId":"55555555-5555-5555-5555-555555555555","title":"Chamado criado","message":"Seu chamado foi registrado","type":"INFO","read":false}
```

## 7) Endpoints disponíveis
Todos têm prefixo `/api/v1`.

### 7.1 Departments
- `GET /departments`
- `GET /departments/{id}`
- `POST /departments`
- `DELETE /departments/{id}`

### 7.2 SLA Policies
- `GET /sla-policies`
- `GET /sla-policies/{id}`
- `POST /sla-policies`
- `DELETE /sla-policies/{id}`

### 7.3 Categories
- `GET /categories`
- `GET /categories/{id}`
- `POST /categories`
- `DELETE /categories/{id}`

### 7.4 Users
- `GET /users`
- `GET /users/{id}`
- `POST /users`
- `DELETE /users/{id}`

### 7.5 Tickets
- `GET /tickets`
- `GET /tickets/{id}`
- `POST /tickets`
- `DELETE /tickets/{id}`

### 7.6 Ticket Messages
- `GET /ticket-messages`
- `GET /ticket-messages/{id}`
- `POST /ticket-messages`
- `DELETE /ticket-messages/{id}`

### 7.7 Notifications
- `GET /notifications`
- `GET /notifications/{id}`
- `POST /notifications`
- `DELETE /notifications/{id}`

## 8) Valores válidos de enums
Use exatamente os valores abaixo.

- `Role`: `ADMIN`, `ATENDENTE`, `CLIENTE`
- `UserStatus`: `ACTIVE`, `INACTIVE`
- `DepartmentStatus`: `ACTIVE`, `INACTIVE`
- `TicketPriority`: `CRITICA`, `ALTA`, `MEDIA`, `BAIXA`
- `TicketStatus`: `ABERTO`, `EM_ANDAMENTO`, `RESOLVIDO`, `FECHADO`
- `TicketChannel`: `EMAIL`, `TELEFONE`, `SLACK`, `PORTAL`
- `MessageType`: `PUBLIC`, `INTERNAL`
- `NotificationType`: `ALERTA_SLA`, `VIOLACAO_SLA`, `INFO`, `SUCCESS`

## 9) Roteiro de testes no Postman do início ao fim
### 9.1 Happy path completo
1. Criar Department
2. Criar SlaPolicy
3. Criar Category vinculando ao SlaPolicy
4. Criar User cliente
5. Criar User atendente
6. Criar Ticket com `clientId` do cliente e `assigneeId` do atendente
7. Criar TicketMessage `PUBLIC`
8. Criar TicketMessage `INTERNAL`
9. Criar Notification para o cliente
10. Validar listagens (`GET`) de todas as coleções
11. Validar buscas por ID (`GET /{id}`)
12. Excluir Notification
13. Excluir TicketMessages
14. Excluir Ticket
15. Excluir Users
16. Excluir Category
17. Excluir SlaPolicy
18. Excluir Department

### 9.2 Casos de erro por entidade
#### A) Department
- `POST` com `status` inválido: retorna erro de constraint/check
- `POST` com `name` duplicado: erro de unique
- `GET /{id}` inexistente: erro de entidade não encontrada

#### B) SlaPolicy
- `POST` com `responseTimeMinutes <= 0`: erro de check
- `POST` com `resolutionTimeMinutes <= 0`: erro de check
- `POST` com `name` duplicado: erro de unique
- `GET /{id}` inexistente: erro de entidade não encontrada

#### C) Category
- `POST` com `slaPolicyId` inexistente: erro de FK
- `POST` com `name` duplicado: erro de unique
- `DELETE` de category referenciada por ticket: erro por `ON DELETE RESTRICT`

#### D) User
- `POST` com `email` duplicado: erro de unique
- `POST` com `role` inválido: erro de check
- `POST` com `status` inválido: erro de check
- `POST` com `departmentId` inexistente: erro de FK
- `DELETE` de user referenciado como `client_id`: erro por `ON DELETE RESTRICT`
- `DELETE` de user referenciado como `assignee_id`: ticket mantém registro com `assignee_id` nulo

#### E) Ticket
- `POST` com `categoryId` inexistente: erro de FK
- `POST` com `clientId` inexistente: erro de FK
- `POST` com `assigneeId` inexistente: erro de FK
- `POST` com `code` duplicado: erro de unique
- `POST` com `priority/status/channel` inválidos: erro de check
- `GET /{id}` inexistente: erro de entidade não encontrada

#### F) TicketMessage
- `POST` com `ticketId` inexistente: erro de entidade não encontrada/fk
- `POST` com `authorId` inexistente: erro de entidade não encontrada/fk
- `POST` com `type` inválido: erro de check
- `DELETE` de ticket remove mensagens em cascata (`ON DELETE CASCADE`)

#### G) Notification
- `POST` com `userId` inexistente: erro de entidade não encontrada/fk
- `POST` com `ticketId` inexistente: erro de entidade não encontrada/fk
- `POST` com `type` inválido: erro de check
- `DELETE` de ticket mantém notificação e zera `ticket_id` (`ON DELETE SET NULL`)

## 10) Coleção Postman sugerida
Pastas:
- 00 Health/Smoke
- 01 Departments
- 02 SlaPolicies
- 03 Categories
- 04 Users
- 05 Tickets
- 06 TicketMessages
- 07 Notifications
- 08 E2E Flow
- 09 Error Cases

Variáveis de collection:
- `baseUrl = http://localhost:8080`
- `departmentId`
- `slaPolicyId`
- `categoryId`
- `clientUserId`
- `assigneeUserId`
- `ticketId`
- `ticketMessageId`
- `notificationId`

## 11) Scripts de teste no Postman (aba Tests)
Exemplo para salvar ID criado:
```javascript
pm.test("status 201", function () {
  pm.response.to.have.status(201);
});
const body = pm.response.json();
pm.collectionVariables.set("ticketId", body.id);
```

Exemplo para validar lista não vazia:
```javascript
pm.test("status 200", function () {
  pm.response.to.have.status(200);
});
const body = pm.response.json();
pm.expect(Array.isArray(body)).to.eql(true);
pm.expect(body.length).to.be.greaterThan(0);
```

## 12) Observações importantes de comportamento atual
- Não há autenticação ativa no momento (security está desabilitada no POM).
- Não há endpoints de update (`PUT/PATCH`) nas entidades atuais.
- Não existe handler global de erros customizado; respostas de erro são as padrão do Spring/JPA.
- Como `createdAt/updatedAt` são preenchidos no controller, não envie estes campos no request.

## 13) Checklist final de validação
- Banco subiu e migrations executaram sem erro
- Todos os `GET` retornam 200
- Todos os `POST` válidos retornam 201
- Todos os `DELETE` válidos retornam 204
- Casos inválidos retornam erro consistente (4xx/5xx conforme constraint)
- Fluxo E2E completo executado com sucesso

## 14) Referências de implementação
- Configuração: `src/main/resources/application.yaml:1`
- Migrations: `src/main/resources/db/migration/V1__create_users.sql:1` até `V9__seed_initial_data.sql:1`
- Controllers:
  - `src/main/java/com/helpdesk/controller/DepartmentController.java:23`
  - `src/main/java/com/helpdesk/controller/SlaPolicyController.java:23`
  - `src/main/java/com/helpdesk/controller/CategoryController.java:25`
  - `src/main/java/com/helpdesk/controller/UserController.java:23`
  - `src/main/java/com/helpdesk/controller/TicketController.java:27`
  - `src/main/java/com/helpdesk/controller/TicketMessageController.java:27`
  - `src/main/java/com/helpdesk/controller/NotificationController.java:27`
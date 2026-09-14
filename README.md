# ARIA Backend

Backend da plataforma ARIA, desenvolvido para a Sprint 2 do Challenge Águia Branca (FIAP).

## Tecnologias
- Java 17 · Spring Boot 4.1.1
- Spring Security + JWT (autenticação stateless)
- Spring Data MongoDB
- MongoDB Atlas

## Pré-requisitos
- JDK 17+
- Maven
- Acesso à connection string do MongoDB Atlas (solicitar à equipe)

## Configuração

Defina as variáveis de ambiente antes de rodar:

| Variável | Descrição |
|---|---|
| `MONGODB_URI` | Connection string do MongoDB Atlas |
| `JWT_SECRET` | Chave de assinatura do JWT (opcional em dev — usa um valor padrão se não definida) |

- **IntelliJ:** Run → Edit Configurations → Environment variables
- **Terminal (PowerShell):** `$env:MONGODB_URI="mongodb+srv://..."`

## Como rodar
\`\`\`bash
./mvnw spring-boot:run
\`\`\`
A aplicação sobe em `http://localhost:8080`.

## Autenticação

A API usa JWT. Fluxo:
1. `POST /api/v1/auth/register` ou `POST /api/v1/auth/login` retorna um `accessToken`
2. Envie esse token em requisições subsequentes: `Authorization: Bearer <token>`
3. O token expira em 24h — é necessário logar novamente após esse período

### Perfis (roles) e permissões

| Role | Pode fazer |
|---|---|
| OPERADOR | CRUD das próprias ideias; consulta projetos e orientações |
| GESTOR | CRUD de projetos; aprova/rejeita/pontua ideias; consulta orientações |
| LIDER | CRUD de orientações estratégicas; consulta ideias e projetos |

### Usuários de teste

| Perfil | E-mail | Senha |
|---|---|---|
| OPERADOR | operador@aria.com | senha123 |
| GESTOR | gestor@aria.com | senha123 |
| LIDER | lider@aria.com | senha123 |

## Endpoints

| Recurso | Rotas |
|---|---|
| Auth | `POST /auth/register`, `POST /auth/login` |
| Ideas | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `PATCH /{id}/review` (GESTOR), `DELETE /{id}` |
| Projects | `GET`, `GET /{id}`, `POST` (GESTOR), `PUT /{id}` (GESTOR), `PATCH /{id}/progress` (GESTOR), `DELETE /{id}` (GESTOR) |
| Orientations | `GET`, `GET /{id}`, `POST` (LIDER), `PUT /{id}` (LIDER), `DELETE /{id}` (LIDER) |
| Notifications | `GET`, `POST`, `PATCH /{id}/read`, `PATCH /read-all`, `DELETE /{id}` |

Todas as rotas (exceto `/auth/**`) exigem token válido. Especificação completa (payload/resposta) será documentada em `/docs` antes da entrega final.

## Estrutura do projeto
\`\`\`
com.fiap.aria_backend
├── config       → configurações (Security, CORS)
├── controller   → endpoints REST
├── dto          → objetos de request/response
├── exception    → tratamento global de erros
├── model        → documentos do MongoDB
├── repository   → interfaces Spring Data MongoDB
├── security     → JWT, filtros, UserDetails
└── service      → regras de negócio
\`\`\`

## Pendências conhecidas
- Endpoint de recuperação de senha
- Dashboard/relatórios agregados
- Integração com IA (Gemini API) para pontuação de ideias

## Equipe
- Ana Cristina dos Santos — RM 565086
- Bruno Queiroz Pires — RM 561676
- Vítor Mello de Araújo — RM 561632
- Vítor Passeri de Souza Kaluf — RM 562852
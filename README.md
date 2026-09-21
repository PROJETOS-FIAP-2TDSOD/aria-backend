# ARIA Backend

Backend da plataforma ARIA, desenvolvido para a Sprint 2 do Challenge Águia Branca (FIAP).

## Tecnologias
- Java 17 · Spring Boot 4.1.1
- Spring Security + JWT (autenticação stateless)
- Spring Data MongoDB
- MongoDB Atlas
- Google Gemini API (pontuação de ideias por IA)
- Docker (deploy)

## Deploy

O backend está hospedado no **Render** (free tier), conectado ao **MongoDB Atlas**:

```
https://aria-backend-p7bk.onrender.com
```

O app Android (`.apk` de entrega) já consome essa URL diretamente — não é
necessário rodar o backend localmente para testar o app.

Para rodar o backend **localmente** (código-fonte anexado à entrega), siga as
instruções abaixo.

## Pré-requisitos
- JDK 17+
- Maven
- Acesso à connection string do MongoDB Atlas (solicitar à equipe, ou usar as
  credenciais já preenchidas no `application.properties` do `.zip` de entrega)

## Configuração

Defina as variáveis de ambiente antes de rodar:

| Variável | Descrição |
|---|---|
| `MONGODB_URI` | Connection string do MongoDB Atlas |
| `JWT_SECRET` | Chave de assinatura do JWT (opcional em dev — usa um valor padrão se não definida) |
| `GEMINI_API_KEY` | Chave da API do Google Gemini, usada em `POST /ideas/{id}/ai-score` |

- **IntelliJ:** Run → Edit Configurations → Environment variables
- **Terminal (PowerShell):** `$env:MONGODB_URI="mongodb+srv://..."`

No `.zip` de entrega, essas variáveis já vêm preenchidas com valores reais
diretamente no `application.properties`, para que não seja necessário
configurar nada — basta rodar. **Esse arquivo com credenciais reais nunca é
commitado no repositório público**; a versão no GitHub usa apenas variáveis de
ambiente (`${MONGODB_URI}`, etc.), sem nenhum valor sensível exposto.

## Como rodar

**Via Maven (recomendado para desenvolvimento local):**
```bash
./mvnw spring-boot:run
```
A aplicação sobe em `http://localhost:8080`.

**Via Docker** (mesmo processo usado no deploy do Render):
```bash
docker build -t aria-backend .
docker run -p 8080:8080 \
  -e MONGODB_URI="sua-connection-string" \
  -e JWT_SECRET="sua-chave" \
  -e GEMINI_API_KEY="sua-chave" \
  aria-backend
```

## Autenticação

A API usa JWT. Fluxo:
1. `POST /api/v1/auth/register` ou `POST /api/v1/auth/login` retorna um `accessToken`
2. Envie esse token em requisições subsequentes: `Authorization: Bearer <token>`
3. O token expira em 24h — é necessário logar novamente após esse período
4. Esqueceu a senha? `POST /api/v1/auth/recover-password` gera uma nova senha temporária (sem serviço de e-mail configurado nesta sprint, o envio é simulado via log do servidor)

### Perfis (roles) e permissões

| Role | Pode fazer |
|---|---|
| OPERADOR | CRUD completo das próprias ideias (incluindo exclusão); consulta projetos e orientações |
| GESTOR | Analisa, aprova/rejeita e pontua ideias por IA; CRUD de projetos; consulta orientações |
| LIDER | CRUD de orientações estratégicas; consulta ideias e projetos (sem criar/editar projeto); consulta o dashboard de relatórios |

### Usuários de teste

| Perfil | E-mail | Senha |
|---|---|---|
| OPERADOR | operador@aria.com | senha123 |
| GESTOR | gestor@aria.com | senha123 |
| LIDER | lider@aria.com | senha123 |

## Endpoints

| Recurso | Rotas |
|---|---|
| Auth | `POST /auth/register`, `POST /auth/login`, `POST /auth/recover-password` |
| Ideas | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `PATCH /{id}/review` (GESTOR), `POST /{id}/ai-score` (GESTOR), `DELETE /{id}` (autor) |
| Projects | `GET`, `GET /{id}`, `POST` (GESTOR), `PUT /{id}` (GESTOR), `PATCH /{id}/progress` (GESTOR), `DELETE /{id}` (GESTOR) |
| Orientations | `GET`, `GET /{id}`, `POST` (LIDER), `PUT /{id}` (LIDER), `DELETE /{id}` (LIDER) |
| Notifications | `GET`, `POST`, `PATCH /{id}/read`, `PATCH /read-all`, `DELETE /{id}` |
| Users | `GET /me`, `GET`, `GET /{id}` |
| Dashboard | `GET /dashboard/summary` (LIDER), `GET /dashboard/roi-by-strategy` (LIDER), `GET /dashboard/roi-by-project` (LIDER) |

Todas as rotas (exceto `/auth/**`) exigem token válido. Especificação completa
de payload/resposta de cada endpoint está na apresentação de entrega.

## Estrutura do projeto
```
com.fiap.aria_backend
├── config       → configurações (Security, CORS)
├── controller   → endpoints REST
├── dto          → objetos de request/response
├── exception    → tratamento global de erros
├── model        → documentos do MongoDB
├── repository   → interfaces Spring Data MongoDB
├── security     → JWT, filtros, UserDetails
├── service      → regras de negócio
└── util         → utilitários compartilhados (formatação de moeda, cálculo de ROI)
```

## Diferencial de IA (Plus)

Pontuação e priorização de ideias via **Google Gemini API**
(`POST /ideas/{id}/ai-score`, restrito a GESTOR): a IA analisa título,
categoria, descrição, problema, benefícios e recursos da ideia e retorna uma
nota (`aiScore`) e uma justificativa textual (`aiJustification`), auxiliando o
gestor na priorização de quais ideias avançam para projeto.

## Equipe
- Ana Cristina dos Santos — RM 565086
- Bruno Queiroz Pires — RM 561676
- Vítor Mello de Araújo — RM 561632
- Vítor Passeri de Souza Kaluf — RM 562852
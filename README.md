# ARIA Backend

Backend da plataforma ARIA, desenvolvido para a Sprint 2 do Challenge Águia Branca (FIAP).

## Tecnologias
- Java 17 · Spring Boot 4.1.1
- Spring Security + JWT
- Spring Data MongoDB
- MongoDB Atlas

## Pré-requisitos
- JDK 17+
- Maven
- Acesso à connection string do MongoDB Atlas (solicitar à equipe)

## Configuração
Defina a variável de ambiente `MONGODB_URI` com a connection string do Atlas antes de rodar:

- **IntelliJ:** Run → Edit Configurations → Environment variables → `MONGODB_URI=mongodb+srv://...`
- **Terminal (Windows PowerShell):** `$env:MONGODB_URI="mongodb+srv://..."`

## Como rodar
\`\`\`bash
./mvnw spring-boot:run
\`\`\`
A aplicação sobe em `http://localhost:8080`.

## Estrutura do projeto
\`\`\`
com.fiap.aria_backend
├── config       → configurações (Security, CORS)
├── controller   → endpoints REST
├── dto          → objetos de request/response
├── model        → documentos do MongoDB
├── repository   → interfaces Spring Data MongoDB
├── security     → JWT
└── service      → regras de negócio
\`\`\`

## Endpoints
_Em construção — especificação completa em `/docs`._

## Equipe
- Ana Cristina dos Santos — RM 565086
- Bruno Queiroz Pires — RM 561676
- Vítor Mello de Araújo — RM 561632
- Vítor Passeri de Souza Kaluf — RM 562852
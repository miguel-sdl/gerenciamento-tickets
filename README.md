# 🧾 Sistema de Gerenciamento de Tickets

Sistema de gerenciamento de tickets para **Help Desk / Suporte Técnico**, desenvolvido em **Spring Boot**, com:
- Autenticação **JWT** (roles: ADMIN, TÉCNICO, USUÁRIO)
- Persistência com **JPA / MySQL**
- Monitoramento via **Prometheus + Grafana**
- Documentação **Swagger / OpenAPI**
- Testes: unitários e de integração



## 🚀 Tecnologias utilizadas
- Java 21 
- Spring Boot 3 (Web, Security, Data JPA, Validation, Actuator)
- JWT
- MySQL / H2 (testes)
- Docker & Docker Compose
- Prometheus, Grafana
- JUnit 5, Mockito, MockMvc



## 🔐 Endpoints Principais
### 🔑 Autenticação
- `POST /auth/register` — cadastrar usuário
- `POST /auth/login` — obter JWT

### 🎫 Tickets
- `GET /tickets` — Lista tickets do usuário autenticado
- `GET /tickets/{id}` — Busca ticket por id 
- `POST /tickets` — Cria ticket
- `POST /tickets/comentario` — Adiciona comentário ao ticket

Os endpoints em `/tickets` retornam 403 quando usuario tenta interagir com um ticket de outro usuário.

### 🛠️ Admin
- `POST /admin/tickets/categoria` — Cria nova categoria de ticket
- `POST /admin/tickets/` — Busca por status, categoria, técnico, vencimento etc.

### 📚 Documentação da API (Swagger / OpenAPI)
Para mais informações de endpoints a API fornece documentação automática via Swagger/OpenAPI. Após subir a aplicação, acesse:
```
http://localhost:8080/swagger-ui/index.html
```
## ▶️ Como executar

1. Build e execução completa com Docker (app, mysql, prometheus, grafana)
```bash 
docker-compose up --build
```

## ✅ Boas práticas aplicadas
- DTOs para entrada/saída
- Tratamento centralizado de exceções
- Paginação com Spring Data Pageable
- Specifications para filtros dinâmicos
- Testes cobrindo regras críticas e endpoints

## 🧪 Testes
- Testes Unitários 
  - Testes com Mockito para Services. 
  - Validam regras de negócio (ex: ticket inexistente, usuário sem permissão, etc).
  

- Testes de Integração
  - Utilizam MockMvc e banco H2. 
  - Validam persistência, autenticação JWT e regras de autorização

Para executar os testes:
```bash
mvn test
```
## 📈 Monitoramento
- Prometheus coleta métricas do endpoint ``/actuator/prometheus``
- Para verificar as metricas acesse:  http://localhost:3000
  - Login padrão: **admin / admin**
  - Adiciona o prometheus como data source: http://prometheus:9090
- Dashboards recomendados:
  - **19004** – Spring Boot 3.x Statistics
  - **4701** – JVM Micrometer

## Autor
- Miguel Sousa Dela Libera
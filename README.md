# Task Manager API

API REST de gerenciamento de tarefas pessoais com autenticação JWT e assistente de IA integrado via LangChain4j. O assistente interpreta comandos em linguagem natural e executa operações diretamente no sistema via Tool Calling.

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3 |
| Segurança | Spring Security + JWT |
| Persistência | Spring Data JPA + Hibernate |
| Banco de Dados | PostgreSQL |
| Migrações | Flyway |
| IA | LangChain4j + Gemini API |
| Testes | JUnit 5 + Mockito |
| Documentação | Swagger / OpenAPI |
| Infra | Docker + Docker Compose |

---

## Como rodar

### Pré-requisitos

- Docker e Docker Compose instalados
- Chave de API do Google Gemini — obtenha em https://aistudio.google.com

### Passo a passo

**1. Clone o repositório**

```bash
git clone https://github.com/lucasrenanjava-cloud/taskmanager.git
cd taskmanager
```

**2. Crie o arquivo `.env` na raiz do projeto**

```env
DB_NAME=taskmanager
DB_USER=postgres
DB_PASSWORD=sua_senha_aqui
JWT_SECRET=sua_chave_base64_aqui
JWT_EXPIRATION=86400000
IA_GEMMINI_KEY=sua_chave_gemini_aqui
IA_MODEL=gemini-2.5-flash-preview-04-17
```

Para gerar um JWT_SECRET seguro:
```bash
openssl rand -base64 32
```

**3. Suba a aplicação**

```bash
docker-compose up --build
```

**4. Acesse a documentação interativa**

```
http://localhost:8080/swagger-ui/index.html
```

---

## Funcionalidades

### Autenticação
- Cadastro de usuários
- Login com retorno de token JWT
- Todas as rotas protegidas por token, exceto cadastro e login

### Tarefas
- Criar, listar, buscar, atualizar e deletar tarefas
- Filtros por status e prioridade
- Busca por título
- Listagem de tarefas com prazo vencido
- Paginação nas listagens
- Isolamento por usuário — cada usuário acessa apenas suas próprias tarefas

### Agente IA
- Conversa em linguagem natural com assistente de IA
- Criação, listagem e atualização de tarefas via comandos em texto
- Histórico de conversa persistido no banco de dados
- Memória de contexto entre mensagens

---

## Endpoints

### Auth
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | /auth/register | Cadastrar usuário | Não |
| POST | /auth/login | Login, retorna JWT | Não |

### Tarefas
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | /tarefas | Criar tarefa | Sim |
| GET | /tarefas | Listar tarefas paginado | Sim |
| GET | /tarefas/{id} | Buscar por ID | Sim |
| GET | /tarefas/filtrar?status= | Filtrar por status | Sim |
| GET | /tarefas/filtrar?prioridade= | Filtrar por prioridade | Sim |
| GET | /tarefas/atrasadas | Listar tarefas com prazo vencido | Sim |
| GET | /tarefas/buscar?titulo= | Buscar por título | Sim |
| PUT | /tarefas/{id} | Atualizar tarefa | Sim |
| PATCH | /tarefas/{id}/status | Atualizar status | Sim |
| DELETE | /tarefas/{id} | Deletar tarefa | Sim |

### Agente IA
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | /agente/chat | Enviar mensagem ao agente | Sim |
| GET | /agente/historico | Listar histórico da conversa | Sim |
| DELETE | /agente/historico | Limpar histórico | Sim |

---

## Exemplos de uso

### Cadastro e login

```bash
# Cadastrar
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nome": "Lucas", "email": "lucas@email.com", "senha": "123456"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "lucas@email.com", "senha": "123456"}'
```

### Criar tarefa

```bash
curl -X POST http://localhost:8080/tarefas \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"titulo": "Estudar Spring Boot", "prioridade": "ALTA", "prazo": "2026-05-30"}'
```

### Conversar com o agente

```bash
curl -X POST http://localhost:8080/agente/chat \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"mensagem": "Cria uma tarefa para estudar Docker com prioridade alta"}'
```

---

## Enums

**Prioridade:** `BAIXA`, `MEDIA`, `ALTA`

**StatusTarefa:** `PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`

---

## Estrutura do projeto

```
src/
├── agent/
│   ├── config/         # TarefaAssistant (interface AiService)
│   └── tools/          # TarefaTools (Tool Calling) e TarefaAgent
├── config/             # SecurityConfig, SwaggerConfig
├── controller/         # AuthController, TarefaController, AgenteController
├── dto/
│   ├── request/        # RegisterRequest, LoginRequest, TarefaRequest, ChatRequest
│   └── response/       # LoginResponse, TarefaResponse, MensagemChatResponse
├── exception/          # GlobalExceptionHandler e exceções customizadas
├── model/
│   ├── entity/         # User, Tarefa, MensagemChat
│   └── enums/          # Prioridade, StatusTarefa, RoleChat
├── repository/         # UserRepository, TarefaRepository, MensagemChatRepository
├── security/           # JwtService, JwtAuthFilter
└── service/            # AuthService, TarefaService, AgenteService
```

---

## Migrations Flyway

| Versão | Descrição |
|---|---|
| V1 | Criação da tabela tb_users |
| V2 | Criação da tabela tb_tarefas |
| V3 | Alteração do id de tb_tarefas para BIGSERIAL |
| V4 | Criação da tabela tb_mensagens_chat |

---

## Testes

Para rodar os testes:

```bash
mvn test
```

Para gerar relatório de cobertura:

```bash
mvn verify
```

O relatório é gerado em `target/site/jacoco/index.html`.

---

## Autor

Lucas Renan Santana Barbosa

- LinkedIn: linkedin.com/in/lucas-renan-900171298
- GitHub: github.com/lucasrenanjava-cloud
- Email: lucasrenan.java@gmail.com
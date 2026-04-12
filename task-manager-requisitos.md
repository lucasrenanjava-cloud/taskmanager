# Task Manager API — Análise de Requisitos & Planejamento de Sprints

API REST de gerenciamento de tarefas com autenticação JWT e assistente de IA via LangChain4j.

---

## Visão Geral

Sistema de gerenciamento de tarefas pessoais onde cada usuário, após autenticar-se, pode criar, organizar e acompanhar suas tarefas. O sistema conta com um agente de IA integrado via LangChain4j capaz de interpretar comandos em linguagem natural e executar operações diretamente no banco via Tool Calling.

---

## Personas

| Persona | Descrição |
|---|---|
| Usuário Final | Pessoa que usa o sistema para organizar suas tarefas do dia a dia |
| Agente IA | Assistente que interpreta comandos em linguagem natural e interage com o sistema |

---

## Requisitos Funcionais

### Autenticação
- RF01 — O sistema deve permitir o cadastro de novos usuários com nome, e-mail e senha
- RF02 — O sistema deve autenticar usuários via e-mail e senha, retornando um token JWT
- RF03 — Todos os endpoints, exceto cadastro e login, devem exigir token JWT válido
- RF04 — Senhas devem ser armazenadas com hash BCrypt

### Tarefas
- RF05 — O usuário pode criar tarefas com título, descrição, prioridade, status e prazo
- RF06 — O usuário pode listar todas as suas tarefas com paginação
- RF07 — O usuário pode filtrar tarefas por status, prioridade e prazo vencido
- RF08 — O usuário pode buscar tarefas por título
- RF09 — O usuário pode atualizar os dados de uma tarefa
- RF10 — O usuário pode excluir uma tarefa
- RF11 — O usuário só pode visualizar e manipular suas próprias tarefas
- RF12 — O sistema deve registrar data de criação e última atualização de cada tarefa

### Agente IA
- RF13 — O usuário pode enviar comandos em linguagem natural ao agente
- RF14 — O agente deve ser capaz de criar tarefas via Tool Calling
- RF15 — O agente deve ser capaz de listar e consultar tarefas do usuário
- RF16 — O agente deve ser capaz de atualizar o status de uma tarefa
- RF17 — O agente deve manter contexto durante a conversa via histórico de mensagens

---

## Requisitos Não Funcionais

| ID | Requisito |
|---|---|
| RNF01 | Autenticação stateless via JWT, sem sessão no servidor |
| RNF02 | Senhas armazenadas com BCrypt |
| RNF03 | Versionamento do banco de dados com Flyway |
| RNF04 | Documentação dos endpoints via Swagger/OpenAPI |
| RNF05 | Cobertura mínima de 70% nos testes unitários dos services |
| RNF06 | Separação clara entre camadas: controller, service e repository |
| RNF07 | Tratamento centralizado de exceções com mensagens de erro padronizadas |
| RNF08 | Variáveis sensíveis como credenciais do banco, JWT secret e API key via variáveis de ambiente |

---

## Regras de Negócio

- RN01 — E-mail de usuário deve ser único no sistema
- RN02 — Uma tarefa sem prazo definido não pode ter status "ATRASADA"
- RN03 — Tarefa com status "CONCLUIDA" não pode ser reaberta pelo agente IA, apenas pelo usuário
- RN04 — O agente IA só pode acessar tarefas do usuário autenticado na sessão
- RN05 — Prioridade padrão de uma nova tarefa é MEDIA, caso não informada
- RN06 — Status padrão de uma nova tarefa é PENDENTE

---

## Entidades

### User
| Campo | Tipo | Descrição |
|---|---|---|
| id | UUID | Identificador único |
| nome | String | Nome do usuário |
| email | String | E-mail único |
| senha | String | Hash BCrypt |
| criadoEm | LocalDateTime | Data de cadastro |
| atualizadoEm | LocalDateTime | Última atualização |

### Tarefa
| Campo | Tipo | Descrição |
|---|---|---|
| id | UUID | Identificador único |
| titulo | String | Título da tarefa |
| descricao | String | Descrição detalhada |
| prioridade | Enum | BAIXA, MEDIA, ALTA |
| status | Enum | PENDENTE, EM_ANDAMENTO, CONCLUIDA |
| prazo | LocalDate | Data limite, opcional |
| criadoEm | LocalDateTime | Data de criação |
| atualizadoEm | LocalDateTime | Última atualização |
| usuario | User | Dono da tarefa (FK) |

### MensagemChat
| Campo | Tipo | Descrição |
|---|---|---|
| id | UUID | Identificador único |
| role | Enum | USER, ASSISTANT |
| conteudo | String | Conteúdo da mensagem |
| criadoEm | LocalDateTime | Data e hora da mensagem |
| usuario | User | Usuário da conversa (FK) |

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
| GET | /tarefas/filtrar?status=&prioridade= | Filtrar tarefas | Sim |
| GET | /tarefas/atrasadas | Listar tarefas com prazo vencido | Sim |
| GET | /tarefas/buscar?titulo= | Buscar por título | Sim |
| PUT | /tarefas/{id} | Atualizar tarefa | Sim |
| PATCH | /tarefas/{id}/status | Atualizar só o status | Sim |
| DELETE | /tarefas/{id} | Excluir tarefa | Sim |

### Agente IA
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | /agente/chat | Enviar mensagem ao agente | Sim |
| GET | /agente/historico | Listar histórico da conversa | Sim |
| DELETE | /agente/historico | Limpar histórico | Sim |

---

## Estrutura de Pacotes

```
src/
├── config/
│   ├── SecurityConfig.java
│   ├── JwtConfig.java
│   └── SwaggerConfig.java
├── controller/
│   ├── AuthController.java
│   ├── TarefaController.java
│   └── AgenteController.java
├── dto/
│   ├── request/
│   └── response/
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── (exceções customizadas)
├── model/
│   ├── entity/
│   │   ├── User.java
│   │   ├── Tarefa.java
│   │   └── MensagemChat.java
│   └── enums/
│       ├── Prioridade.java
│       └── StatusTarefa.java
├── repository/
├── security/
│   ├── JwtService.java
│   └── JwtAuthFilter.java
├── service/
│   ├── AuthService.java
│   ├── TarefaService.java
│   └── AgenteService.java
└── agent/
    ├── TarefaAgent.java
    └── tools/
        └── TarefaTools.java
```

---

## Planejamento de Sprints

### Sprint 0 — Setup do Projeto (3–4 dias)
Objetivo: projeto rodando com estrutura base e banco configurado.

- [x] Criar projeto Spring Boot com todas as dependências
- [x] Configurar banco de dados local e variáveis de ambiente
- [x] Criar primeira migration Flyway: V1__create_tb_users.sql
- [x] Configurar Swagger/OpenAPI
- [x] Commit inicial com README base

Entregável: aplicação sobe sem erros, Swagger acessível, banco conectado.

---

### Sprint 1 — Autenticação JWT (4–5 dias)
Objetivo: usuário consegue se cadastrar e receber token JWT.

- [x] Criar entidade User e migration Flyway
- [x] Implementar AuthService (register e login)
- [x] Implementar JwtService (gerar e validar token)
- [x] Implementar JwtAuthFilter
- [x] Configurar SecurityConfig com rotas públicas e protegidas
- [x] Criar AuthController com /auth/register e /auth/login
- [x] Tratamento de exceções: e-mail duplicado, credenciais inválidas
- [x] Testes unitários: AuthService

Entregável: cadastro e login funcionando, token JWT retornado e validado.

---

### Sprint 2 — CRUD de Tarefas (5–6 dias)
Objetivo: usuário autenticado gerencia suas tarefas.

- [x] Criar entidade Tarefa e migration Flyway
- [x] Implementar TarefaService (criar, listar, buscar, atualizar, deletar)
- [x] Garantir isolamento: usuário só acessa suas próprias tarefas
- [x] Implementar filtros por status, prioridade, prazo vencido e busca por título
- [x] Paginação nas listagens
- [x] Criar TarefaController com todos os endpoints
- [x] Validações com Bean Validation
- [x] Testes unitários: TarefaService

Entregável: CRUD completo e funcional, filtros operando, testes passando.

---

### Sprint 3 — Testes e Qualidade (4–5 dias)
Objetivo: cobertura mínima de 70% nos services.

- [x] Testes unitários AuthService — cenários de sucesso e falha
- [x] Testes unitários TarefaService — CRUD, filtros, isolamento por usuário
- [x] Testes de integração nos controllers principais com @SpringBootTest
- [x] Revisar e padronizar respostas de erro
- [x] Revisar migrations Flyway
- [x] Atualizar Swagger com descrições e exemplos

Entregável: suíte de testes verde, cobertura maior ou igual a 70%, documentação Swagger completa.

---

### Sprint 4 — Agente IA com LangChain4j (6–7 dias)
Objetivo: usuário conversa com agente que gerencia tarefas por linguagem natural.

- [x] Configurar LangChain4j com Gemini API
- [x] Criar entidade MensagemChat e migration Flyway
- [x] Implementar TarefaTools com Tool Calling (criar, listar, atualizar status)
- [x] Implementar TarefaAgent com memória de conversa por usuário
- [x] Criar AgenteController com /agente/chat e /agente/historico
- [x] Garantir que o agente só acessa dados do usuário autenticado
- [x] Testes unitários: AgenteService

Entregável: agente responde em linguagem natural e executa operações reais no banco.

---

### Sprint 5 — Refinamento e Deploy (3–4 dias)
Objetivo: projeto pronto para portfólio.

- [x] Dockerizar a aplicação com Dockerfile e docker-compose.yml
- [x] README completo com instruções de instalação, endpoints e exemplos
- [x] Revisar e limpar código
- [x] Garantir que todas as variáveis sensíveis estão em .env
- [x] Testar fluxo completo: cadastro, login, criar tarefa, conversar com agente

Entregável: projeto rodando com docker-compose up, README profissional, pronto para o GitHub.

---

## Resumo das Sprints

| Sprint | Foco | Duração estimada |
|---|---|---|
| Sprint 0 | Setup e infraestrutura | 3–4 dias |
| Sprint 1 | Autenticação JWT | 4–5 dias |
| Sprint 2 | CRUD de Tarefas | 5–6 dias |
| Sprint 3 | Testes e Qualidade | 4–5 dias |
| Sprint 4 | Agente IA (LangChain4j) | 6–7 dias |
| Sprint 5 | Refinamento e Deploy | 3–4 dias |

---

## Stack

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

Autor: Lucas Renan Santana Barbosa
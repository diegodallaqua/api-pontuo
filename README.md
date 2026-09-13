# api-pontuo

API REST do **Pontuo**, uma plataforma de preparação para vestibulares. Ela
mantém o catálogo de instituições, vestibulares, matérias, tópicos e questões
de múltipla escolha, e permite que estudantes montem e respondam simulados.

A API é stateless, autenticada por JWT e separa o acesso em dois perfis:
**Estudante**, que consulta o catálogo e gerencia simulados, e
**Administrador**, que também mantém o catálogo e os usuários.

## Sumário

- [Funcionalidades](#funcionalidades)
- [Tecnologias](#tecnologias)
- [Dependências](#dependências)
- [Como rodar o projeto](#como-rodar-o-projeto)
- [Banco de dados e migrations](#banco-de-dados-e-migrations)
- [Endpoints](#endpoints)
- [Autenticação e segurança](#autenticação-e-segurança)
- [Testes](#testes)
- [Integração contínua](#integração-contínua)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Pontos ainda abertos](#pontos-ainda-abertos)
- [Licença](#licença)

## Funcionalidades

- **Localidades**: estados, cidades e endereços. Os 27 estados e as 5.571
  cidades do Brasil já vêm cadastrados.
- **Instituições e vestibulares**: bancas organizadoras (INEP, FUVEST, VUNESP,
  COMVEST, CEBRASPE, entre outras) e seus vestibulares por ano e etapa
  (ex.: ENEM 2026, 1º Dia e 2º Dia). Um vestibular é único pela combinação de
  instituição, ano, nome e etapa.
- **Conteúdo**: áreas do conhecimento, matérias e tópicos, organizados em
  hierarquia (ex.: Ciências da Natureza › Biologia › Genética).
- **Questões**: enunciado, explicação e dificuldade de 1 a 3, vinculadas a um
  vestibular e classificadas por tópico **ou** por matéria. Cada questão tem
  alternativas de A a E, sem letra repetida e com uma única alternativa
  correta, e pode ter imagens.
- **Simulados**: o estudante cria simulados com número de questões, tempo
  máximo, horários de início e fim, acertos e aproveitamento. Uma questão não
  se repete no mesmo simulado, e a alternativa marcada precisa pertencer à
  questão respondida.
- **Usuários e acesso**: cadastro público (sempre como Estudante), login por
  username ou email, logout que invalida o token e gestão de usuários e
  perfis pelo administrador.

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17 | Linguagem |
| Spring Boot | 4.1.1 | Base da aplicação |
| Spring Web MVC | 7.0 | API REST |
| Spring Data JPA / Hibernate | 7.4 | Persistência |
| Spring Security + OAuth2 Resource Server | 7.1 | Autenticação JWT (HS256) e autorização por perfil |
| Jakarta Bean Validation (Hibernate Validator) | 9.1 | Validação das requisições |
| MariaDB | 12.3 (versão usada no desenvolvimento) | Banco de dados |
| Flyway | 12.4 | Migrations do banco |
| JUnit, Mockito, AssertJ, MockMvc | JUnit 6 / Mockito 5 | Testes automatizados |
| Maven (via Maven Wrapper) | incluso no projeto | Build e dependências |
| GitHub Actions | — | Integração contínua |

## Dependências

Declaradas no [`pom.xml`](pom.xml). As versões são gerenciadas pelo
`spring-boot-starter-parent` 4.1.1, exceto o spring-dotenv, gerenciado pelo
seu próprio BOM (5.1.0).

| Dependência | Para que serve |
|---|---|
| `spring-boot-starter-webmvc` | Controllers REST, conversão JSON e servidor embutido |
| `spring-boot-starter-data-jpa` | Repositórios e mapeamento das entidades com Hibernate |
| `spring-boot-starter-security` | Filtros de segurança, regras de acesso e BCrypt |
| `spring-boot-starter-oauth2-resource-server` | Emissão e validação dos tokens JWT |
| `spring-boot-starter-validation` | Anotações de validação nos DTOs (`@NotBlank`, `@Size`...) |
| `spring-boot-starter-flyway` + `flyway-mysql` | Execução das migrations no MariaDB |
| `mariadb-java-client` | Driver JDBC do MariaDB |
| `springboot4-dotenv` | Carrega as variáveis do arquivo `.env` |
| `spring-boot-starter-webmvc-test` | JUnit, Mockito, AssertJ e MockMvc |
| `spring-boot-starter-security-test` | Simulação de usuários e tokens nos testes |
| `spring-boot-starter-data-jpa-test` | Testes de repositório com `@DataJpaTest` |

Não é preciso instalar nada disso manualmente: o Maven baixa as dependências
no primeiro build.

## Como rodar o projeto

### 1. Pré-requisitos

- [Git](https://git-scm.com/)
- [JDK 17](https://adoptium.net/) ou superior
- [MariaDB](https://mariadb.org/download/) rodando em `localhost:3306`

O Maven não precisa estar instalado: o projeto inclui o Maven Wrapper
(`mvnw` e `mvnw.cmd`).

### 2. Clonar o repositório

```bash
git clone https://github.com/diegodallaqua/api-pontuo.git
cd api-pontuo
```

### 3. Criar o banco e o usuário

Conecte-se ao MariaDB como root (`mariadb -u root -p`) e execute:

```sql
CREATE DATABASE db_pontuo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'db_pontuo'@'localhost' IDENTIFIED BY 'troque_esta_senha';
GRANT ALL PRIVILEGES ON db_pontuo.* TO 'db_pontuo'@'localhost';
```

Crie apenas o banco vazio: as tabelas e os dados de referência são criados
pelas migrations na primeira execução. Se o MariaDB estiver em outro host ou
porta, ajuste `spring.datasource.url` em
[`application.properties`](src/main/resources/application.properties).

### 4. Configurar as variáveis de ambiente

Copie o modelo para `.env` na raiz do projeto:

```bash
cp .env.example .env
```

No PowerShell: `Copy-Item .env.example .env`.

Preencha o `.env`:

| Variável | Descrição |
|---|---|
| `DB_USERNAME` | Usuário criado no passo 3 |
| `DB_PASSWORD` | Senha desse usuário |
| `JWT_SECRET` | Segredo de assinatura dos tokens, com no mínimo 32 caracteres |

Para gerar um `JWT_SECRET` aleatório:

```bash
openssl rand -base64 48
```

No PowerShell:

```powershell
$bytes = New-Object byte[] 48
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
[Convert]::ToBase64String($bytes)
```

O `.env` não é versionado. Sem `JWT_SECRET` a aplicação não sobe, para evitar
que rode com uma chave conhecida.

### 5. Iniciar a aplicação

```bash
./mvnw spring-boot:run
```

No Windows: `.\mvnw.cmd spring-boot:run`.

Na primeira execução o Maven baixa as dependências e o Flyway cria a estrutura
do banco. O log deve mostrar
`Successfully applied 6 migrations to schema db_pontuo`. A API fica
disponível em `http://localhost:8080`.

Para gerar e executar o jar:

```bash
./mvnw clean package
java -jar target/api-pontuo-0.0.1-SNAPSHOT.jar
```

Execute o jar a partir da raiz do projeto, onde está o `.env`.

### 6. Criar a primeira conta e fazer login

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"maria","email":"maria@email.com","birthDate":"2001-03-09","password":"SenhaForte123"}'
```

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"maria","password":"SenhaForte123"}'
```

Use o `accessToken` da resposta nas demais requisições:

```bash
curl http://localhost:8080/api/auth/me -H "Authorization: Bearer SEU_TOKEN"
```

No PowerShell, chame `curl.exe` em vez de `curl`, ou use um cliente como
Postman ou Insomnia.

### 7. Criar o primeiro administrador

O cadastro público só cria estudantes, e a gestão de usuários exige perfil de
administrador. Promova a primeira conta direto no banco:

```sql
UPDATE users SET user_role_id = 1 WHERE username = 'maria';
```

Faça login de novo para receber um token com o perfil atualizado.

## Banco de dados e migrations

A estrutura do banco é versionada com Flyway, em
[`src/main/resources/db/migration`](src/main/resources/db/migration). As
migrations rodam automaticamente sempre que a aplicação sobe, antes do
Hibernate validar as entidades (`ddl-auto=validate`).

| Migration | O que faz |
|---|---|
| `V1__criar_tabelas.sql` | Cria as 15 tabelas, índices e chaves estrangeiras |
| `V2__inserir_dados_de_referencia.sql` | Áreas, matérias, tópicos, estados, cidades, endereços, instituições, vestibulares e perfis |
| `V3__corrigir_numero_do_endereco_da_coperve.sql` | Corrige o número de um endereço cadastrado como 0 |
| `V4__incluir_etapa_na_chave_unica_de_vestibular.sql` | Inclui a etapa na chave única de `entrance_exam` |
| `V5__inserir_etapas_de_vestibular.sql` | Cadastra as etapas que faltavam (ENEM 2º Dia, 2ª fases e etapas do PAS) |
| `V6__tornar_etapa_de_vestibular_obrigatoria.sql` | Torna `entrance_exam.stage` obrigatória |

Regras para alterar o banco:

- **Nunca edite uma migration já aplicada.** O Flyway guarda um checksum de
  cada arquivo e recusa iniciar a aplicação se um deles mudar.
- Toda mudança vira um arquivo novo, com a próxima versão
  (ex.: `V7__adicionar_coluna_x.sql`).
- Se uma migration falhar, a aplicação não sobe e o log mostra o erro do banco
  e a linha do SQL.

Para ver em que versão o banco está:

```sql
SELECT version, description, installed_on, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

Bancos criados antes da adoção do Flyway já tinham o equivalente à V1 e à V2.
Por isso o projeto usa `spring.flyway.baseline-on-migrate=true` com
`baseline-version=2`: nesses bancos o Flyway registra a versão 2 como ponto de
partida e aplica só as migrations seguintes. Um banco vazio não é afetado e
recebe todas desde a V1.

## Endpoints

Todos os recursos seguem o mesmo padrão CRUD:

| Método | Rota | Resposta |
|---|---|---|
| `GET` | `/api/<recurso>` | Lista, com filtros opcionais |
| `GET` | `/api/<recurso>/{id}` | Um registro, ou `404` |
| `POST` | `/api/<recurso>` | `201` com o registro criado |
| `PUT` | `/api/<recurso>/{id}` | Registro atualizado |
| `DELETE` | `/api/<recurso>/{id}` | `204` |

| Recurso | Rota | Filtros da listagem |
|---|---|---|
| Estados | `/api/states` | — |
| Cidades | `/api/cities` | `stateId` |
| Endereços | `/api/addresses` | `cityId` |
| Instituições | `/api/institutions` | `acronym` |
| Vestibulares | `/api/entrance-exams` | `institutionId`, `year` |
| Áreas do conhecimento | `/api/knowledge-areas` | — |
| Matérias | `/api/subjects` | `knowledgeAreaId` |
| Tópicos | `/api/topics` | `subjectId` |
| Questões | `/api/questions` | `entranceExamId`, `topicId`, `subjectId` |
| Alternativas | `/api/answer-options` | `questionId` |
| Imagens de questões | `/api/question-images` | `questionId` |
| Simulados | `/api/mock-exams` | `userId` |
| Questões do simulado | `/api/mock-exam-questions` | `mockExamId`, `questionId` |
| Usuários | `/api/users` | `userRoleId` |
| Perfis | `/api/user-roles` | — |

Quando mais de um filtro é enviado, vale apenas o primeiro na ordem da tabela.
Por exemplo, em `/api/questions?entranceExamId=1&topicId=2` só o vestibular é
considerado.

Respostas de erro:

| Status | Quando | Corpo |
|---|---|---|
| `400` | Corpo da requisição inválido | Mensagem por campo: `{"stage": "stage é obrigatório"}` |
| `401` | Sem token, token inválido, expirado ou revogado; ou login incorreto | `{"message": "..."}` |
| `403` | Perfil sem permissão para o recurso | `{"message": "..."}` |
| `404` | Registro ou registro relacionado não encontrado | `{"message": "..."}` (vazio no `GET /{id}`) |
| `409` | Regra de negócio violada (ex.: username já cadastrado) | `{"message": "..."}` |

## Autenticação e segurança

A API não usa sessão nem cookie: a identidade de cada requisição vem do header
`Authorization: Bearer <token>`. Os tokens são assinados em HS256 e as senhas
são gravadas com BCrypt.

### Endpoints de autenticação

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/api/auth/register` | público | Cria conta. O perfil é sempre `Estudante`, definido pelo servidor. |
| `POST` | `/api/auth/login` | público | Recebe `username` (aceita username ou email) e `password`; devolve o token. |
| `POST` | `/api/auth/logout` | autenticado | Invalida o token usado na requisição. |
| `GET` | `/api/auth/me` | autenticado | Dados do usuário dono do token. |

Resposta do login:

```json
{
  "tokenType": "Bearer",
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 7200,
  "user": { "id": 1, "username": "maria", "userRole": { "id": 2, "description": "Estudante" } }
}
```

### Proteção das rotas

Nenhuma rota `/api/**` responde sem token, exceto `login` e `register`.

| Rota | Estudante | Administrador |
|---|---|---|
| `GET` no catálogo (estados, cidades, endereços, instituições, vestibulares, áreas, matérias, tópicos, questões, alternativas, imagens) | leitura | leitura |
| `POST`/`PUT`/`PATCH`/`DELETE` no catálogo | negado (403) | permitido |
| `/api/mock-exams/**`, `/api/mock-exam-questions/**` | CRUD completo | CRUD completo |
| `/api/users/**`, `/api/user-roles/**` | negado (403) | permitido |

As permissões vêm do perfil (`user_role`) do usuário. A descrição é
normalizada em authority (`Administrador` vira `ROLE_ADMINISTRADOR`) e gravada
no claim `roles` do token.

### Configuração

| Propriedade | Descrição |
|---|---|
| `security.jwt.secret` | Chave HS256, vinda de `JWT_SECRET` |
| `security.jwt.issuer` | Emissor gravado e validado no token (`api-pontuo`) |
| `security.jwt.expiration-minutes` | Validade do token, em minutos (padrão: 120) |

### Como funciona o logout

O JWT é autocontido, então descartá-lo no cliente não o invalidaria no
servidor. O logout registra o `jti` do token numa lista de revogados,
consultada em toda requisição até o token expirar.

Essa lista fica em memória: é perdida quando a aplicação reinicia e não é
compartilhada entre instâncias. Para rodar em mais de um servidor, troque o
`TokenRevocationService` por um armazenamento compartilhado, como Redis ou uma
tabela no banco.

## Testes

Os testes são automatizados com JUnit, Mockito e MockMvc. Para rodar a suíte:

```bash
./mvnw test
```

| Tipo | O que cobre | Precisa de banco? |
|---|---|---|
| Unitários | Services, segurança (JWT, revogação), tratamento de erros e validação dos DTOs | Não |
| Web (`@WebMvcTest`) | Permissões por perfil, fluxo de cadastro/login/logout e respostas dos controllers | Não |
| Repositório (`@DataJpaTest`) | Consultas e restrições do banco | Sim |
| Integração (`@SpringBootTest`) | Contexto completo da aplicação e regras de simulado | Sim |

Os testes com banco usam o MariaDB configurado no `application.properties` e
desfazem tudo o que gravam ao final de cada teste. Quando não há MariaDB em
`localhost:3306`, eles são pulados em vez de falhar.

Alguns testes descrevem regras ainda não implementadas (marcados com
`@Pendente`, na etapa "red" do TDD) e ficam desligados por padrão. Para
executá-los:

```bash
./mvnw test -Dpontuo.pendentes=true
```

Quando uma regra for implementada e o teste passar, remova a anotação
`@Pendente` dele.

## Integração contínua

O workflow [`.github/workflows/ci.yml`](.github/workflows/ci.yml) roda a cada
push na `main` e em pull requests. Ele sobe um MariaDB vazio, aplica as
migrations e executa `./mvnw -B verify`, incluindo os testes com banco.

## Estrutura do projeto

```
src/main/java/com/pontuo/api_pontuo
├── config        # SecurityConfig: regras de acesso, JWT e BCrypt
├── controller    # Endpoints REST
├── dto           # Records de requisição e resposta, com validação
├── entity        # Entidades JPA
├── exception     # Tratamento global de erros
├── repository    # Repositórios Spring Data
├── security      # Emissão, validação e revogação de tokens
└── service       # Regras de negócio
src/main/resources
├── application.properties
└── db/migration  # Migrations do Flyway
src/test/java/com/pontuo/api_pontuo
└── ...           # Testes, espelhando os pacotes acima, e utilitários em support/
```

## Pontos ainda abertos

- `MockExam` tem `user_id`, mas as rotas de simulado ainda não filtram por
  dono: qualquer usuário autenticado pode ler e alterar o simulado de outro. A
  proteção atual é por perfil, não por propriedade do registro.
- Não há rate limiting nem bloqueio temporário após tentativas de login
  falhas.
- Não há refresh token: quando o token expira, é preciso fazer login de novo.
- CORS não está configurado, o que é necessário antes de consumir a API por um
  frontend em outra origem.

## Licença

Distribuído sob a licença MIT. Veja o arquivo [LICENSE](LICENSE).

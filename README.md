# api-pontuo

**English** | [Português](README.pt-BR.md)

REST API for **Pontuo**, a platform for preparing for Brazilian university
entrance exams (*vestibulares*). It manages a catalog of institutions,
entrance exams, subjects, topics and multiple-choice questions, and lets
students build and answer mock exams.

The API is stateless, authenticated with JWT, and splits access into two
roles: **Estudante** (student), who reads the catalog and manages mock exams,
and **Administrador** (administrator), who also maintains the catalog and the
users.

## Table of contents

- [Features](#features)
- [Technologies](#technologies)
- [Dependencies](#dependencies)
- [Running the project](#running-the-project)
- [Database and migrations](#database-and-migrations)
- [Endpoints](#endpoints)
- [Authentication and security](#authentication-and-security)
- [Tests](#tests)
- [Continuous integration](#continuous-integration)
- [Project structure](#project-structure)
- [Design patterns](#design-patterns)
- [Known limitations](#known-limitations)
- [License](#license)

## Features

- **Locations**: states, cities and addresses. All 27 Brazilian states and
  5,571 cities come preloaded.
- **Institutions and entrance exams**: exam boards (INEP, FUVEST, VUNESP,
  COMVEST, CEBRASPE and others) and their entrance exams by year and stage
  (e.g. ENEM 2026, "1º Dia" and "2º Dia"). An entrance exam is unique by
  institution, year, name and stage.
- **Content**: knowledge areas, subjects and topics, organized as a hierarchy
  (e.g. Natural Sciences › Biology › Genetics).
- **Questions**: statement, explanation and difficulty from 1 to 3, linked to
  an entrance exam and classified by topic **or** by subject. Each question
  has answer options A to E, with no repeated letter and a single correct
  option, and may have images.
- **Mock exams**: students create mock exams with number of questions, time
  limit, start and finish times, correct answers and accuracy. A question
  cannot appear twice in the same mock exam, and the selected option must
  belong to the answered question.
- **Users and access**: public sign-up (always as a student), login with
  username or email, logout that invalidates the token, and user and role
  management by administrators.

## Technologies

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Language |
| Spring Boot | 4.1.1 | Application framework |
| Spring Web MVC | 7.0 | REST API |
| Spring Data JPA / Hibernate | 7.4 | Persistence |
| Spring Security + OAuth2 Resource Server | 7.1 | JWT authentication (HS256) and role-based authorization |
| Jakarta Bean Validation (Hibernate Validator) | 9.1 | Request validation |
| MariaDB | 12.3 (version used in development) | Database |
| Flyway | 12.4 | Database migrations |
| JUnit, Mockito, AssertJ, MockMvc | JUnit 6 / Mockito 5 | Automated tests |
| Maven (via Maven Wrapper) | bundled with the project | Build and dependencies |
| GitHub Actions | — | Continuous integration |

## Dependencies

Declared in [`pom.xml`](pom.xml). Versions are managed by
`spring-boot-starter-parent` 4.1.1, except spring-dotenv, which is managed by
its own BOM (5.1.0).

| Dependency | Purpose |
|---|---|
| `spring-boot-starter-webmvc` | REST controllers, JSON conversion and embedded server |
| `spring-boot-starter-data-jpa` | Repositories and entity mapping with Hibernate |
| `spring-boot-starter-security` | Security filters, access rules and BCrypt |
| `spring-boot-starter-oauth2-resource-server` | Issuing and validating JWT tokens |
| `spring-boot-starter-validation` | Validation annotations on DTOs (`@NotBlank`, `@Size`...) |
| `spring-boot-starter-flyway` + `flyway-mysql` | Running migrations on MariaDB |
| `mariadb-java-client` | MariaDB JDBC driver |
| `springboot4-dotenv` | Loads variables from the `.env` file |
| `spring-boot-starter-webmvc-test` | JUnit, Mockito, AssertJ and MockMvc |
| `spring-boot-starter-security-test` | Mock users and tokens in tests |
| `spring-boot-starter-data-jpa-test` | Repository tests with `@DataJpaTest` |

You don't need to install any of these manually: Maven downloads them on the
first build.

## Running the project

### 1. Prerequisites

- [Git](https://git-scm.com/)
- [JDK 17](https://adoptium.net/) or later
- [MariaDB](https://mariadb.org/download/) running on `localhost:3306`

Maven does not need to be installed: the project includes the Maven Wrapper
(`mvnw` and `mvnw.cmd`).

### 2. Clone the repository

```bash
git clone https://github.com/diegodallaqua/api-pontuo.git
cd api-pontuo
```

### 3. Create the database and user

Connect to MariaDB as root (`mariadb -u root -p`) and run:

```sql
CREATE DATABASE db_pontuo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'db_pontuo'@'localhost' IDENTIFIED BY 'change_this_password';
GRANT ALL PRIVILEGES ON db_pontuo.* TO 'db_pontuo'@'localhost';
```

Create only the empty database: tables and reference data are created by the
migrations on the first run. If MariaDB runs on another host or port, change
`spring.datasource.url` in
[`application.properties`](src/main/resources/application.properties).

### 4. Configure the environment variables

Copy the template to `.env` in the project root:

```bash
cp .env.example .env
```

In PowerShell: `Copy-Item .env.example .env`.

Fill in `.env`:

| Variable | Description |
|---|---|
| `DB_USERNAME` | User created in step 3 |
| `DB_PASSWORD` | That user's password |
| `JWT_SECRET` | Token signing secret, at least 32 characters |

To generate a random `JWT_SECRET`:

```bash
openssl rand -base64 48
```

In PowerShell:

```powershell
$bytes = New-Object byte[] 48
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
[Convert]::ToBase64String($bytes)
```

`.env` is not committed. Without `JWT_SECRET` the application refuses to
start, so it never runs with a known key.

### 5. Start the application

```bash
./mvnw spring-boot:run
```

On Windows: `.\mvnw.cmd spring-boot:run`.

On the first run Maven downloads the dependencies and Flyway creates the
database structure. The log should show
`Successfully applied 6 migrations to schema db_pontuo`. The API is available
at `http://localhost:8080`.

To build and run the jar:

```bash
./mvnw clean package
java -jar target/api-pontuo-0.0.1-SNAPSHOT.jar
```

Run the jar from the project root, where `.env` lives.

### 6. Create the first account and log in

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

Use the `accessToken` from the response in the other requests:

```bash
curl http://localhost:8080/api/auth/me -H "Authorization: Bearer YOUR_TOKEN"
```

In PowerShell, call `curl.exe` instead of `curl`, or use a client such as
Postman or Insomnia.

### 7. Create the first administrator

Public sign-up only creates students, and user management requires the
administrator role. Promote the first account directly in the database:

```sql
UPDATE users SET user_role_id = 1 WHERE username = 'maria';
```

Log in again to get a token with the updated role.

## Database and migrations

The database structure is versioned with Flyway, in
[`src/main/resources/db/migration`](src/main/resources/db/migration).
Migrations run automatically every time the application starts, before
Hibernate validates the entities (`ddl-auto=validate`).

| Migration | What it does |
|---|---|
| `V1__criar_tabelas.sql` | Creates the 15 tables, indexes and foreign keys |
| `V2__inserir_dados_de_referencia.sql` | Knowledge areas, subjects, topics, states, cities, addresses, institutions, entrance exams and roles |
| `V3__corrigir_numero_do_endereco_da_coperve.sql` | Fixes an address whose number was stored as 0 |
| `V4__incluir_etapa_na_chave_unica_de_vestibular.sql` | Adds the stage to the unique key of `entrance_exam` |
| `V5__inserir_etapas_de_vestibular.sql` | Adds the missing stages (ENEM "2º Dia", second phases and PAS stages) |
| `V6__tornar_etapa_de_vestibular_obrigatoria.sql` | Makes `entrance_exam.stage` required |

Rules for changing the database:

- **Never edit a migration that has already been applied.** Flyway stores a
  checksum of each file and refuses to start the application if one changes.
- Every change goes in a new file with the next version
  (e.g. `V7__add_column_x.sql`).
- If a migration fails, the application does not start and the log shows the
  database error and the SQL line.

To check which version the database is at:

```sql
SELECT version, description, installed_on, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

Databases created before Flyway was adopted already had the equivalent of V1
and V2. That is why the project sets `spring.flyway.baseline-on-migrate=true`
with `baseline-version=2`: on those databases Flyway records version 2 as the
starting point and applies only the later migrations. An empty database is not
affected and receives every migration from V1.

## Endpoints

Every resource follows the same CRUD pattern:

| Method | Route | Response |
|---|---|---|
| `GET` | `/api/<resource>` | List, with optional filters |
| `GET` | `/api/<resource>/{id}` | A single record, or `404` |
| `POST` | `/api/<resource>` | `201` with the created record |
| `PUT` | `/api/<resource>/{id}` | Updated record |
| `DELETE` | `/api/<resource>/{id}` | `204` |

| Resource | Route | List filters |
|---|---|---|
| States | `/api/states` | — |
| Cities | `/api/cities` | `stateId` |
| Addresses | `/api/addresses` | `cityId` |
| Institutions | `/api/institutions` | `acronym` |
| Entrance exams | `/api/entrance-exams` | `institutionId`, `year` |
| Knowledge areas | `/api/knowledge-areas` | — |
| Subjects | `/api/subjects` | `knowledgeAreaId` |
| Topics | `/api/topics` | `subjectId` |
| Questions | `/api/questions` | `entranceExamId`, `topicId`, `subjectId` |
| Answer options | `/api/answer-options` | `questionId` |
| Question images | `/api/question-images` | `questionId` |
| Mock exams | `/api/mock-exams` | `userId` |
| Mock exam questions | `/api/mock-exam-questions` | `mockExamId`, `questionId` |
| Users | `/api/users` | `userRoleId` |
| Roles | `/api/user-roles` | — |

When more than one filter is sent, only the first one in the table's order
applies. For example, in `/api/questions?entranceExamId=1&topicId=2` only the
entrance exam is used.

Error responses (messages are returned in Portuguese):

| Status | When | Body |
|---|---|---|
| `400` | Invalid request body | One message per field: `{"stage": "stage é obrigatório"}` |
| `401` | Missing, invalid, expired or revoked token; or wrong login | `{"message": "..."}` |
| `403` | Role not allowed to access the resource | `{"message": "..."}` |
| `404` | Record or related record not found | `{"message": "..."}` (empty on `GET /{id}`) |
| `409` | Business rule violated (e.g. username already taken) | `{"message": "..."}` |

## Authentication and security

The API uses no session or cookie: each request is identified by the
`Authorization: Bearer <token>` header. Tokens are signed with HS256 and
passwords are stored with BCrypt.

### Authentication endpoints

| Method | Route | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | public | Creates an account. The role is always `Estudante`, set by the server. |
| `POST` | `/api/auth/login` | public | Takes `username` (username or email) and `password`; returns the token. |
| `POST` | `/api/auth/logout` | authenticated | Invalidates the token used in the request. |
| `GET` | `/api/auth/me` | authenticated | Data of the user who owns the token. |

Login response:

```json
{
  "tokenType": "Bearer",
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 7200,
  "user": { "id": 1, "username": "maria", "userRole": { "id": 2, "description": "Estudante" } }
}
```

### Route protection

No `/api/**` route responds without a token, except `login` and `register`.

| Route | Student | Administrator |
|---|---|---|
| `GET` on the catalog (states, cities, addresses, institutions, entrance exams, knowledge areas, subjects, topics, questions, answer options, images) | read | read |
| `POST`/`PUT`/`PATCH`/`DELETE` on the catalog | denied (403) | allowed |
| `/api/mock-exams/**`, `/api/mock-exam-questions/**` | full CRUD | full CRUD |
| `/api/users/**`, `/api/user-roles/**` | denied (403) | allowed |

Permissions come from the user's role (`user_role`). Its description is
normalized into an authority (`Administrador` becomes `ROLE_ADMINISTRADOR`) and
stored in the token's `roles` claim.

### Configuration

| Property | Description |
|---|---|
| `security.jwt.secret` | HS256 key, taken from `JWT_SECRET` |
| `security.jwt.issuer` | Issuer written to and validated in the token (`api-pontuo`) |
| `security.jwt.expiration-minutes` | Token lifetime in minutes (default: 120) |

### How logout works

A JWT is self-contained, so discarding it on the client would not invalidate
it on the server. Logout stores the token's `jti` in a revocation list that is
checked on every request until the token expires.

This list is kept in memory: it is lost when the application restarts and is
not shared between instances. To run on more than one server, replace
`TokenRevocationService` with shared storage, such as Redis or a database
table.

## Tests

Tests are automated with JUnit, Mockito and MockMvc. To run the suite:

```bash
./mvnw test
```

| Type | What it covers | Needs a database? |
|---|---|---|
| Unit | Services, security (JWT, revocation), error handling and DTO validation | No |
| Web (`@WebMvcTest`) | Role permissions, sign-up/login/logout flow and controller responses | No |
| Repository (`@DataJpaTest`) | Database queries and constraints | Yes |
| Integration (`@SpringBootTest`) | Full application context and mock exam rules | Yes |

Database tests use the MariaDB configured in `application.properties` and roll
back everything they write at the end of each test. When there is no MariaDB on
`localhost:3306`, they are skipped instead of failing.

Some tests describe rules that are not implemented yet (annotated with
`@Pendente`, the "red" step of TDD) and are disabled by default. To run them:

```bash
./mvnw test -Dpontuo.pendentes=true
```

Once a rule is implemented and its test passes, remove the `@Pendente`
annotation from it.

## Continuous integration

The [`.github/workflows/ci.yml`](.github/workflows/ci.yml) workflow runs on
every push to `main` and on pull requests. It starts an empty MariaDB, applies
the migrations and runs `./mvnw -B verify`, database tests included.

## Project structure

```
src/main/java/com/pontuo/api_pontuo
├── config        # SecurityConfig: access rules, JWT and BCrypt
├── controller    # REST endpoints
├── dto           # Request and response records, with validation
├── entity        # JPA entities
├── exception     # Global error handling
├── repository    # Spring Data repositories
├── security      # Token issuing, validation and revocation
└── service       # Business rules
src/main/resources
├── application.properties
└── db/migration  # Flyway migrations
src/test/java/com/pontuo/api_pontuo
└── ...           # Tests mirroring the packages above, plus helpers in support/
```

## Design patterns

The project uses the same patterns explored in DIO's
[Explorando Padrões de Projetos na Prática com Java](https://github.com/digitalinnovationone/lab-padroes-projeto-spring)
lab. Each package has a `package-info.java` describing the pattern behind it,
which also shows up in the generated Javadoc.

| Pattern | Where | How it shows up |
|---|---|---|
| **Singleton** | `service`, `controller`, `security`, `config` | Every component (`@Service`, `@RestController`, `@Component`, `@Bean`) is a singleton-scoped bean: the container creates one instance and injects it through the constructor. No static field and no `getInstance()`, which keeps the classes testable with mocks. |
| **Repository** | [`repository`](src/main/java/com/pontuo/api_pontuo/repository) | Each interface extends `JpaRepository` and exposes queries in domain terms (`findByCityId`, `findByUsername`). Spring Data generates the implementation at runtime. |
| **Facade** | [`controller`](src/main/java/com/pontuo/api_pontuo/controller) | Each controller exposes one simple route and hides the service, the repositories, request validation and DTO ↔ entity conversion from the client. Documented example: [`AddressController`](src/main/java/com/pontuo/api_pontuo/controller/AddressController.java). |
| **Strategy** | [`security`](src/main/java/com/pontuo/api_pontuo/security) | Spring Security extension points defined as interfaces: `AppUserDetailsService` (`UserDetailsService`), `RevokedTokenValidator` (`OAuth2TokenValidator<Jwt>`) and the `PasswordEncoder` picked in a single `@Bean`. Callers depend on the interface, not on the implementation. |

One difference from the lab: there, Strategy is also applied to the business
layer (`ClienteService` + `ClienteServiceImpl`). Here each domain has a single
implementation, and controllers inject the concrete service class. If a domain
ever needs more than one variation of a rule, the move is to extract the
interface and push the current implementation into `service/impl`, with no
change to the controllers beyond the injected type.

The lab also uses a facade over an external integration (ViaCEP, through
OpenFeign) to fill addresses from a postal code. This API consumes no external
services: addresses are already loaded by the migrations.

## Known limitations

- `MockExam` has a `user_id`, but the mock exam routes do not filter by owner
  yet: any authenticated user can read and change another user's mock exam.
  Protection is currently by role, not by record ownership.
- There is no rate limiting or temporary lockout after failed login attempts.
- There is no refresh token: when the token expires, the user must log in
  again.
- CORS is not configured, which is required before consuming the API from a
  frontend on another origin.

## License

Distributed under the MIT License. See [LICENSE](LICENSE).

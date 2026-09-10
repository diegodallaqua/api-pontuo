# api-pontuo

## Autenticação e segurança

A API é stateless e autenticada por JWT (assinatura HS256). Não há sessão nem
cookie: a identidade de cada requisição vem do header
`Authorization: Bearer <token>`.

### Configuração

O segredo de assinatura vem da variável de ambiente `JWT_SECRET`, lida do
arquivo `.env` (que não é versionado). Precisa ter no mínimo 32 caracteres; sem
ela a aplicação não sobe, para evitar que rode com uma chave conhecida.

Para gerar um segredo novo:

```powershell
$bytes = New-Object byte[] 48
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
[Convert]::ToBase64String($bytes)
```

Propriedades relacionadas em `application.properties`:

| Propriedade | Descrição |
|---|---|
| `security.jwt.secret` | Chave HS256, vinda de `JWT_SECRET`. |
| `security.jwt.issuer` | Emissor gravado e validado no token. |
| `security.jwt.expiration-minutes` | Validade do access token (padrão: 120). |

### Endpoints de autenticação

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| POST | `/api/auth/register` | público | Cria conta. A role é sempre `Estudante`, definida pelo servidor. |
| POST | `/api/auth/login` | público | Recebe `username` (aceita username ou email) e `password`; devolve o token. |
| POST | `/api/auth/logout` | autenticado | Invalida o token usado na requisição. |
| GET | `/api/auth/me` | autenticado | Dados do usuário dono do token. |

Exemplo de login:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"maria","password":"SenhaForte123"}'
```

Resposta:

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
| `GET /api/**` (catálogo: estados, cidades, instituições, vestibulares, áreas, matérias, tópicos, questões, alternativas, imagens) | leitura | leitura |
| `POST`/`PUT`/`PATCH`/`DELETE` no catálogo | negado (403) | permitido |
| `/api/mock-exams/**`, `/api/mock-exam-questions/**` | CRUD completo | CRUD completo |
| `/api/users/**`, `/api/user-roles/**` | negado (403) | permitido |

As permissões vêm da `user_role` do usuário: a descrição é normalizada em
authority (`Administrador` vira `ROLE_ADMINISTRADOR`) e gravada no claim `roles`
do token.

Respostas de erro seguem o formato `{"message": "..."}`:

- `401` — sem token, token expirado, token inválido ou token já usado no logout.
- `403` — autenticado, mas sem permissão para o recurso.

### Como funciona o logout

O JWT é autocontido, então descartá-lo no cliente não o invalidaria no servidor.
O logout registra o `jti` do token em uma lista de revogados, consultada em toda
requisição até o token expirar.

Essa lista é mantida em memória: é perdida no restart da aplicação e não é
compartilhada entre instâncias. Para rodar em mais de um nó, trocar
`TokenRevocationService` por um store compartilhado (Redis ou tabela dedicada).

### Primeiro administrador

O cadastro público só cria estudantes, e a gestão de usuários exige perfil de
administrador. O primeiro administrador precisa ser criado direto no banco:
cadastre-se por `/api/auth/register` e atualize a role.

```sql
UPDATE users SET user_role_id = 1 WHERE username = 'seu_usuario';
```

### Pontos ainda abertos

- `MockExam` tem `user_id`, mas as rotas de simulado ainda não filtram por dono:
  qualquer usuário autenticado pode ler e alterar o simulado de outro. A proteção
  atual é por perfil, não por propriedade do registro.
- Não há rate limiting nem bloqueio temporário após tentativas de login falhas.
- Não há refresh token: quando o access token expira, é preciso logar de novo.
- CORS não está configurado — necessário antes de consumir a API por um
  frontend em outra origem.

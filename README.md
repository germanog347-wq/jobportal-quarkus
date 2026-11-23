# Portal de Vagas — Quarkus + MySQL (Laragon)

## Estrutura (camadas limpas)

```
controller/  → apenas HTTP + templates (sem regras de negócio)
bo/          → TODAS as regras de negócio e validações
dao/         → acesso a dados (EntityManager)
model/       → entidades JPA
dto/         → formulários
service/     → SessaoService (cookie HttpOnly + SameSite=Lax)
templates/   → HTML Qute
META-INF/resources/css/  → estilo moderno
META-INF/resources/js/   → máscaras CPF/telefone, confirmações, busca
```

## Banco (MySQL / Laragon)

```sql
CREATE DATABASE jobportal;
```

```bash
mvn quarkus:dev
```

http://localhost:8080

### Contas pré-carregadas
| E-mail | Senha | Perfil |
|--------|-------|--------|
| admin@portal.com | admin123 | Admin |
| aluno@email.com | 123456 | Usuário |

## Novidades
- UI moderna (navbar, cards, hero, perfil)
- JavaScript: máscaras CPF/telefone, confirmação de exclusão
- Busca de vagas por título/empresa/cidade/descrição
- Cadastro com CPF, nascimento, telefone e cidade
- Perfil com painel de credenciais
- Cookie de sessão HttpOnly + SameSite=Lax

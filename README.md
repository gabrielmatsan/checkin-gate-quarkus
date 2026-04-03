# Checkin Gate - Quarkus

Reescrita do projeto [Checkin Gate](https://github.com/gabrielmatsan/checkin-gate) utilizando **Java 25** e **Quarkus 3.31**.

O projeto original foi desenvolvido com Golang. Esta versao aplica os mesmos conceitos e funcionalidades, portados para o ecossistema Java com arquitetura hexagonal.

## Tecnologias

- Java 25
- Quarkus 3.31
- PostgreSQL + Flyway
- Redis (fila de certificados)
- Hibernate ORM com Panache
- JWT (SmallRye JWT)
- Google OAuth2
- OpenHTML to PDF (geracao de certificados)
- Quarkus Mailer

## Como executar

```bash
# dev mode com hot reload
./mvnw quarkus:dev
```

## Estrutura

```
src/main/java/com/checkingate/
  events/          # Modulo de eventos, atividades, check-ins e certificados
  identity/        # Modulo de autenticacao e sessoes
  shared/          # Excecoes e seguranca compartilhados
```

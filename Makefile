.PHONY: dev build test clean infra infra-down package native

## Inicia o Quarkus em modo dev com hot reload
dev:
	./mvnw quarkus:dev

## Compila o projeto
build:
	./mvnw compile

## Executa os testes
test:
	./mvnw test

## Empacota o projeto (gera o JAR)
package:
	./mvnw package -DskipTests

## Limpa os artefatos de build
clean:
	./mvnw clean

## Sobe PostgreSQL e Redis via Docker Compose
infra:
	docker compose up -d

## Para os containers de infra
infra-down:
	docker compose down

## Build nativo com GraalVM
native:
	./mvnw package -Dnative -DskipTests

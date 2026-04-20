# test

Spring Boot backend project for a live streaming application.

## Tech Stack

- Java 17
- Spring Boot
- Maven
- Kubernetes deployment model
- Protocol Buffers for frontend/backend contracts

## Development Rules

This repository uses the engineering rules defined in [AGENTS.md](/Users/gray/project/test/test/AGENTS.md). The most important rules are:

- All code changes must include unit tests.
- Java code must follow Google Java Style.
- Frontend/backend interaction contracts must use Protocol Buffers.
- Deployment design must remain compatible with Kubernetes.
- Git commits must be split into small, focused units.
- All commit messages must be generated with the `git-commit-message` skill at [SKILL.md](/Users/gray/project/skill/git-commit-message/SKILL.md).

## Commit Rules

Before every `git commit`:

1. Make sure the change is focused and does not mix unrelated concerns.
2. Make sure unit tests are added or updated.
3. Generate the commit message with the `git-commit-message` skill.

Do not create a single large commit covering unrelated schema changes, API changes, refactoring, generated code, and tests all together unless the user explicitly approves that scope.

## API Contract Rules

- New frontend/backend interfaces must be defined with `.proto` files.
- Do not introduce new ad hoc JSON contracts for app-facing interfaces unless explicitly approved.
- Treat `.proto` definitions as the source of truth for request/response contracts.

## Spring Boot Expectations

- Keep controllers thin.
- Put business logic in services.
- Keep repository and configuration responsibilities clear.
- Prefer testable abstractions for external integrations.

## Deployment Expectations

- Design runtime behavior for Kubernetes environments.
- Prefer environment-based configuration.
- Keep health checks, service configuration, and dependency assumptions container-friendly.

## Local Commands

Run tests:

```bash
./mvnw test
```

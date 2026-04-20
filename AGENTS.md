# AGENTS.md

## Purpose

This file defines the project-level working rules for any AI agent or automated coding assistant operating in this repository. These instructions apply to all future implementation work unless the user explicitly overrides them.

## Project Context

- Backend framework: Spring Boot
- Deployment target: Kubernetes
- Frontend/backend interaction protocol: Protocol Buffers (PB)
- Primary language: Java

## Mandatory Engineering Rules

### 1. Commit Discipline

- Git commits must be split into small, coherent units.
- Do not bundle many unrelated file changes into a single commit.
- Prefer one commit per focused concern, such as schema setup, API contract, service logic, or tests.
- If a task is too large for one clean commit, break the work into multiple commits with clear boundaries.
- Do not create a "catch-all" commit that mixes refactoring, feature code, generated files, and tests without separation.

### 2. Commit Message Rules

- All commits in this repository must use the `git-commit-message` skill.
- The skill definition is located at [SKILL.md](/Users/gray/project/skill/git-commit-message/SKILL.md).
- Before running `git commit`, the agent must read and follow that skill to generate the commit message.
- The commit message must not be written freehand when this skill is available at the configured path.

### 3. Testing Rules

- All code changes must include unit tests.
- No implementation is considered complete without corresponding automated tests.
- Tests must cover normal paths, important edge cases, and key failure behavior where applicable.
- Bug fixes must include regression tests when feasible.
- If a change cannot reasonably be unit tested, the agent must explain why explicitly before completion.

### 4. Java Style Rules

- All Java code must follow Google Java Style.
- Keep naming, formatting, imports, class organization, and constant definitions aligned with Google Java Style conventions.
- Avoid introducing code that conflicts with established Java style checks or formatter expectations.

### 5. Protocol Rules

- Frontend/backend contracts must use Protocol Buffers (`.proto`) as the interaction protocol.
- When defining or changing request/response contracts, update PB definitions first or alongside the implementation.
- Do not introduce ad hoc JSON contracts for new frontend/backend interfaces unless the user explicitly approves an exception.
- Generated code should be treated as derived artifacts and should not replace the source-of-truth `.proto` definitions.

### 6. Deployment Rules

- Deployment assumptions and runtime design must be compatible with Kubernetes.
- New services, configuration, health checks, and runtime dependencies should be designed with containerized deployment in mind.
- Avoid implementation decisions that assume a single-host, manually managed runtime.
- Configuration should prefer environment-driven or externalized settings suitable for Kubernetes deployment.

## Spring Boot Implementation Expectations

- Follow standard Spring Boot layering where appropriate, for example controller, service, repository, and configuration separation.
- Keep business logic out of controllers.
- Prefer clear dependency injection and testable service boundaries.
- External integrations should be isolated behind explicit service abstractions.
- Configuration properties should be structured for maintainability and deployment safety.

## Change Scope Rules

- Minimize the number of files touched in a single implementation step when possible.
- Do not perform broad unrelated refactors while implementing a focused requirement.
- Keep generated files, formatting-only edits, and behavioral changes separated unless there is a strong reason not to.
- Respect existing in-progress user changes and never revert them unless explicitly asked.

## Completion Checklist For Agents

Before declaring work complete, the agent must confirm:

1. The change is scoped and does not include unrelated edits.
2. The implementation is covered by unit tests.
3. Java code follows Google Java Style expectations.
4. API contract changes are reflected in PB definitions where applicable.
5. Deployment assumptions remain compatible with Kubernetes.
6. Commits are split into focused units.
7. Commit messages are generated with the `git-commit-message` skill at `/Users/gray/project/skill/git-commit-message/SKILL.md`.

## Priority

These repository instructions are project-level constraints and should be treated as high-priority guidance for all future agent work in this repository.

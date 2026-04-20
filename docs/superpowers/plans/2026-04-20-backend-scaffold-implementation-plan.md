# Backend Scaffold Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the repository scaffold for PB contracts, Kubernetes manifests, and Spring Boot domain-oriented package structure so future auth work can follow a stable project convention.

**Architecture:** Keep the application as a single Spring Boot process, but create domain-oriented directories and placeholder templates that align with future service boundaries. Treat `proto/` as the contract source of truth and `k8s/` as a Kustomize-style deployment tree with `base` and `overlays`.

**Tech Stack:** Java 17, Spring Boot, Maven, Protocol Buffers, Kubernetes, Kustomize-style manifests

---

### Task 1: Add PB Contract Scaffold

**Files:**
- Create: `proto/buf.yaml`
- Create: `proto/common/errors.proto`
- Create: `proto/common/pagination.proto`
- Create: `proto/common/metadata.proto`
- Create: `proto/common/types.proto`
- Create: `proto/live/auth/auth_service.proto`
- Create: `proto/live/auth/auth_model.proto`
- Create: `proto/live/auth/auth_enum.proto`
- Create: `proto/live/user/.gitkeep`
- Create: `proto/live/stream/.gitkeep`

- [ ] **Step 1: Create the PB directory tree**

Create these paths:

```text
proto/
proto/common/
proto/live/auth/
proto/live/user/
proto/live/stream/
```

- [ ] **Step 2: Add `buf.yaml`**

```yaml
version: v2
modules:
  - path: proto
lint:
  use:
    - STANDARD
breaking:
  use:
    - FILE
```

- [ ] **Step 3: Add common proto placeholders**

Use focused placeholder contracts that compile cleanly later:

```proto
syntax = "proto3";

package live.common;

message ErrorDetail {
  string code = 1;
  string message = 2;
}
```

```proto
syntax = "proto3";

package live.common;

message PageRequest {
  int32 page_no = 1;
  int32 page_size = 2;
}
```

- [ ] **Step 4: Add auth proto placeholders**

`auth_service.proto` should define service signatures only:

```proto
syntax = "proto3";

package live.auth.v1;

import "live/auth/auth_model.proto";

service AuthService {
  rpc SendSmsCode(SendSmsCodeRequest) returns (SendSmsCodeResponse);
  rpc LoginBySms(LoginBySmsRequest) returns (LoginBySmsResponse);
  rpc RefreshToken(RefreshTokenRequest) returns (RefreshTokenResponse);
  rpc Logout(LogoutRequest) returns (LogoutResponse);
  rpc GetCurrentUser(GetCurrentUserRequest) returns (GetCurrentUserResponse);
}
```

- [ ] **Step 5: Commit**

```bash
git add proto
git commit -m "feat(proto): 新增 PB 协议目录与 auth 契约骨架"
```

### Task 2: Add Kubernetes Scaffold

**Files:**
- Create: `k8s/base/app/deployment.yaml`
- Create: `k8s/base/app/service.yaml`
- Create: `k8s/base/app/hpa.yaml`
- Create: `k8s/base/app/pdb.yaml`
- Create: `k8s/base/config/configmap.yaml`
- Create: `k8s/base/config/secret.example.yaml`
- Create: `k8s/base/ingress/ingress.yaml`
- Create: `k8s/base/kustomization.yaml`
- Create: `k8s/overlays/dev/kustomization.yaml`
- Create: `k8s/overlays/dev/patch-deployment.yaml`
- Create: `k8s/overlays/dev/patch-ingress.yaml`
- Create: `k8s/overlays/test/kustomization.yaml`
- Create: `k8s/overlays/test/patch-deployment.yaml`
- Create: `k8s/overlays/test/patch-ingress.yaml`
- Create: `k8s/overlays/prod/kustomization.yaml`
- Create: `k8s/overlays/prod/patch-deployment.yaml`
- Create: `k8s/overlays/prod/patch-ingress.yaml`

- [ ] **Step 1: Create the K8s directory tree**

Create these paths:

```text
k8s/base/app/
k8s/base/config/
k8s/base/ingress/
k8s/overlays/dev/
k8s/overlays/test/
k8s/overlays/prod/
```

- [ ] **Step 2: Add base deployment and service templates**

Use a minimal but production-shaped deployment:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: live-app-backend
spec:
  replicas: 1
  selector:
    matchLabels:
      app: live-app-backend
  template:
    metadata:
      labels:
        app: live-app-backend
    spec:
      containers:
        - name: app
          image: live-app-backend:latest
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
```

- [ ] **Step 3: Add base config and ingress templates**

`configmap.yaml` should include profile placeholders; `secret.example.yaml` must contain fake sample keys only.

- [ ] **Step 4: Add overlay kustomization and patch files**

Each environment overlay must:
- reference `../../base`
- patch replica/image/profile
- patch ingress host

- [ ] **Step 5: Commit**

```bash
git add k8s
git commit -m "feat(k8s): 新增 base 与 overlays 部署骨架"
```

### Task 3: Add Spring Boot Domain Package Scaffold

**Files:**
- Create: `src/main/java/com/test/test/bootstrap/.gitkeep`
- Create: `src/main/java/com/test/test/common/.gitkeep`
- Create: `src/main/java/com/test/test/infrastructure/.gitkeep`
- Create: `src/main/java/com/test/test/domain/auth/api/.gitkeep`
- Create: `src/main/java/com/test/test/domain/auth/application/.gitkeep`
- Create: `src/main/java/com/test/test/domain/auth/domain/.gitkeep`
- Create: `src/main/java/com/test/test/domain/auth/infrastructure/.gitkeep`
- Create: `src/main/java/com/test/test/domain/auth/convert/.gitkeep`
- Create: `src/main/java/com/test/test/domain/user/api/.gitkeep`
- Create: `src/main/java/com/test/test/domain/user/application/.gitkeep`
- Create: `src/main/java/com/test/test/domain/user/domain/.gitkeep`
- Create: `src/main/java/com/test/test/domain/user/infrastructure/.gitkeep`
- Create: `src/main/java/com/test/test/domain/user/convert/.gitkeep`
- Create: `src/main/java/com/test/test/domain/stream/api/.gitkeep`
- Create: `src/main/java/com/test/test/domain/stream/application/.gitkeep`
- Create: `src/main/java/com/test/test/domain/stream/domain/.gitkeep`
- Create: `src/main/java/com/test/test/domain/stream/infrastructure/.gitkeep`
- Create: `src/main/java/com/test/test/domain/stream/convert/.gitkeep`
- Create: `src/test/java/com/test/test/domain/auth/.gitkeep`
- Create: `src/test/java/com/test/test/domain/user/.gitkeep`
- Create: `src/test/java/com/test/test/domain/stream/.gitkeep`
- Create: `src/test/java/com/test/test/common/.gitkeep`
- Create: `src/test/java/com/test/test/infrastructure/.gitkeep`

- [ ] **Step 1: Write the failing test**

Add a structure verification test:

```java
@Test
void scaffoldDirectoriesShouldExist() {
  assertTrue(Files.exists(Path.of("src/main/java/com/test/test/domain/auth/api")));
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
./mvnw -q -Dtest=ScaffoldStructureTest test
```

Expected: FAIL because the directories or test class do not yet exist.

- [ ] **Step 3: Add the package scaffold and the test**

Create the directory tree with `.gitkeep` files and add `ScaffoldStructureTest`.

- [ ] **Step 4: Run test to verify it passes**

Run:

```bash
./mvnw -q -Dtest=ScaffoldStructureTest test
```

Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src
git commit -m "feat(backend): 新增按业务域划分的代码骨架"
```

### Task 4: Add Resource and Config Scaffold

**Files:**
- Modify: `src/main/resources/application.properties`
- Create: `src/main/resources/application.yml`
- Create: `src/main/resources/application-dev.yml`
- Create: `src/main/resources/application-test.yml`
- Create: `src/main/resources/application-prod.yml`
- Create: `src/main/resources/db/migration/.gitkeep`

- [ ] **Step 1: Write the failing test**

Add a resource-layout verification test:

```java
@Test
void resourceProfilesShouldExist() {
  assertTrue(Files.exists(Path.of("src/main/resources/application.yml")));
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
./mvnw -q -Dtest=ScaffoldResourceLayoutTest test
```

Expected: FAIL because the YAML resource files are not present yet.

- [ ] **Step 3: Add the resource scaffold**

Create the YAML files with placeholder profile-safe configuration:

```yaml
spring:
  application:
    name: live-app-backend
```

- [ ] **Step 4: Run test to verify it passes**

Run:

```bash
./mvnw -q -Dtest=ScaffoldResourceLayoutTest test
```

Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/resources src/test/java
git commit -m "chore(config): 补充应用配置与资源目录骨架"
```

### Task 5: Document the Executable Scaffold Conventions

**Files:**
- Modify: `README.md`
- Modify: `docs/architecture/2026-04-20-backend-scaffold-conventions.md`

- [ ] **Step 1: Add README pointers to the real scaffold paths**

Update `README.md` to mention:

```md
- `proto/` stores PB contracts
- `k8s/` stores Kubernetes manifests
- `src/main/java/com/test/test/domain/` stores domain-oriented code
```

- [ ] **Step 2: Align the architecture document with the created paths**

Ensure the document reflects the exact checked-in tree and placeholder files.

- [ ] **Step 3: Run targeted verification**

Run:

```bash
./mvnw test
```

Expected: PASS

- [ ] **Step 4: Commit**

```bash
git add README.md docs/architecture
git commit -m "docs: 同步后端脚手架落地说明"
```

## Self-Review

### Spec coverage

- PB contract structure: covered by Task 1
- K8s base and overlay structure: covered by Task 2
- Spring Boot domain layering: covered by Task 3
- Resource/config scaffold: covered by Task 4
- Documentation alignment: covered by Task 5

### Placeholder scan

- No `TODO` or `TBD` placeholders are used in the plan.
- Each task has exact file paths and a commit boundary.

### Type consistency

- `proto/live/auth/*` naming is consistent across all proto references.
- `src/main/java/com/test/test/domain/<biz>/...` naming is consistent across domain examples.
- `live-app-backend` is used consistently as the app deployment name in K8s examples.

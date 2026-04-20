# 后端脚手架规范：PB 协议目录、K8s 目录与 Spring Boot 分层约定

## 1. 文档信息

- 文档主题：后端脚手架规范
- 适用项目：视频直播 APP 后端
- 文档日期：2026-04-20
- 适用阶段：V1 架构骨架约定

## 2. 目标

本规范用于统一项目的后端脚手架结构，覆盖以下三个核心方面：

- Protocol Buffers 协议目录结构
- Kubernetes 部署目录结构
- Spring Boot 单体应用的业务域分层约定

本项目当前采用单体 Spring Boot 运行方式，但目录和契约设计应为未来按业务域拆分微服务预留清晰边界。因此，本规范遵循“单体先行、边界先立”的原则：当前实现成本保持可控，未来拆分 `auth`、`user`、`stream` 等服务时不需要推翻整体结构。

## 3. 总体设计原则

- 当前以单体应用交付，但按业务域隔离目录边界。
- PB 协议作为前后端交互的事实源，不以 Java DTO 或 JSON 结构为准。
- Kubernetes 目录按 `base + overlays` 组织，避免环境配置散落。
- Spring Boot 代码结构按业务域收口，域内再分层，而不是仅按全局技术层堆叠。
- 公共能力下沉到 `common` 或基础设施层，避免横向复制。
- 任何新业务能力都必须先找到其所属业务域，再决定其协议、代码和部署归属。

## 4. 推荐仓库结构

```text
.
├── AGENTS.md
├── README.md
├── docs/
│   ├── architecture/
│   │   └── 2026-04-20-backend-scaffold-conventions.md
│   └── superpowers/specs/
├── k8s/
│   ├── base/
│   │   ├── app/
│   │   ├── config/
│   │   ├── ingress/
│   │   └── kustomization.yaml
│   └── overlays/
│       ├── dev/
│       ├── test/
│       └── prod/
├── proto/
│   ├── buf.yaml
│   ├── common/
│   └── live/
├── src/
│   ├── main/
│   │   ├── java/com/test/test/
│   │   └── resources/
│   └── test/
│       └── java/com/test/test/
└── pom.xml
```

## 5. PB 协议目录规范

### 5.1 设计目标

PB 协议层需要满足以下要求：

- 成为前后端接口唯一事实源
- 支持公共协议复用
- 支持业务域独立演进
- 避免业务域之间的双向耦合
- 为未来拆分独立服务保留可迁移性

### 5.2 推荐目录结构

```text
proto/
├── buf.yaml
├── common/
│   ├── errors.proto
│   ├── pagination.proto
│   ├── metadata.proto
│   └── types.proto
└── live/
    ├── auth/
    │   ├── auth_service.proto
    │   ├── auth_model.proto
    │   └── auth_enum.proto
    ├── user/
    │   ├── user_service.proto
    │   ├── user_model.proto
    │   └── user_enum.proto
    └── stream/
        ├── stream_service.proto
        ├── stream_model.proto
        └── stream_enum.proto
```

### 5.3 分层规则

#### `proto/common/`

用于存放跨业务域共用的协议定义，只允许承载通用语义，不允许塞入某个具体业务域的私有字段。

建议内容：

- `errors.proto`：错误码、错误对象结构
- `pagination.proto`：分页请求与分页响应
- `metadata.proto`：请求元信息、客户端信息、追踪字段
- `types.proto`：公共复用消息，如用户基础摘要、时间区间、通用 ID 包装类型

#### `proto/live/<domain>/`

每个业务域使用独立子目录，当前建议最少预留：

- `auth`
- `user`
- `stream`

每个业务域内部建议按以下文件职责拆分：

- `*_service.proto`：服务接口、RPC/方法定义、request/response 入口
- `*_model.proto`：业务消息结构
- `*_enum.proto`：该域独有枚举

### 5.4 依赖约束

- 业务域 proto 可以依赖 `common/*`
- 一个业务域 proto 不应随意直接依赖另一个业务域 proto
- 若存在跨域共享模型，应优先下沉至 `common/types.proto`
- 禁止形成 `auth -> user -> auth` 之类循环依赖

### 5.5 命名约定

- 文件名使用英文小写加下划线
- service 名、message 名、enum 名使用标准 Proto 风格
- package 名建议与目录对应，例如：
  - `live.common`
  - `live.auth.v1`
  - `live.user.v1`

### 5.6 首版业务域建议

围绕当前直播 APP，建议首先预留以下业务域：

- `auth`：注册、登录、验证码、Token、会话
- `user`：用户资料、用户状态、账号档案
- `stream`：直播间、流状态、房间信息

对于当前注册/登录功能，建议优先在以下位置定义协议：

```text
proto/live/auth/
├── auth_service.proto
├── auth_model.proto
└── auth_enum.proto
```

示例职责：

- `auth_service.proto`
  - `SendSmsCode`
  - `LoginBySms`
  - `RefreshToken`
  - `Logout`
  - `GetCurrentUser`
- `auth_model.proto`
  - `SendSmsCodeRequest`
  - `SendSmsCodeResponse`
  - `LoginBySmsRequest`
  - `LoginBySmsResponse`
  - `TokenPair`
- `auth_enum.proto`
  - `SmsScene`
  - `AccountStatus`
  - `DeviceType`

### 5.7 PB 变更流程约定

- 新增或修改前后端接口时，必须先改 `.proto`
- Java 代码、适配层和测试必须围绕 `.proto` 同步调整
- 不允许先定义 Java DTO，再反推 PB
- 生成代码属于派生物，`proto/` 下源文件才是事实源

## 6. Spring Boot 分层与包结构规范

### 6.1 设计目标

Spring Boot 代码结构需要同时满足：

- 当前单体项目易开发
- 业务域边界清晰
- 未来拆服务时迁移成本低
- 控制器、流程编排、领域规则、基础设施职责分明

### 6.2 推荐包结构

```text
src/main/java/com/test/test/
├── bootstrap/
├── common/
├── infrastructure/
└── domain/
    ├── auth/
    │   ├── api/
    │   ├── application/
    │   ├── domain/
    │   ├── infrastructure/
    │   └── convert/
    ├── user/
    │   ├── api/
    │   ├── application/
    │   ├── domain/
    │   ├── infrastructure/
    │   └── convert/
    └── stream/
        ├── api/
        ├── application/
        ├── domain/
        ├── infrastructure/
        └── convert/
```

测试代码建议镜像主代码结构：

```text
src/test/java/com/test/test/
├── common/
├── infrastructure/
└── domain/
    ├── auth/
    ├── user/
    └── stream/
```

### 6.3 顶层目录职责

#### `bootstrap/`

放启动类和应用装配入口，例如：

- Spring Boot 启动类
- 顶层模块扫描配置
- 启动初始化逻辑

该层不承载业务规则。

#### `common/`

放全项目级共通能力，例如：

- 公共异常基类
- 通用错误码定义
- 时间、脱敏、校验等纯工具能力
- 基础注解或通用返回模型

约束：

- `common` 不承载具体业务逻辑
- 不允许将某个域的专属概念错误地下沉到 `common`

#### `infrastructure/`

放跨域共享的基础设施能力，例如：

- 数据库连接与 ORM 配置
- Redis 配置
- PB 编解码或传输适配支持
- 鉴权拦截器
- 统一日志与 Trace 配置
- 短信网关客户端基础能力

该层可以被多个业务域复用，但不应定义某个业务域的特定规则。

### 6.4 域内分层职责

每个业务域内统一采用以下结构：

#### `api/`

对外入口层，负责：

- 接收入参
- 基础参数校验
- 鉴权接入
- 调用 application 层
- 返回 PB 或外部响应对象

约束：

- 不在 controller 或 endpoint 中写业务规则
- 不在 `api` 层直接访问数据库

#### `application/`

用例编排层，负责：

- 事务边界
- 业务流程编排
- 协调多个领域对象
- 处理一个完整用例，如“验证码登录并自动注册”

约束：

- 可以调用本域 `domain`
- 可以调用必要的跨域能力，但应通过清晰接口
- 不应直接依赖别的域的 `api`

#### `domain/`

核心领域层，负责：

- 实体
- 值对象
- 领域服务
- 领域规则
- 仓储接口

这是最稳定的业务规则归属层。与协议格式、HTTP 层、控制器、数据库实现解耦。

#### `infrastructure/`

该业务域自己的技术实现层，负责：

- Repository 实现
- DO/PO 持久化对象
- ORM Mapper
- 第三方适配实现
- Token 生成实现
- 短信渠道实现

约束：

- Repository 接口放在 `domain`，实现放在 `infrastructure`
- 不要把业务编排逻辑堆进 `infrastructure`

#### `convert/`

专门放转换逻辑，例如：

- PB Message 与 Application Command 转换
- Entity 与 DO 转换
- DTO 与领域对象转换

目标是避免转换代码散落在 controller、service、repository 中。

### 6.5 `auth` 业务域示例

```text
domain/auth/
├── api/
│   └── AuthController.java
├── application/
│   ├── AuthApplicationService.java
│   └── command/
├── domain/
│   ├── model/
│   ├── service/
│   ├── repository/
│   └── event/
├── infrastructure/
│   ├── persistence/
│   ├── sms/
│   └── token/
└── convert/
```

### 6.6 强制边界约束

- Controller 不写核心业务逻辑
- Application 层不跨域直接依赖其他域的持久化实现
- Domain 层不依赖 Spring MVC、HTTP、Servlet、Controller DTO
- Repository 接口必须位于 `domain`，实现位于 `infrastructure`
- 跨域复用时优先抽公共协议或公共服务接口，避免直接穿透内部实现
- 任何新功能都必须首先归属到某个业务域，而不是直接塞入 `common`

## 7. K8s 目录与模板规范

### 7.1 设计目标

Kubernetes 部署目录需要做到：

- 环境无关基础清单与环境差异分离
- 保持开发、测试、生产环境的一致部署模型
- 为未来拆分多服务提供自然扩展点

### 7.2 推荐目录结构

```text
k8s/
├── base/
│   ├── app/
│   │   ├── deployment.yaml
│   │   ├── service.yaml
│   │   ├── hpa.yaml
│   │   └── pdb.yaml
│   ├── config/
│   │   ├── configmap.yaml
│   │   └── secret.example.yaml
│   ├── ingress/
│   │   └── ingress.yaml
│   └── kustomization.yaml
└── overlays/
    ├── dev/
    │   ├── kustomization.yaml
    │   ├── patch-deployment.yaml
    │   └── patch-ingress.yaml
    ├── test/
    │   ├── kustomization.yaml
    │   ├── patch-deployment.yaml
    │   └── patch-ingress.yaml
    └── prod/
        ├── kustomization.yaml
        ├── patch-deployment.yaml
        └── patch-ingress.yaml
```

### 7.3 `base/` 目录职责

#### `base/app/`

放应用级通用清单：

- `deployment.yaml`
- `service.yaml`
- `hpa.yaml`
- `pdb.yaml`

这些清单应尽量不带具体环境差异。

#### `base/config/`

放基础配置：

- `configmap.yaml`：非敏感配置
- `secret.example.yaml`：密钥结构示例，不提交真实值

#### `base/ingress/`

放统一入口清单，例如：

- 路由规则
- 主机名占位
- TLS 配置骨架

#### `base/kustomization.yaml`

统一声明 `base` 资源聚合关系。

### 7.4 `overlays/` 目录职责

每个环境独立一个 overlay：

- `dev`
- `test`
- `prod`

每个 overlay 只放环境差异，例如：

- 副本数
- 镜像 tag
- 域名
- 资源请求和限制
- Spring profile
- 环境变量覆盖

### 7.5 清单内容约束

#### `deployment.yaml`

至少应包含：

- 容器镜像定义
- 端口定义
- `readinessProbe`
- `livenessProbe`
- 资源请求与限制
- 环境变量或配置挂载
- 滚动更新策略

#### `service.yaml`

应只定义服务暴露与端口映射，不直接混入环境特有域名逻辑。

#### `hpa.yaml`

首版即使参数保守，也建议预留自动扩缩容模板，便于后续接入。

#### `pdb.yaml`

用于保证升级或节点波动期间的可用性，建议从一开始就预留。

#### `configmap.yaml`

仅放非敏感配置项，例如：

- `SPRING_PROFILES_ACTIVE`
- 日志级别
- 业务开关

#### `secret.example.yaml`

只放字段骨架，例如：

- 数据库密码键名
- Token 密钥键名
- 短信服务凭证键名

禁止提交真实密钥。

### 7.6 命名约定

当前虽然是单体部署，但命名建议保持业务化前缀，例如：

- `live-app-backend`
- `live-app-config`
- `live-app-ingress`

这样后续拆分 `auth-service`、`user-service` 时命名迁移更平滑。

## 8. 配置与资源目录建议

建议 `src/main/resources/` 至少逐步演进为：

```text
src/main/resources/
├── application.yml
├── application-dev.yml
├── application-test.yml
├── application-prod.yml
└── db/
    └── migration/
```

约定如下：

- 默认配置放 `application.yml`
- 环境差异放 `application-<profile>.yml`
- 敏感配置不硬编码在仓库中
- 数据库变更脚本集中管理，避免散落

## 9. 新功能落位规则

新增功能时，按以下顺序判断其归属：

1. 它属于哪个业务域？
2. 它是否需要新增或修改 PB 协议？
3. 它在该业务域内属于 `api`、`application`、`domain` 还是 `infrastructure`？
4. 它是否需要新增部署配置或环境变量？

以“注册/登录”功能为例：

- PB 放在 `proto/live/auth/`
- Java 代码放在 `domain/auth/`
- 数据持久化实现放在 `domain/auth/infrastructure/`
- 配置项通过 `resources` 和 `k8s/config` 管理

## 10. 首版落地建议

建议下一步按以下顺序真正建立骨架：

1. 创建 `proto/common` 与 `proto/live/auth` 目录
2. 创建 `k8s/base` 与 `k8s/overlays/{dev,test,prod}` 目录
3. 调整 `src/main/java` 到按业务域收口的包结构
4. 为注册/登录功能先建立 `auth` 域骨架
5. 增加与目录约定一致的单元测试结构

## 11. 验收标准

当本规范被真正落地为脚手架时，应满足以下标准：

- PB 目录已按 `common + domain` 组织
- Spring Boot 代码已按业务域分层
- K8s 已按 `base + overlays` 组织
- 新增业务功能可以明确定位其协议、代码和部署归属
- 不再出现新接口直接使用临时 JSON 契约的情况
- 不再出现业务逻辑直接堆积在 controller 或全局 service 的情况

## 12. 结论

本规范采用“单体先行、边界先立”的策略，为当前直播 APP 后端建立统一脚手架标准。通过将 PB、K8s 和 Spring Boot 分层一起规范化，项目可以在当前阶段保持开发效率，同时为未来业务域拆分和服务化演进保留稳定边界。

# Implementation Plan: STS 直传 OSS + 统一 ImageResponse

**Branch**: `003-image-response-oss` | **Date**: 2026-05-23 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/003-image-response-oss/spec.md`

## Summary

把 admin 后台的图片"先服务端 multipart 上传"模型重构为"前端 STS 直传 OSS + 业务绑定时服务端校验"模型：
1. 新增 `POST /api/admin/files/upload-credentials`，服务端通过 STS `AssumeRole` 拿临时凭证，用其在服务端计算 PostObject V4 签名（`OSS4-HMAC-SHA256`），并预生成 `objectKey`（`images/<uuidv7>.<ext>`）一并下发；`accessKeySecret` 不下发。
2. 前端用 `multipart/form-data` 表单 POST 直传 OSS，不经过 admin 进程。
3. 业务实体创建/更新时把 objectKey 提交给业务接口；服务端 `headObject` 做存在性 + MIME + 大小校验后入库。
4. 所有读接口的图片字段统一返回 `ImageResponse(id, url)`，`url` 由 `ImageUrlSigner` 即时签名（默认 30 分钟）。
5. 孤儿对象由 OSS bucket lifecycle 规则（`images/` 前缀，未带 `bound=true` tag 且超过 24 小时）自动删除；绑定成功时服务端给对象打 `bound=true` tag。
6. 在 `love-space-app` 复制读路径（`ImageResponse` + `ImageUrlSigner`）；app 端无写入。

## Technical Context

**Language/Version**: Java 25（admin + app 后端）；TypeScript 5 + React 19（web 前端）。

**Primary Dependencies**:
- 已有：Spring Boot 4.0.6、Spring Data JPA、Spring Security、Liquibase、Lombok、`com.github.f4b6a3:uuid-creator`、`hibernate-jpamodelgen`。
- 新增（admin + app 各一份）：
  - `com.aliyun.oss:aliyun-sdk-oss:3.18.1`（OSS 客户端：headObject、签名、tagging）。
  - `com.aliyun:aliyun-java-sdk-core:4.7.1` + `com.aliyun:aliyun-java-sdk-sts:3.1.2`（STS AssumeRole；仅 admin 真正调用，app 只读不需要 STS 但 admin 必须）。
  - 实际上 app 端只需 `aliyun-sdk-oss`（签名 GET URL 不需要 STS）。
- 前端：无需 OSS SDK，用浏览器原生 `FormData` + `XMLHttpRequest` 表单 POST 直传。

**Storage**:
- 阿里云 OSS bucket，配置 lifecycle 规则：`images/` 前缀，按 object tag `bound != "true"` 且对象创建后 24 小时过期删除。
- PostgreSQL：现有列 `loves_banner.image_urls`(jsonb)、`loves_merchant.logo`(text)、`loves_merchant_image.image_url`(text)、`loves_city.background_image`(text) 改为存 OSS 对象 key（即 `images/<uuid>.<ext>`）；列名/类型不变。

**Testing**: JUnit 5 + Spring Boot Test (MockMvc) + Mockito。所有阿里云 SDK 客户端（OSSClient、STS 调用、ImageUrlSigner、ObjectKeyValidator）通过接口在测试中以 stub 注入，不需要真实 OSS / STS。前端用 Vitest + Testing Library（如已用），表单 POST 直传逻辑在测试中 mock（XHR / fetch）。

**Target Platform**: Linux server（2 个 Spring Boot 进程） + 浏览器（admin web）。

**Project Type**: Web 多服务（admin + app + web 前端）单 git 仓库。

**Performance Goals**:
- 直传 PUT：服务端零 CPU/带宽占用（不经手字节流）；前端从点击"选择图片"到 PUT 200 的 p95 ≤ 3 秒（10MB 内）。
- upload-credentials 端点：p95 ≤ 200ms（一次 STS AssumeRole 调用 ~100–150ms）。
- 业务绑定校验：每张图片 `headObject` p95 ≤ 50ms；同请求多张图片串行执行。

**Constraints**:
- STS / OSS 配置缺失 MUST 启动失败。
- STS 凭证默认 15 分钟，读签名 URL 默认 30 分钟，均可配置。
- 业务接口 MUST 拒绝不以 `images/` 开头或含 `..` 的 objectKey。
- MUST NOT 静默回退本地磁盘；MUST NOT 提供"绕过校验"开关。

**Scale/Scope**:
- admin 后端：新增 `files/credentials` 路径 + 改造 banner / merchant / city 三模块的请求 DTO + service 校验路径 + 所有响应 DTO；删除 `LocalFileStorage` / `FileService.upload(multipart)` / `FileController#upload`。
- app 后端：新增 `ImageResponse` + `ImageUrlSigner` + 改造 banner / merchant / city 响应。
- web 前端：新增 `uploadToOss`（FormData + XHR 表单直传）+ 改造 file/banner/merchant/city 页面的上传组件与表单类型；删除原 multipart 上传调用。
- 不涉及：category / tag / manager / auth / operationlog 模块（无图片字段）。

## Constitution Check

*Constitution version: 1.1.0（2026-05-22）。逐条核对：*

| 原则 | 评估 | 备注 |
|---|---|---|
| I. 中文 JavaDoc | ✅ | 新增 `StsCredentialIssuer` / `ObjectKeyValidator` / `ImageUrlSigner` / `OssProperties` / `StsProperties` / `ImageResponses` / `UploadCredentialResponse` 及改造的 DTO、service 方法 MUST 配中文 JavaDoc；controller 中文 JavaDoc 含路径、请求体、错误码描述。 |
| II. UUIDv7 主键 & 禁外键 | ✅ | 不新增 JPA 实体；objectKey 中 uuid 段使用 `UuidV7Generator.next()`。 |
| III. 命名清晰不缩写 | ✅ | `objectKey` / `securityToken` / `accessKeyId` / `expirationSeconds` / `urlExpirationSeconds` / `bound` / `bucketName` 等全部完整命名。 |
| IV. 双后端隔离 | ✅ | admin / app 各自一份 `ImageResponse` / `ImageUrlSigner` / `OssProperties`；STS 类只在 admin；MUST NOT 跨包。 |
| V. 测试 & 本地可运行 | ✅ | 所有外部调用通过接口注入；测试不依赖真实 OSS / STS；application-test.yml 用占位值；本地 `./mvnw test` 离线可跑。 |
| VI. JPA Metamodel | ✅ | 本特性不动 Specification / Criteria 代码；现有 banner Specification 仍用 `Banner_` metamodel。 |

**Gate Result (pre-research)**: PASS（无 Complexity Tracking 条目）。

## Project Structure

### Documentation (this feature)

```text
specs/003-image-response-oss/
├── spec.md                              # 已存在（含 Clarifications）
├── plan.md                              # 本文件
├── research.md                          # Phase 0
├── data-model.md                        # Phase 1
├── quickstart.md                        # Phase 1
├── contracts/
│   ├── upload-credentials-endpoint.md   # admin REST：POST /api/admin/files/upload-credentials
│   ├── business-binding.md              # admin REST：业务实体创建/更新时的 objectKey 校验契约
│   ├── ImageUrlSigner.md                # 内部接口：根据 objectKey 即时签名 GET URL
│   ├── StsCredentialIssuer.md           # 内部接口：调 STS AssumeRole 下发临时凭证
│   ├── ObjectKeyValidator.md            # 内部接口：headObject + MIME/大小校验 + 打 bound tag
│   ├── api-admin-read.md                # admin 读响应 schema 变更
│   ├── api-app-read.md                  # app 读响应 schema 变更
│   └── bucket-lifecycle.md              # OSS bucket lifecycle 与 tag 机制说明（运维配置 + 服务端绑定时的 tag 行为）
└── checklists/requirements.md           # 已存在
```

### Source Code (repository root)

```text
love-space-admin/                                                # admin 后端（写 + 读）
└── src/main/java/com/loves/space/
    ├── common/dto/ImageResponse.java                            # 已存在；record(id, url)
    ├── common/util/ImageResponses.java                          # 新：批量把 objectKey → ImageResponse
    ├── infrastructure/storage/
    │   ├── OssProperties.java                                   # 新：@ConfigurationProperties("app.storage.oss")
    │   ├── StsProperties.java                                   # 新：@ConfigurationProperties("app.storage.sts")
    │   ├── OssClientConfig.java                                 # 新：构造 OSS bean + 启动 sanity check
    │   ├── StsClientConfig.java                                 # 新：构造 AcsClient bean + 启动校验 role ARN
    │   ├── StsCredentialIssuer.java                             # 新：接口
    │   ├── AliyunStsCredentialIssuer.java                       # 新：AssumeRole 实现
    │   ├── ObjectKeyValidator.java                              # 新：接口
    │   ├── AliyunOssObjectKeyValidator.java                     # 新：headObject + tag 打标实现
    │   ├── ImageUrlSigner.java                                  # 新：接口
    │   ├── AliyunOssImageUrlSigner.java                         # 新：预签名 GET URL 实现
    │   ├── FileStorage.java                                     # 删（直传不再需要"服务端落盘"抽象）
    │   └── LocalFileStorage.java                                # 删
    ├── modules/file/
    │   ├── dto/UploadCredentialResponse.java                    # 新：PostObject 表单签名 + objectKey 下发包
    │   ├── dto/UploadCredentialRequest.java                     # 新：客户端声明 contentType
    │   ├── dto/FileUploadResponse.java                          # 删
    │   ├── service/FileService.java                             # 重写：原 multipart upload 删除；改为 issueUploadCredential(contentType)
    │   └── controller/FileController.java                       # 改：POST /upload 删；新增 POST /upload-credentials
    ├── modules/banner/
    │   ├── dto/BannerCreateRequest.java                         # imageUrls 语义改为 objectKey 列表（字段名不变）；中文 JavaDoc 同步
    │   ├── dto/BannerUpdateRequest.java                         # 同上
    │   ├── dto/BannerDetailResponse.java                        # imageUrls: List<ImageResponse>
    │   ├── dto/BannerListItemResponse.java                      # 同上
    │   └── service/BannerService.java                           # 写入前调 ObjectKeyValidator 校验；读时 ImageResponses 转
    ├── modules/merchant/
    │   ├── dto/MerchantUpsertRequest.java                       # logo / images 语义改为 objectKey
    │   ├── dto/MerchantDetailResponse.java                      # logo: ImageResponse, images: List<ImageResponse>
    │   ├── dto/MerchantAdminItem.java                           # logo: ImageResponse
    │   └── service/MerchantService.java                         # 写入前校验；读时签名
    └── modules/city/
        ├── dto/CityCreateRequest.java                           # backgroundImage 语义改为 objectKey
        ├── dto/CityUpdateRequest.java                           # 同上
        ├── dto/CityItemResponse.java                            # backgroundImage: ImageResponse（可空）
        ├── dto/CityDetailResponse.java                          # 同上
        └── service/CityService.java                             # 写入前校验（可空跳过）；读时签名

love-space-app/                                                  # app 后端（只读）
└── src/main/java/com/space/app/
    ├── common/dto/ImageResponse.java                            # 新
    ├── common/util/ImageResponses.java                          # 新
    ├── infrastructure/storage/
    │   ├── OssProperties.java                                   # 新
    │   ├── OssClientConfig.java                                 # 新
    │   ├── ImageUrlSigner.java                                  # 新（接口）
    │   └── AliyunOssImageUrlSigner.java                         # 新（实现）
    └── modules/{banner,merchant,city}/dto + service             # 响应升级为 ImageResponse / List<ImageResponse>

love-space-web/                                                  # React 19 + Vite 前端
└── src/
    ├── types/image.ts                                           # 新：ImageResponse、UploadCredentialResponse
    ├── lib/ossUpload.ts                                         # 新：uploadToOss，FormData + XHR 表单 POST 直传
    ├── pages/Files/...                                          # 上传组件：调 upload-credentials → 表单 POST → 暴露 objectKey
    ├── pages/Banners/...                                        # 表单改用 objectKey；详情/列表用 ImageResponse 渲染
    ├── pages/Merchants/...                                      # 同上（logo + images）
    └── pages/Cities/...                                         # 同上（backgroundImage）
```

**Structure Decision**: 沿用现有 monorepo 三子项目结构。本特性写路径完全在 admin + web 上重构（不再有"服务端代理上传"代码）；app 后端只复制读路径。infrastructure/storage 包内的抽象由"FileStorage（落盘）"升级为"StsCredentialIssuer + ObjectKeyValidator + ImageUrlSigner"三件套，更贴合直传模型。

## Complexity Tracking

> 本特性 Constitution Check 全部通过，无需填写。

---

## Phase 0 Outline (drives research.md)

研究主题：
1. **阿里云 STS Java SDK 版本与 AssumeRole 流程**：sdk-core + sdk-sts 推荐版本；AssumeRole 入参（RoleArn、RoleSessionName、DurationSeconds、Policy 内嵌限制）；返回 `Credentials{ AccessKeyId, AccessKeySecret, SecurityToken, Expiration }` 的解析与序列化。
2. **RAM Role policy 设计**：限制 action 仅 `oss:PutObject` + `oss:PutObjectTagging`（后者可选，留给绑定时打 tag 用，但绑定打 tag 应由服务端主 AK 而非客户端，故 policy 只需 `PutObject`）；资源限定 `acs:oss:*:*:<bucket>/images/*`；可在 AssumeRole 时内嵌额外 Policy 进一步收敛到本次预生成的 objectKey。
3. **objectKey 预生成 & 扩展名映射**：`png → png`、`jpeg → jpg`、`webp → webp`；服务端用 `UuidV7Generator.next()` + content-type 反查；客户端 PUT 时 `Content-Type` 必须与 contentType 一致。
4. **业务绑定时校验**：`OSSClient.getObjectMetadata(bucket, key)` 拿 `Content-Type` + `Content-Length` + 存在性；不通过则抛 `ValidationException`。通过后用 `OSSClient.setObjectTagging(bucket, key, [bound=true])` 标记保留。
5. **bucket lifecycle 与 bound tag**：阿里云 OSS Lifecycle 支持基于 tag 的过滤；规则示例：`Filter.tagSet[bound != "true"]` + `Days=1`。运维需在控制台 / Terraform 准备；plan 把规则配置写入 `bucket-lifecycle.md`。
6. **读路径签名**：仍用 `OSSClient.generatePresignedUrl(GetObject, expiration)`，默认 30 分钟。
7. **前端表单直传接入**：服务端用 STS 临时凭证计算 PostObject V4 签名（`OSS4-HMAC-SHA256`），前端用 `FormData`（`key` / `policy` / `x-oss-signature` / `x-oss-credential` / `x-oss-date` / `x-oss-security-token` / `success_action_status` / `file`）POST 到 `host`；不向浏览器下发 `accessKeySecret`，也不让前端自选 key。
8. **测试策略**：所有 SDK 接入点用接口替身；StsCredentialIssuer 在测试中返回固定 `Credentials`；ObjectKeyValidator 用 in-memory map 模拟 OSS metadata；ImageUrlSigner 直接拼装伪 URL。
9. **生产 / 测试环境配置分离**：admin 用 dev / test profile 接同一 dev bucket，但通过 `key-prefix` 区分（如 `images-dev/`）以避免共享 lifecycle 误删 → 决定 keep `images/` 单一前缀，环境隔离用不同 bucket。
10. **旧数据迁移**：spec Assumptions 已锁定不做自动迁移；plan 中提供手工 SQL 清空脚本。

`research.md` 会输出每项的 Decision / Rationale / Alternatives。

## Phase 1 Outline (drives data-model.md, contracts/*, quickstart.md, CLAUDE.md)

1. **data-model.md**：
   - `ImageResponse(id, url)` —— 不变。
   - `UploadCredentialResponse(host, objectKey, policy, signature, signatureVersion, xOssCredential, xOssDate, securityToken, expiration)` —— 新（PostObject 表单签名，不含 accessKeySecret）。
   - `UploadCredentialRequest(contentType)` —— 新；`contentType ∈ {image/png, image/jpeg, image/webp}`。
   - `OssProperties` / `StsProperties` 字段表。
   - 业务实体列语义变更矩阵（持久化值 = objectKey）。
   - 请求 / 响应 DTO 矩阵。
2. **contracts/**：
   - `upload-credentials-endpoint.md`：admin REST 契约（method、path、auth、request body、response body、错误码）。
   - `business-binding.md`：业务接口接收 objectKey 时的统一校验流程契约（前置：String 形态合法性；中置：`ObjectKeyValidator.validateAndMarkBound`；后置：失败错误码）。
   - `StsCredentialIssuer.md`、`ObjectKeyValidator.md`、`ImageUrlSigner.md`：三个内部接口的输入 / 输出 / 错误语义。
   - `api-admin-read.md` / `api-app-read.md`：响应 schema 变更。
   - `bucket-lifecycle.md`：OSS lifecycle 规则 JSON 与控制台操作清单 + 服务端在绑定成功时打 `bound=true` 的行为约定。
3. **quickstart.md**：从准备 OSS bucket + RAM Role + lifecycle 开始，到本地启动 admin + web，到端到端 6 步流程演示，到测试命令。
4. **agent context update**：`CLAUDE.md` SPECKIT 块已指向本 plan，无需再改（仍是 `specs/003-image-response-oss/plan.md`）。

## Phase 1 — Constitution Re-check (post-design)

- I 中文 JavaDoc：契约文档对每个新增类与方法都写明 JavaDoc 要求。PASS。
- II UUIDv7：不新增实体；objectKey 中 uuid 段用 `UuidV7Generator`。PASS。
- III 命名：契约对象与配置项命名通过 `objectKey` / `securityToken` / `expirationSeconds` 等完整命名。PASS。
- IV 双后端：admin / app 各自一份依赖；STS 类仅 admin。PASS。
- V 测试：所有 SDK 入口经接口注入，测试不依赖真实云资源。PASS。
- VI JPA Metamodel：本特性不动 Specification 代码。PASS。

**Re-check Result**: PASS — 不进入 Complexity Tracking。

下一阶段：`/speckit-tasks` 生成任务列表。

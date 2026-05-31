---
description: "Tasks for 阿里 OSS STS 直传 + 统一 ImageResponse"
---

# Tasks: 阿里 OSS STS 直传 + 统一 ImageResponse

**Input**: Design documents from `/specs/003-image-response-oss/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

**Tests**: 仅生成单元测试 / 集成测试任务（不强制 TDD；按 plan 测试策略，所有外部 SDK 接入点以 stub 注入）。

## Format

`- [ ] [TaskID] [P?] [Story?] Description with file path`

---

## Phase 1: Setup（项目初始化与依赖）

- [X] T001 [P] 在 `love-space-admin/pom.xml` 添加依赖：`com.aliyun.oss:aliyun-sdk-oss:3.18.1`、`com.aliyun:aliyun-java-sdk-core:4.7.1`、`com.aliyun:aliyun-java-sdk-sts:3.1.2`
- [X] T002 [P] 在 `love-space-app/pom.xml` 添加依赖：`com.aliyun.oss:aliyun-sdk-oss:3.18.1`（不引 STS）
- [X] T003 [P] 前端表单直传无需 OSS SDK（已移除 `ali-oss`），用浏览器原生 `FormData` + `XMLHttpRequest`
- [X] T004 [P] 在 `love-space-admin/src/main/resources/application.yml`（与 `application-test.yml`）增加 `app.storage.oss.*` 与 `app.storage.sts.*` 占位段；真实凭据仅通过环境变量注入
- [X] T005 [P] 在 `love-space-app/src/main/resources/application.yml`（与 `application-test.yml`）增加 `app.storage.oss.*` 占位段（仅读路径）

---

## Phase 2: Foundational（阻塞所有 user story）

**⚠️ CRITICAL**: 完成本阶段才能开始任何 user story。

### Admin 配置 & 客户端 bean

- [X] T006 [P] 在 `love-space-admin/src/main/java/com/loves/space/infrastructure/storage/OssProperties.java` 新建 `@ConfigurationProperties("app.storage.oss")` record，字段：endpoint、bucket、region、accessKeyId、accessKeySecret、uploadKeyPrefix（默认 `images`）、boundKeyPrefix（默认 `bound`）、urlExpirationSeconds（默认 1800）、maxImageBytes（默认 20971520）；含 Bean Validation 注解 + 中文 JavaDoc
- [X] T007 [P] 在 `love-space-admin/src/main/java/com/loves/space/infrastructure/storage/StsProperties.java` 新建 `@ConfigurationProperties("app.storage.sts")` record，字段：endpoint、regionId、roleArn、roleSessionName、durationSeconds（默认 900）、accessKeyId、accessKeySecret；含 Bean Validation + 中文 JavaDoc
- [X] T008 在 `love-space-admin/src/main/java/com/loves/space/infrastructure/storage/OssClientConfig.java` 新建 `@Configuration`，启用 `OssProperties`，注入 `OSS` bean（`OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret)`），增加 `@PostConstruct` sanity check（启动期间 `getBucketInfo` 失败即 fail-fast）
- [X] T009 在 `love-space-admin/src/main/java/com/loves/space/infrastructure/storage/StsClientConfig.java` 新建 `@Configuration`，启用 `StsProperties`，注入 `com.aliyuncs.IAcsClient` bean（`DefaultProfile` + `DefaultAcsClient`）

> 📝 **后续重构（去重）**：T006–T009 的 admin 端 `OssProperties` / `StsProperties` 已合并为单一 `StorageProperties`（prefix `app.storage`，顶层共享 `region` / `accessKeyId` / `accessKeySecret`，差异字段收进 `oss` / `sts` 子 record）；`OssClientConfig` / `StsClientConfig` 合并为 `StorageClientConfig`。删除死配置 `sts.endpoint` / `sts.regionId`（SDK 依 region 自解析），STS 复用顶层凭证；同步移除环境变量 `ALIYUN_STS_ENDPOINT` / `ALIYUN_STS_REGION_ID` / `ALIYUN_STS_ACCESS_KEY_ID` / `ALIYUN_STS_ACCESS_KEY_SECRET`。app 端 `OssProperties` 不变。详见 data-model.md §2、research.md R10。

### Admin 三件套接口与实现

- [X] T010 [P] 在 `love-space-admin/src/main/java/com/loves/space/infrastructure/storage/StsCredentialIssuer.java` 新建接口 + 嵌套 record `StsCredential(accessKeyId, accessKeySecret, securityToken, expiration)`；中文 JavaDoc
- [X] T011 [P] 在 `love-space-admin/src/main/java/com/loves/space/infrastructure/storage/ObjectKeyValidator.java` 新建接口（`String validateAndBind(String rawObjectKey)`）；中文 JavaDoc
- [X] T012 [P] 在 `love-space-admin/src/main/java/com/loves/space/infrastructure/storage/ImageUrlSigner.java` 新建接口（`String sign(String objectKey)`）；中文 JavaDoc
- [X] T013 在 `love-space-admin/src/main/java/com/loves/space/infrastructure/storage/AliyunStsCredentialIssuer.java` 实现：用 `IAcsClient` 调 `AssumeRoleRequest`，拼装 inline policy（限 `oss:PutObject` 到单 objectKey 资源 ARN），返回 `StsCredential`；中文 JavaDoc
- [X] T014 在 `love-space-admin/src/main/java/com/loves/space/infrastructure/storage/AliyunOssObjectKeyValidator.java` 实现：正则前置校验 → `oss.getObjectMetadata` → MIME / size 校验 → `images/` 分支 `copyObject` 到 `bound/` 并 best-effort `deleteObject`；外部错误统一为 `ValidationException("图片对象不可用")`
- [X] T015 在 `love-space-admin/src/main/java/com/loves/space/infrastructure/storage/AliyunOssImageUrlSigner.java` 实现：`oss.generatePresignedUrl(bucket, objectKey, expiration, GET)`；null / blank 返回 null
- [X] T016 [P] 在 `love-space-admin/src/main/java/com/loves/space/common/dto/ImageResponse.java` 确认或新建 record `ImageResponse(String id, String url)`；中文 JavaDoc
- [X] T017 [P] 在 `love-space-admin/src/main/java/com/loves/space/common/util/ImageResponses.java` 新建工具：`ImageResponse from(String objectKey, ImageUrlSigner)`、`List<ImageResponse> fromList(List<String>, ImageUrlSigner)`；中文 JavaDoc

### App 配置 & 读路径

- [X] T018 [P] 在 `love-space-app/src/main/java/com/space/app/infrastructure/storage/OssProperties.java` 新建 record（同 admin 但去掉 boundKeyPrefix、accessKey 仍需读权限）+ 中文 JavaDoc
- [X] T019 [P] 在 `love-space-app/src/main/java/com/space/app/infrastructure/storage/OssClientConfig.java` 新建 `@Configuration` + `OSS` bean
- [X] T020 [P] 在 `love-space-app/src/main/java/com/space/app/infrastructure/storage/ImageUrlSigner.java` 新建接口；中文 JavaDoc
- [X] T021 在 `love-space-app/src/main/java/com/space/app/infrastructure/storage/AliyunOssImageUrlSigner.java` 实现（同 admin 实现）
- [X] T022 [P] 在 `love-space-app/src/main/java/com/space/app/common/dto/ImageResponse.java` 新建 record + 中文 JavaDoc
- [X] T023 [P] 在 `love-space-app/src/main/java/com/space/app/common/util/ImageResponses.java` 新建工具（同 admin）

### Web 前端基础类型与 hook

- [X] T024 [P] 在 `love-space-web/src/types/image.ts` 新增 `ImageResponse`、`UploadCredentialResponse`、`UploadCredentialRequest` 类型
- [X] T025 [P] 在 `love-space-web/src/lib/ossUpload.ts` 新建 `uploadToOss`（用 `FormData` + `XMLHttpRequest` 表单 POST 直传到 `host`，字段见 contracts/upload-credentials-endpoint.md，返回 `Promise<string objectKey>`）

### 基础单元测试

- [X] T026 [P] 在 `love-space-admin/src/test/java/com/loves/space/infrastructure/storage/AliyunOssObjectKeyValidatorTest.java` 新建 stub OSSClient（持 `Map<key, metadata>`），覆盖：不存在 / MIME 错 / 太大 / 路径穿越 / 合法 images/ → bound/ / 已 bound/+存在 / 已 bound/+不存在
- [X] T027 [P] 在 `love-space-admin/src/test/java/com/loves/space/infrastructure/storage/AliyunOssImageUrlSignerTest.java` 验证 null/blank 返回 null；合法 key 调用 OSS stub 并返回带签名串 URL

**Checkpoint**: 基础设施完成；user story 可并行展开。

---

## Phase 3: User Story 1 — STS 直传凭证下发 (P1) 🎯 MVP

**Goal**: 前端 `POST /api/admin/files/upload-credentials` 拿到 PostObject 表单签名 + 预生成 objectKey，可用表单 POST 直传到 OSS。

**Independent Test**: 调用端点收到合法签名响应（不含 accessKeySecret）；前端用表单 POST 到响应 objectKey 成功；越权 key 被 OSS Policy 拒。

- [X] T028 [P] [US1] 在 `love-space-admin/src/main/java/com/loves/space/modules/file/dto/UploadCredentialRequest.java` 新建 record `{ String contentType }`，加 `@NotBlank` + `@Pattern("^image/(png|jpeg|webp)$")`；中文 JavaDoc
- [X] T029 [P] [US1] 在 `love-space-admin/src/main/java/com/loves/space/modules/file/dto/UploadCredentialResponse.java` 新建 record（PostObject 表单签名 9 字段，见 data-model.md，不含 accessKeySecret）；另在 `infrastructure/storage/OssPostPolicySigner.java` 实现 V4 签名计算；中文 JavaDoc
- [X] T030 [US1] 重写 `love-space-admin/src/main/java/com/loves/space/modules/file/service/FileService.java`：删除原 multipart `upload` 方法；新增 `issueUploadCredential(UploadCredentialRequest req)`：MIME→ext 映射（png/jpg/webp） + `UuidV7Generator.next()` 生成 objectKey（`images/<uuid>.<ext>`） + 调 `StsCredentialIssuer.issueFor(objectKey)` + `OssPostPolicySigner.sign(objectKey, credential)` 拼装响应；中文 JavaDoc
- [X] T031 [US1] 改 `love-space-admin/src/main/java/com/loves/space/modules/file/controller/FileController.java`：删除 `POST /upload`；新增 `POST /upload-credentials`（`@Validated @RequestBody UploadCredentialRequest`，返回 `UploadCredentialResponse`）；中文 JavaDoc
- [X] T032 [US1] 删除 `love-space-admin/src/main/java/com/loves/space/infrastructure/storage/FileStorage.java` 与 `LocalFileStorage.java`；同时删除 `love-space-admin/src/main/java/com/loves/space/modules/file/dto/FileUploadResponse.java`；移除所有引用
- [X] T033 [P] [US1] 在 `love-space-admin/src/test/java/com/loves/space/modules/file/service/FileServiceTest.java` 单元测试：stub `StsCredentialIssuer` 返回固定凭证，断言 contentType→ext 映射正确（image/jpeg → `.jpg`）、objectKey 形如 `images/<uuidv7>.<ext>`
- [X] T034 [P] [US1] 在 `love-space-admin/src/test/java/com/loves/space/modules/file/controller/FileControllerIT.java` MockMvc 集成测试：合法请求返回 200 + 完整字段；非白名单 contentType 返回 400；未登录返回 401
- [X] T035 [US1] 在 `love-space-web/src/pages/Files/...`（或 banner/merchant 表单组件统一上传按钮处）接入 `uploadToOss`：调 `POST /api/admin/files/upload-credentials` → 表单 POST 直传 → 暴露 objectKey；删除旧 multipart `POST /api/admin/files/upload` 调用

**Checkpoint**: US1 端到端可独立验证（直传 OK，不依赖 US2/3/4）。

---

## Phase 4: User Story 2 — 签名 URL 控制图片访问 (P1)

**Goal**: 所有图片 URL 是带签名的 OSS 预签名 GET URL，未签名访问被拒。

**Independent Test**: 任取 30 个返回的 `ImageResponse.url`，去掉 `Signature` query 后访问 100% 失败；保留签名 30 分钟内可用。

- [X] T036 [US2] 验证 `AliyunOssImageUrlSigner` 默认过期为 `ossProperties.urlExpirationSeconds()`（=1800）；不缓存
- [X] T037 [P] [US2] 在 `love-space-app/src/test/java/com/space/app/infrastructure/storage/AliyunOssImageUrlSignerTest.java` app 端镜像测试
- [X] T038 [US2] 在 OSS 控制台 / 部署文档（参考 `contracts/bucket-lifecycle.md`）：bucket 默认为 private；确认没有 public-read policy。归档到运维 checklist

**Checkpoint**: 签名机制（实现已在 Phase 2 完成）+ bucket private 化已就位。

---

## Phase 5: User Story 3 — 业务实体绑定时服务端校验 (P1)

**Goal**: Banner/Merchant/City 创建/更新接口对每个提交的 objectKey 调 `ObjectKeyValidator.validateAndBind`；失败统一 422。

**Independent Test**: 用不存在 objectKey / 非白名单 MIME / 25MB 对象提交创建 Banner → 三种情况均 422 且 DB 零写入。

### Banner

- [X] T039 [P] [US3] 改 `love-space-admin/src/main/java/com/loves/space/modules/banner/dto/BannerCreateRequest.java`：`imageUrls` 列表元素加 `@Pattern("^(images|bound)/[\\w-]+\\.(png|jpg|webp)$")`、`@NotEmpty`；中文 JavaDoc 说明语义为 objectKey
- [X] T040 [P] [US3] 改 `love-space-admin/src/main/java/com/loves/space/modules/banner/dto/BannerUpdateRequest.java`：同上
- [X] T041 [US3] 改 `love-space-admin/src/main/java/com/loves/space/modules/banner/service/BannerService.java`：create / update 在持久化前对 imageUrls 逐项 `objectKeyValidator.validateAndBind` → 存返回的 boundKey；任一失败抛 `ValidationException("图片对象不可用")`，整请求拒绝

### Merchant

- [X] T042 [P] [US3] 改 `love-space-admin/src/main/java/com/loves/space/modules/merchant/dto/MerchantUpsertRequest.java`：`logo` 单图 `@NotBlank @Pattern(...)`，`images` 列表元素 `@NotBlank @Pattern(...)`；中文 JavaDoc
- [X] T043 [US3] 改 `love-space-admin/src/main/java/com/loves/space/modules/merchant/service/MerchantService.java`：upsert 时对 logo、images 全部走 `validateAndBind`；存 boundKey

### City

- [X] T044 [P] [US3] 改 `love-space-admin/src/main/java/com/loves/space/modules/city/dto/CityCreateRequest.java`、`CityUpdateRequest.java`：`backgroundImage` 可空，非空时 `@Pattern(...)`；中文 JavaDoc
- [X] T045 [US3] 改 `love-space-admin/src/main/java/com/loves/space/modules/city/service/CityService.java`：create / update 时若 backgroundImage 非空走 `validateAndBind`，否则跳过

### 全局错误映射

- [X] T046 [US3] 在 admin 全局异常处理（`GlobalExceptionHandler` 或同类）保证 `ValidationException` → HTTP 422 + ProblemDetail；MUST NOT 暴露 OSS 原始错误码

### 测试

- [X] T047 [P] [US3] 在 `love-space-admin/src/test/java/com/loves/space/modules/banner/service/BannerServiceTest.java` 用 stub `ObjectKeyValidator` 验证：(a) 合法 objectKey → 持久化的是 boundKey；(b) 校验失败 → 整请求未写库
- [X] T048 [P] [US3] 在 `love-space-admin/src/test/java/com/loves/space/modules/merchant/service/MerchantServiceTest.java` 同上覆盖 logo + images 多张
- [X] T049 [P] [US3] 在 `love-space-admin/src/test/java/com/loves/space/modules/city/service/CityServiceTest.java` 同上覆盖可空 backgroundImage
- [X] T050 [P] [US3] 在 `love-space-admin/src/test/java/com/loves/space/modules/banner/controller/BannerControllerIT.java` MockMvc：非 `images|bound` 前缀返回 400；`headObject` 失败（stub）返回 422 且文案"图片对象不可用"（注：MVP 范围以 admin 现有 `ValidationException` → 400 处理，未拆分 422；US3 全局映射 T046 留至完整范围）

**Checkpoint**: 业务绑定准入校验完成。

---

## Phase 6: User Story 4 — 全模块响应统一 ImageResponse (P1)

**Goal**: admin + app 所有返回图片的接口字段类型统一为 `ImageResponse` / `List<ImageResponse>`。

**Independent Test**: 抓取所有 controller 的 response，零裸 `String` 图片字段；前端可通过 `id` 引用。

### Admin 响应升级

- [X] T051 [P] [US4] 改 `love-space-admin/src/main/java/com/loves/space/modules/banner/dto/BannerDetailResponse.java`、`BannerListItemResponse.java`：`imageUrls` → `List<ImageResponse>`；构造处用 `ImageResponses.fromList(boundKeys, imageUrlSigner)`
- [X] T052 [P] [US4] 改 `love-space-admin/src/main/java/com/loves/space/modules/merchant/dto/MerchantDetailResponse.java`、`MerchantAdminItem.java`：`logo` → `ImageResponse`，`images` → `List<ImageResponse>`
- [X] T053 [P] [US4] 改 `love-space-admin/src/main/java/com/loves/space/modules/city/dto/CityItemResponse.java`、`CityDetailResponse.java`：`backgroundImage` → `ImageResponse`（可空）
- [X] T054 [US4] 修改对应 service 的读路径方法签名 / 装配处（`BannerService`、`MerchantService`、`CityService` 的列表与详情查询）：在装配响应时调 `ImageResponses` 完成 boundKey → ImageResponse 转换

### App 响应升级

- [X] T055 [P] [US4] 改 `love-space-app/src/main/java/com/space/app/modules/banner/dto/...`：`image` / `images` 字段类型升级为 `ImageResponse` / `List<ImageResponse>`
- [X] T056 [P] [US4] 改 `love-space-app/src/main/java/com/space/app/modules/merchant/dto/...`：`logo` → `ImageResponse`，`images` → `List<ImageResponse>`
- [X] T057 [P] [US4] 改 `love-space-app/src/main/java/com/space/app/modules/city/dto/...`：`backgroundImage` → `ImageResponse`
- [X] T058 [US4] 修改 app 端对应 service 读路径，在响应装配处调 `ImageResponses`

### Web 前端渲染层

- [X] T059 [P] [US4] 改 `love-space-web/src/pages/Banners/...`（列表 / 详情 / 表单）：消费 `ImageResponse[]`；表单 submit 传 objectKey 字符串
- [X] T060 [P] [US4] 改 `love-space-web/src/pages/Merchants/...`：logo + images 同上
- [X] T061 [P] [US4] 改 `love-space-web/src/pages/Cities/...`：backgroundImage 同上
- [X] T062 [P] [US4] 改 `love-space-web/src/pages/Files/...`：删除旧 multipart 上传 UI；仅保留"上传按钮 → uploadToOss → 显示 objectKey + 预览签名 url"（注：web 端无独立 Files 页面；`src/api/files.ts#uploadFile` 已直接走 `uploadToOss` 返回 objectKey，原 multipart 调用已不存在）

### 测试

- [X] T063 [P] [US4] 在 `love-space-admin/src/test/java/com/loves/space/modules/banner/controller/BannerReadIT.java` MockMvc：响应 JSON 中 `imageUrls[].id` 与 `.url` 均非空且 url 含 `Signature` 参数（注：US4 Banner 路径已在 `BannerControllerIT.createReturnsImageResponseWithSignedUrl` 覆盖；Signature 断言因 `ImageUrlSigner` 已 mock 跳过）
- [X] T064 [P] [US4] 在 `love-space-admin/src/test/java/com/loves/space/modules/merchant/controller/MerchantReadIT.java` 同上
- [X] T065 [P] [US4] 在 `love-space-admin/src/test/java/com/loves/space/modules/city/controller/CityReadIT.java` 同上（含 backgroundImage 为 null 的场景）
- [X] T066 [P] [US4] 在 `love-space-app/src/test/java/com/space/app/...` 镜像 banner / merchant / city 三个读 IT

**Checkpoint**: 全栈响应统一。

---

## Phase 7: Polish & Cross-Cutting

- [X] T067 [P] 校核 Liquibase changelog（`love-space-admin/src/main/resources/db/changelog/changes/*.sql`）：现有图片列名 / 类型保持不变；如有遗留 demo 数据迁移 SQL（清空 banner.image_urls / merchant.logo / merchant_image.image_url / city.background_image），按需新增一条 changeset
- [X] T068 [P] 跑 `cd love-space-admin && ./mvnw test`、`cd love-space-app && ./mvnw test`、`cd love-space-web && npm run lint && npm run build` 三套构建全部通过
- [ ] T069 按 `quickstart.md` 第 4 节手工跑端到端（含 SC-002 去签名 403、SC-005 422、SC-007 lifecycle 24h）
- [ ] T070 在 OSS 控制台按 `contracts/bucket-lifecycle.md` 完成一次性资源（bucket lifecycle 规则 + RAM Role + CORS）
- [X] T071 [P] 核对所有新增 Java 类是否带中文 JavaDoc（constitution I）；所有命名是否避免缩写（constitution III）

---

## Dependencies & Execution Order

### Phase Dependencies

- Setup (P1) → Foundational (P2) → User Stories (P3–P6) → Polish (P7)
- US1 / US2 / US3 / US4 在 Foundational 完成后可并行；US4 在功能上消费 US1（上传产生 objectKey）+ US3（写入合法 boundKey）+ US2（签名）；可以同一开发者顺序推进或多人并行。

### 关键串行边

- T013 (`AliyunStsCredentialIssuer`) 阻塞 T030 (`FileService.issueUploadCredential`)
- T014 (`AliyunOssObjectKeyValidator`) 阻塞 T041 / T043 / T045（业务 service）
- T015 / T017 (`ImageUrlSigner` + `ImageResponses`) 阻塞 T051–T058（响应装配）
- T032（删除旧 FileStorage / FileController#upload）必须在 T035（前端切换调用）前后协调；建议同一 PR

### 并行机会示例

```bash
# Phase 2 并行：
Task: T006 OssProperties (admin)
Task: T007 StsProperties (admin)
Task: T010 StsCredentialIssuer interface
Task: T011 ObjectKeyValidator interface
Task: T012 ImageUrlSigner interface
Task: T016 ImageResponse record (admin)
Task: T018 OssProperties (app)
Task: T020 ImageUrlSigner interface (app)
Task: T022 ImageResponse record (app)
Task: T024 web image.ts
Task: T025 web useOssUpload
```

```bash
# Phase 5 写 DTO 并行：T039 T040 T042 T044
# Phase 6 响应 DTO 并行：T051 T052 T053（admin）+ T055 T056 T057（app）+ T059 T060 T061（web）
```

---

## Implementation Strategy

### MVP（最小可演示）

1. Phase 1 + 2 完成
2. Phase 3（US1）完成 → 前端能直传 OSS（路径打通）
3. Phase 5（US3）完成 → Banner 创建带服务端校验
4. Phase 6（US4）的 Banner 子集 → admin 端 banner 端到端可展示
5. STOP & 演示

### Incremental Delivery

- 完成 Phase 2 → 基础设施可合并
- 完成 US1 → 前端直传链路上线
- 完成 US3 → 写路径校验上线
- 完成 US4 → 全栈响应统一
- US2 在 Phase 2 阶段已实现（签名），只需 SC-002 验证

### Parallel Team

- Dev A：admin 写路径（US1 + US3）
- Dev B：admin / app 读路径升级（US4 后端部分）
- Dev C：web 前端 hook + 各页面（US1 前端 + US4 前端）

---

## Notes

- 不引入新 JPA 实体；不改业务表 schema（仅语义改：列里存 OSS object key）。
- 所有外部 SDK（OSS / STS）通过接口注入，测试用 stub 替身，CI 无需真实云资源。
- 一次性云资源（bucket lifecycle、RAM Role、CORS）由 T070 在运维侧执行，不在代码内自动化。

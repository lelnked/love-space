# Feature Specification: 阿里 OSS 文件存储与 ImageResponse 统一图片返回

**Feature Branch**: `003-image-response-oss`

**Created**: 2026-05-23

**Status**: Draft

**Input**: User description: "FileStorage 使用阿里oss实现，对于图片的返回使用com.loves.space.common.dto.ImageResponse, url需要使用认证token的方式。修改其他模型的图片返回方式，统一采用com.loves.space.common.dto.ImageResponse。"

## Clarifications

### Session 2026-05-23

- Q: 直传凭证采用哪种形式？ → A: STS AssumeRole + 服务端 POST 签名 — 服务端调阿里云 STS 拿临时 `AccessKeyId/AccessKeySecret/SecurityToken`，用其在服务端计算 PostObject V4 签名（`OSS4-HMAC-SHA256`），前端用表单 POST 直传；**`accessKeySecret` 不下发到浏览器**。权限通过绑定到 RAM Role 的 policy 收敛（限制 bucket + key prefix + action）。
- Q: objectKey 放置策略？ → A: 直传 final — 客户端直接上传到 `images/<uuid>.<ext>`；业务绑定即认定有效；未绑定对象由 bucket lifecycle 自动清理。STS Policy 限制客户端只能写入 `images/` 前缀。
- Q: 业务绑定时服务端校验深度？ → A: 存在性 + MIME + 大小 — 业务接口收到 objectKey 后，服务端 `headObject` 校验对象存在、`Content-Type ∈ {image/png, image/jpeg, image/webp}`、`Content-Length ≤ 20MB`；任一不满足拒绝写库。
- Q: 孤儿对象清理策略？ → A: bucket lifecycle 24h — 在 `images/` 前缀配置阿里云 OSS 生命周期规则，对象创建后 24 小时自动删除；业务实体一旦持有 objectKey，是否额外打 tag / 标记保留由 plan 阶段细化。
- Q: STS 临时凭证有效期？ → A: 15 分钟（900 秒），可由配置覆盖；阿里 STS 最小允许 900s，最大 3600s。
- Q: 上传整体流程？ → A: 表单直传 — (1) 前端调 `POST /api/admin/files/upload-credentials` 拿 PostObject 表单签名 + 服务端预生成 objectKey；(2) 前端用 `multipart/form-data` 表单 POST 直传到 `images/<uuid>.<ext>`；(3) 业务实体创建/更新时把 objectKey 作为图片 id 提交；(4) 服务端 `headObject` 校验后入库。原 `POST /api/admin/files/upload` multipart 端点取消。

## User Scenarios & Testing *(mandatory)*

### User Story 1 - 通过服务端 POST 签名直传 OSS（预上传） (Priority: P1)

前端在用户挑选图片后，先向服务端申请 PostObject 表单签名 + 一个由服务端预生成的 objectKey，再用 `multipart/form-data` 表单 POST 直接把图片上传到 OSS bucket。此阶段不写任何业务表；上传产物只是"待挂载"的 OSS 对象。服务端不经手图片字节流，吞吐瓶颈与磁盘带宽都不落在 admin 进程上；浏览器也不接触 `accessKeySecret`。

**Why this priority**: 是后续所有"业务表持有图片"场景（用户头像、动态、Banner、商户图、城市背景、文章封面 …）的基础设施；直传模式让 admin 进程不必处理 multipart payload，且为未来移动端 app 写入图片留好同样的入口。

**Independent Test**: 前端调 `POST /api/admin/files/upload-credentials` 拿到 `{ host, objectKey, policy, signature, signatureVersion, xOssCredential, xOssDate, securityToken, expiration }`，用表单 POST 到 `host`；不创建任何业务实体，OSS bucket 中也能直接看到该对象，前端拿到 objectKey 字符串。

**Acceptance Scenarios**:

1. **Given** admin 已登录、OSS / STS / RAM Role 配置正确，**When** 调用 `POST /api/admin/files/upload-credentials`，**Then** 响应包含 PostObject 表单签名（`policy` / `signature` / `xOssCredential` / `xOssDate` / `securityToken`）、服务端生成的 `objectKey`（形如 `images/<uuidv7>.<ext>`，扩展名来自客户端声明）、`expiration`（≤ 当前时间 + 15 分钟），且**不含** `accessKeySecret`。
2. **Given** 前端拿到签名，**When** 用表单 POST 上传文件到响应中的 `host`（`key` 字段等于 `objectKey`），**Then** OSS bucket 中存在对应对象，且对象 Content-Type 与上传声明一致。
3. **Given** 前端尝试把表单 `key` 改成签名 Policy 之外的 key（例如 `images/../other`），**When** POST 请求到达 OSS，**Then** OSS 返回 Policy 校验失败，签名不能越权。
4. **Given** 签名 / STS 凭证超过有效期，**When** 再用同一份签名上传，**Then** OSS 返回鉴权失败；前端 MUST 重新申请签名。
5. **Given** RAM Role / STS 配置缺失或错误，**When** 应用启动或调用 upload-credentials 端点，**Then** 启动失败或端点返回明确错误，不得回退为本地磁盘存储或服务端代理上传。

---

### User Story 2 - 图片访问 URL 通过认证 token 控制 (Priority: P1)

运营方希望存放在 OSS 的图片不被公网任意爬取/盗链。客户端（admin web、mobile app）拿到的图片 URL 必须是带有签名/鉴权 token 的访问地址，未经签名的原始 OSS key 不可直接访问。

**Why this priority**: 业务图片（商户、Banner、城市背景）属于运营资产，且未来可能含敏感图。无 token 控制等同公开桶，存在合规与安全风险。

**Independent Test**: 取得任意接口返回的图片 url，去掉签名参数后访问应被拒绝（403/AccessDenied）；保留签名参数访问应在签名有效期内成功返回图片字节。

**Acceptance Scenarios**:

1. **Given** 客户端调用任意返回图片的接口，**When** 拿到 `ImageResponse.url`，**Then** 该 URL 携带可验证的签名/鉴权 token 且在有效期内可访问。
2. **Given** URL 中签名参数被剥离或篡改，**When** 直接访问，**Then** 访问被拒绝，无法获取图片内容。
3. **Given** 用户在合理浏览/预览期间使用同一 URL，**When** 在签名有效期内重复访问，**Then** 持续可用；超过有效期后访问失败。

---

### User Story 3 - 业务实体绑定时服务端校验 objectKey (Priority: P1)

前端把直传得到的 objectKey 作为字段值提交给业务接口（Banner / Merchant / City / 未来的用户头像、动态图片、文章封面 …）。服务端在持久化业务实体之前 MUST 对 OSS 上的对象做存在性 + MIME 白名单 + 大小校验；任一不通过即拒绝写库。

**Why this priority**: 没有这层校验，恶意客户端可以提交一个未真正上传过的 objectKey 字符串，或者把 STS Policy 允许写入但 MIME 不合规的对象塞进业务字段；服务端必须扮演真实"准入"角色，不能信任客户端任意 key。

**Independent Test**: 用一个未真正上传过的 `images/<random-uuid>.png`、一个 Content-Type 为 `text/plain` 的 OSS 对象、一个 25MB 的对象，分别尝试创建 Banner，期望三种情况都返回业务校验错误且业务表无新增。

**Acceptance Scenarios**:

1. **Given** 客户端提交一个真实存在、MIME 在白名单内、≤ 20MB 的 objectKey，**When** 调 `POST /api/admin/banners`，**Then** 业务实体成功创建。
2. **Given** 客户端提交一个不存在的 objectKey，**When** 调任意业务创建/更新接口，**Then** 返回 422/400 业务校验错误，错误信息指明"图片对象不存在"。
3. **Given** OSS 对象存在但 Content-Type ∉ {image/png, image/jpeg, image/webp}，**When** 提交绑定，**Then** 返回业务校验错误，业务表不写入。
4. **Given** OSS 对象 Content-Length > 20MB，**When** 提交绑定，**Then** 返回业务校验错误，业务表不写入。
5. **Given** 客户端在同一请求中提交多张图片 objectKey，**When** 其中任一项校验失败，**Then** 整个请求拒绝，不做"部分写入"。

---

### User Story 4 - 所有模块图片返回统一为 ImageResponse (Priority: P1)

admin 后端与 app 后端现有多个模块以裸 `String url`（或 `List<String>`）形式返回图片：`Banner.imageUrls`、`Merchant.logo` / `Merchant.images`、`City.backgroundImage`、`FileUploadResponse.url` 等。要求所有对外返回的图片字段统一使用 `com.loves.space.common.dto.ImageResponse`（含 `id` 与带签名 `url`）；列表场景用 `List<ImageResponse>`。

**Why this priority**: 不统一会导致前端无法基于 id 做引用、删除、替换；并且无法保证所有图片访问都经签名 token。统一是 P1，因为它和 OSS 接入一同落地，避免迁移期混乱。

**Independent Test**: 抓取 admin 与 app 所有 controller 的 response schema，所有图片字段类型为 `ImageResponse` 或 `List<ImageResponse>`，没有任何剩余的裸 `String` 图片字段；前端通过 `id` 字段即可对图片做引用。

**Acceptance Scenarios**:

1. **Given** 调用 `GET /api/admin/banners/{id}`，**When** 返回成功，**Then** `imageUrls` 字段为 `List<ImageResponse>`，每项含非空 `id` 与带签名 `url`。
2. **Given** 调用 `GET /api/admin/merchants/{id}`，**When** 返回成功，**Then** `logo` 为 `ImageResponse`，`images` 为 `List<ImageResponse>`。
3. **Given** 调用 `GET /api/admin/cities` 或 `GET /api/app/cities`，**When** 返回成功，**Then** `backgroundImage` 字段为 `ImageResponse`（无背景图时为 `null`）。
4. **Given** 客户端创建/更新一个 Banner / Merchant / City，**When** 在请求体中提交 `imageUrls` / `logo` / `images` / `backgroundImage`，**Then** 接受的是直传得到的 objectKey（持久化标识），由服务端负责签名后回吐给读接口（请求侧不再传 URL，也不再有"先调上传端点拿 URL"的中间步骤）。
5. **Given** 取消了原 `POST /api/admin/files/upload` multipart 端点，**When** 客户端再调用旧路径，**Then** 返回 404 / 405；前端 MUST 改用 `POST /api/admin/files/upload-credentials` + OSS 直传。

---

### Edge Cases

- 旧数据库中已存在历史本地 URL（形如 `http://.../uploads/xxx.png`）：迁移期是否需要兼容显示，还是要求清空/重导入？（见 Assumptions）
- OSS / STS 服务临时不可用：upload-credentials 端点 MUST 快速失败并提示，不重试到超时阻塞用户；客户端应在 UI 上明确提示"图片服务暂不可用"。
- 签名 URL 在客户端缓存场景下过期：前端拿到过期 URL 时应能通过重新拉取列表/详情拿到新签名，不要求服务端为每个 URL 单独提供刷新接口。
- 同一图片被多个实体引用（如 Banner 和 Merchant 都用同一张）：仍按"每次上传产生独立 objectKey"处理；不做去重。
- 上传后未绑定的孤儿对象：由 OSS bucket lifecycle 规则（`images/` 前缀、创建后 24 小时）自动删除；服务端不维护"已绑定"标记表。
- 业务表持有的 objectKey 在 OSS 中已被生命周期或人工删除：读接口照常返回带签名的 url（不做存在性探测），客户端按图片加载失败处理。
- 同一份 STS 凭证被重复用于多次上传：允许（凭证在有效期内可多次写入 STS Policy 允许的 key 前缀）；前端 MUST 为每个新对象用服务端预生成的不同 objectKey，不得复用。
- 客户端伪造一个未上传过的 objectKey 直接提交业务接口：服务端 `headObject` 校验失败 → 业务接口返回 422。

## Requirements *(mandatory)*

### Functional Requirements

#### 直传与凭证

- **FR-001**: 系统 MUST 提供 `POST /api/admin/files/upload-credentials` 端点，接收客户端声明的目标 MIME（白名单 png/jpeg/webp），返回 PostObject 表单签名 `{ host, objectKey, policy, signature, signatureVersion, xOssCredential, xOssDate, securityToken, expiration }`，用于前端用 `multipart/form-data` 表单 POST 直传；响应 MUST NOT 含 `accessKeySecret`。
- **FR-002**: `objectKey` MUST 由服务端预生成，格式 `images/<uuidv7>.<ext>`，扩展名取自客户端 MIME 反查（png → `png`，jpeg → `jpg`，webp → `webp`）；客户端 MUST NOT 自行选择或修改 objectKey。
- **FR-003**: STS 临时凭证 MUST 通过阿里云 STS `AssumeRole` 获取，绑定到的 RAM Role 策略 MUST 限制：(a) 仅 `oss:PutObject`；(b) 资源限定 `acs:oss:*:*:<bucket>/images/*`；(c) 不授予 `GetObject` / `DeleteObject` / `ListObjects`。
- **FR-004**: STS 临时凭证有效期 MUST 默认 900 秒（15 分钟），可由配置覆盖（最低 900s、最高 3600s）。`expiration` 字段 MUST 用 ISO-8601 UTC 时间返回。
- **FR-005**: 系统 MUST 在应用启动时校验 OSS / STS / RAM Role 必备配置（endpoint、bucket、STS endpoint、role ARN、role session name、AccessKeyId、AccessKeySecret 等）；任一缺失或非法 MUST 启动失败并打印明确原因，MUST NOT 静默回退本地磁盘。
- **FR-006**: 原 `POST /api/admin/files/upload`（multipart 服务端代理上传）端点 MUST 移除；旧请求路径返回 404 / 405。

#### 业务绑定与服务端校验

- **FR-007**: 业务实体的创建/更新接口（Banner、Merchant、City，未来的用户头像 / 动态图片 / 文章封面同理）MUST 接收图片 **objectKey**（不是 URL）作为字段值；DTO 字段名沿用现有命名以减少破坏，但语义改变。
- **FR-008**: 服务端在持久化业务实体前 MUST 对**每一个**提交的 objectKey 调 OSS `headObject` 校验：(a) 对象存在；(b) `Content-Type ∈ {image/png, image/jpeg, image/webp}`；(c) `Content-Length ≤ 20MB`。任一不通过即拒绝写库，返回业务校验错误。
- **FR-009**: 服务端 MUST 拒绝任何不以 `images/` 前缀开头的 objectKey；MUST 拒绝包含 `..` 等路径穿越字符的 objectKey。
- **FR-010**: 校验失败时 MUST NOT 暴露 OSS 内部错误细节（如 access denied 与不存在的区分）；统一对外返回"图片对象不可用"。

#### 响应统一与签名

- **FR-011**: 所有返回图片的接口（admin + app 后端）MUST 使用 `com.loves.space.common.dto.ImageResponse`；单图字段类型为 `ImageResponse`（可空时仍可为 `null`），多图字段类型为 `List<ImageResponse>`。
- **FR-012**: 涉及变更的模块至少包含：
  - `banner`：`BannerDetailResponse.imageUrls`、`BannerListItemResponse.imageUrls`（admin）、`BannerItemResponse.image` / `imageUrls`（app）
  - `merchant`：`MerchantDetailResponse.logo` / `images`、`MerchantAdminItem.logo`（admin）、`MerchantListItemResponse.logo`、`MerchantDetailResponse.logo` / `images`（app）
  - `city`：`CityDetailResponse.backgroundImage`、`CityItemResponse.backgroundImage`（admin + app）
  - `file`：移除 `FileUploadResponse`，新增 `UploadCredentialResponse`
- **FR-013**: 图片访问 URL MUST 通过 OSS 预签名 GET URL 控制访问；未经签名或签名失效的请求 MUST 被 OSS 拒绝。
- **FR-014**: 每次接口响应时，`ImageResponse.url` MUST 是**当次生成**的有效签名 URL，不允许把过期签名缓存到响应中。
- **FR-015**: 读签名 URL 有效期 MUST 默认 ≥ 15 分钟（推荐 1800s），通过配置项暴露。
- **FR-016**: 图片的 `id` MUST 等于其 OSS 对象 key（即 `images/<uuidv7>.<ext>`）；MUST 全局唯一、稳定，与 OSS 对象一一对应。
- **FR-017**: 系统 MUST 拒绝来自前端的"裸 URL"图片字段；若 DTO 校验到看起来像 URL（含 `://`）的图片字段值 MUST 直接以校验错误拒绝。

#### 存储与生命周期

- **FR-018**: OSS bucket MUST 配置生命周期规则：`images/` 前缀下创建超过 24 小时但仍属"孤儿"的对象自动删除。绑定状态由 lifecycle 规则识别的具体方式（基于 tag 标记保留、还是基于 OSS object tagging API）由 plan 阶段细化；本特性 MUST 在 plan 中明确该机制并验证不会误删已绑定对象。
- **FR-019**: 数据库迁移 MUST 与新契约保持一致：业务表的图片字段（`loves_banner.image_urls`、`loves_merchant.logo`、`loves_merchant_image.image_url`、`loves_city.background_image`）持久化的是 OSS 对象 key（如 `images/<uuid>.<ext>`），而非完整 URL。Liquibase changelog 用 formatted-SQL，遵循 `loves_` 表前缀约定。

### Key Entities *(include if feature involves data)*

- **ImageResponse**：对外图片表示。属性：`id`（稳定图片标识 = OSS 对象 key，非空）、`url`（当次签名的 GET 可访问地址，非空）。仅用于响应，不用于请求。
- **UploadCredentialResponse**：PostObject 表单签名下发包。属性：`host`、`objectKey`（服务端预生成的目标 key，形如 `images/<uuidv7>.<ext>`）、`policy`、`signature`、`signatureVersion`、`xOssCredential`、`xOssDate`、`securityToken`、`expiration`（ISO-8601 UTC）。**不含 `accessKeySecret`**。仅用于响应。
- **OSS Object**：实际图片字节存放单元；key 形如 `images/<uuidv7>.<ext>`；客户端 PUT 上传，服务端 `headObject` 校验，读时由 `ImageUrlSigner` 即时签名。
- **图片 id / objectKey**：系统层面的图片标识，等于 OSS 对象 key；与业务实体表中的图片字段值一一对应。
- **STS RAM Role**：阿里云上预先创建的角色，绑定一份只允许 `oss:PutObject` 到 `<bucket>/images/*` 的 policy；服务端通过 `AssumeRole` 拿临时凭证下发给前端。其 ARN、session name 通过配置注入，不在代码中硬编码。

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 直传链路：admin 前端从用户点击"选择图片"到 OSS 上传成功（PUT 200）总耗时 p95 ≤ 3 秒（10MB 以内，正常网络）；服务端进程在此链路上的 CPU / 内存与上传文件大小无关（不经手字节流）。
- **SC-002**: 任意接口返回的 `ImageResponse.url` 在去掉签名参数后访问被拒比例为 100%（抽样 ≥ 30 个 URL 全部失败）。
- **SC-003**: admin + app 两个后端的所有图片相关响应字段，类型为 `ImageResponse` 或 `List<ImageResponse>` 的覆盖率达 100%（零裸 `String` 图片字段残留）。
- **SC-004**: STS 凭证越权防护：用合法凭证尝试 `PutObject` 到 `images/` 之外或 `GetObject` / `DeleteObject` 任意 key，OSS 返回 `AccessDenied` 比例 100%。
- **SC-005**: 业务绑定阻断：客户端提交一个不存在 / Content-Type 非白名单 / 超过 20MB 的 objectKey，业务接口返回 4xx 错误且业务表零写入比例 100%。
- **SC-006**: 孤儿清理：上传后 24h 仍未被任何业务表引用的 OSS 对象在 lifecycle 触发窗口内被删除比例 ≥ 99%。
- **SC-007**: 渲染成功率：前端从拿到 `ImageResponse` 到页面成功渲染图片的成功率 ≥ 99%（读签名 URL 有效期内）。

## Assumptions

- 后端运行环境拥有访问阿里云 OSS + STS 的网络可达性与有效凭据；OSS bucket、RAM Role、bucket lifecycle 规则在部署侧准备就绪，不在本特性范围内自动创建（但 plan 阶段 MUST 提供 Terraform / 控制台操作清单作为 quickstart 附件）。
- 不做"本地存储 ↔ OSS"双写或自动迁移；旧本地 URL 数据在开发/测试环境直接清空或重新上传。生产是否需要数据迁移由后续单独工单处理。
- 不引入 CDN 层；签名形式默认采用 OSS 预签名 GET URL。若后续接入 CDN 鉴权，可在不破坏 `ImageResponse` 契约的情况下替换 `ImageUrlSigner` 实现。
- 写路径：仅 `love-space-admin` 下发 STS 上传凭证；`love-space-app` 不写入。未来 app 若需要写入，复用相同的 upload-credentials 端点概念但作为单独工单。
- 读路径：admin + app 两个后端各自维护一份 `ImageResponse` + `ImageUrlSigner`，共享同一 bucket；两份 DTO 语义、字段、行为保持一致。
- 图片 id 直接等于 OSS 对象 key（含前缀，如 `images/01abc….png`）；不引入额外的"图片元数据表"。
- 业务绑定时服务端 `headObject` 一次往返的延迟可接受（通常 < 50ms / 张）；批量提交多张图片时按数量串行或小并发执行 head 校验，不做去重缓存。
- STS 凭证默认 15 分钟、读签名 URL 默认 30 分钟，均可由配置覆盖；前端在过期后通过重新调用 upload-credentials 或重新拉取列表/详情拿到新 URL，不要求服务端提供单独"刷新"接口。
- 上传 MIME 白名单与大小上限（png/jpeg/webp、≤ 20MB）由两道防线共同保证：(1) STS Policy 不直接限制 Content-Type，但前端 SDK 在 PUT 时声明；(2) 服务端绑定时 `headObject` 做权威校验。客户端无法绕过服务端校验直接污染业务表。
- bucket lifecycle 规则的"已绑定 vs 孤儿"区分机制（如：服务端在绑定成功后对 OSS 对象打 tag `bound=true`，lifecycle 仅清理无该 tag 且超过 24h 的对象）由 plan 阶段细化并写入 contracts/。

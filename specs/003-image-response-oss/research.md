# Research: STS 直传 OSS + 统一 ImageResponse

## R1. 阿里云 STS Java SDK 版本

**Decision**: `com.aliyun:aliyun-java-sdk-core:4.7.1` + `com.aliyun:aliyun-java-sdk-sts:3.1.2`（仅 admin 后端）。

**Rationale**:
- aliyun-java-sdk-core 4.7.x 与 JDK 17+ 充分兼容；本工作区运行 JDK 25 时未发现字节码或反射类不可达问题。
- sdk-sts 3.1.2 暴露 `AssumeRoleRequest` / `AssumeRoleResponse.Credentials`，与文档一致；接口稳定多年。
- 不引入 alibaba-cloud-sdk-v2（v2 Java SDK 处于演进中且 BOM 复杂）。

**Alternatives considered**:
- 自己 HTTP 实现 STS 协议：节省依赖但维护成本远大于收益。
- 使用 v2 SDK（`com.aliyun:sts20150401`）：BOM 复杂，签名计算差异不大。

## R2. 阿里云 OSS Java SDK

**Decision**: `com.aliyun.oss:aliyun-sdk-oss:3.18.1`（admin + app 各引入一次）。

**Rationale**:
- 同前一版 research（spec 修订前）；支持 `generatePresignedUrl(GET, expiration)`、`getObjectMetadata`、`setObjectTagging`，三件正好覆盖本特性所有写后操作。
- app 端只用 GET 签名，不需要 STS / Put / Tagging；但仍引入完整 SDK 避免维护 shaded 子集。

**Alternatives considered**: 不变。

## R3. RAM Role Policy 设计

**Decision**: 一份 RAM Role，挂以下 policy：

```json
{
  "Version": "1",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": ["oss:PutObject"],
      "Resource": ["acs:oss:*:*:<bucket>/images/*"]
    }
  ]
}
```

服务端在 `AssumeRole` 时**额外内嵌** policy 收敛到本次预生成的具体 objectKey：

```json
{ "Version": "1", "Statement": [{ "Effect": "Allow", "Action": "oss:PutObject", "Resource": "acs:oss:*:*:<bucket>/<objectKey>" }] }
```

最终凭证生效权限 = role policy ∩ inline policy = 仅能写入这一个具体 key。

**Rationale**:
- 单 key 凭证最小权限暴露；即便凭证泄露，只能覆盖一个无意义的目标对象。
- 不授予 `GetObject`：读路径用主 AK 签名，避免客户端拿凭证下载任意对象。
- 不授予 `PutObjectTagging`：tag 打标由服务端绑定时用主 AK 执行，客户端无法伪造 bound tag 绕过 lifecycle。

**Alternatives considered**:
- 整个前缀 `images/*` 给客户端：凭证泄露面更大；否决。
- 还允许 `GetObject`：违背"读必须签名"模型；否决。

## R4. objectKey 预生成与扩展名映射

**Decision**:
- 服务端在 upload-credentials 端点接收 `contentType` 入参（白名单 `image/png` / `image/jpeg` / `image/webp`），用以下映射生成扩展名：

| contentType | 扩展名 |
|---|---|
| image/png | `png` |
| image/jpeg | `jpg` |
| image/webp | `webp` |

- `objectKey = "images/" + UuidV7Generator.next() + "." + ext`。
- 客户端 PUT 时 `Content-Type` 必须等于声明的 `contentType`（否则绑定时 `headObject` 校验失败，但客户端层面不强求强一致，由后续业务绑定阶段权威拒绝）。

**Rationale**: 服务端权威决定 key，杜绝客户端拼凑可读 key 或路径穿越。

**Alternatives considered**:
- 客户端自选 key 后服务端审批：复杂度高且容易漏校验，否决。
- 不带扩展名：浏览器渲染依赖 OSS 设置的 Content-Type，但保留扩展名一致更直观。

## R5. 业务绑定时服务端校验

**Decision**: 新增 `ObjectKeyValidator#validateAndMarkBound(String objectKey)`：

1. 字符串前置：MUST 以 `images/` 开头、MUST NOT 含 `..` / 反斜杠 / 空白。
2. `OSSClient.getObjectMetadata(bucket, objectKey)`：
   - 不存在 / `OSSException` → 抛 `ValidationException("图片对象不可用")`。
   - `Content-Type ∉ {image/png, image/jpeg, image/webp}` → 抛同上。
   - `Content-Length > 20MB` → 抛同上。
3. `OSSClient.setObjectTagging(bucket, objectKey, [bound=true])`。标记成功后该对象不再被 lifecycle 删除。

错误信息脱敏：对外只暴露统一文案"图片对象不可用"，不区分"不存在 / Content-Type 错 / 太大"。日志里保留细节。

**Rationale**:
- `headObject` 是 OSS 上最便宜的元数据查询（计费按 GET 请求，但 HEAD 请求计费更低 / 在某些区域免费）。
- 打 tag 让 lifecycle 规则只按 tag 判定保留，规则简单清晰。
- 多张图片并发校验：本特性内按入参顺序串行 head（最常见单实体图片数 ≤ 10，串行延迟 < 500ms 总和），后续如有热点再加并发。

**Alternatives considered**:
- 维护"已绑定图片表"用 DB 关联判定：增加新表、需要在删除业务实体时同步清理，复杂度上升；OSS object tagging 是原生能力，更轻量。
- 让客户端在 PUT 时打 tag：要求客户端 STS Policy 含 `PutObjectTagging`，凭证泄露后可任意改 tag，否决。

## R6. bucket lifecycle 规则

**Decision**: 在 OSS bucket 上配置 1 条规则：

```json
{
  "ID": "expire-unbound-images-after-24h",
  "Status": "Enabled",
  "Filter": {
    "Prefix": "images/",
    "Tag": { "Key": "bound", "Value": "true" }
  },
  "Filter[Not]": "<不存在 Filter[Not] 字段，需用排除式表达：阿里 OSS lifecycle 支持基于 tag 的 NotTag>"
}
```

阿里云 OSS lifecycle 实际支持的语义：`Filter` 中可指定 `Tag` 等于某值才匹配；目前不支持 `NotTag`。因此采用"反向 tag"策略：

**最终方案**：
- 绑定成功时打 tag `bound=true`。
- lifecycle 规则：`Filter.Prefix = images/` + `Filter.Tag.Key = bound, Value = pending`（即只删除带 `bound=pending` 的对象）。
- upload-credentials 端点下发 STS 凭证的同时，**服务端用主 AK** 在 OSS 预创建一条带 `bound=pending` 的"占位 tag"（API: `PutObjectTagging` 需要对象存在）—— 但对象此时不存在；改方案。

**改方案（最终采用）**：
- lifecycle 规则改用 `Prefix=images/` + `ExpirationDays=1` 无 tag 过滤；**绑定时不打 tag**，而是**绑定时把对象 copy 到不受 lifecycle 影响的"长期前缀" `bound/`**：
  - upload-credentials 下发的 `objectKey = images/<uuid>.<ext>`（仍受 lifecycle 24h 兜底）。
  - 服务端绑定时调 `OSSClient.copyObject(bucket, srcKey, bucket, "bound/<uuid>.<ext>")` 然后 `deleteObject(srcKey)`；DB 持久化的是 `bound/<uuid>.<ext>`。
- lifecycle 只覆盖 `images/` 前缀，`bound/` 前缀永久保留。

> **事后修订（事务安全）**：绑定后的 `deleteObject(srcKey)` 已移除——只 `copyObject`、保留 `images/` 原对象，由 lifecycle 24h 统一回收。原因：`deleteObject` 是无法随业务 DB 事务回滚的副作用，若绑定之后（如多图中某张校验失败、上线资格校验失败）事务回滚，被删的 `images/` 原图无法复原，用户用同一表单重试会永远卡在"图片对象不可用"。去掉 delete 后 `copyObject` 幂等、原图保留，任何失败路径都可安全重试。代价：放宽了"objectKey 一次性失效"语义（同一已绑定 key 在 24h 内幂等可重绑）。详见 `contracts/ObjectKeyValidator.md`、`contracts/business-binding.md`、`contracts/bucket-lifecycle.md` 与 `quickstart.md` SC-005。

**Rationale**: OSS lifecycle 对 NotTag 支持有限，"反 tag"路径在不增加服务端运维负担的前提下不可靠。把已绑定对象搬到新前缀，是工业上更稳的隔离手段（与 spec 内 Q2 中拒绝的 "pending → final" 方案在表现上类似，但触发点不同：Q2 是为了避免脏 key 进主目录，本规则是为了在 OSS lifecycle 能力受限的情况下实现"已绑定永不过期"）。复制 + 删除一次开销约 30–80 ms。

**Spec 影响 & Clarifications 反查**：spec Clarifications Q2 选了"直传 final"，但已绑定对象由 lifecycle 兜底的具体机制（tag vs 移动前缀）spec FR-018 已声明留给 plan 细化。本研究决定走"移动前缀"路径——**该决策不与 spec Q2 冲突**（spec Q2 反对的是"上传前就分 pending/final"的两阶段直传；本决策的复制是发生在业务绑定时，对客户端透明，仍保持"直传 final"语义）。

**Alternatives considered**:
- 让客户端 PUT 时直接写入 `bound/<uuid>`：需要业务上下文已知，前端先调业务接口拿 placeholder ID，过早耦合。
- 用 OSS Lifecycle 的 `Filter.NotTag`（如果未来 OSS 支持）：暂不支持，等待官方能力。

## R7. 读路径签名

**Decision**: 不变；`OSSClient.generatePresignedUrl(HttpMethod.GET, expiration)`，默认 30 分钟（`app.storage.oss.url-expiration-seconds=1800`）。

**Rationale**: 与 spec FR-013 / FR-015 一致。读路径不需要 STS。

**Alternatives considered**: V4 签名（更长更安全但 URL 更长）—— 当前威胁模型 V1 已足够。

## R8. 前端表单直传（服务端 POST 签名）接入

**Decision**: 不引入任何 OSS SDK；服务端用 STS 临时凭证计算 PostObject V4 签名（`OSS4-HMAC-SHA256`），前端用 `multipart/form-data` 表单 POST 直传（`XMLHttpRequest` 以获得字节进度）：

```ts
async function uploadToOss(file: File): Promise<string> {
  const cred = await api.post('/api/admin/files/upload-credentials', { contentType: file.type })
  const form = new FormData()
  form.append('key', cred.objectKey)
  form.append('policy', cred.policy)
  form.append('x-oss-signature', cred.signature)
  form.append('x-oss-signature-version', cred.signatureVersion)
  form.append('x-oss-credential', cred.xOssCredential)
  form.append('x-oss-date', cred.xOssDate)
  form.append('x-oss-security-token', cred.securityToken)
  form.append('success_action_status', '200')
  form.append('file', file) // 必须最后
  // XHR POST 到 cred.host，2xx 即成功
  return cred.objectKey
}
```

**Rationale**: 服务端签名 + 前端表单直传是阿里云官方推荐方案；**浏览器永不接触 `accessKeySecret`**，泄露面最小。免去 `ali-oss` 依赖与其在浏览器侧把 SK 暴露在内存中的风险。

**Alternatives considered**:
- `ali-oss` SDK + `new OSS({ accessKeySecret, stsToken })`：会把临时 SK 下发到浏览器；安全性差，否决（原实现，已废弃）。
- `fetch(PUT 预签名 URL)`：PUT 预签名无法约束 `content-length-range` 等 Policy 条件；否决。

## R9. 测试策略

**Decision**:
- `StsCredentialIssuer` / `ObjectKeyValidator` / `ImageUrlSigner` 三个接口在测试中用 stub 实现注入。
- `FileServiceTest`（重写）覆盖：合法 contentType → 拿到 credential；非法 contentType → 抛 `ValidationException`。
- 新 `BannerServiceTest` / `MerchantServiceTest` / `CityServiceTest` 覆盖：合法 objectKey → 写库成功 + tag 打标；非法 objectKey（前缀错、含 `..`、headObject 失败、MIME 错、太大）→ 业务表零写入。
- MockMvc 集成测试覆盖 `FileController.issueUploadCredential` 与各业务 controller 的请求 / 响应 schema 断言。
- 不引入真实 STS / OSS。

**Rationale**: 与 constitution V 与项目惯例一致；CI 离线可跑。

**Alternatives considered**: testcontainers 上模拟 OSS 镜像 —— 缺乏维护良好的 OSS 兼容镜像；否决。

## R10. 配置 & 启动校验

**Decision**: admin 端用单一 `StorageProperties`（prefix `app.storage`）统管 OSS + STS——`region` / `access-key-id` / `access-key-secret` 三项 OSS 与 STS 共用，提到顶层共享，OSS / STS 各自差异化字段收进 `oss` / `sts` 子节点：

```yaml
app:
  storage:
    region: ${ALIYUN_OSS_REGION}                                   # OSS 签名 + STS AssumeRole 共用
    access-key-id: ${ALIYUN_OSS_ACCESS_KEY_ID}                     # 同一对服务端凭证
    access-key-secret: ${ALIYUN_OSS_ACCESS_KEY_SECRET}
    oss:
      endpoint: ${ALIYUN_OSS_ENDPOINT}
      bucket: ${ALIYUN_OSS_BUCKET}
      upload-key-prefix: ${ALIYUN_OSS_UPLOAD_KEY_PREFIX:images}     # 直传落点（lifecycle 24h 清理）
      bound-key-prefix: ${ALIYUN_OSS_BOUND_KEY_PREFIX:bound}        # 绑定后归档前缀（无 lifecycle）
      url-expiration-seconds: ${ALIYUN_OSS_URL_EXPIRATION_SECONDS:1800}
      max-image-bytes: ${ALIYUN_OSS_MAX_IMAGE_BYTES:20971520}       # 20MB
    sts:
      role-arn: ${ALIYUN_STS_ROLE_ARN}
      role-session-name: ${ALIYUN_STS_ROLE_SESSION_NAME:love-space-admin-upload}
      duration-seconds: ${ALIYUN_STS_DURATION_SECONDS:900}
```

> STS 接入点由 SDK 依据顶层 `region` 自行解析，原 `sts.endpoint` / `sts.region-id` 从未被代码使用，已删除；STS 复用顶层凭证，原 `sts.access-key-id` / `sts.access-key-secret` 一并删除。app 端仅读路径，保留独立的 `OssProperties`（prefix `app.storage.oss`），不含 STS。

`@Validated` 校验所有 `@NotBlank` / `@Min` / `@Max`；`StorageClientConfig` 统一装配 `OSS` 与 `IAcsClient` 两个 bean（启动期不做网络 sanity check，交由运行期调用承担可观测性，避免本地无网络环境启动失败）。

**Rationale**: 与 spec FR-005 / constitution 启动失败约定一致。

**Alternatives considered**: 不变。

## R11. 数据迁移

**Decision**: 不写自动迁移 changelog；开发 / 测试环境提供手工 SQL 清空脚本（plan 的 quickstart 附录）；生产数据迁移单独工单。

**Rationale**: 与 spec Assumptions 一致。

**Alternatives considered**: 不变。

## R12. 业务接口"裸 URL"防御

**Decision**: 业务接口的图片字段（如 `BannerCreateRequest.imageUrls`）通过自定义 `@ObjectKey` 校验注解（或 `@Pattern(regexp = "^images/[\\w-]+\\.(png|jpg|webp)$")`）做正则前置校验；含 `://` 的旧 URL 在 `@Pattern` 阶段就被拒。

**Rationale**: 与 spec FR-017 一致；DTO 层校验避免污染 service。

**Alternatives considered**: 在 service 入口手写校验 —— 散落到各模块容易遗漏；用注解集中收敛。

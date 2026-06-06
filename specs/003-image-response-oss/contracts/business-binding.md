# Contract: 业务实体绑定 objectKey

适用于所有持有图片字段的业务接口：
- `POST /api/admin/banners`、`PUT /api/admin/banners/{id}`
- `POST /api/admin/merchants`、`PUT /api/admin/merchants/{id}`
- `POST /api/admin/cities`、`PUT /api/admin/cities/{id}`
- 未来同形态接口（用户头像、动态、文章封面 …）

## Request 形态

请求 DTO 中的图片字段（`imageUrls` / `logo` / `images` / `backgroundImage`）携带由前端直传得到的 objectKey 字符串，形如 `images/<uuidv7>.<ext>`。

DTO 校验注解：
- 单图必填：`@NotBlank` + `@Pattern("^images/[\\w-]+\\.(png|jpg|webp)$")`
- 单图可空：非空时同 `@Pattern`
- 列表：`@NotEmpty List<@NotBlank @Pattern(...) String>`

任何含 `://` 的"裸 URL"或不以 `images/` 开头的值 MUST 在 DTO 校验阶段以 400 返回。

## Service 层校验流程

业务 service（如 `BannerService.create`）在持久化前 MUST 对**每一个**提交的 objectKey 调用：

```java
String boundKey = objectKeyValidator.validateAndBind(rawObjectKey);
```

- 输入：`images/<uuid>.<ext>`
- 输出：`bound/<uuid>.<ext>`（写入业务表的最终持久化值）
- 失败：抛 `ValidationException("图片对象不可用")` → 全局异常处理返回 422 + `ProblemDetail`。

校验语义见 `ObjectKeyValidator.md`。

## 错误语义

| 场景 | 状态 |
|---|---|
| DTO 正则不匹配（含 URL、含 `..`、前缀错） | 400 |
| objectKey 在 OSS 不存在 | 422 + "图片对象不可用" |
| Content-Type 非白名单 | 422 + "图片对象不可用" |
| Content-Length > 20MB | 422 + "图片对象不可用" |
| `copyObject` 失败 | 500（不应发生；记错日志） |
| 单请求多图中任一失败 | 422，**整请求拒绝**，业务表零写入 |

对外信息脱敏统一为"图片对象不可用"；OSS 原始 error code 仅记入服务端日志。

## 幂等性

- 绑定时**只 copy 不 delete**，`images/<uuid>.<ext>` 原对象保留至 lifecycle 24h 回收。因此同一 `images/` objectKey 在该窗口内第二次 `validateAndBind` 仍会成功（`copyObject` 幂等，落到同一 `bound/<uuid>`）；超过 24h 原对象被 lifecycle 删除后，再次提交才会因 `headObject` NoSuchKey 抛 `ValidationException`。此设计以"幂等可重试"取代了早期"objectKey 一次性失效"语义（见 `quickstart.md` SC-005 调整）——失败的业务请求回滚后，前端可用同一表单原样重试，不会因图片已被消费而陷入死锁。仍建议前端每次选新图时走全新"直传 → 提交"。
- 业务更新接口（`PUT`）：若 `imageUrls` 中某项已是 `bound/...` 形态（来自历史详情接口的读出值），DTO 校验会被 `@Pattern` 拒（pattern 只接受 `images/...`）。**前端 MUST 区分"未改动的旧图"与"新上传图"**：旧图在请求里以 `bound/...` 形态承载？不行——会被拒。

**解决方案（contract 决定）**：
- 请求 DTO 接受**两种形态**之一：`images/<uuid>.<ext>`（新上传）或 `bound/<uuid>.<ext>`（旧图保留）。`@Pattern("^(images|bound)/[\\w-]+\\.(png|jpg|webp)$")`。
- `ObjectKeyValidator.validateAndBind` 实现：当输入已是 `bound/...` 时跳过 OSS head + copy，直接返回原值（前提：服务端可信任旧 boundKey，因为它只能来自此前业务表的查询结果）。
- 不直接信任客户端可以伪造 `bound/<任意>.png` 提交 → 仍需做存在性 head 校验：若 `bound/<uuid>.<ext>` 在 OSS 存在，则信任；否则按"不可用"拒。

更新后的契约：

```java
String boundKey = objectKeyValidator.validateAndBind(rawObjectKey);
// 内部：
//   - rawObjectKey 匹配 images/ → head + 校验 + copy(images→bound)（保留 images/，不 delete）→ 返回 bound key
//   - rawObjectKey 匹配 bound/  → head 校验存在 → 返回原值（不再 copy）
//   - 其他              → 抛 ValidationException
```

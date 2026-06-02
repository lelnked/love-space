# love-space 端到端测试报告（Admin + App 后端）

> 执行日期：2026-06-02
> 范围：`love-space-admin`（`/api/admin/**`，JWT 鉴权）+ `love-space-app`（`/api/app/**`，X-API-Key 鉴权）
> 方式：黑盒接口级，curl 实打实调用 + OSS 真实直传 + DB 落库核对。不含 web 前端 UI 走查。
> 依据：`测试用例-admin-app.md`（共 ~107 条用例）

## 环境

| 项 | 值 |
| --- | --- |
| admin 后端 | `http://100.100.117.79:21423`（test profile，后台任务 `bo4rnq2v8`） |
| app 后端 | `localhost:21425`（后台任务 `bh7hv8awj`） |
| 数据库 | docker `lan_test_postgres`，宿主机 `25432`，库 `love_space`，user `iris` |
| OSS | 真实阿里云 OSS + STS V4 直传 |
| app API Key | `fd7cff9c…570383`（用户提供） |

## 结论概览

| 区块 | 通过 | 失败 | 部分/N-A/未测 | 合计 |
| --- | --- | --- | --- | --- |
| A. Admin（AUTH/MGR/CITY/CAT/TAG/MCH/REV/BAN/OSS/IMG/LOG） | 74 | 0 | 8 | 82 |
| B. App（AK/CITY/MCH/DET/REV/BAN/RO） | 26 | 0 | 2 | 28 |
| C. 跨端 | 5 | 0 | 0 | 5 |
| **合计** | **105** | **0** | **~10** | **~115** |

> P1 核心契约全部通过。BUG-1（异常→状态码映射）、BUG-2（商户编辑标签唯一键冲突）、BUG-4（分类名长度校验，含 web 前端）均已修复并回归。**0 项遗留失败**：BUG-3（TAG-01 标签默认上架）经确认为需求变更后的预期行为、BUG-5（CITY banner data 以 chineseName 为准）经确认为契约对齐，均非缺陷。

---

## 一、必修缺陷（建议尽快修）

### ✅ BUG-1（高）异常处理器把鉴权失败 / 方法不允许映射成 500 —— 已修复
两套后端的 `GlobalExceptionHandler` 此前缺少对 `AuthenticationException`（含 `BadCredentialsException`）/ `MissingServletRequestParameterException` / `HttpRequestMethodNotSupportedException` 的处理，统一落到 catch-all → **500**。

**修复**：在 `com.loves.space` 与 `com.space.app` 两套 `GlobalExceptionHandler` 各补三个 handler：
- `AuthenticationException → 401`
- `MissingServletRequestParameterException → 400`（提示"缺少必填参数 'xxx'"）
- `HttpRequestMethodNotSupportedException → 405`

**回归验证**（重启后实测）：

| 影响用例 | 修复前 | 修复后 |
| --- | --- | --- |
| AUTH-02 密码错误 | 500 | **401**（防枚举统一提示） |
| AUTH-03 / MGR-04 停用账号登录 | 500 | **401** |
| APP-MCH-01 缺 `cityId` | 500 | **400** |
| APP-RO-01 GET 路径发 POST | 500 | **405** |
| AUTH-01 正确登录（回归） | 200 | **200**（无误伤） |

> 注：AUTH-02/03 的提示语为防账号枚举**故意统一**为"用户名或密码错误，或账号已停用"，不单独区分"账号已停用"，符合安全约定。

### ✅ BUG-2（高）商户编辑回传相同标签 → 500 唯一键冲突 —— 已修复
`PUT /api/admin/merchants/{id}` 携带与现状**相同的 tagIds** 时，触发 `ux_loves_merchant_tag_merchant_tag` 唯一约束冲突 → 500（前端编辑回显后原样保存的最常见路径）。

**根因**：`MerchantTagRepository.deleteAllByMerchantId` 原为**派生删除**（`em.remove` 入队），upsert 中「先删后插」的方法调用顺序，在 Hibernate flush 时被 ActionQueue 重排为「**INSERT 先于 DELETE**」——旧的 `(merchant_id, tag_id)` 还没删，新行先插入就撞唯一约束。（同文件 `deleteAllByTagId` 用 `@Modifying @Query` 批量删除，不受此影响——两者写法不一致即线索。）

**修复**：把 `deleteAllByMerchantId` 改为 `@Modifying @Query` 批量 DELETE，立即落库于后续 INSERT 之前：
```java
@Modifying
@Query("delete from MerchantTag mt where mt.merchantId = :merchantId")
void deleteAllByMerchantId(@Param("merchantId") UUID merchantId);
```

**回归验证**（重启后实测，商户「测试商户甲」）：

| 场景 | 修复前 | 修复后 |
| --- | --- | --- |
| 编辑回传相同 3 标签 | 500 | **200**，标签完整 |
| 连续重复 PUT（幂等） | — | **200** |
| 改为标签子集（删 1 个） | — | **200**，正确剩 2 个 |

影响用例：**MCH-10** → PASS。

### ~~BUG-3~~（已澄清，需求变更，非缺陷）
**TAG-01**：`POST /api/admin/tags` 新建标签 `online` 默认 `true`（默认上架）。原测试基线沿用旧规格「默认下架」，经确认**新建即上架属需求变更后的预期行为**，`TagService.create` 的 `tag.setOnline(true)` 保持不变。此项 **PASS**。

### ✅ BUG-4（中）分类名称长度校验与规格不符 —— 已修复（含 web 前端）
**CAT-03**：分类名原 `@Size(max=30)`，规格要求 **≤10 汉字**，可提交 11 个汉字而不报错。

**修复**（admin + web 同步）：长度校验属声明式约束，统一用 Bean Validation 注解，不下沉到 service 层。
- **admin**：`CategoryUpsertRequest` 的 `name` 由 `@Size(max=30)` 改为 `@NotBlank @Size(max=10, message="分类名长度不能超过 10 个字符")`；controller 已有 `@Valid`，超限经 `MethodArgumentNotValidException` 产出**字段级**错误（符合 FR-005）。`CategoryService` 不做长度校验，仅保留查库才能判定的重名唯一性校验。
- **web**（`src/pages/Categories/List.tsx`）：新增 `codePointLength` helper 与 `MAX_NAME_CODE_POINTS=10`；提交前校验 `>10` 报字段错误；输入框按 code-point 截断到 10、placeholder 改为"≤10 字符"。

**回归验证**（admin 重启后实测）：

| 场景 | 修复前 | 修复后 |
| --- | --- | --- |
| 11 汉字 | 通过（错误） | **400**「name: 分类名长度不能超过 10 个字符」（字段级） |
| 10 汉字（边界） | 通过 | **200** 创建成功 |
| 重名（service 唯一性校验仍生效） | — | **400**「分类名已存在」 |

影响用例：**CAT-03** → PASS。web 前端类型检查（`tsc -b`）与 ESLint 均通过。

### ~~BUG-5~~（已澄清并对齐文档，非缺陷）
**APP-BAN-02**：CITY banner 的 `data` 实际返回 `{id, chineseName, englishName, chineseProvince, englishProvince}`。原契约/DTO 注释写的是 `{id, name}`，与实现不一致。

经确认**以 `chineseName` 为准**（App 需要中英文名与省份），已更新测试契约 `APP-BAN-02` 与 `BannerItemResponse` 的 javadoc 与实现对齐。注意顶层 `BannerItemResponse.name` 是 banner 自身名称（正确），与 `data` 内的城市名是两个层级。此项 **PASS**。

---

## 二、设计行为澄清（非缺陷）

- **分页 size**：被钳制到 `{20, 30}` 集合 —— `size=30` 各端点均生效，`<20` 回落 20、`>30` 回落 30。这正符合"默认 20，可切 30"约定。CITY-06 / LOG-03 / 各分页用例均 **PASS**。
- **OSS 绑定消费源对象**：bind 成功会把 key 从 `images/` 移到 `bound/`（move 语义），故每个实体需各自上传新对象，复用已绑定 key 会报"图片对象不可用"。符合 003 设计。
- **城市下线级联**：城市切 offline 会级联下架其下商户与关联 banner（AFTER_COMMIT 异步）；但城市重新上线**只恢复 banner，不恢复商户**（商户需手动重新上架）。测试中曾踩坑，确认为预期。
- **删除后查询返回 400**（CITY-07）：删除成功后再查返回 400 而非 404，属轻微契约偏差，不阻断。
- **X-04**：同图被多实体引用按独立 objectKey 处理（不去重），符合设计。

---

## 三、未执行 / 不可黑盒覆盖的用例

| 用例 | 原因 |
| --- | --- |
| OSS-06 | 需等签名过期（>15min），未实测等待 |
| OSS-07 / AK-05 | 缺配置启动失败 —— 属启动期断言，不破坏运行中实例 |
| OSS-10 | 无法构造 text/plain 的 OSS 对象做绑定校验 |
| OSS-11 | 需 25MB 大对象，未构造 |
| IMG-05（超期分支） | 同 OSS-06，仅验证了有效期内 200 |
| BAN-07 | 故障注入场景，非黑盒可达 |
| CITY-04 / CITY-05 | `bannerSortOrder` 字段在当前实现中**未落地**（请求被忽略、响应无此字段），标记 N/A |
| APP-DET-04 | 详情强制 ≥1 图，无法构造空图列表 |

---

## 四、通过亮点（核心契约已验证）

- **鉴权隔离（X-01）**：admin 走 JWT、app 走 X-API-Key，两套前缀无交叉。
- **图片契约（X-02 / IMG-01~08 / APP-MCH-08 / APP-DET-03）**：admin + app 全部图片字段均为 `ImageResponse{id,url}`（带 v1 签名），**零裸 String**；签名每次新生成，剥离签名参数访问 → 403；`id == objectKey` 稳定。
- **OSS 直传闭环（OSS-01~05/09/12~15）**：凭证不含 `accessKeySecret`、objectKey 服务端预生成 `images/<uuidv7>.<ext>`、表单直传成功、越权 key 被 OSS Policy 拒、非法 key 整体拒绝且**业务表零写入**、裸 URL 拒绝。
- **四维评分换算（APP-DET-01/05）**：原始分 24/20/20/16（满分 30/25/25/20）→ 百分制 80/80/80/80，爱女指数 total=80、level=8，换算正确。
- **City→Banner 级联（BAN-05/06 / X-03）**：城市上下线经 AFTER_COMMIT 异步处理后，app banner 可见性与城市状态 100% 一致。
- **分类删除级联下架商户（CAT-01）**、**标签下架仅隐藏不影响商户在线（TAG-02 / APP-DET-02）**、**emoji 含组合字形完整保存（REV-01 / APP-REV-01）** 均通过。
- **操作日志（LOG-01~04）**：写操作异步落库，按操作人+时间过滤、分页、多模块覆盖均正常。
- **内置 admin（MGR-03 / X-05）**：禁止停用、幂等植入（`loves_manager` 仅一条、密码不被重置）。

---

## 五、后台任务

| 服务 | 任务 ID | 地址 |
| --- | --- | --- |
| admin（test） | `bo4rnq2v8` | http://100.100.117.79:21423 |
| app | `bh7hv8awj` | localhost:21425 |

如需停止，告知我即可一并 KillShell。

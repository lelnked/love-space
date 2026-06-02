# love-space 测试用例（Admin 后端 + App 后端）

> 依据需求规格：
> - `specs/001-aiwomap-mvp/spec.md`（爱女地图 MVP）
> - `specs/002-banner-module/spec.md`（Banner 模块）
> - `specs/003-image-response-oss/spec.md`（OSS 直传与 ImageResponse 统一）
>
> 范围：`love-space-admin`（`/api/admin/**`）与 `love-space-app`（`/api/app/**`）两套后端 API 的功能测试用例。
> 不含 `love-space-web` 前端 UI 走查与性能压测（性能项已被规格推迟到性能专项阶段）。

## 文档约定

- **用例编号**：`<模块前缀>-<序号>`，例如 `AUTH-01`。
- **优先级**：`P1`（核心，必须通过）/ `P2`（重要）/ `P3`（增强/边界）。
- 每条用例以复选框 `- [ ]` 呈现，执行时勾选即记录通过；下方 `前置 / 步骤 / 预期` 为执行细节。
- **关联** 列指向需求功能点（FR-xxx）或验收场景。
- 测试用例为**黑盒接口级**，可用集成测试（`./mvnw test` + `@SpringBootTest`/MockMvc）或 API 调试工具（Postman/HTTP 文件）执行。

### 通用约定

| 项 | 约定 |
| --- | --- |
| Admin 鉴权 | 除 `POST /api/admin/auth/login` 外，所有 `/api/admin/**` 需 `Authorization: Bearer <jwt>` |
| App 鉴权 | 所有 `/api/app/**` 需请求头 `X-API-Key: <key>`（命中 `app.security.api-keys` 任一） |
| 失败响应 | 统一 RFC 7807 ProblemDetail；字段级校验返回 field + message 列表（FR-005 / FR-054） |
| 成功响应 | 直接返回业务对象，不额外包装 |
| 默认排序 | admin 列表默认 `createdAt DESC`；商户额外 `weight DESC, createdAt DESC`（FR-050） |
| 分页 | 默认 size=20，可切 30 |
| 内置 admin | username=`admin`，密码 `8@y2eoRLyStM*UVU`，role=ADMIN，BCrypt 存储，幂等植入 |
| 图片字段 | 请求侧传 OSS objectKey（`images/<uuid>.<ext>`）；响应侧统一 `ImageResponse{id,url}`（带签名） |

---

# A. Admin 后端（love-space-admin）

## A1. 鉴权 `/api/admin/auth`

- [x] **AUTH-01** `P1` 正确账号密码登录成功
  - 前置：内置 admin 已植入
  - 步骤：`POST /api/admin/auth/login` body `{username:"admin", password:"8@y2eoRLyStM*UVU"}`
  - 预期：200，返回 JWT 与顶层字段 `manager`（非 `user`），含 role=ADMIN
  - 关联：FR-020 / US3-AC3

- [x] **AUTH-02** `P1` 密码错误登录失败
  - 步骤：以错误密码登录
  - 预期：401/鉴权失败 ProblemDetail，不返回 token，不泄露"用户名是否存在"

- [x] **AUTH-03** `P1` 已停用账号登录被拒
  - 前置：存在一个 enable=false 的 Manager
  - 步骤：用该账号登录
  - 预期：登录失败并提示"账号已停用"
  - 关联：FR-053 / US3-AC4

- [x] **AUTH-04** `P1` 未携带 token 访问受保护接口返回 401
  - 步骤：不带 Authorization 调 `GET /api/admin/cities`
  - 预期：401 ProblemDetail
  - 关联：FR-002

- [x] **AUTH-05** `P1` 过期/非法 token 返回 401
  - 步骤：用篡改/过期 JWT 调任意受保护接口
  - 预期：401
  - 关联：Edge（token 过期统一 401）

- [x] **AUTH-06** `P2` `GET /api/admin/auth/me` 返回当前登录 Manager
  - 步骤：登录后带 token 调 `/me`
  - 预期：200，返回当前 manager 信息（username/role/nickname）

- [x] **AUTH-07** `P2` `POST /api/admin/auth/logout` 登出
  - 步骤：登录后调 logout
  - 预期：200；按实现语义令 token 失效/前端清除

## A2. Manager 管理 `/api/admin/managers`（仅 ADMIN）

- [x] **MGR-01** `P1` MEMBER 角色访问 Manager 接口返回 403
  - 前置：以 MEMBER 角色 Manager 登录
  - 步骤：`GET /api/admin/managers/page`
  - 预期：403 拒绝
  - 关联：FR-003 / US3-AC1 / SC-004

- [x] **MGR-02** `P1` 新建 Manager 强制 role=MEMBER（显式传 ADMIN 被忽略）
  - 步骤：ADMIN 登录，`POST /api/admin/managers` body 含 `role:"ADMIN"`
  - 预期：创建成功，落库 role=MEMBER
  - 关联：FR-022 / US3-AC2

- [x] **MGR-03** `P1` 内置 admin 账号禁止停用
  - 步骤：`PUT /api/admin/managers/{adminId}/disable`（目标为 username=admin）
  - 预期：拒绝，提示"内置管理员 admin 账号不可停用"
  - 关联：ManagerService 业务规则（commit 979e38d）

- [x] **MGR-04** `P1` 停用普通 Manager 后其无法登录
  - 步骤：`PUT /api/admin/managers/{id}/disable` → 该 Manager 登录
  - 预期：停用成功；登录失败提示已停用
  - 关联：US3-AC4

- [x] **MGR-05** `P2` 启用已停用 Manager 后可登录
  - 步骤：`PUT /api/admin/managers/{id}/enable` → 登录
  - 预期：启用成功；登录成功

- [x] **MGR-06** `P2` 重置密码后旧密码失效、新密码可登录
  - 步骤：`PUT /api/admin/managers/{id}/password` 设新密码
  - 预期：旧密码登录失败，新密码登录成功；密码 BCrypt 存储
  - 关联：FR-022 / FR-053

- [x] **MGR-07** `P2` 分页列表按 username 模糊 / role / 启用状态 / 创建时间过滤
  - 步骤：带各组合过滤参数调 `GET /page`
  - 预期：结果集与过滤条件一致，默认 `createdAt DESC`，分页生效
  - 关联：FR-022 / FR-006

- [x] **MGR-08** `P3` 重复 username 新建被拒
  - 步骤：用已存在 username 新建
  - 预期：字段级校验错误（用户名已存在）

## A3. 城市管理 `/api/admin/cities`

- [x] **CITY-01** `P1` 新增城市成功且默认下线
  - 步骤：`POST /api/admin/cities` 填中英文名称/省份等
  - 预期：创建成功；未上线状态（需手动上线）
  - 关联：FR-023

- [x] **CITY-02** `P1` 同名（chineseName）城市新增/编辑被拒
  - 前置：已存在"上海"
  - 步骤：再次新增 chineseName="上海"
  - 预期：字段级校验错误（名称不重复）
  - 关联：FR-023 / Edge（同名拒绝）

- [x] **CITY-03** `P1` 上线城市后 App `/api/app/cities` 可见
  - 步骤：`PUT /api/admin/cities/{id}/online` 上线 → 调 App cities
  - 预期：上线成功；App 端同步可见
  - 关联：US2-AC1

- [ ] **CITY-04** `P1` `bannerSortOrder` 拒绝负数
  - 步骤：编辑城市设 `bannerSortOrder=-1`
  - 预期：校验错误（MUST >= 0）
  - 关联：FR-010 / FR-023

- [ ] **CITY-05** `P2` `bannerSortOrder` 仅影响 banner 不影响列表排序
  - 步骤：多个城市设不同 bannerSortOrder，查 admin 城市列表
  - 预期：列表仍按 `createdAt DESC`；bannerSortOrder 不参与列表排序
  - 关联：FR-023

- [x] **CITY-06** `P2` 城市列表名称/上下线过滤 + 分页
  - 预期：过滤结果正确，默认 `createdAt DESC`，分页 20/30
  - 关联：FR-023 / FR-006

- [x] **CITY-07** `P2` 删除城市
  - 步骤：`DELETE /api/admin/cities/{id}`
  - 预期：删除成功；后续查询 404/不返回

- [x] **CITY-08** `P3` 背景图字段以 ImageResponse 返回（详见 IMG 用例）
  - 预期：详情/列表 `backgroundImage` 为 `ImageResponse`，无背景图为 null
  - 关联：003-FR-011 / US4-AC3

## A4. 分类管理 `/api/admin/categories`

- [x] **CAT-01** `P1` 删除分类联动下架其下所有商户
  - 前置：分类下有 5 个上架商户
  - 步骤：`DELETE /api/admin/categories/{id}`
  - 预期：删除成功；5 个商户自动下架，App 列表不再返回
  - 关联：FR-024 / FR-051 / US2-AC5

- [x] **CAT-02** `P1` 分类名称不重复
  - 步骤：新增重名分类
  - 预期：校验错误

- [x] **CAT-03** `P2` 分类名称 ≤10 汉字
  - 步骤：提交 11 汉字名称
  - 预期：字段级校验错误
  - 关联：FR-024

- [x] **CAT-04** `P2` 新增/编辑/列表（默认 createdAt DESC，无排序字段）
  - 预期：CRUD 正常；列表按创建时间倒序

## A5. 标签管理 `/api/admin/tags`

- [x] **TAG-01** `P1` 新增标签默认上架（需求变更，原"默认下架"基线作废）
  - 步骤：`POST /api/admin/tags` → `PUT /api/admin/tags/{id}/online`
  - 预期：创建/上架成功
  - 关联：FR-025

- [x] **TAG-02** `P1` 标签下架仅隐藏不影响商户上下架
  - 前置：商户绑定 3 个标签，其中 1 个下架
  - 步骤：App 调商户详情
  - 预期：返回 2 个上架标签；商户仍在线
  - 关联：FR-015 / FR-052 / US1-AC3 / US2-AC4

- [x] **TAG-03** `P2` 标签名 ≤6 汉字
  - 步骤：提交 7 汉字标签名
  - 预期：字段级校验错误
  - 关联：FR-025

- [x] **TAG-04** `P2` 标签不重名
  - 预期：重名新增/编辑被拒

- [x] **TAG-05** `P2` 标签列表名称/上下架过滤 + 分页（createdAt DESC）
  - 预期：过滤与排序正确

## A6. 商户管理 `/api/admin/merchants`

- [x] **MCH-01** `P1` 新增商户成功且默认下架，上架后 App 可见
  - 前置：已有上线城市、≥1 上架标签
  - 步骤：填完整表单（logo 1 张、images ≥1、四维评分、≥1 标签、≥1 评价、故事、weight、推荐周期）提交 → `PUT /api/admin/merchants/{id}/online`
  - 预期：保存成功默认下架；上架后 App `/api/app/merchants` 可查
  - 关联：FR-026/027 / US2-AC2

- [x] **MCH-02** `P1` 四维评分超上限被拒（S≤30/L≤25/E≤25/I≤20）
  - 步骤：提交 S=31（或任一维超限）
  - 预期：字段级校验错误，不写库
  - 关联：FR-027 / US2-AC2 / Edge

- [x] **MCH-03** `P1` 商户名称 >15 汉字被拒
  - 步骤：提交 16 汉字名称
  - 预期：校验错误（≤15 汉字）
  - 关联：FR-027 / US2-AC3

- [x] **MCH-04** `P1` 图片至少 1 张校验
  - 步骤：images 为空提交
  - 预期：校验错误（≥1 张）
  - 关联：FR-027 / Edge

- [x] **MCH-05** `P1` logo 必填且仅 1 张
  - 步骤：缺 logo / 传多张 logo
  - 预期：校验错误
  - 关联：FR-027

- [x] **MCH-06** `P2` 商户故事 >5000 字被拒
  - 步骤：提交 5001 字故事
  - 预期：校验错误（≤5000）
  - 关联：FR-027 / Edge

- [x] **MCH-07** `P2` admin 列表默认排序 `weight DESC, createdAt DESC`
  - 前置：不同 weight 的多个商户
  - 步骤：`GET /api/admin/merchants/page`
  - 预期：先按 weight 降序，同 weight 按创建时间降序
  - 关联：FR-050

- [x] **MCH-08** `P2` 商户列表多条件过滤（name/cityId/categoryId/period/上下架）
  - 预期：过滤结果与条件一致；分页生效
  - 关联：FR-026

- [x] **MCH-09** `P2` 推荐周期多选保存与回显
  - 步骤：保存月经期+排卵期，查详情
  - 预期：recommendedPeriods 含两项
  - 关联：FR-027

- [x] **MCH-10** `P2` 编辑、下架、删除商户
  - 预期：各操作成功；下架后 App 不返回；删除后详情 404

- [x] **MCH-11** `P3` categoryId 可为空（无分类商户）
  - 步骤：不传 categoryId 创建并上架
  - 预期：保存成功；App 端省略 categoryId 时该商户也返回
  - 关联：FR-012 / Clarification

## A7. 商户评价 `/api/admin/merchants/{merchantId}/reviews`

- [x] **REV-01** `P1` 创建评价（支持 emoji，含组合 emoji 👨‍👩‍👧）
  - 步骤：`POST` body nickname/title/content（含组合 emoji）
  - 预期：创建成功；回查 content 字符完整无丢失
  - 关联：FR-013 / SC-007 / Edge（emoji UTF-8/4字节）

- [x] **REV-02** `P2` 评价分页列表按 sortOrder 升序
  - 步骤：`GET /reviews/page`
  - 预期：按 sortOrder ASC 返回

- [x] **REV-03** `P2` 更新/删除评价
  - 步骤：`PUT /reviews/{id}`、`DELETE /reviews/{id}`
  - 预期：更新/删除成功

- [x] **REV-04** `P2` 设置评价推荐位 recommended
  - 步骤：`PATCH /reviews/{id}/recommended`
  - 预期：recommended 状态切换成功

## A8. Banner 管理 `/api/admin/banners` + City 联动

- [x] **BAN-01** `P1` 新增 CITY banner 默认 offline
  - 前置：≥1 个 online 城市
  - 步骤：`POST /api/admin/banners` 填 name、image ≥1、type=CITY、link=城市id
  - 预期：创建成功，online 默认 false
  - 关联：002-FR-010 / US1-AC1

- [x] **BAN-02** `P1` 保存校验：name 非空 / image≥1 / CITY 时 link 非空且城市存在
  - 步骤：分别缺 name、缺图片、缺/错 link 提交
  - 预期：各自字段级校验错误（如"至少上传一张图片"）
  - 关联：002-FR-020 / Edge

- [x] **BAN-03** `P1` 仅列表页可上下线，编辑不改 online
  - 步骤：`POST /api/admin/banners/{id}/online` 上线；用 `PUT /api/admin/banners/{id}` 编辑其它字段
  - 预期：上线入口仅此一处；编辑保存不改变 online 状态
  - 关联：002-FR-006/009 / US1-AC4 / SC-005

- [x] **BAN-04** `P1` 上线 CITY banner 时校验关联城市为 online
  - 前置：关联城市当前 offline
  - 步骤：尝试上线该 banner
  - 预期：拒绝并提示先启用城市
  - 关联：002-FR-011 / Edge

- [x] **BAN-05** `P1` City 下线联动其关联 banner 下线（AFTER_COMMIT）
  - 前置：城市 X online，关联 N 条 online=true 的 CITY banner
  - 步骤：`PUT /api/admin/cities/{X}/online` 切 offline → 事件处理后查 App banner
  - 预期：N 条 banner online 置 false；App 不再返回
  - 关联：002-FR-016/017/018 / US3-AC1 / SC-002

- [x] **BAN-06** `P1` City 重新上线联动其关联 banner 上线
  - 步骤：X 由 offline 切 online
  - 预期：原 N 条 banner online 置 true；App 重新返回
  - 关联：002-FR-017 / US3-AC2

- [ ] **BAN-07** `P2` 单条 banner 联动更新失败不回滚城市状态
  - 步骤：构造一条 banner 更新异常场景，切换城市状态
  - 预期：城市状态变更成功（最终一致），错误记录到日志，不阻塞
  - 关联：002-FR-018 / US3-AC3

- [x] **BAN-08** `P2` 编辑 banner 替换关联城市更新 link 与 updatedAt
  - 步骤：将 link 改为另一 online 城市保存
  - 预期：link 更新为新城市 id，updatedAt 刷新
  - 关联：US1-AC5

- [x] **BAN-09** `P2` banner 列表展示与过滤（name 模糊 / type / online / 分页）
  - 预期：列表含 name、type、关联城市名、online、updatedAt；过滤分页正确
  - 关联：002-FR-005

- [x] **BAN-10** `P3` Banner 接口对任一已登录 Manager（含 MEMBER）开放
  - 步骤：MEMBER 登录执行 banner CRUD/上下线
  - 预期：允许（不限 ADMIN）
  - 关联：002-FR-019

## A9. 文件直传 / OSS `/api/admin/files`

- [x] **OSS-01** `P1` 申请直传凭证返回完整签名且不含 accessKeySecret
  - 前置：OSS/STS/RAM Role 配置正确，admin 已登录
  - 步骤：`POST /api/admin/files/upload-credentials` 声明 MIME=image/png
  - 预期：返回 `{host, objectKey, policy, signature, signatureVersion, xOssCredential, xOssDate, securityToken, expiration}`；**不含 accessKeySecret**
  - 关联：003-FR-001 / US1-AC1

- [x] **OSS-02** `P1` objectKey 由服务端预生成且格式正确
  - 步骤：检查返回 objectKey
  - 预期：形如 `images/<uuidv7>.<ext>`，扩展名由 MIME 反查（png→png, jpeg→jpg, webp→webp）
  - 关联：003-FR-002

- [x] **OSS-03** `P1` expiration ≤ 当前 +15 分钟，ISO-8601 UTC
  - 预期：默认 900s 有效期；ISO-8601 UTC 格式
  - 关联：003-FR-004

- [x] **OSS-04** `P1` 用签名表单 POST 直传成功，对象出现在 bucket
  - 步骤：以返回签名 `multipart/form-data` POST 到 host（key=objectKey）
  - 预期：上传成功；bucket 存在该对象，Content-Type 与声明一致
  - 关联：003-FR-001 / US1-AC2

- [x] **OSS-05** `P1` 越权 key 被 OSS Policy 拒绝
  - 步骤：把表单 key 改为 `images/../other`
  - 预期：OSS 返回 Policy 校验失败/AccessDenied
  - 关联：003-FR-003 / US1-AC3 / SC-004

- [ ] **OSS-06** `P2` 过期签名再次上传被拒
  - 步骤：超过有效期后复用同签名上传
  - 预期：OSS 鉴权失败；需重新申请
  - 关联：US1-AC4

- [ ] **OSS-07** `P1` 缺失 OSS/STS 配置时启动失败，不回退本地磁盘
  - 步骤：移除必备配置启动应用
  - 预期：启动失败并打印明确原因；不静默回退本地
  - 关联：003-FR-005 / US1-AC5

- [x] **OSS-08** `P1` 旧 `POST /api/admin/files/upload` 端点已移除
  - 步骤：调用旧 multipart 路径
  - 预期：404 / 405
  - 关联：003-FR-006 / US4-AC5

- [x] **OSS-09** `P1` 业务绑定校验：对象不存在拒绝写库
  - 步骤：用未上传的 `images/<random>.png` 调 `POST /api/admin/banners`
  - 预期：422/400 业务校验错误（图片对象不可用），业务表无新增
  - 关联：003-FR-008 / US3-AC2 / SC-005

- [ ] **OSS-10** `P1` 业务绑定校验：Content-Type 非白名单拒绝
  - 步骤：提交一个 text/plain 的 OSS 对象 key 绑定
  - 预期：业务校验错误，不写库
  - 关联：003-FR-008 / US3-AC3

- [ ] **OSS-11** `P1` 业务绑定校验：Content-Length >20MB 拒绝
  - 步骤：提交一个 25MB 对象 key 绑定
  - 预期：业务校验错误，不写库
  - 关联：003-FR-008 / US3-AC4

- [x] **OSS-12** `P1` 多图任一校验失败整体拒绝（不部分写入）
  - 步骤：提交多个 objectKey，其中一个非法
  - 预期：整个请求拒绝，业务表零写入
  - 关联：003-FR-008 / US3-AC5

- [x] **OSS-13** `P1` 拒绝非 images/ 前缀或含 `..` 的 objectKey
  - 步骤：提交 `other/x.png`、`images/../x.png`
  - 预期：直接校验拒绝
  - 关联：003-FR-009

- [x] **OSS-14** `P2` 校验失败不暴露 OSS 内部细节
  - 步骤：分别用"不存在"与"无权限"对象绑定
  - 预期：对外统一返回"图片对象不可用"，不区分 access denied / 不存在
  - 关联：003-FR-010

- [x] **OSS-15** `P1` 拒绝裸 URL 图片字段
  - 步骤：图片字段提交含 `://` 的值（如 http://.../x.png）
  - 预期：校验错误拒绝
  - 关联：003-FR-017

## A10. ImageResponse 统一返回（admin 侧）

- [x] **IMG-01** `P1` Banner 详情 imageUrls 为 List<ImageResponse>
  - 步骤：`GET /api/admin/banners/{id}`
  - 预期：imageUrls 每项含非空 id 与带签名 url
  - 关联：003-FR-011/012 / US4-AC1

- [x] **IMG-02** `P1` 商户详情 logo 为 ImageResponse、images 为 List<ImageResponse>
  - 步骤：`GET /api/admin/merchants/{id}`
  - 预期：logo 单对象、images 列表，均带签名 url 与 id
  - 关联：US4-AC2

- [x] **IMG-03** `P1` 城市 backgroundImage 为 ImageResponse（无图为 null）
  - 步骤：`GET /api/admin/cities`、`/cities/{id}`
  - 预期：有背景图为 ImageResponse；无背景图为 null
  - 关联：US4-AC3

- [x] **IMG-04** `P1` 签名 URL 去掉签名参数后访问被拒
  - 步骤：取任一返回 url，剥离签名参数访问
  - 预期：403/AccessDenied
  - 关联：003-FR-013 / US2-AC2 / SC-002

- [ ] **IMG-05** `P1` 签名 URL 有效期内可访问、超期失败
  - 步骤：有效期内访问；超期后访问
  - 预期：期内 200 返回字节，超期失败
  - 关联：003-FR-015 / US2-AC3

- [x] **IMG-06** `P2` 每次响应 url 为当次新生成签名（不缓存过期签名）
  - 步骤：两次拉同一详情，比对 url 签名参数
  - 预期：每次为新签名 url
  - 关联：003-FR-014

- [x] **IMG-07** `P2` ImageResponse.id 等于 OSS object key 且稳定
  - 预期：id == `images/<uuid>.<ext>`，多次返回稳定一致
  - 关联：003-FR-016

- [x] **IMG-08** `P2` 无裸 String 图片字段残留（契约扫描）
  - 步骤：抓取 admin 所有 controller 响应 schema
  - 预期：所有图片字段为 ImageResponse / List<ImageResponse>，零裸 String
  - 关联：US4 独立测试 / SC-003

## A11. 操作日志 `/api/admin/logs`

- [x] **LOG-01** `P1` 写操作异步落日志
  - 步骤：修改商户 weight → `GET /api/admin/logs/page`
  - 预期：存在 module=merchant、action=update、target=商户id 的记录
  - 关联：FR-004/029 / US4-AC1 / SC-006

- [x] **LOG-02** `P2` 按操作人 + 时间区间过滤
  - 步骤：按 username=admin + 今天过滤
  - 预期：仅返回 admin 今日日志
  - 关联：US4-AC2

- [x] **LOG-03** `P2` 日志分页（默认 20，可切 30，右下角分页器）
  - 前置：>20 条日志
  - 预期：分页正确，默认每页 20，可切 30
  - 关联：US4-AC3 / FR-006

- [x] **LOG-04** `P3` 各模块关键写操作均可查
  - 步骤：城市/标签/分类/商户/Manager 的增改删/上下线/重置密码各触发一次
  - 预期：对应日志可查率 ≥99%
  - 关联：SC-006

---

# B. App 后端（love-space-app）

## B1. API Key 鉴权

- [x] **AK-01** `P1` 缺失 X-API-Key 返回 401
  - 步骤：不带头调 `GET /api/app/cities`
  - 预期：401 ProblemDetail
  - 关联：FR-017 / Edge

- [x] **AK-02** `P1` key 不在白名单返回 401（不区分原因）
  - 步骤：带错误 key 调接口
  - 预期：401 + 通用提示，不透露具体原因
  - 关联：FR-017/018

- [x] **AK-03** `P1` 命中白名单任一 key 放行
  - 步骤：带合法 key 调接口
  - 预期：200 正常返回
  - 关联：FR-017

- [x] **AK-04** `P2` 鉴权失败记录 WARN 审计日志且不含 key 明文
  - 步骤：触发一次鉴权失败，检查日志
  - 预期：WARN 日志含远端 IP、是否携带头、路径、时间戳；仅 key 的 SHA-256 前 6 位脱敏指纹，无明文
  - 关联：FR-019

- [ ] **AK-05** `P2` `app.security.api-keys` 为空时应用启动失败
  - 步骤：配置空 api-keys 启动
  - 预期：启动失败并打印明确告警（防裸奔）
  - 关联：Edge

## B2. 城市 `GET /api/app/cities`

- [x] **APP-CITY-01** `P1` 仅返回已上线城市，按运营排序
  - 预期：仅 online=true 城市，按约定排序
  - 关联：FR-011 / US2-AC1

- [x] **APP-CITY-02** `P2` 城市 backgroundImage 为 ImageResponse（无图 null）
  - 预期：图片字段统一 ImageResponse
  - 关联：003-FR-011 / US4-AC3

## B3. 商户列表 `GET /api/app/merchants`

- [x] **APP-MCH-01** `P1` cityId 必填
  - 步骤：不传 cityId 调用
  - 预期：校验错误/缺参提示
  - 关联：FR-012

- [x] **APP-MCH-02** `P1` period 单值过滤（recommendedPeriods 包含该值）
  - 步骤：`?cityId=..&period=OVULATION`
  - 预期：仅返回推荐周期含 OVULATION 的上架商户
  - 关联：FR-012 / US1-AC2

- [x] **APP-MCH-03** `P1` 排序 weight DESC, createdAt DESC
  - 预期：列表按权重降序、创建时间降序
  - 关联：FR-050 / US1-AC2

- [x] **APP-MCH-04** `P1` 仅返回上架商户
  - 前置：城市下有上架与下架商户
  - 预期：仅返回 online=true 商户

- [x] **APP-MCH-05** `P1` 当前城市/周期无商户返回空集合（非 500）
  - 步骤：筛选无结果条件
  - 预期：返回空列表 + 空状态语义，不报 500
  - 关联：US1-AC5 / Edge

- [x] **APP-MCH-06** `P2` 省略 categoryId 返回该城市所有上架商户（含 categoryId=NULL）
  - 步骤：不传 categoryId
  - 预期：含无分类商户；不接受 none/null 特殊值
  - 关联：FR-012 / Clarification

- [x] **APP-MCH-07** `P2` cityId 不存在/已下线返回空列表（非 404）
  - 预期：空列表 + 空状态语义
  - 关联：Edge

- [x] **APP-MCH-08** `P2` 列表项 logo 为 ImageResponse
  - 关联：003-FR-012 / US4

## B4. 商户详情 `GET /api/app/merchants/{id}`

- [x] **APP-DET-01** `P1` 四维百分制换算正确（整数）
  - 前置：原始分 S=24/L=20/E=20/I=16（满分 30/25/25/20）
  - 步骤：拉详情
  - 预期：返回 80/80/80/80，爱女指数 80
  - 关联：FR-014 / US1-AC4 / SC-003

- [x] **APP-DET-02** `P1` 仅返回上架标签（隐藏已下架）
  - 前置：3 标签其中 1 下架
  - 预期：返回 2 个标签，商户仍可见
  - 关联：FR-015 / US1-AC3

- [x] **APP-DET-03** `P1` 详情含 logo/images（ImageResponse）、地址、坐标(可空)、评价、故事
  - 预期：logo 为 ImageResponse、images 为 List<ImageResponse>；评价支持 emoji；故事文本完整
  - 关联：FR-013 / US4-AC2 / SC-007

- [ ] **APP-DET-04** `P2` 历史图片为空时返回空数组而非 500
  - 关联：Edge

- [x] **APP-DET-05** `P2` 爱女指数 10 级映射正确
  - 步骤：构造不同原始分组合验证 10 级/星级映射
  - 关联：FR-014

## B5. 商户评价 `GET /api/app/merchants/{merchantId}/reviews`

- [x] **APP-REV-01** `P1` 返回评价列表（昵称/标题/正文，支持 emoji 完整）
  - 预期：emoji（含组合 emoji）字符完整无丢失
  - 关联：FR-013 / SC-007

- [x] **APP-REV-02** `P2` 分页/排序符合约定
  - 预期：按 sortOrder 返回，分页正常

## B6. Banner `GET /api/app/banners`

- [x] **APP-BAN-01** `P1` 仅返回 online=true 的 banner
  - 前置：一条 online、一条 offline
  - 预期：仅返回 online 的那条
  - 关联：002-FR-013 / US2(banner)-AC1

- [x] **APP-BAN-02** `P1` CITY banner data 含关联城市信息，image 为 url 列表
  - 步骤：取一条 online CITY banner（关联城市 X 在线）
  - 预期：`data={id:X.id, chineseName, englishName, chineseProvince, englishProvince}`（城市名以 `chineseName` 为准），`image` 为图片 url 列表（ImageResponse）
  - 关联：002-FR-013/014 / US2(banner)-AC2

- [x] **APP-BAN-03** `P1` 防御性跳过关联实体不存在/已下线的 banner
  - 前置：banner online=true 但关联城市被删除或已下线
  - 步骤：调 App banner
  - 预期：跳过该 banner，不返回，不报错
  - 关联：002-FR-015 / Edge

- [x] **APP-BAN-04** `P2` explore 模块已移除
  - 步骤：调用原 explore 路径
  - 预期：404（explore 入口/接口已从 app 后端移除）
  - 关联：002-FR-012 / US2(banner)-AC3

- [x] **APP-BAN-05** `P2` data 结构可容纳任意 JSON（扩展性）
  - 预期：接口结构允许非 CITY 类型扩展，不破坏契约
  - 关联：002-FR-014

## B7. App 只读约束

- [x] **APP-RO-01** `P1` App 端不提供任何写入/账号/注册/登录接口
  - 步骤：尝试对 `/api/app/**` 发 POST/PUT/DELETE 写请求
  - 预期：不存在写接口（404/405）；无用户管理/注册/登录入口
  - 关联：FR-016

---

# C. 跨端 / 一致性

- [x] **X-01** `P1` admin 与 app 路径前缀互不混用
  - 步骤：分别访问 `/api/admin/**` 与 `/api/app/**`
  - 预期：两套入口独立，鉴权方式各异（JWT vs API Key），无交叉
  - 关联：FR-001

- [x] **X-02** `P1` admin+app 所有图片字段 ImageResponse 覆盖率 100%
  - 步骤：抓取两后端全部 controller 响应 schema
  - 预期：零裸 String 图片字段
  - 关联：003-SC-003 / US4 独立测试

- [x] **X-03** `P2` City 状态联动后 app banner 可见性与城市状态 100% 一致
  - 步骤：切换城市 online 状态，事件处理完成后比对 app banner
  - 预期：可见性变化与城市新状态一致
  - 关联：002-SC-002

- [x] **X-04** `P2` 同一图片被多实体引用按独立 objectKey 处理（不去重）
  - 关联：003 Edge

- [x] **X-05** `P3` 默认 admin 幂等植入（重复启动不重复创建/重置）
  - 步骤：重复启动应用
  - 预期：`loves_manager` 中 admin 仅一条，密码不被重置
  - 关联：FR-021 / SC-005 / US3-AC3 / Edge

---

## 附：执行建议

1. **优先级顺序**：先跑 P1（核心契约与安全），再 P2，最后 P3 边界。
2. **数据准备**：每个测试套先用 fixtures 准备最小数据（1 上线城市 + 1 上架商户 + 标签/评价/图片），可参考各 spec 的 *Independent Test* 段落。
3. **OSS 相关用例**（A9/A10/IMG）依赖真实或 mock 的 OSS/STS 环境；CI 中可用 LocalStack/MinIO 兼容层或对 `ImageUrlSigner`/`headObject` 打桩，保持契约断言不变。
4. **联动用例**（BAN-05/06/07、X-03）需等待 `@TransactionalEventListener(AFTER_COMMIT)` 异步处理完成后再断言。
5. **自动化落点**：admin/app 各 `src/test/java` 下按模块建 `*IntegrationTest`，用 `@SpringBootTest` + MockMvc 覆盖；执行 `./mvnw test`。

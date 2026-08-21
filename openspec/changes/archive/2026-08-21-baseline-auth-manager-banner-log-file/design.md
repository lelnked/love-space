## Context

五个域的代码稳定运行已久，但行为真源在工具链切换时丢失：

```
   SpecKit 时代                    换装 OpenSpec (d41f855)          现在
   specs/001-aiwomap-mvp/   ──✂──▶  整体删除，未迁移        ──▶   openspec/specs/ 缺 5 个域
   ├── spec.md (326 行)                                            contracts/ 缺 17 条 operation
   ├── contracts/admin-api.md                                      tests/ 缺 9 个用例文件
   └── data-model.md                                               modules.md 缺 2 行登记
```

后来用 OpenSpec 做的 7 个域（city / merchant / recommend-list / route / activity / article / featured）真源完整；这 5 个域是历史断层。

本 change 的输入是三份从**代码**反推的行为盘点（auth+manager+banner、operation-log、file），SpecKit 老规格仅用于查漏，冲突处一律以代码为准。

## Goals / Non-Goals

**Goals:**

- 让 `openspec/specs/` 覆盖代码中的全部业务能力，追溯矩阵首次完整
- 契约文件登记全部对外接口，后续改动有 delta 可比
- 用例定义就位（本轮不执行），回归轮可直接按域跑
- 现有 38 个测试方法回填 `@scenario` 锚，使既有覆盖被矩阵识别

**Non-Goals:**

- 不改变任何生产行为——所有 Scenario 描述的都是已在线上运行的逻辑
- 不执行 IT / WEB 用例（用户 2026-08-21 明确本轮不跑测试）
- 不修盘点发现的任何缺陷（见「已知偏差」）
- 不给 `StsCredentialIssuer` 加测试桩——本轮不跑测试即无必要
- 不改动 40 个把存储组件打桩的跨域测试类
- 不为 `category` / `tag` 单列域——它们归属 `merchant` 域，注册表已如此登记

## Decisions

### D1：补录以代码为准，老规格只作查漏

盘点中发现老规格与代码已大幅分叉，典型如 Banner：老规格写「banner 即 City 本身，无独立表，通过 `/api/app/explore` 按 `bannerSortOrder>0` 准入」，实际是独立 `loves_banner` 表 + 独立模块 + `/api/app/banners?positionCode=`。若照抄老规格，living specs 会一步到位地失真。

**替代方案**：以老规格为骨架再逐条核对。**否决理由**：分叉点太多（banner 整体作废、manager 创建路由不存在、日志接口路径与响应结构均不符、分页 UI 能力不存在），核对成本高于重写，且容易漏掉「老规格没写但代码有」的行为。

### D2：`operation-log` 与 `file` 的域 slug 与端

- `operation-log`：与 `recommend-list` 的连字符风格一致（代码包名是 `operationlog`，但已注册域 slug 全用连字符）。接口前缀只登记 `/api/admin/logs/*`——横切留痕虽覆盖 16 个模块的 60 个写接口，但那些路径已归属各自域，重复登记会让追溯归属混乱。本域对留痕的断言方式是「调其它域接口 → 查本域接口验证」。端填 `web`。
- `file`：端**留空**（纯 API 域）。上传组件没有独立路由页，`ImageUploader` / `ImageUploaderList` / `RichTextEditor` 嵌在 10 处业务表单里；真实上传字节流又跑不通。把壳层交互断言分派给复用它的业务域，比给 file 域单开 web 用例更合理。故 file 域只产出 `tests/file/it.md`。

### D3：`file` 域的界面交互仍写进 spec，但用例归属业务域

D2 决定 file 不产 web 用例，但上传控件的三态、浮层操作、并发上传、失败不阻塞这些行为是**真实的产品契约**，不写进 spec 就没有真源。

**做法**：spec 中保留「图片上传的界面交互」Requirement，其 Scenario 由各业务域的 web 用例覆盖（如 `tests/merchant/web.md` 断言商户表单的 LOGO 上传格）。本轮不回填这些业务域的 web 用例——那会扩大到 7 个既有域的用例改动，超出补录范围；矩阵会显示这条 Requirement 暂无用例覆盖，属已知缺口，记在 tasks 的收尾说明里。

### D4：不可自动化的行为在 spec 中显式声明

`file` 域有三条行为任何测试都覆盖不了：真实 OSS 直传、真实 STS 签发、bound 对象真实可读。与其让它们在矩阵里表现为「无覆盖」而与真正的遗漏混同，不如在 spec 里单列一条 Requirement（「图片链路的自动化覆盖边界」）显式声明，并写明桩实现的行为差异。

这样测试档位下「绑定校验不访问存储」这件事本身成为可断言的契约，而非隐藏的环境差异。

### D5：现存缺陷照实写进 spec

补录的目的是让 specs 反映**现实**。把缺陷顺手改掉会让本 change 同时具备「补录」和「变更」两种性质，delta 无法表达（ADDED 的内容与线上行为不符），也让回归失去基线。

因此所有已知偏差按现行为写入 Scenario，并在下方「已知偏差」集中登记，供后续单独开 change 处理。

## 契约映射

需向 `contracts/api-spec.json` 新增 17 条 operation，每条加 `x-requirement` 反链：

| 域 | operation | x-requirement |
|---|---|---|
| auth | `POST /api/admin/auth/login` | `auth/运营账号登录` |
| auth | `POST /api/admin/auth/logout` | `auth/当前登录人查询与登出` |
| auth | `GET /api/admin/auth/me` | `auth/当前登录人查询与登出` |
| manager | `GET /api/admin/managers/page` | `manager/运营账号分页查询` |
| manager | `POST /api/admin/managers` | `manager/运营账号管理` |
| manager | `GET /api/admin/managers/{id}` | `manager/运营账号分页查询` |
| manager | `PUT /api/admin/managers/{id}/enable` | `manager/账号启停与内置管理员保护` |
| manager | `PUT /api/admin/managers/{id}/disable` | `manager/账号启停与内置管理员保护` |
| manager | `PUT /api/admin/managers/{id}/password` | `manager/运营账号管理` |
| banner | `GET /api/admin/banners/page` | `banner/Banner 管理` |
| banner | `GET /api/admin/banners/{id}` | `banner/Banner 管理` |
| banner | `POST /api/admin/banners` | `banner/Banner 管理` |
| banner | `PUT /api/admin/banners/{id}` | `banner/Banner 管理` |
| banner | `DELETE /api/admin/banners/{id}` | `banner/Banner 管理` |
| banner | `POST /api/admin/banners/{id}/online` | `banner/Banner 上架前置校验` |
| banner | `GET /api/app/banners` | `banner/App 端 Banner 查询` |
| operation-log | `GET /api/admin/logs/page` | `operation-log/操作日志查询` |
| file | `POST /api/admin/files/upload-credentials` | `file/图片上传凭证签发` |

（合计 18 行，其中 banner 域 7 条含 app 端 1 条。）

沿用项目既有的轻量契约风格——现有 62 个 operation 均只有 `summary` + `x-requirement`，无 `responses` schema，本次不引入新格式。

## 界面实现映射

本 change 不改任何前端代码，此处仅登记 spec 中界面 Requirement 对应的现有实现位置：

| Requirement | 实现 |
|---|---|
| `auth/web 端登录页与路由守卫` | `pages/AuthPages/`、`components/auth/SignInForm.tsx`、`RequireAuth`、`api/client.ts` 响应拦截器、`layout/AppSidebar.tsx`（ADMIN 入口过滤） |
| `manager/web 端管理员管理页面` | `pages/Managers/List.tsx`（列表 + 双模式弹窗） |
| `banner/web 端 Banner 管理页面` | `pages/Banners/BannerList.tsx`、`pages/Banners/BannerForm.tsx`、`components/form/CitySelect` |
| `operation-log/web 端操作日志页面` | `pages/Logs/List.tsx`、`api/logs.ts`、`layout/AppSidebar.tsx` |
| `file/图片上传的界面交互` | `components/form/ImageUploader.tsx`、`ImageUploaderList.tsx`、`RichTextEditor.tsx`、`lib/ossUpload.ts` |

## 已知偏差（照实写入 spec，修复另开 change）

| # | 域 | 偏差 | 影响 |
|---|---|---|---|
| 1 | manager / banner | 资源不存在返回 **400** 而非 404 | 与常见 REST 约定冲突；客户端无法凭状态码区分「参数错」与「不存在」 |
| 2 | operation-log | `target` 取「第一个 UUID 入参」→ 创建类操作恒为 null；嵌套资源取到父级 id | 审计无法定位新建对象；商户评价的日志指向商户而非评价 |
| 3 | operation-log | `payload` 只写不读，无接口可查 | 审计详情只能查库 |
| 4 | operation-log | 动作命名分裂：一期 `set-online`、二期 `online` | 前端只映射前者，二期模块动作列显示英文原值 |
| 5 | operation-log | 前端模块下拉仅 8 项，缺 9 个二期模块 | 界面无法按二期模块筛选日志 |
| 6 | banner | 城市重新上架时关联 Banner **一并恢复上架** | 运营手动下架过的 Banner 会被城市上架动作意外重新上架 |
| 7 | banner | 事件级联失败仅记日志、不抛出 | 级联失效无人感知 |
| 8 | 全站 | 前端多处读 `data.detail`，后端返回 `message` | 后端中文业务错误在界面上恒落到兜底文案 |
| 9 | 全站 | 前端多处按 `data.errors[]` 解析字段级错误，后端从不返回该字段 | 死分支 |
| 10 | auth / manager | 枚举值非法、时间格式错、登录字段空白返回**英文**消息 | 与全站中文口径不一致 |
| 11 | file | `/uploads/**` 免认证 + multipart 20MB 配置 | SpecKit 本地存储时代遗留，现无对应端点，疑似死配置（未确认外部依赖，故未写入 spec） |
| 12 | file | 40 个跨域测试把存储组件打桩 | file 域行为回归不会被这些测试捕获 |
| 13 | file | 签名 URL 有效期数值无测试验证（UT 用 `any(Date)`） | 有效期改错不会被发现 |
| 14 | file | app 端 `ImageUrlSigner` 零测试覆盖 | — |
| 15 | file | `StubObjectKeyValidator` 硬编码 `bound/`，不读配置前缀 | 若改前缀配置，测试档位与生产行为分叉 |
| 16 | banner | 实体注释声称有 `ck_loves_banner_type` 约束，changelog 中并不存在 | 注释误导 |
| 17 | file | 多处注释写「复制后删除原图」，实现早已只复制不删除 | 注释误导，且有 UT 锁死「不删除」 |

## Risks / Trade-offs

- **[补录的 spec 与代码实际行为有出入]** → 五份 spec 全部从代码反推，非从老规格照抄；但盘点靠阅读，仍可能有遗漏。缓解：本轮不跑测试，首次回归轮跑这批用例时若出现失败，优先怀疑 spec 写错而非代码有 bug。
- **[WEB 用例积压扩大]** → 本轮新增的 web 用例全为 ⬜，加上既有 8 条，Playwright 恢复后需一次性补跑较大批量。
- **[`file/图片上传的界面交互` 暂无用例覆盖]** → D3 的取舍结果，矩阵会显示该 Requirement 无覆盖，属已知缺口。
- **[`operation-log` 的留痕行为跨域]** → 60 个留痕点分散在 16 个模块，本域用例只抽样验证代表性动作（创建/更新/嵌套/脱敏），不逐一覆盖。

## Migration Plan

无数据库迁移、无部署动作。本 change 只增改文档、契约与用例定义，以及测试注释。

回滚即 `git revert`，不涉及运行时。

## Open Questions

- 「已知偏差」中的 17 项是否要修、按什么优先级修，待用户决定。其中 #2（审计 target 失准）与 #6（城市上架连带 Banner 上架）影响实际业务语义，建议优先评估。
- #11 的 `/uploads/**` 与 multipart 配置是否真为死配置，需确认是否有 nginx 等外部依赖后再清理。

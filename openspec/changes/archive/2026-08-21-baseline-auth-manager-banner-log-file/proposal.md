## Why

`auth` / `manager` / `banner` / `operation-log` / `file` 五个域是早期用 SpecKit 做的，`d41f855` 换装 OpenSpec 时把 SpecKit 工件整体删除，规格没有迁移过来。后果是这五个域**代码在线上跑，但没有行为真源**：

- `openspec/specs/` 下无这五个域——living specs 失真，回归与追溯断链
- `contracts/api-spec.json` 46 条路径中**零条**命中这五个域（含登录、账号管理、Banner、审计日志、上传凭证）
- `tests/` 下无对应目录，`tests/modules.md` 甚至没登记 `operation-log` 与 `file`
- 追溯矩阵因此从未覆盖过登录鉴权、账号启停、Banner 级联、操作审计、图片绑定这些高风险路径

这不是新增功能，是**把既有行为补录成真源**。补录后追溯矩阵首次覆盖全部代码能力，后续任何改动才有 delta 可比。

## What Changes

**不改变任何生产行为。**所有 delta 均为 `ADDED Requirements`，描述的是已在线上运行的逻辑，从代码反推得出（SpecKit 老规格仅用作查漏提示，与代码冲突处一律以代码为准）。

- 新增五份 living spec：`openspec/specs/{auth,manager,banner,operation-log,file}/spec.md`
- `contracts/api-spec.json` 补 17 条 operation（auth 3、manager 6、banner 7、operation-log 1、file 1），逐一加 `x-requirement` 反链
- `tests/modules.md` 新增 `operation-log` 与 `file` 两行登记（`auth`/`manager`/`banner` 已登记，仅核对路径前缀）
- 新增用例文件：`tests/{auth,manager,banner,operation-log}/{it,web}.md`、`tests/file/it.md`
- 给这五个域现有的 38 个测试方法回填 `@scenario` 注释锚（当前全部无锚，追溯矩阵认不到）

**零生产代码改动。**本 change 只产出规格、契约、用例定义与测试注释锚，不碰任何 Java/TS 业务代码。

**不做的事**：

- 不执行任何 IT / WEB 用例（用户 2026-08-21 明确：本轮只补录，不跑测试）。用例文件产出后状态均为 ⬜ 未测试，留待后续回归轮执行。
- 不修本次盘点发现的任何现存缺陷（详见 design.md「已知偏差」），它们照实写进 spec，是否修复另开 change。
- 不给 `StsCredentialIssuer` 加 test-profile 桩——该桩的唯一价值是让 `file` 域成功分支可实跑 IT，本轮既不跑测试即无必要；若将来要跑，另开小 change。

## Capabilities

### New Capabilities

- `auth`：运营账号登录认证与会话——JWT 签发与校验、登录失败口径、`/me` 与 `/logout`、SecurityConfig 授权链、前端登录页与路由守卫。
- `manager`：运营账号管理——分页过滤、创建（强制 MEMBER 角色）、启停、重置密码、内置 admin 保护、ADMIN 角色门禁。
- `banner`：Banner 管理——admin CRUD 与上下架前置校验、名称唯一、图片 objectKey 绑定、城市事件级联（上架/下架双向、删除下架）、app 端按 positionCode 只读查询与城市不可见时的剔除。
- `operation-log`：运营操作审计——`@OperationLog` 横切留痕（60 个写接口）、切面取值与脱敏语义、异步落库与失败隔离、日志分页查询。
- `file`：图片上传凭证与对象存储——STS 直传签名、objectKey 两段式生命周期（`images/` → `bound/`）与 `validateAndBind` 校验契约、签名访问 URL。

### Modified Capabilities

（无。五个域在 living specs 中均不存在，全部为新建。）

## Impact

**新增文件**：`openspec/specs/` 下 5 个 spec；`tests/` 下 9 个用例文件；`test-evidence/regression/{operation-log,file}/` 两个目录。

**修改文件**：`contracts/api-spec.json`（+17 operation）；`tests/modules.md`（+2 行登记）；五个域现有测试类（**仅加 `@scenario` 注释，不改断言、不改测试逻辑**）。

**不涉及**：数据库迁移、生产代码逻辑、前端代码、测试执行。

**测试覆盖的已知边界**：

- `file` 域是「UT 主导、IT 边缘」——真实 OSS 直传、真实 AssumeRole、bound 对象可读三条无法自动化，spec 中标为人工/联调验证。`POST /api/admin/files/upload-credentials` 的 200 分支另需 test-profile 桩才能实跑 IT，本轮不加桩，该用例定义写出但标注前置条件不满足。
- 40 个跨域测试类（admin 31 + app 9）把 `ObjectKeyValidator`/`ImageUrlSigner` 用 `@MockitoBean` 打桩，它们对 file 域零覆盖——本 change 不改这些测试，但会在 spec 中记录这一事实。
- WEB 用例产出后将全部为 ⬜ 未测——远程 Playwright `100.103.199.95:9233` 持续不可达，与既有 8 条积压用例一并等待补跑。

**追溯**：补录完成后 `openspec/specs/` 覆盖全部 12 个业务域（现有 7 + 新增 5），代码中除 `category`/`tag`（归属 `merchant` 域）外再无未登记能力。

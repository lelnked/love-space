## 1. 域注册表

- [x] 1.1 `tests/modules.md` 新增 `operation-log` 行：职责「运营操作审计（横切留痕 + 日志查询）」、接口路径前缀 `/api/admin/logs/*`、端 `web`、页面域 `/love-space/logs`、用例文件 `tests/operation-log/{it,web}.md`
- [x] 1.2 `tests/modules.md` 新增 `file` 行：职责「图片上传凭证与对象存储」、接口路径前缀 `/api/admin/files/*`、端**留空**（纯 API 域）、页面域留空并注明上传组件嵌于各业务表单、用例文件 `tests/file/it.md`
- [x] 1.3 核对 `auth` / `manager` / `banner` 三行的接口路径前缀与页面域与代码一致（盘点结论：完全一致，仅需确认后打勾）

## 2. 契约补录（`contracts/api-spec.json`）

沿用既有轻量风格（`summary` + `x-requirement`，不加 `responses` schema）。

- [x] 2.1 auth 域 3 条：`POST /api/admin/auth/login`、`POST /api/admin/auth/logout`、`GET /api/admin/auth/me`
- [x] 2.2 manager 域 6 条：`/api/admin/managers/page` GET、`/api/admin/managers` POST、`/api/admin/managers/{id}` GET、`/{id}/enable` PUT、`/{id}/disable` PUT、`/{id}/password` PUT
- [x] 2.3 banner 域 7 条：admin 的 page GET、`{id}` GET/PUT/DELETE、根 POST、`{id}/online` **POST**（注意是 POST 不是 PUT），以及 app 的 `GET /api/app/banners`
- [x] 2.4 operation-log 域 1 条：`GET /api/admin/logs/page`（注意带 `/page` 后缀）
- [x] 2.5 file 域 1 条：`POST /api/admin/files/upload-credentials`
- [x] 2.6 逐条核对 `x-requirement` 反链值与五份 delta spec 的 Requirement 名完全一致（格式 `{domain}/{Requirement 名}`）
- [x] 2.7 `python3 -c "import json;json.load(open('contracts/api-spec.json'))"` 校验 JSON 合法，且 `git diff --stat` 确认未打乱既有 62 条 operation 的格式

## 3. living spec 落地

- [x] 3.1 五份 delta spec 已在 propose 阶段产出并 validate 通过（22 Requirement / 78 Scenario）——确认后打勾
- [x] 3.2 `openspec validate baseline-auth-manager-banner-log-file` 通过

## 4. 用例定义（只写定义，本轮不执行）

所有新建用例状态一律 `⬜ 未测试`，执行存证字段留空。TC 编号从各域 001 起（五个域均为全新域，无既有编号）。

- [x] 4.1 新建 `tests/auth/it.md` + `tests/auth/web.md`，覆盖 auth 域 4 个 Requirement / 13 个 Scenario
- [x] 4.2 新建 `tests/manager/it.md` + `tests/manager/web.md`，覆盖 manager 域 4 个 Requirement / 15 个 Scenario
- [x] 4.3 新建 `tests/banner/it.md` + `tests/banner/web.md`，覆盖 banner 域 5 个 Requirement / 18 个 Scenario（含 app 端 4 个 Scenario）
- [x] 4.4 新建 `tests/operation-log/it.md` + `tests/operation-log/web.md`，覆盖 operation-log 域 4 个 Requirement / 16 个 Scenario
- [x] 4.5 新建 `tests/file/it.md`，覆盖 file 域中可测的 Scenario；`file/图片上传凭证签发#签发合法图片类型的上传凭证` 标注前置条件不满足（测试档位下 STS 不可用），`file/图片上传的界面交互` 的 Scenario 按 D3 不在本域产用例
- [x] 4.6 每个 Scenario 至少一条用例覆盖；每层内按 happy / boundary / error / state 四象限配比
- [x] 4.7 新建存证目录 `test-evidence/regression/{auth,manager,banner,operation-log,file}/` — 已建，但空目录不进 git，首次回归轮 runner 写存证时会自动创建，本项实际无版本化产出

## 5. 测试注释锚回填

给这五个域现有测试方法补 `// @scenario: {domain}/{Requirement 名}#{Scenario 名}` 注释。**只加注释，不改断言、不改测试逻辑、不新增测试方法。**

- [x] 5.1 `AuthControllerWebMvcTest` 3 个方法回填（登录成功 / 密码错误 / 停用账号）
- [x] 5.2 `ManagerServiceTest` 5 个 + `ManagerControllerSecurityTest` 3 个方法回填
- [x] 5.3 `BannerServiceTest` 3 个 + `BannerControllerIT` 2 个 + app `BannerReadIT` 2 个方法回填
- [x] 5.4 `OperationLogControllerWebMvcTest` 2 个 + `OperationLogAspectIT` 1 个方法回填
- [x] 5.5 `AliyunOssObjectKeyValidatorTest` 8 个 + `AliyunOssImageUrlSignerTest` 3 个 + `FileServiceTest` 3 个 + `FileControllerIT` 3 个方法回填
- [x] 5.6 `RichTextImagesTest` 中未带锚的 2 个方法回填到 `file` 域（`rewritesEverySrcAndKeepsRestOfHtml` 已有 activity 域锚，保持不动）
- [x] 5.7 编译确认：两端 `./mvnw -q -DskipTests compile` 通过（仅注释改动，不应有编译影响）

## 6. 收尾核对（不执行测试）

- [x] 6.1 `node scripts/generate-traceability-matrix.js --change baseline-auth-manager-banner-log-file` 生成矩阵
- [x] 6.2 核对矩阵 — 结论如下：
  - 用例状态：99 条新增用例全部 ⬜ 未测试 ✅
  - 关联需求名：与 spec Scenario **100% 逐字对应**，无悬空用例、无拼写不符 ✅
  - 反向覆盖：初次生成报「DELETE /api/admin/banners/{id} 无 IT 用例」，已补 `TC-banner-IT-017` 修复 ✅
  - **正向覆盖遗留 34 项 ⚠**：矩阵脚本的正向判定要求每个 Scenario 有 WEB/APP 用例**或** UT `@scenario` 锚——**IT 用例不计入**。这 34 个 Scenario 均有 IT 用例覆盖，但补录域现存 UT 仅 40 个方法，无法一一对应 78 个 Scenario。**不为洗绿而乱挂锚**，如实保留。其中 4 项（`file/图片上传的界面交互`）是 design D3 的预期缺口
- [x] 6.3 `node scripts/generate-traceability-matrix.js` 刷新全局矩阵 — 报 81 项「悬空用例：关联需求在 specs 中不存在」，**属预期**：五份 delta spec 尚在 change 内，未合入 `openspec/specs/`；全局矩阵读 living specs，故找不到。**archive 同步后即消失**，届时需重跑本命令复核 12 个域全部在册
- [x] 6.4 **不跑** `.quality-gate.yml` 的测试项与 `/run-api-test`、`/run-web-test`（用户 2026-08-21 明确本轮不执行测试）；仅确认注释改动未破坏编译（见 5.7）

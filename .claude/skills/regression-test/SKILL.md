---
name: regression-test
description: 回归测试编排（OpenSpec 版）。按域(--module)/优先级(--priority)/全量三种范围，对 tests/{domain}/ 的 living 用例按端编排执行：IT 恒有（api-test-runner），WEB/APP 为可插拔插件端（web-test-runner / app-test-runner），按实际存在的用例文件与已安装的 runner 自适应，未启用的端自动跳过。存证落 test-evidence/regression/{domain}/，跑完刷新全局追溯矩阵。living specs 与 tests/{domain}/ 本身就是现行为真源，回归与交付共用同一份用例，只差执行范围。当用户说「跑回归 / 回归测试 / regression / 合入后验证 / 发版前全量」时使用。
metadata:
  author: codeing-test-workflow
---

# 回归测试编排（OpenSpec 版，端插拔自适应）

三种执行范围（对应方案 §5.2），四种项目形态（纯 API / web+API / app+API / web+app+API）同一命令：

```bash
/regression-test --module auth          # 变更影响域全量（分支上，archive 前后）
/regression-test --priority P0,P1       # 合入主干后
/regression-test                        # 发版前全量
```

## 端与 runner 的对应（唯一的映射表，执行与否由文件存在性决定）

| 端 | 用例文件 | runner agent | 归属 |
|----|----------|--------------|------|
| IT | `tests/{domain}/it.md` | `api-test-runner` | 核心基线，恒有 |
| WEB | `tests/{domain}/web.md` | `web-test-runner` | web 插件 |
| APP | `tests/{domain}/app.md` | `app-test-runner` | app 插件 |

**端自适应规则（接线点 3）**：某域某端是否执行 = 该用例文件存在 **且** 对应 runner agent 已安装（`.claude/agents/<runner>.md`）**且** 该域在 `tests/modules.md`「端」列有登记。三者缺一跳过该端并明确告知（插件已拔但 `web.md`/`app.md` 留档是正常形态——用例是资产，再插回即恢复执行），不误标 ❌。

## 执行流程(主 agent 按此操作)

### 1. 圈定范围

- `--module <domain>`：只跑该域，须已在 `tests/modules.md` 注册。
- `--priority P0,P1`：按存在文件通配扫 `tests/*/{it,web,app}.md`，筛出 `优先级` 命中的用例。
- 无参：全部域全部用例（含 `tests/flows/web.md` 跨域 smoke，存在才跑）。

按上表核对每个域实际启用的端，列出本轮将执行的 域 × 端 × TC 数量，向用户确认后再跑（全量轮次耗时长）。

### 2. 前置校验（按端独立，互不牵连）

- `tests/modules.md` 白名单校验（回归轮**强制**）：IT baseUrl、WEB 前端地址、APP appId 都必须命中白名单。
- IT：后端可达；WEB：前端实例 API base 指向 Tailscale IP（探测逻辑复用 run-web-test §2）；APP：设备/模拟器在线（`maestro devices`，复用 run-app-test 前置）。
- 不满足只影响对应端：后端不可达跳过 IT、前端不可达跳过 WEB、设备不在线跳过 APP，明确告知，不误标 ❌。

### 3. 编排执行（IT 先行，WEB/APP 随后；三端回写文件物理隔离，同域也不冲突）

- 每个域先调起 `api-test-runner`：`--module <domain>` 回归模式，跑该域 `it.md` **全部**用例（含 ✅，回归就是重验），存证 `test-evidence/regression/<domain>/<TC完整ID>/`（每轮覆盖，历史靠 git）。
- 再按该域启用的插件端调起 `web-test-runner` 跑 `web.md`、`app-test-runner` 跑 `app.md`；prompt 注入方式见各端编排 skill 的模板（IP/端口约束照抄 run-web-test；appId/设备约束照抄 app 插件的 run-app-test）。
- `--priority` 模式把筛出的 TC 完整 ID 清单塞进各 runner 的 `--cases`。

### 4. 刷新全局追溯矩阵（必做）

```bash
node scripts/generate-traceability-matrix.js    # 无参 = 全局核对
```

读覆盖核对结论（未覆盖 Scenario / 悬空用例 / 状态存疑），纳入汇报。

### 5. 汇总报告

- 按域汇总，域内按端（IT / WEB / APP）分列：总数 / ✅ / ❌ / 未执行（含被跳过的端及原因），失败用例附原因与存证路径。
- 矩阵核对结论。
- 失败用例的处置建议：行为回归 → 开修复 change（`/opsx:propose fix-*`）；用例过期 → 也开 change 走 delta 更新用例，不手改。
- HTML 报告（可选，用户要报告时生成）：

```bash
node scripts/generate-regression-report.js   # 输出 test-evidence/regression/report.html
```

  按域分节（域内再按端分 IT / WEB / APP，标题与优先级取自 tests/{domain}/*.md，按存在文件通配、未启用端不渲染），UT 全局一节（数据源
  `test-evidence/regression/ut-summary.json`，跑完 UT 后由编排方写入；缺省则省略该节）。
  失败用例默认展开并标红，支持按 P0/P1/P2 优先级筛选。截图/存证用相对路径引用，报告与存证同目录整体可归档。
  视觉真源在 `scripts/report-template.html`（生成器只填占位符）——改样式改模板，别改生成脚本。

## 不做什么

- 不修改用例定义字段（步骤/预期/关联需求/来源），runner 只回写状态。
- 不代为启动前后端服务或移动端设备/模拟器。
- 不跑白名单外的任何地址与 appId。
- 不硬编码端列表——新增/移除测试端只动插件文件与 modules.md 登记，本 skill 零改动。
- 交付轮（按 change 清单）不归本 skill 管——用 `/run-api-test --change`、`/run-web-test --change` 与 `/run-app-test --change`。

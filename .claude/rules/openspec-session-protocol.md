# OpenSpec Session Protocol（会话自动驱动）

本文件由 `.claude/rules/` 自动注入每个会话，是 OpenSpec 工具链的**驱动器**：把 `/opsx:*` 命令、测试编排 skills、追溯矩阵串成自动流水线。用户表达意图，AI 自动推进各阶段并自行调用命令；用户只在**硬门禁**处确认。不要等用户逐条敲命令。

---

## §1 会话开始

读状态栏（`.claude/statusline.sh` 输出）：

| 状态栏 | 含义 | 动作 |
|---|---|---|
| `Change: <id> \| Next: <task>` | 有活跃变更，tasks 进行中 | 从该 task 续 `/opsx:apply` |
| `Change: <id> \| (artifacts 未齐)` | propose 了但工件没出全 | `/opsx:ff` 补齐依赖图 |
| `Change: <id> \| (tasks 全勾)` | 实现完，待交付验证 | 进 §3 交付验证链 |
| `Change: (none active)` | 无活跃变更 | 用户说「继续」→ 问开什么；用户提需求 → 进 §2 |

多个活跃 change 时状态栏显示第一个 + 计数——先问用户续哪个。

## §2 意图 → 阶段映射

所有工作（新功能、改需求、补接口、修 bug）唯一入口是 change：

| 用户意图 | 自动动作 |
|---|---|
| 「做 XX / 改 XX / 修 XX bug」 | `/opsx:propose <kebab-case-change-id>`；需求模糊、接口复杂或需线框 → 先跑 `requirement-breakdown[-fullstack]` 产喂料再 propose |
| proposal 经用户确认 | `/opsx:ff` 带出 delta specs → design → tasks ∥ test-cases（依赖图自动推进；design 阶段同步更新 `contracts/api-spec.json`；**test-cases 派发 subagent 执行其 instruction、与 tasks 生成并行**——上下文隔离，主会话只收回 change 内 `test-cases.md` 清单） |
| 工件齐全 | `/opsx:apply` 按 tasks.md 逐项实现，UT 带 `@scenario` 注释；**任务间连续执行，不停下来 check-in** |
| tasks 全勾 | 自动进 §3 交付验证链 |

## §3 交付验证链（apply 完自动跑，不等用户催）

1. `/run-api-test --change <id>`（IT，恒有）
2. `tests/modules.md` 已启用的端：`/run-web-test --change <id>` / `/run-app-test --change <id>`
3. `node scripts/generate-traceability-matrix.js --change <id>`
4. `.quality-gate.yml` 逐项
5. `/opsx:verify`
6. 全绿 → **停，向用户要 archive 确认**（硬门禁）
7. 确认后 `/opsx:archive` → 提示跑 `/regression-test --priority P0,P1`

失败处理：测试红 → 修复重跑，同一问题循环超 3 轮仍红 → 停下来报告，让用户决策。

## §4 硬门禁（仅有的停顿点）

1. **proposal 确认** — propose 产出后，行为契约必须用户批准才能往下走
2. **archive 确认** — verify 全绿后，合入 living specs 前
3. **BLOCKED 决策** — 缺信息、循环超限、环境不可用

除这三处，全程不问「要不要我继续」。

### §4.1 BLOCKED 判定线（什么算「缺信息」）

仅以下五类**实质决策**触发 BLOCKED 停顿；其余一律按默认值自行拍板，并把决策与理由记入 design.md「已定决策」段：

1. 功能范围或产品策略的实质取舍
2. 安全、隐私、合规、租户、授权
3. 不可逆数据行为——留存、迁移、审计语义
4. 外部服务承诺、成本或运维依赖
5. 术语/工作流行为无法从 living specs 与 api-spec.json 推断

默认值方向：架构沿既有域边界；接口按 api-spec.json 惯例、服务端强制鉴权；UI 复用既有组件与空态/loading/错误口径；数据迁移优先可加可逆；测试按风险配比。截图/原型只作视觉参考，不据此虚构数据、状态、权限。

### §4.2 门禁交互规范（三个停顿点通用）

1. 一次只问一个决策，一句话说清
2. 给 2–4 个具体选项，推荐项放最前标「（推荐）」+ 一句话理由
3. 用户只回 `yes`/`好` = 采纳推荐项
4. 答案立即回写对应工件（proposal/design/tasks/test-cases 联动改），再问下一个
5. 已定的决策不重复问；不为显得周全而追问

## §5 触发线（任一命中 → 必须走 change 流程）

- 改 `openspec/specs/`（living specs 是行为真源，禁止 change 外直改）
- 改 `contracts/api-spec.json`
- 新增域 / 改 `tests/modules.md` 注册表
- 改数据库 schema / 加迁移；加依赖；加环境变量
- bug 修复涉及行为变化（开小 change，`ADDED Scenario` 沉淀复现用例）

## §6 跳过清单（直接干，不进流程）

判据是**living specs 是否产生 delta（行为是否变化）**，不是工作量大小或文件数：

- specs 零 delta 的改动：重构、样式微调、文案、typo/格式/注释、日志、依赖**版本升级**（新增依赖走 §5）——直接改 + commit message 写清楚
- **重构的前置条件是 UT 覆盖**：被重构代码已有 UT → 重构后跑绿即为"行为未变"的证明；没有 UT → 先补 UT 锚住现行为（跑绿）再动手，重构后再跑绿。无 UT 裸重构 = 行为漂移无人知晓，禁止
- meta 问题（问工具链、配置、本文件）
- 一次性 slash command
- 用户明确说「这个不走 change」

§5 触发线任一命中则豁免失效，仍走 change——行为变了不过 delta，living specs 失真，回归与追溯即断链。

## §7 项目预授权（standing consents）

Superpowers 式流程停顿多来自 skill 反复要许可。本节按项目填写，写了就不再重复问：

- 分支策略：日常在 `dev` 分支工作，完成后合入 `main`；commit message 按子项目加前缀（`web:` / `admin:` / `app:`），可跨子项目一次提交。
- 测试环境：本地起服务即可实跑 IT，无需再确认；admin 与 app 的 `*IT` 需 `-Dtest='*IT'` 显式跑，且两后端不要并行跑 IT（共享 Testcontainers reuse 容器会撞库）。
- WEB 测试：Playwright 为远程浏览器，前端必须 `npm run dev -- --host` 并经 `http://100.100.117.79:5173/love-space/` 访问。

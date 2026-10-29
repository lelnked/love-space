# Proposal: ai-scene-thinking-toggle（场景模型配置支持「深度思考」开关）

## Why

方舟部分模型（如 doubao-seed 系列）默认开启深度思考，耗时与 token 成本显著上升，而当前场景只能配主/备模型，无法控制思考模式。本 change 在场景「模型配置」中加一个深度思考开关（默认关闭），链路：运营在模型配置弹窗勾选 → 存入场景 → admin 提示词测试与 app 生成调用方舟时按场景开关传 `thinking`。

## What Changes

| # | 功能 | 端 | 后台菜单 |
|---|---|---|---|
| F1 | 场景新增 `thinkingEnabled` 配置，随主/备模型一起保存与返回 | admin + web | AI 管理 › AI 模型 |
| F2 | 生成调用按场景开关向方舟传 `thinking`，并写入参数快照 | ai 基础库 + admin + app | — |

明确**不做**：
- 不做 `auto` 模式，只有开 / 关两态；
- 不做按模型粒度的开关（主、备模型共用场景的一个开关）；
- 连接测试（`test-connection`）不传 `thinking`，行为不变；
- 不按模型能力做白名单过滤（不判断某模型是否支持 thinking）。

### 全局约定

- **权限**：沿用现有 admin 鉴权链。
- **操作日志**：沿用现有「配置场景模型」的操作日志 action，不新增。

---

### F1 场景深度思考配置

**规则**
- `thinkingEnabled` 为布尔，存量场景与新建场景默认 `false`（关闭）。
- 随 `PUT /api/admin/ai/scenes/{id}/models` 一起提交；body 中未传或为 `null` 视为 `false`。
- 保存即对之后的新请求生效，与主/备模型规则一致；其他校验不变。

**表**：无新表，`loves_ai_scene` 新增列

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| thinking_enabled | boolean | NOT NULL DEFAULT false | 是否开启深度思考 |

**API**

| 方法 | 路径 | 参数 | 返回 |
|---|---|---|---|
| PUT | `/api/admin/ai/scenes/{id}/models` | body：`{primaryModel*, backupModel?, thinkingEnabled?}` | 204；校验失败 400（同现状） |
| GET | `/api/admin/ai/scenes/…`（列表/详情） | 同现状 | SceneItem / SceneDetail 新增 `thinkingEnabled: boolean` |

**页面 `/ai/models`（模型配置弹窗）**

```
┌ 配置模型：<场景名> (<sceneId>) ───────────────────────┐
│ 主模型 *   [请选择 ▼]①            [测试连接]          │
│ 备用模型   [不使用 ▼]②            [测试连接]          │
│ 深度思考   [○ 关]③  开启后响应更慢、token 消耗更高    │
│ 提示：连接成功只代表服务可达，不代表生成质量          │
│                                   [取消]  [保存]④     │
└───────────────────────────────────────────────────────┘
```
③ 初值取场景 `thinkingEnabled`（默认关）；④ → `PUT /api/admin/ai/scenes/{id}/models` 带上 `thinkingEnabled`。

---

### F2 生成调用透传 thinking

**规则**
- `AiGenerator.generate` 增加 `thinkingEnabled` 参数；每次调用方舟 body 显式带 `thinking: {type: "enabled" | "disabled"}`（关闭时也显式传 `disabled`，否则默认开启思考的模型仍会思考）。
- 主模型、重试、备用模型调用都用同一开关。
- 参数快照 `request_params` 新增 `thinkingEnabled` 字段。
- 调用方：admin 提示词测试（`POST /api/admin/ai/prompt-templates/{id}/test`）、app 生成接口，均读场景 `thinkingEnabled`。

**表**：无新表（`request_params` 为 JSON，多一个键）。

**API**：无接口签名变化；生成记录详情 `requestParams` 多出 `thinkingEnabled`。

---

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `ai`：「场景模型配置」新增 `thinkingEnabled`；SceneItem/SceneDetail 结构新增字段；生成调用与参数快照新增 thinking。

## Impact

- **数据库**：admin Liquibase 新增 `029-add-ai-scene-thinking.sql`（加列带默认值，可回滚 drop column）；app 端 `ddl-auto: validate`，实体同步加字段。
- **love-space-ai**：`ArkClient.chat` 增加可空 `thinking` 参数（null 不传，供 ping 用）；`AiGenerator.generate/params` 增参。
- **love-space-admin**：`AiScene` 实体、`AiSceneModelsRequest`、`AiSceneResponse`、`AiModelService.setModels`、`AiPromptTemplateService` 调用点。
- **love-space-app**：`AiScene` 实体、`AiGenerationService` 调用点。
- **love-space-web**：`src/api/ai.ts` 类型、`src/pages/AiModels/ModelConfigModal.tsx` 加开关。
- **契约**：`contracts/api-spec.json` 修改 1 个请求体 + Scene 响应 schema。
- **风险**：`AI_MODELS` 中若有模型不认 `thinking` 参数（如部分第三方模型），显式传 `disabled` 可能被方舟拒绝（400 → UPSTREAM_ERROR）。实现后用 `AI_MODELS` 实测各模型；如有不兼容模型再议。

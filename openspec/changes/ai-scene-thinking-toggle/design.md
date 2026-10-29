## Context

场景（`loves_ai_scene`）目前只存主/备模型；生成经 `love-space-ai` 的 `AiGenerator` → `ArkClient.chat` 调方舟，请求体不含 `thinking`。doubao-seed 等模型不传时默认思考，慢且费 token。admin（提示词测试）与 app（生成接口）各有一份 `AiScene` 实体，共用同一张表；schema 由 admin Liquibase 管，app `ddl-auto: validate`。

## Goals / Non-Goals

**Goals:** 场景级深度思考开关，默认关；所有生成调用显式传 `thinking`；参数快照可追溯。

**Non-Goals:** `auto` 模式；按模型粒度开关；按模型能力过滤；连接测试带 thinking。

## Decisions

- **开关挂在场景而非模型**：模型配置本就按场景存，与主/备模型同一接口保存，不新增接口。备选「每个模型一个开关」→ 需求未要求，不做。
- **关闭时显式传 `{"type":"disabled"}`**：不传等于让模型用默认值，达不到「默认不开启」。
- **`ArkClient.chat` 加可空 `Boolean thinking`**：null 不传（ping 用），非 null 映射 enabled/disabled。备选在 `AiProperties` 加全局开关 → 不能按场景区分，否决。
- **`AiGenerator.generate(messages, primary, backup, thinkingEnabled)`**：签名直接加参，两个调用方同步改；`params()` 快照加 `thinkingEnabled`。
- **请求字段可空**：`AiSceneModelsRequest.thinkingEnabled` 为 `Boolean`，null → false，老客户端不传不报错。
- **迁移**：`029-add-ai-scene-thinking.sql` 加列 `boolean NOT NULL DEFAULT false`，rollback `drop column`。
- **契约**：`contracts/api-spec.json` 的 `AiSceneModelsRequest` 加 `thinkingEnabled`（x-requirement 已为 `ai/场景模型配置`）；Scene 响应现无 schema 登记，不补。

### 界面实现映射

| 线框区域 | 组件/文件 |
|---|---|
| ③ 深度思考开关（proposal 线框）/ spec ⑦ | `love-space-web/src/pages/AiModels/ModelConfigModal.tsx`，复用 `src/components/form/switch/Switch`（如存在，否则 checkbox） |
| ④ 保存 | 同文件 `handleSubmit` → `setAiSceneModels` 带 `thinkingEnabled` |
| 类型 | `love-space-web/src/api/ai.ts` 的 `AiScene` 与 `setAiSceneModels` 入参 |

## Risks / Trade-offs

- [部分模型不认 `thinking` 参数，传 disabled 返回 400] → 实现后对 `AI_MODELS` 逐个实测；有不兼容的再议（如按模型跳过）。
- [app 实体漏加字段] → app `ddl-auto: validate` 不会报（多列允许），但生成会永远按 false；由 UT 覆盖 app 调用点传值。

## 已定决策

- 关闭显式传 disabled、ping 不传（理由见上）。
- 请求未传视为 false（不是「保持原值」），与 backupModel「不传即清空」口径一致。

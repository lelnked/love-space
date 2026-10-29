## 1. AI 基础库（love-space-ai）

- [x] 1.1 `ArkClient.chat` 增加可空 `Boolean thinking`：非 null 时 body 放 `thinking: {type: enabled|disabled}`，null 不放；`ping` 传 null
- [x] 1.2 `AiGenerator.generate` 增加 `boolean thinkingEnabled`，所有尝试（INITIAL/RETRY/FALLBACK）透传；`params()` 快照加 `thinkingEnabled`
- [x] 1.3 UT：body 含 disabled / 切备用沿用 enabled / ping 不含 thinking（@scenario ai/生成调用深度思考参数#关闭时显式传 disabled、#开启时传 enabled 且切备用沿用、#连接测试不传 thinking）

## 2. admin 后端

- [x] 2.1 Liquibase `029-add-ai-scene-thinking.sql`：`loves_ai_scene.thinking_enabled boolean NOT NULL DEFAULT false`，带 rollback；master include
- [x] 2.2 `AiScene` 实体加 `thinkingEnabled`；`AiSceneResponse` 加字段
- [x] 2.3 `AiSceneModelsRequest` 加 `Boolean thinkingEnabled`；`AiModelService.setModels` null → false 保存
- [x] 2.4 `AiPromptTemplateService` 测试调用传 `scene.isThinkingEnabled()`
- [x] 2.5 UT：开启深度思考 / 未传视为关闭 / 创建场景默认 false（@scenario ai/场景模型配置#开启深度思考、#未传深度思考视为关闭；ai/业务场景管理#创建场景）

## 3. app 后端

- [x] 3.1 `AiScene` 实体加 `thinkingEnabled`；`AiGenerationService` 生成传入；参数快照随 `params()` 自动带上
- [x] 3.2 UT 编译与既有用例跑绿（调用点签名变更）

## 4. web

- [x] 4.1 `src/api/ai.ts`：`AiScene.thinkingEnabled: boolean`、`AiSceneModelsRequest.thinkingEnabled?: boolean`
- [x] 4.2 `ModelConfigModal.tsx` ⑦深度思考开关：复用 `components/form/switch/Switch` 受控模式，初值 `scene.thinkingEnabled ?? false`，旁注「开启后响应更慢、token 消耗更高」；保存时提交；提交中禁用；错误沿用现有 toast
- [x] 4.3 `npm run build`（tsc）通过

## 5. 契约

- [x] 5.1 `contracts/api-spec.json` `AiSceneModelsRequest` 加 `thinkingEnabled`

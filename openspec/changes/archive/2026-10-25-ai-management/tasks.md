## 1. love-space-ai 基础库（F0）

- [x] 1.1 新建 `love-space-ai/`：pom（parent spring-boot-starter-parent 4.0.6，`com.loves:love-space-ai`，依赖 spring-boot-starter-web + test）、Maven Wrapper、`.gitignore`；`./mvnw install` 能产出 jar
- [x] 1.2 `config/AiProperties`（`love-space.ai.*`：apiKey、baseUrl、models、temperature、topP、maxTokens、timeoutSeconds=30、maxRetries=1）+ `AiAutoConfiguration` + `AutoConfiguration.imports`；apiKey/models 为空时 WARN 不抛
- [x] 1.3 `AiModelCatalog.models()`：去空白、去空项、保序；`contains(model)`
- [x] 1.4 `prompt/PromptRenderer.validate`：变量名正则/重复 → 声明错误；`{{name}}` 未声明 → 列出变量名 + 位置（SYSTEM_PROMPT / USER_PROMPT_TEMPLATE）
- [x] 1.5 `PromptRenderer.render`：必填缺失抛 `PromptValidationException extends IllegalArgumentException`；选填缺失空串；非字符串按 JSON 文本代入
- [x] 1.6 `ark/ArkClient`：RestClient + JdkClientHttpRequestFactory（connect 5s、read=timeoutSeconds），chat/completions 请求（空参数不传），返回原文 + 解析结构；异常分类 TIMEOUT / RATE_LIMITED / UPSTREAM_ERROR
- [x] 1.7 `generation/AiStatus`、`TriggerReason`、`AiAttempt`、`AiResult`；单次判定：REFUSED / EMPTY / 剥离代码块后 JSON 对象或数组 → SUCCESS，否则 FORMAT_ERROR
- [x] 1.8 `AiGenerator.generate(messages, primary, backup)`：主模型 1+N 次 → 备用 1+N 次；attempts 按 INITIAL/RETRY/FALLBACK 记录；tokens 合计、总耗时、最终取最后一次；未配 apiKey 直接 UPSTREAM_ERROR「未配置 ARK_API_KEY」
- [x] 1.9 `AiGenerator.ping(model)`：`max_tokens=1`，2xx → ok
- [x] 1.10 UT `PromptRendererTest`（`@scenario ai/提示词变量校验与渲染#未声明变量被指出位置`、`#必填变量缺失渲染失败`、`#选填变量缺失替换为空串`、`#非字符串值按 JSON 文本代入`、`#变量名非法或重复为声明错误`）
- [x] 1.11 UT `AiGeneratorTest`，JDK `HttpServer` 假方舟（`@scenario ai/生成重试、切备用与结果判定#主模型首次成功`、`#主模型失败后重试成功`、`#主模型耗尽后切备用`、`#无备用模型时全部失败`、`#各失败状态判定`、`#代码块包裹的 JSON 视为成功`）；ping 成功/失败；`AiModelCatalogTest`（`@scenario ai/AI 调用配置来自环境变量#模型下拉来自环境变量`）
- [x] 1.12 `scripts/mock-ark.mjs`：按 design D4 表实现各 mock 模型（仅 node 内置 http），文件头写启动方式与配套环境变量

## 2. admin：数据库与公共设施

- [x] 2.1 `love-space-admin/pom.xml` 引入 `com.loves:love-space-ai`；`application.yml`（含 test profile）按 design D2 映射 `ARK_*` / `AI_*`
- [x] 2.2 Liquibase `026-create-ai.sql`：`loves_ai_scene`、`loves_ai_prompt_template`、`loves_ai_generation`、`loves_ai_generation_attempt` 四表 + 唯一约束 + 索引 + rollback；master include
- [x] 2.3 `common/exception` 新增 `ResourceNotFoundException`、`ConflictException`；`GlobalExceptionHandler` 加 404 / 409 / `ObjectOptimisticLockingFailureException`→409「内容已被他人更新，请刷新」
- [x] 2.4 UT `GlobalExceptionHandlerTest` 补三种映射

## 3. admin：业务场景与模型配置（F1 / F2）

- [x] 3.1 `modules/ai/entity/AiScene` + `AiSceneRepository`（含 `@Lock(PESSIMISTIC_WRITE) findLockedById`、`existsBySceneId`、`findBySceneId`）
- [x] 3.2 DTO：`AiSceneCreateRequest`（sceneId 正则、name≤64、description≤500，中文 message）、`AiSceneUpdateRequest`、`AiSceneEnabledRequest`、`AiSceneModelsRequest`、`AiModelTestConnectionRequest`、`AiSceneItemResponse`（含 activeTemplate 摘要）、`AiSceneOptionResponse`、`AiConnectionTestResponse`
- [x] 3.3 `AiSceneService`：分页（keyword 模糊 sceneId/name、enabled、createdAt 倒序、PageQuery 校正）、options、详情 404、创建 409「scene_id 已存在」、更新只改 name/description、启停；activeTemplate 摘要批量查模板避免 N+1
- [x] 3.4 `AiModelService`：models 列表；`setModels` 校验主必填、在白名单、主备不同（400）；`testConnection` 校验白名单后 ping
- [x] 3.5 `AiSceneController`（`/api/admin/ai/scenes`：page、options、{id}、POST 201、PUT、PUT enabled 204、PUT models 204）与 `AiModelController`（`/api/admin/ai/models`、`/test-connection`）；写接口 `@OperationLog` 依 spec「AI 管理写操作留痕」表，测试连接不加；中文 javadoc
- [x] 3.6 UT `AiSceneServiceTest`（`@scenario ai/业务场景管理#创建场景`、`#sceneId 重复被拒绝`、`#更新不改 sceneId`、`#按关键字与状态筛选`、`#禁用场景`）；`AiSceneControllerTest` 校验（`#sceneId 格式非法`）
- [x] 3.7 UT `AiModelServiceTest`（`@scenario ai/场景模型配置#配置主备模型`、`#模型不在白名单`、`#主备模型相同`、`#清除备用模型`；`ai/生成重试、切备用与结果判定#连接测试`）
- [x] 3.8 UT 权限：`@scenario ai/AI 管理权限与菜单#MEMBER 可访问 AI 管理`、`#未登录被拒绝`

## 4. admin：提示词模板（F3）

- [x] 4.1 `AiPromptTemplate` 实体（`variables` / `lastTestInputs` / `lastTestContent` jsonb，`createdBy/updatedBy`，`@Version`）+ Repository（`findMaxSeq`、按 sceneId 分页 seq 倒序、条件写回测试结果的 `@Modifying` JPQL：`WHERE id=:id AND version=:version`，不改 version）
- [x] 4.2 DTO：`AiPromptVariable`、`AiPromptTemplateCreateRequest`、`AiPromptTemplateUpdateRequest`（version 必填）、`AiPromptTemplateTestRequest`、`AiPromptTemplatePublishRequest`、`AiPromptTemplateItemResponse`、`AiPromptTemplateDetailResponse`、`AiPromptTemplateTestResponse`
- [x] 4.3 `AiPromptTemplateService.create`：场景 404、变量声明错误 400、锁场景行分配 seq、createdBy/updatedBy 取 `OperatingContext.currentUsername()`
- [x] 4.4 `update`：生效中 409「生效中的模板不能修改」、version 不等 409、变量声明错误 400、`tested=false`
- [x] 4.5 `test`：短事务校验（未声明变量/声明错误 400、必填缺失 400、场景无主模型 409）→ 事务外 `AiGenerator.generate` → 条件写回 last_test_*（SUCCESS 置 tested=true；0 行不写回）；不写生成记录
- [x] 4.6 `publish`：锁场景行；version 409、tested=false 409「请先测试」、变量问题 400；更新 activeTemplateId + publishedAt/By；禁用场景允许
- [x] 4.7 `AiPromptTemplateController`（page、{id}、POST 201、PUT、test、publish 204）+ `@OperationLog`（create/update/publish，test 不加）
- [x] 4.8 UT `AiPromptTemplateServiceTest`（`@scenario ai/提示词模板维护#新建模板自动分配 seq`、`#引用未声明变量也可保存`、`#变量名非法被拒绝`、`#修改已测试模板重置测试标记`、`#生效中模板不能修改`、`#版本冲突`、`#模板列表按 seq 倒序`）
- [x] 4.9 UT 测试接口（mock `AiGenerator`）：`@scenario ai/提示词模板测试#测试成功置已测试`、`#测试失败保留未测试`、`#存在未声明变量拒绝测试`、`#必填变量缺失拒绝测试`、`#场景未配主模型拒绝测试`、`#测试不写生成记录`；测试期间 version 变化不写回
- [x] 4.10 UT 发布：`@scenario ai/提示词模板发布#发布切换生效模板`、`#发布旧版本即回退`、`#未测试不能发布`、`#禁用场景也可发布`
- [x] 4.11 UT 留痕：`@scenario ai/AI 管理写操作留痕#发布留痕`、`#测试不留痕`；`operation-log/运营写操作留痕#AI 场景创建留痕`、`#AI 测试类调用不留痕`

## 5. app：生成接口（F4 / F5 ②③④）

- [x] 5.1 `love-space-app/pom.xml` 引入 `love-space-ai`；`application.yml` 映射 `ARK_*` / `AI_*` 与 `AI_LIMIT_PER_ANON_DAILY` / `AI_LIMIT_GLOBAL_DAILY`（`AiLimitProperties`）；注入 `Clock`（Asia/Shanghai）
- [x] 5.2 `common/exception` 新增 `ConflictException`(409)、`TooManyRequestsException`(429)；`GlobalExceptionHandler` 加两者 + `MissingRequestHeaderException`→400
- [x] 5.3 `modules/ai/entity`：`AiScene`、`AiPromptTemplate`（只读映射）、`AiGeneration`、`AiGenerationAttempt`（jsonb `columnDefinition`，与 026 迁移逐列对齐）+ Repository（按 clientRequestId 查、当日计数两条、条件改 TIMEOUT `WHERE status='PROCESSING'`）
- [x] 5.4 DTO `AiGenerationCreateRequest`（sceneId 必填、clientRequestId ≤64、appVersion ≤32、inputs Map）、`AiGenerationResponse{requestId, clientRequestId, sceneId, status, content}`；record javadoc 完整
- [x] 5.5 `AiGenerationService`：`X-Anonymous-Id` ≤128；inputs 每值 ≤500 字符 / 整体 ≤4096 字节（指出变量名）→ 去重 → 场景 404 / 409「场景不可用」→ 渲染 → 额度 429 → 插 PROCESSING（唯一冲突转去重）→ 事务外 generate → 回写 + 批量插 attempts → 返回
- [x] 5.6 去重分支：同 scene+anon：终态原样返回；PROCESSING 未超阈值 409「生成中，请稍后重试」；超阈值条件改 TIMEOUT 并返回；参数不一致 409「clientRequestId 冲突」
- [x] 5.7 `AiGenerationController` `POST /api/app/ai/generations`（`@RequestHeader("X-Anonymous-Id")`），中文 javadoc；更新 `love-space-app/docs/openapi.json`（生成脚本在 HEAD 已因周期推荐 sealed interface 无法运行，本次手工登记）
- [x] 5.8 UT `AiGenerationServiceTest`（mock `AiGenerator`）：`@scenario ai/App 生成接口#生成成功`、`#已处理的失败返回 200`、`#场景不存在`、`#场景不可用`、`#必填变量缺失`、`#响应不含调用明细`
- [x] 5.9 UT 去重：`@scenario ai/生成请求去重#重复提交返回原结果`、`#生成中重复提交`、`#PROCESSING 超时就地失败`、`#clientRequestId 冲突`、`#重复提交不受场景状态影响`；并发唯一冲突走去重
- [x] 5.10 UT 落库：`@scenario ai/生成记录与调用明细落库#成功生成写完整记录`、`#重试与切备用逐条记明细`、`#选填变量未传记 null`；`ai/AI 调用配置来自环境变量#请求快照不含密钥`
- [x] 5.11 UT 额度（固定 Clock）：`@scenario ai/生成接口每日额度#匿名 ID 超出每日次数`、`#全站超出每日总量`、`#昨日记录不计入`、`#去重不占额度`
- [x] 5.12 UT 长度与头：`@scenario ai/生成接口输入长度限制#单个变量超长`、`#边界值通过`、`#整体超长`；`ai/App 生成接口#缺少匿名 ID`、`#API Key 无效`

## 6. web：API 层与外壳

- [x] 6.1 `src/api/ai.ts`：类型逐字对齐后端 DTO（SceneItem、TemplateItem/Detail、TestResult、PromptVariable…）+ design D9 列出的 15 个函数，全用 `apiClient`
- [x] 6.2 `src/pages/AiPrompts/promptVariables.ts`：`findUndeclaredVariables` 返回 `{name, location}`；与后端同一正则
- [x] 6.3 `src/layout/AppSidebar.tsx` 新增一级「AI 管理」，subItems：业务配置 `/ai/scenes`、模型配置 `/ai/models`、提示词管理 `/ai/prompts`，不按角色过滤（`ai/AI 管理权限与菜单#侧栏菜单`）
- [x] 6.4 `src/App.tsx` 在 `<AppLayout />` 内注册 7 个路由（design D9）

## 7. web：业务配置页 `/ai/scenes`

- [x] 7.1 `AiScenes/List.tsx` ①② `FilterBar`：① text `keyword`（placeholder「搜索 scene_id / 名称」）、② select `enabled`（全部/启用/禁用）；Apply/Reset 回第 1 页
- [x] 7.2 列表 `DataTable`：scene_id、名称、说明（超长截断 + title）、状态 badge（●启用 / ○禁用）、创建时间 `MM-DD HH:mm`、操作 ④编辑 ⑤启用/禁用；loading「加载中...」、空态「暂无场景」、加载失败 toast 后端 detail
- [x] 7.3 ⑥ `Pagination` 表格右下，默认 20、可切 200
- [x] 7.4 ③「+ 新增场景」与 ⑤：⑤ `useConfirm()` 二次确认 → `setAiSceneEnabled` → toast + 刷新当前页（`ai/业务配置页面#禁用需确认`）
- [x] 7.5 `AiScenes/SceneFormModal.tsx` ⑦–⑩：⑦ sceneId（新增必填 + 正则即时提示；编辑 `readOnly` + 「创建后不可修改」）、⑧ name 必填 ≤64、⑨ description ≤500 textarea、⑩ 保存（提交中禁用）；编辑先 `getAiScene` 回填；后端 409/400 message toast 且弹窗不关（`#新增场景弹窗`、`#编辑时 scene_id 只读`、`#重复 scene_id 提示`）

## 8. web：模型配置页 `/ai/models`

- [x] 8.1 `AiModels/List.tsx` ① keyword 筛选 + 列表：scene_id、名称、主模型（空 →「未配置（⚠ 不可生成）」警示色）、备用模型（空 →「—」）、操作 ②配置；loading/空态/错误同 7.2；分页同 7.3（`ai/模型配置页面#未配置主模型提示`）
- [x] 8.2 `AiModels/ModelConfigModal.tsx` ③ 主模型 `Select`（必填，选项 `listAiModels`，加载失败 toast）、⑤ 备用模型 `Select`（首项「不使用」= null）；主备相同即时提示并禁用保存
- [x] 8.3 ④ 两个「测试连接」按钮：loading 态 → `✓ 可达 {latencyMs}ms` 或 `✗ {message}`；未选模型时禁用；固定提示文案「连接成功只代表服务可达，不代表生成质量」（`#测试连接显示结果`）
- [x] 8.4 ⑥ 保存 → `setAiSceneModels` → 关闭 + 刷新列表；400 message toast（`#保存模型配置`）

## 9. web：提示词管理 页面1 / 页面2

- [x] 9.1 `AiPrompts/SceneList.tsx` ① keyword 筛选；列：scene_id、名称、当前生效版本（`V{seq} {name}` / 「未发布（⚠ 不可生成）」）、发布人、发布时间（空「—」）、操作 ②管理模板 → `/ai/prompts/{sceneId}`；loading/空态/错误/分页同 7.x（`ai/提示词管理页面#场景列表显示未发布`）
- [x] 9.2 `AiPrompts/TemplateList.tsx` 头部：标题「提示词管理 › {name} ({sceneId})」（场景通过 options 或 page 按 sceneId 查得；不存在 toast 并回页面1）
- [x] 9.3 列表 ⑤ 勾选框 + 列：版本 V{seq}、名称、修改说明、状态（●生效中 / 未生效）、测试（✓已测 / ✗未测）、更新人、更新时间、操作；seq 倒序；loading/空态「暂无模板，点击新建」/错误；分页
- [x] 9.4 ③「对比所选(n)」：勾选数 2~3 时可用，跳 `/ai/prompts/{sceneId}/compare?ids=…`（`#对比按钮需勾选 2~3 个`）
- [x] 9.5 ④「+ 新建模板」`<Link>` → `/ai/prompts/{sceneId}/templates/new`；⑥ 编辑/查看（生效中文案「查看」）→ `/ai/prompt-templates/{id}`；⑦ 复制 → `new?copyFrom={id}`
- [x] 9.6 ⑧ 发布：生效中行不显示；`tested=false` 置灰 + title「请先测试」；点击 `useConfirm`「发布后 V{新} 立即对新请求生效，V{旧} 失效」→ 先 `getAiPromptTemplate` 取最新 version → `publishAiPromptTemplate` → toast + 刷新；409 toast（`#未测试模板发布置灰`、`#发布二次确认后切换生效`）

## 10. web：模板编辑与测试页（页面3）

- [x] 10.1 `AiPrompts/TemplateEditor.tsx` 外壳：编辑路由 `getAiPromptTemplate`（加载中/失败 toast 回页面2）；新建路由读 `copyFrom` 预填（名称、提示词、变量、说明）；标题「提示词管理 › {场景名} › V{seq} {name}」/「新建模板」；左右两栏（窄屏上下堆叠）
- [x] 10.2 左栏 ⑨ 名称（必填 ≤64）、⑩ 修改说明、⑪ 系统提示词 textarea（必填）、⑫ 输入模板 textarea（必填）；生效中全部只读
- [x] 10.3 ⑬ 未声明变量实时提示：`findUndeclaredVariables` 每项一行「{位置}引用了未声明变量 {{name}}」，警示色（`ai/模板编辑与测试页面#未声明变量实时提示`）
- [x] 10.4 ⑭ `VariableTable.tsx`：行 = 变量名输入（正则/重复即时提示）、必填勾选、说明、✕删除；「+ 添加」；生效中只读无增删
- [x] 10.5 ⑮ 保存：新建 POST → `navigate(/ai/prompt-templates/{id}, replace)`；编辑 PUT 带 version → 用响应刷新本地 version/tested；409/400 toast 且保留本地内容；生效中不渲染保存按钮（`#生效中模板只读可测试`）
- [x] 10.6 `AiPrompts/TestPanel.tsx` 顶部：显示场景主模型（主）/ 备用模型（备用），未配主模型显示「未配置主模型，无法测试」
- [x] 10.7 ⑯ 变量输入框由当前（含未保存）变量表生成，必填带 *，默认回填 `lastTestInputs`
- [x] 10.8 ⑰ 运行测试：禁用条件（未声明变量 / 声明错误 / 必填未填 / 无主模型）各自提示原因；dirty 或未保存的新建 → 先保存再 `testAiPromptTemplate`；运行中按钮 loading 且禁用；400/409 toast（`#运行测试前自动保存`）
- [x] 10.9 ⑱ 测试结果：最后一次 `lastTestAt · lastTestBy · ✓成功/✗失败`、耗时（秒）、tokens（若本次响应有）、输出 JSON 格式化 `<pre>` 或错误码 + 信息；无测试记录显示「尚未测试」；测试后重新 `getAiPromptTemplate` 刷新 tested/version
- [x] 10.10 底部状态条：「生效中/未生效 · ✓已测试 / ✗内容已修改，需重新测试」；[返回]（→ 页面2）[保存]⑮ [发布]⑲
- [x] 10.11 ⑲ 发布：`tested=false` 或 dirty 时置灰 + 提示「请先运行测试」；确认框同 9.6 → publish → toast + 刷新详情（`#未测试时发布置灰`）

## 11. web：对比页（页面4）

- [x] 11.1 `AiPrompts/lineDiff.ts`：输入 N 段文本，返回每段每行是否"不在其他任一段出现"；带 `ponytail:` 注释说明行级上限
- [x] 11.2 `AiPrompts/Compare.tsx`：解析 `ids`，数量不在 2~3 → toast「请选择 2~3 个模板对比」并回页面2（`ai/模板对比页面#ids 数量非法`）；并行 `getAiPromptTemplate`，任一失败 toast 回页面2；加载中占位
- [x] 11.3 每列：标题 `V{seq} {name}（●生效中 / 未生效）`；系统提示词、输入模板、变量（必填 *）、最后一次测试输入（格式化 JSON，无则「—」）、⑳ 最后一次测试输出（无则「尚未测试」）；差异行高亮；底部 [返回]（`#并排对比`）

## 12. 构建、部署与文档

- [x] 12.1 `deploy/deploy-apps.sh` 两个容器加 `ARK_*` / `AI_*` `-e`（app 加 `AI_LIMIT_*`），`ARK_API_KEY`、`AI_MODELS` 纳入必填检查；`deploy/.env.example` 补占位与注释
- [x] 12.2 `docs/部署正式环境.md`、`docs/部署测试环境.md`：打包首步 `cd love-space-ai && ./mvnw install`；列新环境变量
- [x] 12.3 `.claude/skills/deploy` 构建步骤加 love-space-ai install
- [x] 12.4 `docs/部署服务器初始化.md`：nginx `limit_req_zone` + AI location（proposal F5 ① 原文）、real_ip 核对说明、方舟控制台限额与费用预警（`ai/生成接口 IP 限流` 两个 Scenario 由部署后手工/IT 验证）
- [x] 12.5 `docs/admin-开发规范.md`、`docs/app-开发规范.md` 命令段补「先 install love-space-ai」与 mock-ark 启动方式；`docs/app-开发规范.md` 说明 `/api/app/ai/generations` 是 spec 明确要求的写接口例外

## 13. 收尾自检

- [x] 13.1 `love-space-ai` `./mvnw test`；admin / app `./mvnw test` 全绿（按 memory 带库地址与 API key 环境变量）
- [x] 13.2 用 admin 迁移后的库以 `ddl-auto=validate` 起一次 app，确认实体与 026 迁移一致
- [x] 13.3 `love-space-web` `NODE_ENV=development npm run build` + `npm run lint` 通过
- [x] 13.4 `node scripts/generate-app-openapi.js --check`（HEAD 上已因周期推荐 sealed interface 失败，非本 change 引入；ai 接口已手工登记）；`contracts/api-spec.json` 与实现路径/字段核对一致

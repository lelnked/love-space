# ai IT 用例

> admin 后端 baseUrl `http://localhost:21423`（test profile），登录 fixture：`admin` / `8@y2eoRLyStM*UVU`；app 后端 baseUrl `http://localhost:8081`，请求头 `X-API-Key` 认证 + `X-Anonymous-Id`。
> 需真实生成的用例前置条件注明「admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key」；admin 侧前置统一 `AI_MODELS=model-a,model-b`。
> 涉及 `psql` 的步骤查询 admin/app 共用业务库（schema 由 admin Liquibase 管理）。IP 限流用例（nginx）仅在测试环境执行。
> F0 模块内行为（渲染、重试判定、状态判定）以 `love-space-ai` UT 覆盖，此处只断言接口可观察部分。

### TC-ai-IT-001: GET /api/admin/ai/models 返回环境变量配置的模型列表
**关联需求**: ai/AI 调用配置来自环境变量#模型下拉来自环境变量
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1models/get
**来源**: ai-management
**优先级**: P0
**测试步骤**:
1. 前置：admin 进程配置 `AI_MODELS=model-a, model-b`
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. GET /api/admin/ai/models
**预期结果**: 返回 200，body 为 `["model-a","model-b"]`（已去首尾空白、保持配置顺序）；响应中无任何 API Key 字段
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-001/`
**最后更新**: 2026-10-24

### TC-ai-IT-002: POST /api/admin/ai/scenes 创建场景成功
**关联需求**: ai/业务场景管理#创建场景
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1scenes/post
**来源**: ai-management
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. POST /api/admin/ai/scenes，body `{"sceneId":"cycle_advice_<随机后缀>","name":"周期建议","description":"按周期给建议"}`
3. GET /api/admin/ai/scenes/{id}
**预期结果**: 创建返回 201，SceneDetail 中 `enabled=true`、`primaryModel=null`、`backupModel=null`、`activeTemplate=null`、`createdAt` 非空；详情字段与提交一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-002/`
**最后更新**: 2026-10-24

### TC-ai-IT-003: POST /api/admin/ai/scenes sceneId 重复返回 409
**关联需求**: ai/业务场景管理#sceneId 重复被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1scenes/post
**来源**: ai-management
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 前置：已创建 sceneId 为 S 的场景
3. 再次 POST /api/admin/ai/scenes，body `{"sceneId":"<S>","name":"重复"}`
**预期结果**: 返回 409，`message` 为「scene_id 已存在」；场景总数不变
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-003/`
**最后更新**: 2026-10-24

### TC-ai-IT-004: POST /api/admin/ai/scenes sceneId 格式与长度边界校验
**关联需求**: ai/业务场景管理#sceneId 格式非法
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1scenes/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 以 sceneId `Cycle-Advice` 创建
3. 以单字符 sceneId `a` 创建
4. 以 65 字符 sceneId（`a` + 64 个 `b`）创建
5. 以 2 字符 sceneId（如 `z<1 位随机数字>`，不存在则用之）创建
6. 以恰 64 字符 sceneId（`x` + 63 个 `y`，带随机前缀保证唯一且总长 64）创建
7. 以 sceneId 合法但 name 为 65 字符创建
**预期结果**: 步骤 2/3/4/7 均返回 400 且 `message` 为中文校验信息，场景未创建；步骤 5/6（2 字符与 64 字符边界）返回 201
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-004/`
**最后更新**: 2026-10-24

### TC-ai-IT-005: PUT /api/admin/ai/scenes/{id} 更新名称且忽略 body 中 sceneId
**关联需求**: ai/业务场景管理#更新不改 sceneId
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}/put
**来源**: ai-management
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 前置：场景 sceneId=S（如 `cycle_advice_<后缀>`）
3. PUT /api/admin/ai/scenes/{id}，body `{"sceneId":"other","name":"周期建议-改","description":"新说明"}`
4. GET /api/admin/ai/scenes/{id}
**预期结果**: PUT 返回 200 且返回 SceneDetail；详情 `name=周期建议-改`、`description=新说明`、`sceneId` 仍为 S；不存在 sceneId=`other` 的场景
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-005/`
**最后更新**: 2026-10-24

### TC-ai-IT-006: GET /api/admin/ai/scenes/page 按关键字与状态筛选
**关联需求**: ai/业务场景管理#按关键字与状态筛选
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1scenes~1page/get
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 前置：启用场景 `cycle_<后缀>`（周期建议）与已禁用场景 `mood_<后缀>`（情绪回应）
3. GET /api/admin/ai/scenes/page?keyword=cycle_<后缀>&page=1&size=20
4. GET /api/admin/ai/scenes/page?keyword=情绪回应&enabled=false&page=1&size=20
5. GET /api/admin/ai/scenes/page?page=1&size=20
**预期结果**: 步骤 3 仅返回 `cycle_<后缀>`；步骤 4 仅返回 `mood_<后缀>` 且 `enabled=false`；步骤 5 按 `createdAt` 倒序，响应为 `PageResponse{content,page,size,totalElements,totalPages}`、page=1
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-006/`
**最后更新**: 2026-10-24

### TC-ai-IT-007: GET /api/admin/ai/scenes/options 全量下拉与详情 404
**关联需求**: ai/业务场景管理#创建场景
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1scenes~1options/get
**来源**: ai-management
**优先级**: P2
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. GET /api/admin/ai/scenes/options
3. GET /api/admin/ai/scenes/<不存在的 uuid>
**预期结果**: 步骤 2 返回 200 数组，每项仅含 `id, sceneId, name, enabled`，包含启用与禁用场景；步骤 3 返回 404
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-007/`
**最后更新**: 2026-10-24

### TC-ai-IT-008: PUT /api/admin/ai/scenes/{id}/enabled 禁用场景
**关联需求**: ai/业务场景管理#禁用场景
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}~1enabled/put
**来源**: ai-management
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 前置：一个启用中的场景
3. PUT /api/admin/ai/scenes/{id}/enabled，body `{"enabled":false}`
4. GET /api/admin/ai/scenes/{id}
5. PUT /api/admin/ai/scenes/{id}/enabled，body `{"enabled":true}`
6. GET /api/admin/ai/scenes/{id}
**预期结果**: 步骤 3、5 返回 204；步骤 4 详情 `enabled=false`；步骤 6 详情 `enabled=true`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-008/`
**最后更新**: 2026-10-24

### TC-ai-IT-009: PUT /api/admin/ai/scenes/{id}/models 配置主备模型
**关联需求**: ai/场景模型配置#配置主备模型
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}~1models/put
**来源**: ai-management
**优先级**: P0
**测试步骤**:
1. 前置：admin 进程 `AI_MODELS=model-a,model-b`；一个场景
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. PUT /api/admin/ai/scenes/{id}/models，body `{"primaryModel":"model-a","backupModel":"model-b"}`
4. GET /api/admin/ai/scenes/{id}
**预期结果**: 步骤 3 返回 204；详情 `primaryModel=model-a`、`backupModel=model-b`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-009/`
**最后更新**: 2026-10-24

### TC-ai-IT-010: PUT /api/admin/ai/scenes/{id}/models 模型不在白名单返回 400
**关联需求**: ai/场景模型配置#模型不在白名单
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}~1models/put
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：`AI_MODELS=model-a,model-b`；场景已配 `primaryModel=model-a`
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. PUT …/models，body `{"primaryModel":"model-x"}`
4. PUT …/models，body `{"primaryModel":"model-a","backupModel":"model-x"}`
5. PUT …/models，body `{}`（缺 primaryModel）
6. GET /api/admin/ai/scenes/{id}
**预期结果**: 步骤 3/4/5 均返回 400 且 `message` 为中文；步骤 6 场景仍为 `primaryModel=model-a`，模型未变
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-010/`
**最后更新**: 2026-10-24

### TC-ai-IT-011: PUT /api/admin/ai/scenes/{id}/models 主备相同返回 400
**关联需求**: ai/场景模型配置#主备模型相同
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}~1models/put
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：`AI_MODELS=model-a,model-b`；一个场景（记录当前模型配置）
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. PUT …/models，body `{"primaryModel":"model-a","backupModel":"model-a"}`
4. GET /api/admin/ai/scenes/{id}
**预期结果**: 步骤 3 返回 400 中文信息；步骤 4 模型配置与前置一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-011/`
**最后更新**: 2026-10-24

### TC-ai-IT-012: PUT /api/admin/ai/scenes/{id}/models 清除备用模型
**关联需求**: ai/场景模型配置#清除备用模型
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}~1models/put
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：场景已配 `primaryModel=model-a`、`backupModel=model-b`
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. PUT …/models，body `{"primaryModel":"model-a","backupModel":null}`
4. GET 详情
5. PUT …/models，body `{"primaryModel":"model-a","backupModel":""}`
6. GET 详情
**预期结果**: 步骤 3、5 返回 204；步骤 4、6 详情 `backupModel=null`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-012/`
**最后更新**: 2026-10-24

### TC-ai-IT-013: POST /api/admin/ai/models/test-connection 连接测试
**关联需求**: ai/生成重试、切备用与结果判定#连接测试
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1models~1test-connection/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；`model-a` 在 `AI_MODELS` 内
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. POST /api/admin/ai/models/test-connection，body `{"model":"model-a"}`（方舟/mock 可达）
4. 将 mock 停掉（或令 ARK_BASE_URL 指向不可达地址后重启 admin）再执行步骤 3
**预期结果**: 步骤 3 返回 200 `{ok:true, latencyMs:<正整数>, message}`；步骤 4 返回 200 `ok=false` 且 `message` 含失败原因
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-013/`
**最后更新**: 2026-10-24

### TC-ai-IT-014: POST /api/admin/ai/models/test-connection 模型不在白名单返回 400
**关联需求**: ai/场景模型配置#模型不在白名单
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1models~1test-connection/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. POST /api/admin/ai/models/test-connection，body `{"model":"model-x"}`（不在 AI_MODELS）
**预期结果**: 返回 400 中文信息，不调用方舟
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-014/`
**最后更新**: 2026-10-24

### TC-ai-IT-015: POST /api/admin/ai/prompt-templates 新建模板自动分配 seq
**关联需求**: ai/提示词模板维护#新建模板自动分配 seq
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates/post
**来源**: ai-management
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 前置：新场景 S，其下已新建 V1、V2
3. POST /api/admin/ai/prompt-templates，body `{"sceneId":"<S>","name":"温柔版","systemPrompt":"你是助手","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true,"description":"年龄"}],"remark":"首版"}`
**预期结果**: 返回 201，TemplateDetail `seq=3`、`tested=false`、`active=false`、`createdBy=admin`、`variables` 与提交一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-015/`
**最后更新**: 2026-10-24

### TC-ai-IT-016: POST /api/admin/ai/prompt-templates 引用未声明变量也可保存
**关联需求**: ai/提示词模板维护#引用未声明变量也可保存
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. POST /api/admin/ai/prompt-templates，`userPromptTemplate` 为 `心情{{mood}}`，`variables` 为 `[]`
**预期结果**: 返回 201，模板已保存、`tested=false`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-016/`
**最后更新**: 2026-10-24

### TC-ai-IT-017: POST /api/admin/ai/prompt-templates 变量声明非法或重复返回 400
**关联需求**: ai/提示词模板维护#变量名非法被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 新建模板，variables `[{"name":"1abc","required":false}]`
3. 新建模板，variables `[{"name":"age","required":true},{"name":"age","required":false}]`
**预期结果**: 两次均返回 400 且 `message` 为中文；该场景模板数不变
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-017/`
**最后更新**: 2026-10-24

### TC-ai-IT-018: POST /api/admin/ai/prompt-templates 场景不存在返回 404
**关联需求**: ai/提示词模板维护#新建模板自动分配 seq
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates/post
**来源**: ai-management
**优先级**: P2
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 新建模板，`sceneId` 为不存在的 `no_such_scene`
**预期结果**: 返回 404
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-018/`
**最后更新**: 2026-10-24

### TC-ai-IT-019: PUT /api/admin/ai/prompt-templates/{id} 修改已测试模板重置 tested
**关联需求**: ai/提示词模板维护#修改已测试模板重置测试标记
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}/put
**来源**: ai-management
**优先级**: P0
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key（mock 返回合法 JSON）；场景已配主模型；一个未生效模板 T 已测试成功（`tested=true`），记录其 version=v
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. PUT /api/admin/ai/prompt-templates/{T}，body 含新 `systemPrompt` 及 `"version":v`，其余字段同原值
**预期结果**: 返回 200，`tested=false`、`version>v`、`systemPrompt` 为新值、`updatedBy=admin`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-019/`
**最后更新**: 2026-10-24

### TC-ai-IT-020: PUT /api/admin/ai/prompt-templates/{id} 生效中模板不能修改
**关联需求**: ai/提示词模板维护#生效中模板不能修改
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}/put
**来源**: ai-management
**优先级**: P0
**测试步骤**:
1. 前置：场景当前生效模板为 V3（经测试+发布）
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. PUT /api/admin/ai/prompt-templates/{V3}，body 改 name，带正确 version
4. GET /api/admin/ai/prompt-templates/{V3}
**预期结果**: 步骤 3 返回 409「生效中的模板不能修改」；步骤 4 内容与修改前一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-020/`
**最后更新**: 2026-10-24

### TC-ai-IT-021: PUT /api/admin/ai/prompt-templates/{id} 版本冲突返回 409
**关联需求**: ai/提示词模板维护#版本冲突
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}/put
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 前置：未生效模板 T，先以正确 version 修改一次使 version 递增（记新值 v2）
3. PUT /api/admin/ai/prompt-templates/{T}，body 带旧 version（v2-1）
**预期结果**: 返回 409「内容已被他人更新，请刷新」；内容不变
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-021/`
**最后更新**: 2026-10-24

### TC-ai-IT-022: GET /api/admin/ai/prompt-templates/page 按 seq 倒序且仅生效项 active
**关联需求**: ai/提示词模板维护#模板列表按 seq 倒序
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1page/get
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：场景 S 下 V1、V2、V3，V2 已测试并发布（admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key）
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. GET /api/admin/ai/prompt-templates/page?sceneId=<S>&page=1&size=20
4. GET /api/admin/ai/prompt-templates/<不存在 uuid>
**预期结果**: 步骤 3 `content` 顺序 seq 为 3、2、1，仅 V2 `active=true`，条目含 `id, seq, name, remark, active, tested, lastTestAt, publishedAt, publishedBy, updatedBy, updatedAt`；步骤 4 返回 404
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-022/`
**最后更新**: 2026-10-24

### TC-ai-IT-023: POST /api/admin/ai/prompt-templates/{id}/test 测试成功置已测试
**关联需求**: ai/提示词模板测试#测试成功置已测试
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1test/post
**来源**: ai-management
**优先级**: P0
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key（mock 返回合法 JSON）；场景已配主模型；模板声明必填变量 `age`、`tested=false`
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. POST /api/admin/ai/prompt-templates/{id}/test，body `{"inputs":{"age":28}}`
4. GET /api/admin/ai/prompt-templates/{id}
**预期结果**: 步骤 3 返回 200，`success=true`、`model` 非空、`content` 为 JSON、`latencyMs`/`testedAt` 非空；步骤 4 `tested=true`、`lastTestInputs={"age":28}`、`lastTestBy=admin`、`lastTestSuccess=true`、`version` 与内容不变
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-023/`
**最后更新**: 2026-10-24

### TC-ai-IT-024: POST /api/admin/ai/prompt-templates/{id}/test 模型返回非 JSON 测试失败
**关联需求**: ai/提示词模板测试#测试失败保留未测试
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1test/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key（mock 始终返回非 JSON 文本）；模板 `tested=false`
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. POST …/{id}/test，完整 inputs
4. GET 模板详情
**预期结果**: 步骤 3 返回 200，`success=false`、`errorCode=FORMAT_ERROR`、`content=null`；步骤 4 `tested=false`、`lastTestOutput` 为 mock 原始输出、`lastTestSuccess=false`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-024/`
**最后更新**: 2026-10-24

### TC-ai-IT-025: POST /api/admin/ai/prompt-templates/{id}/test 存在未声明变量返回 400
**关联需求**: ai/提示词模板测试#存在未声明变量拒绝测试
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1test/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 前置：模板输入模板引用 `{{mood}}`，variables 未声明 mood；场景已配主模型
3. POST …/{id}/test，body `{"inputs":{}}`
**预期结果**: 返回 400，`message` 含 `mood` 与「输入模板」；不调用模型
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-025/`
**最后更新**: 2026-10-24

### TC-ai-IT-026: POST /api/admin/ai/prompt-templates/{id}/test 必填变量缺失返回 400
**关联需求**: ai/提示词模板测试#必填变量缺失拒绝测试
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1test/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 前置：模板声明必填 `age`，记录其 `lastTestAt`/`lastTestInputs`
3. POST …/{id}/test，body `{"inputs":{}}`
4. POST …/{id}/test，body `{"inputs":{"age":null}}`
5. GET 模板详情
**预期结果**: 步骤 3、4 均返回 400 且 `message` 含 `age`；步骤 5 `last_test_*` 字段与前置一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-026/`
**最后更新**: 2026-10-24

### TC-ai-IT-027: POST /api/admin/ai/prompt-templates/{id}/test 场景未配主模型返回 409
**关联需求**: ai/提示词模板测试#场景未配主模型拒绝测试
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1test/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 前置：新场景未配置模型（`primaryModel=null`），其下一个无变量的模板
3. POST …/{id}/test，body `{"inputs":{}}`
**预期结果**: 返回 409
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-027/`
**最后更新**: 2026-10-24

### TC-ai-IT-028: POST /api/admin/ai/prompt-templates/{id}/test 不写生成记录
**关联需求**: ai/提示词模板测试#测试不写生成记录
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1test/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key
2. psql 查询 `select count(*) from loves_ai_generation`，记为 N
3. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
4. 对任一可测试模板 POST …/{id}/test
5. 再次查询 `loves_ai_generation` 记录数
**预期结果**: 步骤 4 返回 200；步骤 5 记录数仍为 N
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-028/`
**最后更新**: 2026-10-24

### TC-ai-IT-029: POST /api/admin/ai/prompt-templates/{id}/publish 发布切换生效模板
**关联需求**: ai/提示词模板发布#发布切换生效模板
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1publish/post
**来源**: ai-management
**优先级**: P0
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；场景 S 生效模板为 V3，V4 已测试（记录其 version）
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. POST /api/admin/ai/prompt-templates/{V4}/publish，body `{"version":<V4.version>}`
4. GET /api/admin/ai/scenes/{S.id}
5. GET /api/admin/ai/prompt-templates/{V3}、/{V4}
**预期结果**: 步骤 3 返回 204；步骤 4 `activeTemplate.seq=4`、`activeTemplate.publishedBy=admin`；步骤 5 V3 `active=false`，V4 `active=true`、`publishedBy=admin`、`publishedAt` 非空
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-029/`
**最后更新**: 2026-10-24

### TC-ai-IT-030: POST /api/admin/ai/prompt-templates/{id}/publish 发布旧版本即回退
**关联需求**: ai/提示词模板发布#发布旧版本即回退
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1publish/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；场景生效模板为 V3，V1 已测试且以前发布过
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. POST …/{V1}/publish，带 V1 当前 version
4. GET 场景详情
**预期结果**: 步骤 3 返回 204；场景 `activeTemplate.seq=1`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-030/`
**最后更新**: 2026-10-24

### TC-ai-IT-031: POST /api/admin/ai/prompt-templates/{id}/publish 未测试不能发布
**关联需求**: ai/提示词模板发布#未测试不能发布
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1publish/post
**来源**: ai-management
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 前置：场景生效模板为 Vx（或无生效），模板 T `tested=false`
3. POST …/{T}/publish，带 T 当前 version
4. GET 场景详情
**预期结果**: 步骤 3 返回 409「请先测试」；步骤 4 `activeTemplate` 与前置一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-031/`
**最后更新**: 2026-10-24

### TC-ai-IT-032: POST /api/admin/ai/prompt-templates/{id}/publish 禁用场景也可发布
**关联需求**: ai/提示词模板发布#禁用场景也可发布
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1publish/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；场景已禁用，其下模板已测试
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. POST …/{id}/publish，带当前 version
**预期结果**: 返回 204，场景 `activeTemplate` 指向该模板，`enabled` 仍为 false
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-032/`
**最后更新**: 2026-10-24

### TC-ai-IT-033: POST /api/admin/ai/prompt-templates/{id}/publish 版本冲突返回 409
**关联需求**: ai/提示词模板发布#发布切换生效模板
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1publish/post
**来源**: ai-management
**优先级**: P2
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；模板已测试，当前 version=v
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. POST …/{id}/publish，body `{"version":<v-1>}`
**预期结果**: 返回 409「内容已被他人更新，请刷新」；场景生效模板不变
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-033/`
**最后更新**: 2026-10-24

### TC-ai-IT-034: 发布模板产生 ai-prompt:publish 留痕
**关联需求**: ai/AI 管理写操作留痕#发布留痕
**关联契约**: api-spec.json#/paths/~1api~1admin~1logs~1page/get
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；一个已测试模板 T
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. POST …/{T}/publish
4. 轮询 GET /api/admin/logs/page?username=admin&module=ai-prompt&page=1&size=20（最多 20 次、间隔 100ms）
**预期结果**: 命中 `module=ai-prompt`、`action=publish`、`target=<T.id>` 的记录
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-034/`
**最后更新**: 2026-10-25

### TC-ai-IT-035: 测试模板不产生留痕
**关联需求**: ai/AI 管理写操作留痕#测试不留痕
**关联契约**: api-spec.json#/paths/~1api~1admin~1logs~1page/get
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. GET /api/admin/logs/page?module=ai-prompt&page=1&size=200，记录基线
4. POST …/{id}/test 测试任一模板
5. 等待 2s 后再次查询
**预期结果**: 步骤 5 不存在 `action=test` 的记录，`ai-prompt` 记录数与基线一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-035/`
**最后更新**: 2026-10-25

### TC-ai-IT-036: MEMBER 角色可访问 AI 管理接口
**关联需求**: ai/AI 管理权限与菜单#MEMBER 可访问 AI 管理
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1scenes~1page/get
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：存在 MEMBER 角色的 Manager
2. 以 MEMBER 登录取 token
3. GET /api/admin/ai/scenes/page?page=1&size=20
4. GET /api/admin/ai/models
**预期结果**: 步骤 3、4 均返回 200（不返回 403）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-limit-config/TC-ai-IT-036/`
**最后更新**: 2026-10-26

### TC-ai-IT-037: 未登录访问 AI 管理接口返回 401
**关联需求**: ai/AI 管理权限与菜单#未登录被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1scenes~1page/get
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 不带 Authorization 头 GET /api/admin/ai/scenes/page
2. 不带 Authorization 头 POST /api/admin/ai/scenes，body `{"sceneId":"no_auth","name":"x"}`
**预期结果**: 两次均返回 401；场景 `no_auth` 未被创建
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-037/`
**最后更新**: 2026-10-25

### TC-ai-IT-038: POST /api/app/ai/generations 生成成功
**关联需求**: ai/App 生成接口#生成成功
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P0
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key（mock 返回合法 JSON）；场景 S 启用、已发布模板（声明必填 `age`）、已配主模型
2. POST http://localhost:8081/api/app/ai/generations，请求头 `X-API-Key: <有效 key>`、`X-Anonymous-Id: <匿名 ID>`，body `{"sceneId":"<S>","clientRequestId":"c-<uuid>","appVersion":"1.0.0","inputs":{"age":28}}`
**预期结果**: 返回 200，`status=SUCCESS`、`content` 为 mock 输出的 JSON、`requestId` 非空、`clientRequestId`/`sceneId` 与请求一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-limit-config/TC-ai-IT-038/`
**最后更新**: 2026-10-26

### TC-ai-IT-039: 生成成功写完整生成记录与调用明细
**关联需求**: ai/生成记录与调用明细落库#成功生成写完整记录
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P0
**测试步骤**:
1. 前置：同 TC-ai-IT-038，主模型首次即成功
2. 执行一次生成，取 `requestId`
3. psql 查询 `loves_ai_generation` where id=<requestId>
4. psql 查询 `loves_ai_generation_attempt` where generation_id=<requestId>
**预期结果**: 生成记录 1 条：`status=SUCCESS`、`attempt_count=1`、`completed_at` 非空、`template_id`/`template_seq` 为生效模板、`anonymous_user_id` 与请求头一致；调用明细 1 条 `seq=1`、`trigger_reason=INITIAL`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-039/`
**最后更新**: 2026-10-25

### TC-ai-IT-040: 生成记录请求快照不含密钥与认证头
**关联需求**: ai/AI 调用配置来自环境变量#请求快照不含密钥
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P0
**测试步骤**:
1. 前置：同 TC-ai-IT-038
2. 执行一次生成，取 `requestId`
3. psql 查询该记录 `request_messages`、`request_params` 文本
**预期结果**: `request_messages` 为 `[{role:system},{role:user}]`；`request_params` 含 `primaryModel, backupModel, temperature, topP, maxTokens, timeoutSeconds, maxRetries`；两者文本均不含 `ARK_API_KEY` 的值与 `Authorization`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-040/`
**最后更新**: 2026-10-24

### TC-ai-IT-041: POST /api/app/ai/generations 已处理的失败返回 200
**关联需求**: ai/App 生成接口#已处理的失败返回 200
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key（mock 主备模型均返回非 JSON）；场景可用且配了备用模型
2. POST http://localhost:8081/api/app/ai/generations（新 clientRequestId）
**预期结果**: 返回 200，`status=FORMAT_ERROR`、`content=null`；生成记录 `status=FORMAT_ERROR` 且 `completed_at` 非空
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-041/`
**最后更新**: 2026-10-24

### TC-ai-IT-042: POST /api/app/ai/generations 场景不存在返回 404
**关联需求**: ai/App 生成接口#场景不存在
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. POST http://localhost:8081/api/app/ai/generations，请求头 `X-API-Key: <有效 key>`、`X-Anonymous-Id: <匿名 ID>`，body `{"sceneId":"unknown","clientRequestId":"c-<uuid>"}`
**预期结果**: 返回 404；`loves_ai_generation` 无该 clientRequestId 记录
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-042/`
**最后更新**: 2026-10-24

### TC-ai-IT-043: POST /api/app/ai/generations 场景不可用返回 409 且不写记录
**关联需求**: ai/App 生成接口#场景不可用
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P0
**测试步骤**:
1. 前置：三个场景——A 已禁用（有生效模板与主模型）、B 启用但无生效模板、C 启用有生效模板但未配主模型（先配后清空不可行时直接新建模板并经 DB 置 active_template_id）
2. 分别以 A/B/C 与新 clientRequestId 提交生成
3. psql 查询这三个 clientRequestId 的记录
**预期结果**: 三次均返回 409「场景不可用」；无生成记录
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-043/`
**最后更新**: 2026-10-24

### TC-ai-IT-044: POST /api/app/ai/generations 必填变量缺失返回 400 且不写记录
**关联需求**: ai/App 生成接口#必填变量缺失
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：场景可用，生效模板声明必填 `age`
2. 提交 body `{"sceneId":"<S>","clientRequestId":"c-<uuid>","inputs":{}}`
3. psql 查询该 clientRequestId 记录
**预期结果**: 返回 400，`message` 含 `age`；无生成记录
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-044/`
**最后更新**: 2026-10-24

### TC-ai-IT-045: POST /api/app/ai/generations 请求头与 body 参数校验
**关联需求**: ai/App 生成接口#缺少匿名 ID
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 有效 X-API-Key、不带 `X-Anonymous-Id` 提交合法 body
2. `X-Anonymous-Id` 为 129 字符提交
3. body 缺 `clientRequestId` 提交
4. `clientRequestId` 为 65 字符提交
5. `appVersion` 为 33 字符提交
6. body 缺 `sceneId` 提交
**预期结果**: 各步骤均返回 400；均不写生成记录
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-045/`
**最后更新**: 2026-10-24

### TC-ai-IT-046: POST /api/app/ai/generations API Key 无效返回 401
**关联需求**: ai/App 生成接口#API Key 无效
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 请求头 `X-API-Key: wrong-key`、`X-Anonymous-Id: anon-A`，合法 body 提交
2. 不带 X-API-Key 提交
**预期结果**: 两次均返回 401；不写生成记录
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-046/`
**最后更新**: 2026-10-24

### TC-ai-IT-047: POST /api/app/ai/generations 响应不含调用明细
**关联需求**: ai/App 生成接口#响应不含调用明细
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key（mock 主模型首次返回非 JSON、第二次返回合法 JSON；`AI_MAX_RETRIES=1`）
2. 提交生成
**预期结果**: 返回 200 `status=SUCCESS`；响应体顶层键恰为 `requestId, clientRequestId, sceneId, status, content` 五个，无模型名、rawOutput、tokens、attempts
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-047/`
**最后更新**: 2026-10-24

### TC-ai-IT-048: 重试与切备用逐条写调用明细
**关联需求**: ai/生成记录与调用明细落库#重试与切备用逐条记明细
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key（`AI_MAX_RETRIES=1`；mock 对主模型两次均失败（如超时/429），对备用模型首次返回合法 JSON）；场景配主备模型
2. 提交生成，取 requestId
3. psql 查询该记录与其 `loves_ai_generation_attempt`（按 seq）
**预期结果**: 响应 200 `status=SUCCESS`；明细 3 条，seq 1/2/3，`trigger_reason` 依次 INITIAL/RETRY/FALLBACK，第 3 条 model 为备用模型；记录 `attempt_count=3`、`final_model` 为备用模型、tokens 为各次合计
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-048/`
**最后更新**: 2026-10-24

### TC-ai-IT-049: 选填变量未传时生成记录 inputs 记 null
**关联需求**: ai/生成记录与调用明细落库#选填变量未传记 null
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P2
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；生效模板声明必填 `age` 与选填 `user_input`
2. 提交 `inputs:{"age":28}`（不含 user_input），取 requestId
3. psql 查询该记录 `inputs` 与 `request_messages`
**预期结果**: `inputs` 含键 `user_input` 且值为 null；`request_messages` 的 user 内容中该变量位置替换为空串
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-049/`
**最后更新**: 2026-10-24

### TC-ai-IT-050: 重复提交返回原结果且不再调用模型
**关联需求**: ai/生成请求去重#重复提交返回原结果
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P0
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key（可统计 mock 收到的调用次数）；`c-1-<后缀>` 已生成 SUCCESS
2. 以相同 sceneId、匿名 ID、clientRequestId 再次提交
3. psql 统计该 clientRequestId 记录数；核对 mock 调用次数
**预期结果**: 返回 200，`requestId`、`content`、`status` 与首次相同；记录仍 1 条，mock 调用次数未增加
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-limit-config/TC-ai-IT-050/`
**最后更新**: 2026-10-26

### TC-ai-IT-051: 生成中重复提交返回 409
**关联需求**: ai/生成请求去重#生成中重复提交
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：psql 直插一条 `status=PROCESSING`、`created_at=now()`、`client_request_id=c-2-<后缀>`、匿名 ID `anon-A`、场景 S 的生成记录（request_params 含 timeoutSeconds/maxRetries）
2. 以 sceneId S、`anon-A`、`c-2-<后缀>` 提交
**预期结果**: 返回 409「生成中，请稍后重试」；记录仍为 PROCESSING
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-051/`
**最后更新**: 2026-10-24

### TC-ai-IT-052: PROCESSING 超时记录就地改为 TIMEOUT
**关联需求**: ai/生成请求去重#PROCESSING 超时就地失败
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：psql 直插 `status=PROCESSING`、`client_request_id=c-3-<后缀>` 的记录，`created_at` 早于阈值（如 now() - interval '1 hour'，request_params 中 timeoutSeconds=30、maxRetries=1、无备用）
2. 以相同 sceneId、匿名 ID、clientRequestId 提交
3. psql 查询该记录
**预期结果**: 返回 200 `status=TIMEOUT`、`content=null`；记录 `status=TIMEOUT`、`completed_at` 非空
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-052/`
**最后更新**: 2026-10-24

### TC-ai-IT-053: clientRequestId 被不同匿名 ID 复用返回 409
**关联需求**: ai/生成请求去重#clientRequestId 冲突
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；`c-4-<后缀>` 已由 `anon-A` 提交
2. `X-Anonymous-Id: anon-B` 以同 sceneId、`c-4-<后缀>` 提交
3. `anon-A` 以另一个 sceneId、`c-4-<后缀>` 提交
**预期结果**: 步骤 2、3 均返回 409「clientRequestId 冲突」
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-053/`
**最后更新**: 2026-10-24

### TC-ai-IT-054: 重复提交不受场景禁用影响
**关联需求**: ai/生成请求去重#重复提交不受场景状态影响
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；`c-5-<后缀>` 已 SUCCESS
2. admin 侧 PUT /api/admin/ai/scenes/{id}/enabled 禁用该场景
3. 以相同参数再次提交 `c-5-<后缀>`
**预期结果**: 返回 200 与首次相同结果（不返回 409 场景不可用）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-054/`
**最后更新**: 2026-10-24

### TC-ai-IT-055: 匿名 ID 超出每日次数返回 429
**关联需求**: ai/生成接口每日额度#匿名 ID 超出每日次数
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-limit-config
**优先级**: P1
**测试步骤**:
1. 前置：以 admin 登录 admin 后端，PUT /api/admin/ai/limits/PER_ANON_DAILY body `{"value":2}`（返回 200）；admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；`anon-<后缀>` 今日已有 2 条记录（连续生成 2 次）
2. `anon-<后缀>` 以新 clientRequestId 提交
3. psql 统计该匿名 ID 今日记录数
4. 收尾：PUT /api/admin/ai/limits/PER_ANON_DAILY body `{"value":50}` 恢复
**预期结果**: 步骤 2 返回 429「今日生成次数已达上限」；记录数仍为 2
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-limit-config/TC-ai-IT-055/`
**最后更新**: 2026-10-26

### TC-ai-IT-056: 全站超出每日总量返回 429
**关联需求**: ai/生成接口每日额度#全站超出每日总量
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-limit-config
**优先级**: P1
**测试步骤**:
1. 前置：以 admin 登录 admin 后端，PUT /api/admin/ai/limits/GLOBAL_DAILY body `{"value":3}`（`PER_ANON_DAILY` 保持 50）；今日（北京时间）全站已有 ≥3 条记录（不足则用不同匿名 ID 生成补齐）
2. 以全新匿名 ID、新 clientRequestId 提交
3. 收尾：PUT /api/admin/ai/limits/GLOBAL_DAILY body `{"value":5000}` 恢复
**预期结果**: 步骤 2 返回 429「服务繁忙，请稍后再试」；不写记录
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-limit-config/TC-ai-IT-056/`
**最后更新**: 2026-10-26

### TC-ai-IT-057: 北京时间昨日记录不计入今日额度
**关联需求**: ai/生成接口每日额度#昨日记录不计入
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-limit-config
**优先级**: P2
**测试步骤**:
1. 前置：以 admin 登录 admin 后端，PUT /api/admin/ai/limits/PER_ANON_DAILY body `{"value":2}`；admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；psql 为 `anon-y-<后缀>` 直插 2 条 `created_at` 为北京时间昨日 23:30 的记录，今日 0 条
2. `anon-y-<后缀>` 以新 clientRequestId 提交
3. 收尾：PUT /api/admin/ai/limits/PER_ANON_DAILY body `{"value":50}` 恢复
**预期结果**: 步骤 2 返回 200（正常生成，不返回 429）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-limit-config/TC-ai-IT-057/`
**最后更新**: 2026-10-26

### TC-ai-IT-058: 去重命中不占用额度
**关联需求**: ai/生成接口每日额度#去重不占额度
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-limit-config
**优先级**: P1
**测试步骤**:
1. 前置：以 admin 登录 admin 后端，PUT /api/admin/ai/limits/PER_ANON_DAILY body `{"value":1}`；admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；`anon-d-<后缀>` 今日已有 1 条 SUCCESS 记录 `c-1-<后缀>`
2. `anon-d-<后缀>` 重复提交 `c-1-<后缀>`
3. `anon-d-<后缀>` 以新 clientRequestId 提交
4. 收尾：PUT /api/admin/ai/limits/PER_ANON_DAILY body `{"value":50}` 恢复
**预期结果**: 步骤 2 返回 200 与原结果；步骤 3 返回 429「今日生成次数已达上限」
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-limit-config/TC-ai-IT-058/`
**最后更新**: 2026-10-26

### TC-ai-IT-059: 单个变量 501 字符返回 400
**关联需求**: ai/生成接口输入长度限制#单个变量超长
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：可用场景
2. 提交 inputs `{"user_input":"<501 个字符>"}`
3. psql 查询该 clientRequestId 记录
**预期结果**: 返回 400，`message` 含 `user_input`；无记录
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-059/`
**最后更新**: 2026-10-24

### TC-ai-IT-060: 单个变量恰 500 字符通过长度校验
**关联需求**: ai/生成接口输入长度限制#边界值通过
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；可用场景，生效模板声明选填 `user_input`
2. 提交 inputs `{"user_input":"<500 个字符>"}`（整体 ≤ 4KB）
**预期结果**: 不返回 400（返回 200，status 为生成结果）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-060/`
**最后更新**: 2026-10-24

### TC-ai-IT-061: inputs 整体超过 4KB 返回 400
**关联需求**: ai/生成接口输入长度限制#整体超长
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P1
**测试步骤**:
1. 前置：可用场景
2. 提交 inputs 含 `v1`…`v10` 共 10 个变量，各 450 个 ASCII 字符（整体序列化 > 4096 字节）
**预期结果**: 返回 400，`message` 提示整体超限；无记录
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-061/`
**最后更新**: 2026-10-24

### TC-ai-IT-062: nginx 同一 IP 突发超限返回 429
**关联需求**: ai/生成接口 IP 限流#同一 IP 突发超限
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-management
**优先级**: P2
**测试步骤**:
1. 前置：测试环境 nginx 已按 `docs/部署服务器初始化.md` 配置 `ls_ai_gen` 限流
2. 从同一 IP 在 1 秒内连续 15 次 POST https://<测试环境域名>/love-space/app/api/app/ai/generations（可用无效 key，仅观察限流）
3. 核对 app 访问日志到达次数
**预期结果**: 约前 11 次返回非 429（到达 app，如 401/200），其余返回 429；被拒请求未到达 app
**状态**: ⬜ 未执行（nginx IP 限流仅在测试环境 nginx 生效，本地无 nginx 前置，无法执行）
**执行方式**: api-test-runner
**执行存证**: -
**最后更新**: 2026-10-24

### TC-ai-IT-063: nginx 限流不影响非生成接口
**关联需求**: ai/生成接口 IP 限流#非生成接口不受影响
**关联契约**: api-spec.json#/paths/~1api~1app~1cities/get
**来源**: ai-management
**优先级**: P2
**测试步骤**:
1. 前置：同 TC-ai-IT-062
2. 从同一 IP 在 1 秒内连续 15 次 GET https://<测试环境域名>/love-space/app/api/app/cities（带有效 key）
**预期结果**: 15 次均不返回 429
**状态**: ⬜ 未执行（nginx IP 限流仅在测试环境 nginx 生效，本地无 nginx 前置，无法执行）
**执行方式**: api-test-runner
**执行存证**: -
**最后更新**: 2026-10-24

### TC-ai-IT-064: 测试期间模板被他人修改则结果不写回
**关联需求**: ai/提示词模板测试#测试成功置已测试
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1test/post
**来源**: ai-management
**优先级**: P2
**测试步骤**:
1. 前置：admin 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock，mock 延迟约 3s 后返回合法 JSON）；场景已配主模型；未生效模板 T `tested=false`，记录其 `last_test_*` 与 version=v
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. 异步发起 POST …/{T}/test（完整 inputs）
4. 在 mock 返回前 PUT /api/admin/ai/prompt-templates/{T}（带 version=v，改 name）
5. 等待步骤 3 返回后 GET 模板详情
**预期结果**: 步骤 3 返回 200 `success=true`（仍返回本次结果）；步骤 4 返回 200；步骤 5 `tested=false`、`last_test_*` 与前置一致、name 为步骤 4 的新值
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-management/TC-ai-IT-064/`
**最后更新**: 2026-10-24

> **生成记录（ai-generation-records）造数口径**：
> - 「psql 造数」= 直接向 `loves_ai_generation` / `loves_ai_generation_attempt` 插行（库 `localhost:25432/love_space`，iris/iris），参考：
>   `INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), 'gr-<后缀>-<n>', 'anon-gr-<后缀>', '<S>', '<T.id>', <T.seq>, '{"age":28}', '[]', '{}', 'SUCCESS', 'mock-ok', 1, 1200, now(), now());`
>   评价列（027 迁移追加）：`rating`、`rating_tags`（jsonb，默认 `'[]'`）、`rating_remark`、`rated_by`、`rated_at`。需指定 `created_at`（默认 7 天、区间边界）与 `PROCESSING` 记录一律用 psql 造数；时间按北京时间书写，如 `'2026-10-20 00:00:05+08'`。
> - 「app 生成造数」= 经 app `POST /api/app/ai/generations` 生成，app 进程指向假方舟 `node scripts/mock-ark.mjs`（模型 `mock-ok` 首次成功、`mock-flaky` 第 1 次非 JSON 第 2 次成功、`mock-format-error` 恒非 JSON 等）。
> - 列表用例统一加 `anonymousUserId=anon-gr-<后缀>` 隔离本用例造的数据（默认 7 天用例除外的日期参数按步骤给出）。
> - 场景 S、模板 T 为前置中已存在的场景与其模板（任一已发布模板即可）。

### TC-ai-IT-065: GET /api/admin/ai/generations/page 不传日期默认最近 7 天
**关联需求**: ai/生成记录列表查询#默认最近 7 天
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get
**来源**: ai-generation-records
**优先级**: P0
**测试步骤**:
1. 前置：psql 造数 3 条（同一 `anon-gr-<后缀>`），`created_at` 分别为北京时间今天当前时刻、6 天前 00:00:01、8 天前 12:00
2. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
3. GET /api/admin/ai/generations/page?anonymousUserId=anon-gr-<后缀>&page=1&size=20（不传 startDate/endDate）
**预期结果**: 返回 200，`totalElements=2`，`content` 为今天、6 天前两条（按 createdAt 倒序），不含 8 天前的记录
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-065/`
**最后更新**: 2026-10-25

### TC-ai-IT-066: GET /api/admin/ai/generations/page 日期区间含两端整日（北京时间）
**关联需求**: ai/生成记录列表查询#日期区间含两端整日
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get
**来源**: ai-generation-records
**优先级**: P0
**测试步骤**:
1. 前置：psql 造数 4 条（同一 `anon-gr-<后缀>`），`created_at` 为 `2026-10-19 23:59:59+08`、`2026-10-20 00:00:05+08`、`2026-10-21 23:59:50+08`、`2026-10-22 00:00:00+08`
2. 登录取 token
3. GET /api/admin/ai/generations/page?startDate=2026-10-20&endDate=2026-10-21&anonymousUserId=anon-gr-<后缀>&page=1&size=20
**预期结果**: 返回 200，`totalElements=2`，仅含 `10-20 00:00:05` 与 `10-21 23:59:50` 两条（区间外紧邻两条不返回），10-21 那条在前
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-066/`
**最后更新**: 2026-10-25

### TC-ai-IT-067: GET /api/admin/ai/generations/page 只传一端按单边过滤
**关联需求**: ai/生成记录列表查询#日期区间含两端整日
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get
**来源**: ai-generation-records
**优先级**: P2
**测试步骤**:
1. 前置：同 TC-ai-IT-066 的 4 条造数（可复用）
2. 登录取 token
3. GET …/generations/page?startDate=2026-10-21&anonymousUserId=anon-gr-<后缀>&page=1&size=20
4. GET …/generations/page?endDate=2026-10-20&anonymousUserId=anon-gr-<后缀>&page=1&size=20
**预期结果**: 步骤 3 返回 `10-21 23:59:50`、`10-22 00:00:00` 两条（只传一端时不再套用默认 7 天）；步骤 4 返回 `10-19 23:59:59`、`10-20 00:00:05` 两条
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-067/`
**最后更新**: 2026-10-25

### TC-ai-IT-068: GET /api/admin/ai/generations/page 开始晚于结束被拒绝
**关联需求**: ai/生成记录列表查询#开始晚于结束被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get
**来源**: ai-generation-records
**优先级**: P1
**测试步骤**:
1. 前置：同 TC-ai-IT-066 的造数（10-20~10-22 间存在记录）
2. 登录取 token
3. GET …/generations/page?startDate=2026-10-22&endDate=2026-10-20&page=1&size=20
**预期结果**: 返回 400，`message`=「开始日期不能晚于结束日期」，错误体含 status/error/message/path
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-068/`
**最后更新**: 2026-10-25

### TC-ai-IT-069: GET /api/admin/ai/generations/page 按未评价筛选 rating=NONE
**关联需求**: ai/生成记录列表查询#按未评价筛选
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get
**来源**: ai-generation-records
**优先级**: P1
**测试步骤**:
1. 前置：psql 造数 2 条（今天、同一 `anon-gr-<后缀>`）：A `rating='USABLE'`、`rated_by='admin'`、`rated_at=now()`；B 未评价（rating 为 null）
2. 登录取 token
3. GET …/generations/page?rating=NONE&anonymousUserId=anon-gr-<后缀>&page=1&size=20
4. GET …/generations/page?rating=USABLE&anonymousUserId=anon-gr-<后缀>&page=1&size=20
**预期结果**: 步骤 3 仅返回 B，其 `rating=null`；步骤 4 仅返回 A，`rating=USABLE`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-069/`
**最后更新**: 2026-10-25

### TC-ai-IT-070: GET /api/admin/ai/generations/page 按问题标签筛选
**关联需求**: ai/生成记录列表查询#按问题标签筛选
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get
**来源**: ai-generation-records
**优先级**: P1
**测试步骤**:
1. 前置：psql 造数 2 条（今天、同一 `anon-gr-<后缀>`）：A `rating='NEEDS_IMPROVEMENT'`、`rating_tags='["BAD_TONE","VAGUE"]'`；B `rating='NEEDS_IMPROVEMENT'`、`rating_tags='["VAGUE"]'`
2. 登录取 token
3. GET …/generations/page?tag=BAD_TONE&anonymousUserId=anon-gr-<后缀>&page=1&size=20
4. GET …/generations/page?tag=VAGUE&anonymousUserId=anon-gr-<后缀>&page=1&size=20
**预期结果**: 步骤 3 仅返回 A；步骤 4 返回 A、B 两条
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-070/`
**最后更新**: 2026-10-25

### TC-ai-IT-071: GET /api/admin/ai/generations/page 场景 + 状态组合筛选且按时间倒序
**关联需求**: ai/生成记录列表查询#组合筛选与倒序
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get
**来源**: ai-generation-records
**优先级**: P1
**测试步骤**:
1. 前置：psql 造数（同一 `anon-gr-<后缀>`）：场景 S 下 SUCCESS ×3（`created_at` 为 now()-3h、now()-1h、now()-2h）、FORMAT_ERROR ×1、PROCESSING ×1；另一场景 S2 下 SUCCESS ×1
2. 登录取 token
3. GET …/generations/page?sceneId=<S>&status=SUCCESS&anonymousUserId=anon-gr-<后缀>&page=1&size=20
4. GET …/generations/page?status=PROCESSING&anonymousUserId=anon-gr-<后缀>&page=1&size=20
**预期结果**: 步骤 3 返回 3 条，均 `sceneId=S`、`status=SUCCESS`，`createdAt` 依次为 -1h、-2h、-3h；步骤 4 返回 1 条 `status=PROCESSING`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-071/`
**最后更新**: 2026-10-25

### TC-ai-IT-072: GET /api/admin/ai/generations/page requestId / templateId / model 精确匹配
**关联需求**: ai/生成记录列表查询#组合筛选与倒序
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get
**来源**: ai-generation-records
**优先级**: P2
**测试步骤**:
1. 前置：psql 造数（今天、同一 `anon-gr-<后缀>`）：R1 模板 T1、`final_model='mock-ok'`；R2 模板 T2、`final_model='mock-flaky'`
2. 登录取 token
3. GET …/generations/page?requestId=<R1.id>&page=1&size=20
4. GET …/generations/page?requestId=<另一随机 uuid>&page=1&size=20
5. GET …/generations/page?templateId=<T2.id>&anonymousUserId=anon-gr-<后缀>&page=1&size=20
6. GET …/generations/page?model=mock-ok&anonymousUserId=anon-gr-<后缀>&page=1&size=20
7. GET …/generations/page?anonymousUserId=anon-gr-<后缀的前半段>&page=1&size=20
**预期结果**: 步骤 3 仅 R1；步骤 4 空页；步骤 5 仅 R2；步骤 6 仅 R1；步骤 7 空页（匿名 ID 精确匹配）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-072/`
**最后更新**: 2026-10-25

### TC-ai-IT-073: GET /api/admin/ai/generations/page 非法日期与枚举返回 400
**关联需求**: ai/生成记录列表查询#非法枚举被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get
**来源**: ai-generation-records
**优先级**: P1
**测试步骤**:
1. 登录取 token
2. 分别 GET …/generations/page?…：`rating=GOOD`、`startDate=2026/10/20`、`endDate=2026-13-01`、`status=DONE`、`tag=X`
**预期结果**: 5 次均返回 400，body 为 `{status,error,message,path}`（message 沿用现有英文 "Invalid value for parameter …" 口径，不断言中文）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-073/`
**最后更新**: 2026-10-25

### TC-ai-IT-074: GET /api/admin/ai/generations/page 列表条目仅十一个字段、不含正文
**关联需求**: ai/生成记录列表查询#列表不含正文
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get
**来源**: ai-generation-records
**优先级**: P1
**测试步骤**:
1. 前置：app 生成造数一条 SUCCESS（模型 `mock-ok`，`X-Anonymous-Id: anon-gr-<后缀>`），取 requestId
2. 登录取 token
3. GET …/generations/page?requestId=<requestId>&page=1&size=20
**预期结果**: 返回 200，分页结构为 `{content,page,size,totalElements,totalPages}`、`page=1`；条目键集合恰为 `id, createdAt, anonymousUserId, sceneId, sceneName, templateId, templateSeq, finalModel, status, latencyMs, rating`，无 `inputs`/`requestMessages`/`rawOutput`/`content` 等；`sceneName` 为场景当前名称、`finalModel=mock-ok`、`rating=null`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-074/`
**最后更新**: 2026-10-25

### TC-ai-IT-075: GET /api/admin/ai/generations/{id} 详情含调用明细且查看不留痕
**关联需求**: ai/生成记录详情#详情含调用明细
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}/get
**来源**: ai-generation-records
**优先级**: P0
**测试步骤**:
1. 前置：场景 S 主模型配 `mock-flaky`（`AI_MAX_RETRIES≥1`）；app 生成造数一次，响应 `status=SUCCESS`，取 requestId
2. 登录取 token；GET /api/admin/logs/page?module=ai-generation&page=1&size=200 记录 `totalElements` 基线
3. GET /api/admin/ai/generations/{requestId}
4. 等待 2s 后重复步骤 2 的日志查询
**预期结果**: 步骤 3 返回 200：`status=SUCCESS`、`attemptCount=2`、`finalModel=mock-flaky`、`templateName` 为生效模板名称、`sceneName` 为场景名称，含 `clientRequestId, appVersion, inputs, requestMessages, requestParams, rawOutput, content, promptTokens, completionTokens, latencyMs, createdAt, completedAt`；`attempts` 2 条按 seq 升序：seq 1 `triggerReason=INITIAL`（含解析错误/校验错误）、seq 2 `triggerReason=RETRY` 成功，每条含 model、status、rawResponse、起止时间、latencyMs；步骤 4 `totalElements` 与基线一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-075/`
**最后更新**: 2026-10-25

### TC-ai-IT-076: GET /api/admin/ai/generations/{id} 未评价记录评价字段为空
**关联需求**: ai/生成记录详情#未评价记录的评价字段为空
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}/get
**来源**: ai-generation-records
**优先级**: P1
**测试步骤**:
1. 前置：psql 造数一条未评价 SUCCESS 记录 R
2. 登录取 token
3. GET /api/admin/ai/generations/{R.id}
**预期结果**: 返回 200，`rating=null`、`ratingRemark=null`、`ratedBy=null`、`ratedAt=null`，`ratingTags=[]`（空数组而非 null）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-076/`
**最后更新**: 2026-10-25

### TC-ai-IT-077: GET /api/admin/ai/generations/{id} 记录不存在返回 404
**关联需求**: ai/生成记录详情#记录不存在
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}/get
**来源**: ai-generation-records
**优先级**: P1
**测试步骤**:
1. 登录取 token
2. GET /api/admin/ai/generations/<随机 uuid>
**预期结果**: 返回 404，body 为 `{status,error,message,path}`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-077/`
**最后更新**: 2026-10-25

### TC-ai-IT-078: PUT /api/admin/ai/generations/{id}/rating 首次评价
**关联需求**: ai/生成记录人工评价#首次评价
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put
**来源**: ai-generation-records
**优先级**: P0
**测试步骤**:
1. 前置：psql 造数一条未评价 SUCCESS 记录 R
2. 以 admin 登录取 token
3. PUT /api/admin/ai/generations/{R.id}/rating，body `{"rating":"USABLE"}`
4. GET /api/admin/ai/generations/{R.id}
5. GET …/generations/page?requestId=<R.id>&page=1&size=20
**预期结果**: 步骤 3 返回 204、无 body；步骤 4 `rating=USABLE`、`ratingTags=[]`、`ratingRemark=null`、`ratedBy=admin`、`ratedAt` 非空；步骤 5 条目 `rating=USABLE`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-078/`
**最后更新**: 2026-10-25

### TC-ai-IT-079: PUT /api/admin/ai/generations/{id}/rating remark 恰 500 字符与全部标签通过
**关联需求**: ai/生成记录人工评价#评价参数非法
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put
**来源**: ai-generation-records
**优先级**: P1
**测试步骤**:
1. 前置：psql 造数一条未评价 SUCCESS 记录 R
2. 登录取 token
3. PUT …/{R.id}/rating，body `{"rating":"NEEDS_IMPROVEMENT","tags":["IGNORED_INPUT","WEAK_CYCLE_LINK","BAD_TONE","VAGUE","INAPPLICABLE_ADVICE","OVER_MEDICALIZED","FORMAT_ERROR","OTHER"],"remark":"<恰 500 个中文字符>"}`
4. GET 详情
**预期结果**: 步骤 3 返回 204；步骤 4 `ratingTags` 为提交的 8 个标签、`ratingRemark` 长度为 500 且与提交一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-079/`
**最后更新**: 2026-10-25

### TC-ai-IT-080: PUT /api/admin/ai/generations/{id}/rating 再次评价整体覆盖、不保留历史
**关联需求**: ai/生成记录人工评价#再次评价覆盖原评价
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put
**来源**: ai-generation-records
**优先级**: P0
**测试步骤**:
1. 前置：psql 造数一条 SUCCESS 记录 R；存在 MEMBER 账号 ops1（不存在则以 admin 创建并启用）
2. 以 ops1 登录，PUT …/{R.id}/rating，body `{"rating":"NEEDS_IMPROVEMENT","tags":["BAD_TONE"],"remark":"语气生硬"}`；GET 详情记录 `ratedAt`=t1
3. 以 admin 登录，PUT …/{R.id}/rating，body `{"rating":"UNUSABLE","tags":["VAGUE"]}`（不传 remark）
4. GET 详情
5. psql：`SELECT count(*) FROM loves_ai_generation WHERE id='<R.id>'`；`\dt loves_ai_generation*`
**预期结果**: 步骤 2、3 均返回 204；步骤 4 `rating=UNUSABLE`、`ratingTags=["VAGUE"]`、`ratingRemark=null`、`ratedBy=admin`、`ratedAt` 晚于 t1；步骤 5 记录仍 1 条，评价相关只存在 `loves_ai_generation` 的列（无评价历史表/行）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-080/`
**最后更新**: 2026-10-25

### TC-ai-IT-081: PUT /api/admin/ai/generations/{id}/rating 已评价记录不能清除评价
**关联需求**: ai/生成记录人工评价#评价参数非法
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put
**来源**: ai-generation-records
**优先级**: P1
**测试步骤**:
1. 前置：psql 造数一条已评价记录 R（`rating='USABLE'`、`rated_by='admin'`、`rated_at=now()`）
2. 登录取 token
3. PUT …/{R.id}/rating，body `{"rating":null}`
4. PUT …/{R.id}/rating，body `{}`
5. PUT …/{R.id}/rating，body `{"rating":"NONE"}`
6. GET 详情
**预期结果**: 步骤 3~5 均返回 400；步骤 6 仍 `rating=USABLE`，`ratedBy`/`ratedAt` 未变
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-081/`
**最后更新**: 2026-10-25

### TC-ai-IT-082: PUT /api/admin/ai/generations/{id}/rating 生成中记录返回 409
**关联需求**: ai/生成记录人工评价#生成中的记录不能评价
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put
**来源**: ai-generation-records
**优先级**: P1
**测试步骤**:
1. 前置：psql 造数一条 `status='PROCESSING'`、`created_at=now()`、`completed_at` 为 null 的记录 R
2. 登录取 token
3. PUT …/{R.id}/rating，body `{"rating":"USABLE"}`
4. GET 详情；GET /api/admin/logs/page?module=ai-generation&page=1&size=200（等待 2s 后）
**预期结果**: 步骤 3 返回 409，`message`「生成中的记录不能评价」；步骤 4 `rating=null`、`ratingTags=[]`、`ratedBy=null`，日志中无 `target=<R.id>` 的 `action=rate` 记录
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-082/`
**最后更新**: 2026-10-25

### TC-ai-IT-083: PUT /api/admin/ai/generations/{id}/rating 失败记录可以评价
**关联需求**: ai/生成记录人工评价#失败记录可以评价
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put
**来源**: ai-generation-records
**优先级**: P1
**测试步骤**:
1. 前置：场景 S 主模型配 `mock-format-error`、无备用；app 生成造数一次，响应 `status=FORMAT_ERROR`，取 requestId（或 psql 造数 `status='FORMAT_ERROR'`、`content` 为 null）
2. 登录取 token
3. PUT …/{requestId}/rating，body `{"rating":"UNUSABLE","tags":["FORMAT_ERROR"]}`
4. GET 详情
**预期结果**: 步骤 3 返回 204；步骤 4 `status=FORMAT_ERROR`、`rating=UNUSABLE`、`ratingTags=["FORMAT_ERROR"]`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-083/`
**最后更新**: 2026-10-25

### TC-ai-IT-084: PUT /api/admin/ai/generations/{id}/rating 参数非法返回 400 且评价不变
**关联需求**: ai/生成记录人工评价#评价参数非法
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put
**来源**: ai-generation-records
**优先级**: P1
**测试步骤**:
1. 前置：psql 造数一条 SUCCESS 记录 R，已评价 `rating='NEEDS_IMPROVEMENT'`、`rating_tags='["OTHER"]'`、`rating_remark='原备注'`、`rated_by='admin'`
2. 登录取 token
3. 分别 PUT …/{R.id}/rating：
   a. `{"tags":["VAGUE"]}`（缺 rating）
   b. `{"rating":"GOOD"}`
   c. `{"rating":"USABLE","tags":["BAD_TONE","BAD_TONE"]}`
   d. `{"rating":"USABLE","tags":["X"]}`
   e. `{"rating":"USABLE","remark":"<501 个字符>"}`
4. GET 详情
**预期结果**: a~e 均返回 400；步骤 4 评价仍为 `NEEDS_IMPROVEMENT` / `["OTHER"]` / 「原备注」/ `ratedBy=admin`，`ratedAt` 未变
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-084/`
**最后更新**: 2026-10-25

### TC-ai-IT-085: PUT /api/admin/ai/generations/{id}/rating 记录不存在返回 404
**关联需求**: ai/生成记录人工评价#评价参数非法
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put
**来源**: ai-generation-records
**优先级**: P2
**测试步骤**:
1. 登录取 token
2. PUT /api/admin/ai/generations/<随机 uuid>/rating，body `{"rating":"USABLE"}`
**预期结果**: 返回 404，body 为 `{status,error,message,path}`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-085/`
**最后更新**: 2026-10-25

### TC-ai-IT-086: MEMBER 角色可评价生成记录，未登录返回 401
**关联需求**: ai/AI 管理权限与菜单#MEMBER 可评价生成记录
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put
**来源**: ai-generation-records
**优先级**: P1
**测试步骤**:
1. 前置：存在 MEMBER 角色的 Manager；psql 造数一条未评价 SUCCESS 记录 R
2. 以 MEMBER 登录取 token
3. GET /api/admin/ai/generations/page?requestId=<R.id>&page=1&size=20；GET /api/admin/ai/generations/{R.id}
4. PUT /api/admin/ai/generations/{R.id}/rating，body `{"rating":"USABLE"}`
5. 不带 Authorization 头 GET /api/admin/ai/generations/page 与 PUT …/{R.id}/rating
**预期结果**: 步骤 3 均 200；步骤 4 返回 204（不返回 403），详情 `ratedBy` 为该 MEMBER 用户名；步骤 5 均返回 401
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-086/`
**最后更新**: 2026-10-25

### TC-ai-IT-087: 评价生成记录产生 ai-generation:rate 留痕
**关联需求**: ai/生成记录人工评价#评价留痕
**关联契约**: api-spec.json#/paths/~1api~1admin~1logs~1page/get
**来源**: ai-generation-records
**优先级**: P1
**测试步骤**:
1. 前置：psql 造数一条 SUCCESS 记录 R
2. 以 admin 登录取 token
3. PUT …/{R.id}/rating，body `{"rating":"NEEDS_IMPROVEMENT","tags":["VAGUE"],"remark":"留痕"}`
4. 轮询 GET /api/admin/logs/page?username=admin&module=ai-generation&page=1&size=20（最多 20 次、间隔 100ms）
5. psql 查询该日志行 payload
**预期结果**: 步骤 3 返回 204；步骤 4 命中 `module=ai-generation`、`action=rate`、`target=<R.id>` 的记录；步骤 5 payload 含本次 `rating`、`tags`、`remark`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-generation-records/TC-ai-IT-087/`
**最后更新**: 2026-10-25

> **限流配置（ai-limit-config）口径**：
> - 额度只来自表 `loves_ai_limit`（`PER_ANON_DAILY` 初始 50、`GLOBAL_DAILY` 初始 5000，`-1` 不限制），不再读 `AI_LIMIT_*` 环境变量；用例通过 admin 后端 `PUT /api/admin/ai/limits/{key}` 设置额度，结束后恢复 50 / 5000。
> - 需要「从未修改过」状态或删除行的步骤直接 psql 操作 `loves_ai_limit`（库 `localhost:25432/love_space`，iris/iris），结束后恢复初始数据：
>   `UPDATE loves_ai_limit SET limit_value = CASE limit_key WHEN 'PER_ANON_DAILY' THEN 50 ELSE 5000 END, updated_by = NULL, updated_at = NULL;`

### TC-ai-IT-088: GET /api/admin/ai/limits 初始数据
**关联需求**: ai/生成额度配置#初始数据
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1limits/get
**来源**: ai-limit-config
**优先级**: P0
**测试步骤**:
1. 前置：新建库刚执行完迁移；非新库则 psql 执行上方恢复语句，把两行恢复为初始值且 `updated_by`/`updated_at` 置 NULL
2. 以 admin 登录取 token
3. GET /api/admin/ai/limits
**预期结果**: 返回 200，数组共 2 项且按 key 升序：`[0]` `key=GLOBAL_DAILY`、`name=全站每日上限`、`value=5000`；`[1]` `key=PER_ANON_DAILY`、`name=单个匿名 ID 每日上限`、`value=50`；两项 `updatedBy`、`updatedAt` 均为 null
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-limit-config/TC-ai-IT-088/`
**最后更新**: 2026-10-26

### TC-ai-IT-089: PUT /api/admin/ai/limits/{key} 修改单项只记录该项修改人
**关联需求**: ai/生成额度配置#修改单项只记录该项修改人
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1limits~1{key}/put
**来源**: ai-limit-config
**优先级**: P0
**测试步骤**:
1. 前置：psql 执行恢复语句，两项均未修改过
2. 以 admin 登录取 token
3. PUT /api/admin/ai/limits/PER_ANON_DAILY，body `{"value":-1}`
4. GET /api/admin/ai/limits
5. 收尾：PUT /api/admin/ai/limits/PER_ANON_DAILY body `{"value":50}`、PUT /api/admin/ai/limits/GLOBAL_DAILY body `{"value":5000}` 恢复，再 psql 执行恢复语句清空修改人
**预期结果**: 步骤 3 返回 200，body `key=PER_ANON_DAILY`、`value=-1`、`updatedBy=admin`、`updatedAt` 非空；步骤 4 中 `PER_ANON_DAILY` 同上，`GLOBAL_DAILY` 仍为 `value=5000`、`updatedBy`/`updatedAt` 为 null
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-limit-config/TC-ai-IT-089/`
**最后更新**: 2026-10-26

### TC-ai-IT-090: PUT /api/admin/ai/limits/{key} 非法取值返回 400 且数据不变
**关联需求**: ai/生成额度配置#非法取值被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1limits~1{key}/put
**来源**: ai-limit-config
**优先级**: P1
**测试步骤**:
1. 以 admin 登录取 token；GET /api/admin/ai/limits 记录 `GLOBAL_DAILY` 当前 value/updatedBy/updatedAt 基线
2. PUT /api/admin/ai/limits/GLOBAL_DAILY 依次以 body `{"value":0}`、`{"value":-2}`、`{}`、`{"value":1.5}` 请求
3. GET /api/admin/ai/limits
**预期结果**: 步骤 2 四次均返回 400，body 为 `{status,error,message,path}` 且 `message` 含「取值须为 -1 或正整数」；步骤 3 `GLOBAL_DAILY` 的 value/updatedBy/updatedAt 与基线一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-limit-config/TC-ai-IT-090/`
**最后更新**: 2026-10-26

### TC-ai-IT-091: PUT /api/admin/ai/limits/{key} 取值边界 1 与 2147483647 通过
**关联需求**: ai/生成额度配置#修改单项只记录该项修改人
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1limits~1{key}/put
**来源**: ai-limit-config
**优先级**: P2
**测试步骤**:
1. 以 admin 登录取 token
2. PUT /api/admin/ai/limits/GLOBAL_DAILY，body `{"value":1}`
3. PUT /api/admin/ai/limits/GLOBAL_DAILY，body `{"value":2147483647}`
4. 收尾：PUT /api/admin/ai/limits/PER_ANON_DAILY body `{"value":50}`、PUT /api/admin/ai/limits/GLOBAL_DAILY body `{"value":5000}` 恢复
**预期结果**: 步骤 2 返回 200 且 `value=1`；步骤 3 返回 200 且 `value=2147483647`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-limit-config/TC-ai-IT-091/`
**最后更新**: 2026-10-26

### TC-ai-IT-092: PUT /api/admin/ai/limits/{key} 不存在的限制返回 404
**关联需求**: ai/生成额度配置#不存在的限制
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1limits~1{key}/put
**来源**: ai-limit-config
**优先级**: P1
**测试步骤**:
1. 以 admin 登录取 token
2. PUT /api/admin/ai/limits/NOT_EXIST，body `{"value":10}`
3. GET /api/admin/ai/limits
**预期结果**: 步骤 2 返回 404（`{status,error,message,path}`）；步骤 3 仍只有 `GLOBAL_DAILY`、`PER_ANON_DAILY` 两项，未新增行
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-limit-config/TC-ai-IT-092/`
**最后更新**: 2026-10-26

### TC-ai-IT-093: MEMBER 角色可查看与修改限流配置，未登录返回 401
**关联需求**: ai/AI 管理权限与菜单#MEMBER 可修改限流配置
**关联契约**: api-spec.json#/paths/~1api~1admin~1ai~1limits~1{key}/put
**来源**: ai-limit-config
**优先级**: P1
**测试步骤**:
1. 前置：存在 MEMBER 角色的 Manager
2. 以 MEMBER 登录取 token
3. GET /api/admin/ai/limits
4. PUT /api/admin/ai/limits/GLOBAL_DAILY，body `{"value":5000}`
5. 不带 Authorization 头 GET /api/admin/ai/limits 与 PUT /api/admin/ai/limits/GLOBAL_DAILY body `{"value":1}`
**预期结果**: 步骤 3 返回 200；步骤 4 返回 200（不返回 403），`updatedBy` 为该 MEMBER 用户名；步骤 5 均返回 401，`GLOBAL_DAILY` 仍为 5000
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-limit-config/TC-ai-IT-093/`
**最后更新**: 2026-10-26

### TC-ai-IT-094: 修改限流配置产生 ai-limit:update 留痕
**关联需求**: ai/AI 管理写操作留痕#修改限流配置留痕
**关联契约**: api-spec.json#/paths/~1api~1admin~1logs~1page/get
**来源**: ai-limit-config
**优先级**: P1
**测试步骤**:
1. 以 admin 登录取 token
2. PUT /api/admin/ai/limits/GLOBAL_DAILY，body `{"value":4000}`
3. 轮询 GET /api/admin/logs/page?username=admin&module=ai-limit&page=1&size=20（最多 20 次、间隔 100ms）
4. GET /api/admin/ai/limits（查看）后等待 2s，再查一次步骤 3 记录总数
5. 收尾：PUT /api/admin/ai/limits/PER_ANON_DAILY body `{"value":50}`、PUT /api/admin/ai/limits/GLOBAL_DAILY body `{"value":5000}` 恢复
**预期结果**: 步骤 2 返回 200；步骤 3 命中 `module=ai-limit`、`action=update`、`target=GLOBAL_DAILY` 的记录；步骤 4 记录总数不增加（查看不留痕）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-limit-config/TC-ai-IT-094/`
**最后更新**: 2026-10-26

### TC-ai-IT-095: 两项额度均为 -1 时不做额度检查
**关联需求**: ai/生成接口每日额度#-1 不限制
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-limit-config
**优先级**: P1
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；场景 S 启用、已发布模板、已配主模型；以 admin 登录 admin 后端，PUT /api/admin/ai/limits/PER_ANON_DAILY 与 GLOBAL_DAILY body 均为 `{"value":-1}`
2. psql 为 `anon-u-<后缀>` 直插今日（北京时间）100 条记录（`generate_series` 批量插，口径见上方生成记录造数说明）
3. `anon-u-<后缀>` 以新 clientRequestId 提交
4. psql 统计该匿名 ID 今日记录数
5. 收尾：PUT /api/admin/ai/limits/PER_ANON_DAILY body `{"value":50}`、PUT /api/admin/ai/limits/GLOBAL_DAILY body `{"value":5000}` 恢复
**预期结果**: 步骤 3 返回 200（正常生成，不返回 429）；步骤 4 记录数为 101
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-limit-config/TC-ai-IT-095/`
**最后更新**: 2026-10-26

### TC-ai-IT-096: 后台修改额度后下一次请求即生效（无需重启 app）
**关联需求**: ai/生成接口每日额度#修改即时生效
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-limit-config
**优先级**: P1
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；场景 S 启用、已发布模板、已配主模型；以 admin 登录 admin 后端，PUT /api/admin/ai/limits/PER_ANON_DAILY body `{"value":2}`
2. `anon-i-<后缀>` 连续生成 2 次（新 clientRequestId）
3. `anon-i-<后缀>` 以新 clientRequestId 再提交
4. PUT /api/admin/ai/limits/PER_ANON_DAILY body `{"value":3}`（不重启 app）
5. `anon-i-<后缀>` 以另一新 clientRequestId 提交
6. 收尾：PUT /api/admin/ai/limits/PER_ANON_DAILY body `{"value":50}`、PUT /api/admin/ai/limits/GLOBAL_DAILY body `{"value":5000}` 恢复
**预期结果**: 步骤 2 均 200；步骤 3 返回 429「今日生成次数已达上限」；步骤 4 返回 200；步骤 5 返回 200 正常生成
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-limit-config/TC-ai-IT-096/`
**最后更新**: 2026-10-26

### TC-ai-IT-097: 配置行缺失时生成接口返回 500 且不写记录
**关联需求**: ai/生成接口每日额度#配置行缺失
**关联契约**: api-spec.json#/paths/~1api~1app~1ai~1generations/post
**来源**: ai-limit-config
**优先级**: P2
**测试步骤**:
1. 前置：admin/app 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；场景 S 启用、已发布模板、已配主模型；本用例需直接改库：psql `DELETE FROM loves_ai_limit WHERE limit_key='GLOBAL_DAILY';`
2. 以全新匿名 ID `anon-m-<后缀>`、新 clientRequestId 提交
3. psql 统计该匿名 ID 记录数
4. 收尾（必须执行）：psql `INSERT INTO loves_ai_limit (limit_key, name, limit_value) VALUES ('GLOBAL_DAILY', '全站每日上限', 5000);`
**预期结果**: 步骤 2 返回 500（不放行、不返回 200/429）；步骤 3 记录数为 0；收尾后 GET /api/admin/ai/limits 恢复为两项
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ai-limit-config/TC-ai-IT-097/`
**最后更新**: 2026-10-26

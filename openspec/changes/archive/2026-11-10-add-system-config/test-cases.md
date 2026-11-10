# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/{domain}/{it,web,app}.md`（按 modules.md「端」列裁决落点；living 文件，runner 独占回写状态）。
> 新域 `system-config` 已在 `tests/modules.md` 登记（端：web）；operation-log 横切计数同步改为 21 个模块 / 70 个写接口。

## 新增用例

### system-config / IT（`tests/system-config/it.md`，13 条）

- TC-system-config-IT-001: 预置 ios_llm_popup 配置默认存在且 value 以 JSON 原值返回（ADDED Scenario: system-config/系统配置数据模型#预置 ios_llm_popup 配置默认存在且 value 以 JSON 原值返回）
- TC-system-config-IT-002: GET /page 按 keyword 匹配 key 或名称（不区分大小写）（ADDED Scenario: system-config/后台系统配置分页查询#按 keyword 匹配 key 或名称）
- TC-system-config-IT-003: GET /{id} 详情不存在返回 404（ADDED Scenario: system-config/后台系统配置分页查询#详情不存在返回 404）
- TC-system-config-IT-004: 未登录访问后台系统配置接口返回 401（ADDED Scenario: system-config/后台系统配置分页查询#未登录被拒绝）
- TC-system-config-IT-005: PUT /{id} 编辑成功且忽略请求体 key（ADDED Scenario: system-config/后台编辑系统配置#编辑不能修改 key）
- TC-system-config-IT-006: PUT /{id} value 为 null 或缺失返回 400（ADDED Scenario: system-config/后台编辑系统配置#value 为 null 返回 400）
- TC-system-config-IT-007: PUT /{id} ios_llm_popup 的 enabled 非布尔值返回 400（ADDED Scenario: system-config/后台编辑系统配置#ios_llm_popup 的 enabled 非布尔值返回 400）
- TC-system-config-IT-008: 编辑 value 后 App 立即读到新值（ADDED Scenario: system-config/后台编辑系统配置#编辑 value 后 App 立即读到新值）
- TC-system-config-IT-009: GET /api/app/system-configs/{key} 单个查询（ADDED Scenario: system-config/App 查询系统配置#单个 key 查询）
- TC-system-config-IT-010: GET /api/app/system-configs/{key} 不存在返回 404（ADDED Scenario: system-config/App 查询系统配置#单个 key 不存在返回 404）
- TC-system-config-IT-011: GET /api/app/system-configs?keys= 批量查询忽略不存在 key 并保持顺序（ADDED Scenario: system-config/App 查询系统配置#批量查询忽略不存在的 key 并保持顺序）
- TC-system-config-IT-012: 批量查询 keys 缺失、为空与 50/51 边界（ADDED Scenario: system-config/App 查询系统配置#批量查询 keys 缺失或超限返回 400）
- TC-system-config-IT-013: App 系统配置接口 API-key 缺失或非法返回 401（ADDED Scenario: system-config/App 查询系统配置#缺少 API-key 返回 401）

### system-config / WEB（`tests/system-config/web.md`，3 条）

- TC-system-config-WEB-001: 列表页展示预置配置且无新增与删除入口（ADDED Scenario: system-config/系统配置管理页面#列表页展示预置配置且无新增与删除入口）
- TC-system-config-WEB-002: 进入 ios_llm_popup 专属表单页回填并切换开关保存（ADDED Scenario: system-config/系统配置管理页面#进入 ios_llm_popup 专属表单页回填并切换开关保存）
- TC-system-config-WEB-003: 编辑页名称为空时前端拦截（ADDED Scenario: system-config/系统配置管理页面#编辑页名称为空时前端拦截）

### operation-log / IT（`tests/operation-log/it.md`，1 条）

- TC-operation-log-IT-020: 修改系统配置后异步产生 system-config:update 留痕（MODIFIED Requirement 新增 Scenario: operation-log/运营写操作留痕#系统配置修改留痕）

## 修改用例

（无）

## 需重测用例

（无）

## 执行汇总（2026-11-10 交付轮）

- IT：14/14 ✅（TC-system-config-IT-001~013、TC-operation-log-IT-020），admin `:21423` + app `:8081`，存证 `test-evidence/add-system-config/`
- UT：admin 224 + IT 11、app 135 全绿
- WEB：3 条未执行——playwright-company MCP 连接失败（ECONNREFUSED）
- 质量门禁：web lint ✅、tsc + `vite build --mode test` ✅（默认 build 因本机缺 `.env.production` 被守卫拦截，环境原因）；admin/app package ✅；覆盖率节为 TODO 跳过
- `openspec validate add-system-config` ✅

# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/{domain}/{it,web,app}.md`（按 modules.md「端」列裁决落点；living 文件，runner 独占回写状态）。
> 新域 `system-config` 已在 `tests/modules.md` 登记（端：web）；operation-log 横切计数同步改为 21 个模块 / 72 个写接口。

## 新增用例

### system-config / IT（`tests/system-config/it.md`，23 条）

- TC-system-config-IT-001: POST /api/admin/system-configs 新增成功（ADDED Scenario: system-config/后台新增系统配置#新增成功）
- TC-system-config-IT-002: value 以 JSON 原值往返（对象）（ADDED Scenario: system-config/系统配置数据模型#value 以 JSON 原值往返）
- TC-system-config-IT-003: 标量与数组均可作为 value（ADDED Scenario: system-config/系统配置数据模型#标量与数组均可作为 value）
- TC-system-config-IT-004: POST key 重复返回 409（ADDED Scenario: system-config/后台新增系统配置#key 重复返回 409）
- TC-system-config-IT-005: POST key 格式与长度边界（ADDED Scenario: system-config/后台新增系统配置#key 格式非法返回 400）
- TC-system-config-IT-006: POST value 缺失或为 null 返回 400（ADDED Scenario: system-config/后台新增系统配置#value 为 null 返回 400）
- TC-system-config-IT-007: POST value 序列化长度边界 65535 / 65536（ADDED Scenario: system-config/后台新增系统配置#value 为 null 返回 400）
- TC-system-config-IT-008: POST name / description 必填与长度边界（ADDED Scenario: system-config/后台新增系统配置#新增成功）
- TC-system-config-IT-009: GET /page 按 keyword 匹配 key 或名称（不区分大小写）（ADDED Scenario: system-config/后台系统配置分页查询#按 keyword 匹配 key 或名称）
- TC-system-config-IT-010: GET /page 排序 updated_at DESC 与分页参数口径（ADDED Scenario: system-config/后台系统配置分页查询#按 keyword 匹配 key 或名称）
- TC-system-config-IT-011: GET /{id} 详情不存在返回 404（ADDED Scenario: system-config/后台系统配置分页查询#详情不存在返回 404）
- TC-system-config-IT-012: 未登录访问后台系统配置接口返回 401（ADDED Scenario: system-config/后台系统配置分页查询#未登录被拒绝）
- TC-system-config-IT-013: MEMBER 角色可完整使用系统配置接口（ADDED Scenario: system-config/后台系统配置分页查询#未登录被拒绝）
- TC-system-config-IT-014: PUT /{id} 编辑成功且忽略请求体 key（ADDED Scenario: system-config/后台编辑系统配置#编辑不能修改 key）
- TC-system-config-IT-015: PUT /{id} 不存在与校验失败（ADDED Scenario: system-config/后台编辑系统配置#编辑不能修改 key）
- TC-system-config-IT-016: DELETE /{id} 物理删除与重复删除（ADDED Scenario: system-config/后台删除系统配置#删除后 App 查不到）
- TC-system-config-IT-017: 编辑 value 后 App 立即读到新值（ADDED Scenario: system-config/后台编辑系统配置#编辑 value 后 App 立即读到新值）
- TC-system-config-IT-018: 删除后 App 查询返回 404（ADDED Scenario: system-config/后台删除系统配置#删除后 App 查不到）
- TC-system-config-IT-019: GET /api/app/system-configs/{key} 单个查询（ADDED Scenario: system-config/App 查询系统配置#单个 key 查询）
- TC-system-config-IT-020: GET /api/app/system-configs/{key} 不存在返回 404（ADDED Scenario: system-config/App 查询系统配置#单个 key 不存在返回 404）
- TC-system-config-IT-021: GET /api/app/system-configs?keys= 批量查询忽略不存在 key 并保持顺序（ADDED Scenario: system-config/App 查询系统配置#批量查询忽略不存在的 key 并保持顺序）
- TC-system-config-IT-022: 批量查询 keys 缺失、为空与 50/51 边界（ADDED Scenario: system-config/App 查询系统配置#批量查询 keys 缺失或超限返回 400）
- TC-system-config-IT-023: App 系统配置接口 API-key 缺失或非法返回 401（ADDED Scenario: system-config/App 查询系统配置#缺少 API-key 返回 401）

### system-config / WEB（`tests/system-config/web.md`，10 条）

- TC-system-config-WEB-001: 侧栏「系统配置」菜单位置与列表区布局（ADDED Scenario: system-config/系统配置管理页面#新增成功后列表刷新）
- TC-system-config-WEB-002: 非法 JSON 被前端拦截且不发请求（ADDED Scenario: system-config/系统配置管理页面#非法 JSON 被前端拦截）
- TC-system-config-WEB-003: value 为空 / null 与失焦校验（ADDED Scenario: system-config/系统配置管理页面#非法 JSON 被前端拦截）
- TC-system-config-WEB-004: 格式化合法 JSON 与非法 JSON（ADDED Scenario: system-config/系统配置管理页面#格式化合法 JSON）
- TC-system-config-WEB-005: 新增成功后弹窗关闭、列表刷新（ADDED Scenario: system-config/系统配置管理页面#新增成功后列表刷新）
- TC-system-config-WEB-006: value 预览超 40 字符截断、悬停显示全文（ADDED Scenario: system-config/系统配置管理页面#新增成功后列表刷新）
- TC-system-config-WEB-007: 编辑时 key 只读、字段回填并保存（ADDED Scenario: system-config/系统配置管理页面#编辑时 key 只读）
- TC-system-config-WEB-008: key 重复时弹窗内提示且不关闭（ADDED Scenario: system-config/系统配置管理页面#key 重复时弹窗内提示）
- TC-system-config-WEB-009: 删除需二次确认（取消 / 确认）（ADDED Scenario: system-config/系统配置管理页面#删除需二次确认）
- TC-system-config-WEB-010: 搜索 key / 名称与分页（ADDED Scenario: system-config/系统配置管理页面#新增成功后列表刷新）

### operation-log / IT（`tests/operation-log/it.md`，1 条）

- TC-operation-log-IT-020: 系统配置新增、编辑、删除后异步产生 system-config 三条留痕（MODIFIED Requirement 新增 Scenario: operation-log/运营写操作留痕#系统配置增改删留痕）

## 修改用例

（无：operation-log「运营写操作留痕」MODIFIED 仅新增 Scenario 与计数，既有用例断言不受影响）

## 需重测用例

（无）

## 执行汇总

交付轮 2026-11-10：

| 端 | 总数 | ✅ | ❌ | 未执行 | 说明 |
|---|---|---|---|---|---|
| IT | 24 | 24 | 0 | 0 | admin `http://localhost:21423`（test profile）+ app `http://localhost:8081`；存证 `test-evidence/add-system-config/` |
| WEB | 10 | 0 | 0 | 10 | 未执行：本会话 Playwright MCP（playwright-company）连接超时，环境不可用 |

- 契约备注：system-config 的 operation 未声明 `responses`（与文件内既有 AI 接口写法一致），响应按 components.schemas 校验通过。
- TC-system-config-IT-013 收尾步骤由「删除 MEMBER 账号」更正为「禁用」（系统无删除账号接口）。

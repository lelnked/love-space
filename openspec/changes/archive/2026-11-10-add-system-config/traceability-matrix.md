# 追溯矩阵（交付核对）：add-system-config

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change add-system-config`

## 需求与场景
- **operation-log/运营写操作留痕**: 创建城市后异步留痕 / 业务方法失败时不留痕 / 登录不产生日志 / AI 场景创建留痕 / AI 测试类调用不留痕 / AI 生成记录评价留痕 / AI 限流配置修改留痕 / 系统配置修改留痕
- **system-config/App 查询系统配置**: 单个 key 查询 / 单个 key 不存在返回 404 / 批量查询忽略不存在的 key 并保持顺序 / 批量查询 keys 缺失或超限返回 400 / 缺少 API-key 返回 401
- **system-config/后台系统配置分页查询**: 按 keyword 匹配 key 或名称 / 详情不存在返回 404 / 未登录被拒绝
- **system-config/后台编辑系统配置**: 编辑 value 后 App 立即读到新值 / 编辑不能修改 key / value 为 null 返回 400 / ios_llm_popup 的 enabled 非布尔值返回 400
- **system-config/系统配置数据模型**: 预置 ios_llm_popup 配置默认存在且 value 以 JSON 原值返回
- **system-config/系统配置管理页面**: 列表页展示预置配置且无新增与删除入口 / 进入 ios_llm_popup 专属表单页回填并切换开关保存 / 编辑页名称为空时前端拦截

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-operation-log-IT-001 | 创建城市后异步产生 city:create 留痕 | operation-log/运营写操作留痕#创建城市后异步留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/ai-generation-records/TC-operation-log-IT-001/` | ✅ |
| TC-operation-log-IT-002 | 业务校验失败（400）时不产生留痕 | operation-log/运营写操作留痕#业务方法失败时不留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/ai-generation-records/TC-operation-log-IT-002/` | ✅ |
| TC-operation-log-IT-003 | 登录不产生 auth:login 日志 | operation-log/运营写操作留痕#登录不产生日志 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/ai-management/TC-operation-log-IT-003/` | ✅ |
| TC-operation-log-IT-015 | 创建 AI 场景后异步产生 ai-scene:create 留痕 | operation-log/运营写操作留痕#AI 场景创建留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-management | IT | `test-evidence/ai-generation-records/TC-operation-log-IT-015/` | ✅ |
| TC-operation-log-IT-016 | AI 测试连接与模板测试不产生留痕 | operation-log/运营写操作留痕#AI 测试类调用不留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-management | IT | `test-evidence/ai-generation-records/TC-operation-log-IT-016/` | ✅ |
| TC-operation-log-IT-017 | 评价 AI 生成记录后异步产生 ai-generation:rate 留痕 | operation-log/运营写操作留痕#AI 生成记录评价留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-operation-log-IT-017/` | ✅ |
| TC-operation-log-IT-018 | 修改 AI 限流配置后异步产生 ai-limit:update 留痕 | operation-log/运营写操作留痕#AI 限流配置修改留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-limit-config | IT | `test-evidence/ai-limit-config/TC-operation-log-IT-018/` | ✅ |
| TC-operation-log-IT-020 | 修改系统配置后异步产生 system-config:update 留痕 | operation-log/运营写操作留痕#系统配置修改留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | add-system-config | IT | `test-evidence/add-system-config/TC-operation-log-IT-020/` | ✅ |
| TC-system-config-IT-001 | 预置 ios_llm_popup 配置默认存在且 value 以 JSON 原值返回 | system-config/系统配置数据模型#预置 ios_llm_popup 配置默认存在且 value 以 JSON 原值返回 | api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-001/` | ✅ |
| TC-system-config-IT-002 | GET /page 按 keyword 匹配 key 或名称（不区分大小写） | system-config/后台系统配置分页查询#按 keyword 匹配 key 或名称 | api-spec.json#/paths/~1api~1admin~1system-configs~1page/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-002/` | ✅ |
| TC-system-config-IT-003 | GET /{id} 详情不存在返回 404 | system-config/后台系统配置分页查询#详情不存在返回 404 | api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-003/` | ✅ |
| TC-system-config-IT-004 | 未登录访问后台系统配置接口返回 401 | system-config/后台系统配置分页查询#未登录被拒绝 | api-spec.json#/paths/~1api~1admin~1system-configs~1page/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-004/` | ✅ |
| TC-system-config-IT-005 | PUT /{id} 编辑成功且忽略请求体 key | system-config/后台编辑系统配置#编辑不能修改 key | api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/put | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-005/` | ✅ |
| TC-system-config-IT-006 | PUT /{id} value 为 null 或缺失返回 400 | system-config/后台编辑系统配置#value 为 null 返回 400 | api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/put | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-006/` | ✅ |
| TC-system-config-IT-007 | PUT /{id} ios_llm_popup 的 enabled 非布尔值返回 400 | system-config/后台编辑系统配置#ios_llm_popup 的 enabled 非布尔值返回 400 | api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/put | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-007/` | ✅ |
| TC-system-config-IT-008 | 编辑 value 后 App 立即读到新值 | system-config/后台编辑系统配置#编辑 value 后 App 立即读到新值 | api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/put | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-008/` | ✅ |
| TC-system-config-IT-009 | GET /api/app/system-configs/{key} 单个查询 | system-config/App 查询系统配置#单个 key 查询 | api-spec.json#/paths/~1api~1app~1system-configs~1{key}/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-009/` | ✅ |
| TC-system-config-IT-010 | GET /api/app/system-configs/{key} 不存在返回 404 | system-config/App 查询系统配置#单个 key 不存在返回 404 | api-spec.json#/paths/~1api~1app~1system-configs~1{key}/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-010/` | ✅ |
| TC-system-config-IT-011 | GET /api/app/system-configs?keys= 批量查询忽略不存在 key 并保持顺序 | system-config/App 查询系统配置#批量查询忽略不存在的 key 并保持顺序 | api-spec.json#/paths/~1api~1app~1system-configs/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-011/` | ✅ |
| TC-system-config-IT-012 | 批量查询 keys 缺失、为空与 50/51 边界 | system-config/App 查询系统配置#批量查询 keys 缺失或超限返回 400 | api-spec.json#/paths/~1api~1app~1system-configs/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-012/` | ✅ |
| TC-system-config-IT-013 | App 系统配置接口 API-key 缺失或非法返回 401 | system-config/App 查询系统配置#缺少 API-key 返回 401 | api-spec.json#/paths/~1api~1app~1system-configs~1{key}/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-013/` | ✅ |
| TC-system-config-WEB-001 | 列表页展示预置配置且无新增与删除入口 | system-config/系统配置管理页面#列表页展示预置配置且无新增与删除入口 | - | add-system-config | WEB | `test-evidence/regression/system-config/TC-system-config-WEB-001/` | ⬜ |
| TC-system-config-WEB-002 | 进入 ios_llm_popup 专属表单页回填并切换开关保存 | system-config/系统配置管理页面#进入 ios_llm_popup 专属表单页回填并切换开关保存 | - | add-system-config | WEB | `test-evidence/regression/system-config/TC-system-config-WEB-002/` | ⬜ |
| TC-system-config-WEB-003 | 编辑页名称为空时前端拦截 | system-config/系统配置管理页面#编辑页名称为空时前端拦截 | - | add-system-config | WEB | `test-evidence/regression/system-config/TC-system-config-WEB-003/` | ⬜ |

## 覆盖核对

- ⚠ 未覆盖：operation-log/运营写操作留痕#登录不产生日志 无 WEB/APP 用例且无 UT(@scenario) 覆盖

## 测试统计
- 总数：24
- ✅ 通过：21 (87.5%)
- ❌ 失败：0
- ⬜ 未测：3

## 相关提交
- `6687e61` web+admin+app: 新增系统配置功能（key/value，value 为 jsonb）

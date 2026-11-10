# 追溯矩阵（交付核对）：add-system-config

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change add-system-config`

## 需求与场景
- **operation-log/运营写操作留痕**: 创建城市后异步留痕 / 业务方法失败时不留痕 / 登录不产生日志 / AI 场景创建留痕 / AI 测试类调用不留痕 / AI 生成记录评价留痕 / AI 限流配置修改留痕 / 系统配置增改删留痕
- **system-config/App 查询系统配置**: 单个 key 查询 / 单个 key 不存在返回 404 / 批量查询忽略不存在的 key 并保持顺序 / 批量查询 keys 缺失或超限返回 400 / 缺少 API-key 返回 401
- **system-config/后台删除系统配置**: 删除后 App 查不到
- **system-config/后台新增系统配置**: 新增成功 / key 重复返回 409 / key 格式非法返回 400 / value 为 null 返回 400
- **system-config/后台系统配置分页查询**: 按 keyword 匹配 key 或名称 / 详情不存在返回 404 / 未登录被拒绝
- **system-config/后台编辑系统配置**: 编辑 value 后 App 立即读到新值 / 编辑不能修改 key
- **system-config/系统配置数据模型**: value 以 JSON 原值往返 / 标量与数组均可作为 value
- **system-config/系统配置管理页面**: 非法 JSON 被前端拦截 / 格式化合法 JSON / 新增成功后列表刷新 / 编辑时 key 只读 / key 重复时弹窗内提示 / 删除需二次确认

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
| TC-operation-log-IT-020 | 系统配置新增、编辑、删除后异步产生 system-config 三条留痕 | operation-log/运营写操作留痕#系统配置增改删留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | add-system-config | IT | `test-evidence/add-system-config/TC-operation-log-IT-020/` | ✅ |
| TC-system-config-IT-001 | POST /api/admin/system-configs 新增成功 | system-config/后台新增系统配置#新增成功 | api-spec.json#/paths/~1api~1admin~1system-configs/post | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-001/` | ✅ |
| TC-system-config-IT-002 | value 以 JSON 原值往返（对象） | system-config/系统配置数据模型#value 以 JSON 原值往返 | api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-002/` | ✅ |
| TC-system-config-IT-003 | 标量与数组均可作为 value | system-config/系统配置数据模型#标量与数组均可作为 value | api-spec.json#/paths/~1api~1admin~1system-configs/post | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-003/` | ✅ |
| TC-system-config-IT-004 | POST key 重复返回 409 | system-config/后台新增系统配置#key 重复返回 409 | api-spec.json#/paths/~1api~1admin~1system-configs/post | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-004/` | ✅ |
| TC-system-config-IT-005 | POST key 格式与长度边界 | system-config/后台新增系统配置#key 格式非法返回 400 | api-spec.json#/paths/~1api~1admin~1system-configs/post | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-005/` | ✅ |
| TC-system-config-IT-006 | POST value 缺失或为 null 返回 400 | system-config/后台新增系统配置#value 为 null 返回 400 | api-spec.json#/paths/~1api~1admin~1system-configs/post | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-006/` | ✅ |
| TC-system-config-IT-007 | POST value 序列化长度边界 65535 / 65536 | system-config/后台新增系统配置#value 为 null 返回 400 | api-spec.json#/paths/~1api~1admin~1system-configs/post | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-007/` | ✅ |
| TC-system-config-IT-008 | POST name / description 必填与长度边界 | system-config/后台新增系统配置#新增成功 | api-spec.json#/paths/~1api~1admin~1system-configs/post | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-008/` | ✅ |
| TC-system-config-IT-009 | GET /page 按 keyword 匹配 key 或名称（不区分大小写） | system-config/后台系统配置分页查询#按 keyword 匹配 key 或名称 | api-spec.json#/paths/~1api~1admin~1system-configs~1page/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-009/` | ✅ |
| TC-system-config-IT-010 | GET /page 排序 updated_at DESC 与分页参数口径 | system-config/后台系统配置分页查询#按 keyword 匹配 key 或名称 | api-spec.json#/paths/~1api~1admin~1system-configs~1page/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-010/` | ✅ |
| TC-system-config-IT-011 | GET /{id} 详情不存在返回 404 | system-config/后台系统配置分页查询#详情不存在返回 404 | api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-011/` | ✅ |
| TC-system-config-IT-012 | 未登录访问后台系统配置接口返回 401 | system-config/后台系统配置分页查询#未登录被拒绝 | api-spec.json#/paths/~1api~1admin~1system-configs~1page/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-012/` | ✅ |
| TC-system-config-IT-013 | MEMBER 角色可完整使用系统配置接口 | system-config/后台系统配置分页查询#未登录被拒绝 | api-spec.json#/paths/~1api~1admin~1system-configs/post | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-013/` | ✅ |
| TC-system-config-IT-014 | PUT /{id} 编辑成功且忽略请求体 key | system-config/后台编辑系统配置#编辑不能修改 key | api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/put | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-014/` | ✅ |
| TC-system-config-IT-015 | PUT /{id} 不存在与校验失败 | system-config/后台编辑系统配置#编辑不能修改 key | api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/put | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-015/` | ✅ |
| TC-system-config-IT-016 | DELETE /{id} 物理删除与重复删除 | system-config/后台删除系统配置#删除后 App 查不到 | api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/delete | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-016/` | ✅ |
| TC-system-config-IT-017 | 编辑 value 后 App 立即读到新值 | system-config/后台编辑系统配置#编辑 value 后 App 立即读到新值 | api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/put | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-017/` | ✅ |
| TC-system-config-IT-018 | 删除后 App 查询返回 404 | system-config/后台删除系统配置#删除后 App 查不到 | api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/delete | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-018/` | ✅ |
| TC-system-config-IT-019 | GET /api/app/system-configs/{key} 单个查询 | system-config/App 查询系统配置#单个 key 查询 | api-spec.json#/paths/~1api~1app~1system-configs~1{key}/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-019/` | ✅ |
| TC-system-config-IT-020 | GET /api/app/system-configs/{key} 不存在返回 404 | system-config/App 查询系统配置#单个 key 不存在返回 404 | api-spec.json#/paths/~1api~1app~1system-configs~1{key}/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-020/` | ✅ |
| TC-system-config-IT-021 | GET /api/app/system-configs?keys= 批量查询忽略不存在 key 并保持顺序 | system-config/App 查询系统配置#批量查询忽略不存在的 key 并保持顺序 | api-spec.json#/paths/~1api~1app~1system-configs/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-021/` | ✅ |
| TC-system-config-IT-022 | 批量查询 keys 缺失、为空与 50/51 边界 | system-config/App 查询系统配置#批量查询 keys 缺失或超限返回 400 | api-spec.json#/paths/~1api~1app~1system-configs/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-022/` | ✅ |
| TC-system-config-IT-023 | App 系统配置接口 API-key 缺失或非法返回 401 | system-config/App 查询系统配置#缺少 API-key 返回 401 | api-spec.json#/paths/~1api~1app~1system-configs~1{key}/get | add-system-config | IT | `test-evidence/add-system-config/TC-system-config-IT-023/` | ✅ |
| TC-system-config-WEB-001 | 侧栏「系统配置」菜单位置与列表区布局 | system-config/系统配置管理页面#新增成功后列表刷新 | - | add-system-config | WEB | `test-evidence/regression/system-config/TC-system-config-WEB-001/` | ⬜ |
| TC-system-config-WEB-002 | 非法 JSON 被前端拦截且不发请求 | system-config/系统配置管理页面#非法 JSON 被前端拦截 | - | add-system-config | WEB | `test-evidence/regression/system-config/TC-system-config-WEB-002/` | ⬜ |
| TC-system-config-WEB-003 | value 为空 / null 与失焦校验 | system-config/系统配置管理页面#非法 JSON 被前端拦截 | - | add-system-config | WEB | `test-evidence/regression/system-config/TC-system-config-WEB-003/` | ⬜ |
| TC-system-config-WEB-004 | 格式化合法 JSON 与非法 JSON | system-config/系统配置管理页面#格式化合法 JSON | - | add-system-config | WEB | `test-evidence/regression/system-config/TC-system-config-WEB-004/` | ⬜ |
| TC-system-config-WEB-005 | 新增成功后弹窗关闭、列表刷新 | system-config/系统配置管理页面#新增成功后列表刷新 | - | add-system-config | WEB | `test-evidence/regression/system-config/TC-system-config-WEB-005/` | ⬜ |
| TC-system-config-WEB-006 | value 预览超 40 字符截断、悬停显示全文 | system-config/系统配置管理页面#新增成功后列表刷新 | - | add-system-config | WEB | `test-evidence/regression/system-config/TC-system-config-WEB-006/` | ⬜ |
| TC-system-config-WEB-007 | 编辑时 key 只读、字段回填并保存 | system-config/系统配置管理页面#编辑时 key 只读 | - | add-system-config | WEB | `test-evidence/regression/system-config/TC-system-config-WEB-007/` | ⬜ |
| TC-system-config-WEB-008 | key 重复时弹窗内提示且不关闭 | system-config/系统配置管理页面#key 重复时弹窗内提示 | - | add-system-config | WEB | `test-evidence/regression/system-config/TC-system-config-WEB-008/` | ⬜ |
| TC-system-config-WEB-009 | 删除需二次确认（取消 / 确认） | system-config/系统配置管理页面#删除需二次确认 | - | add-system-config | WEB | `test-evidence/regression/system-config/TC-system-config-WEB-009/` | ⬜ |
| TC-system-config-WEB-010 | 搜索 key / 名称与分页 | system-config/系统配置管理页面#新增成功后列表刷新 | - | add-system-config | WEB | `test-evidence/regression/system-config/TC-system-config-WEB-010/` | ⬜ |

## 覆盖核对

- ⚠ 未覆盖：operation-log/运营写操作留痕#登录不产生日志 无 WEB/APP 用例且无 UT(@scenario) 覆盖

## 测试统计
- 总数：41
- ✅ 通过：31 (75.6%)
- ❌ 失败：0
- ⬜ 未测：10

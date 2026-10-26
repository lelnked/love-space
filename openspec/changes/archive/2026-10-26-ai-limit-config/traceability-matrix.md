# 追溯矩阵（交付核对）：ai-limit-config

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change ai-limit-config`

## 需求与场景
- **ai/AI 管理写操作留痕**: 发布留痕 / 测试不留痕 / 修改限流配置留痕
- **ai/AI 管理权限与菜单**: MEMBER 可访问 AI 管理 / 未登录被拒绝 / 侧栏菜单 / MEMBER 可评价生成记录 / MEMBER 可修改限流配置
- **ai/生成接口每日额度**: 匿名 ID 超出每日次数 / 全站超出每日总量 / 昨日记录不计入 / 去重不占额度 / -1 不限制 / 修改即时生效 / 配置行缺失
- **ai/生成额度配置**: 初始数据 / 修改单项只记录该项修改人 / 非法取值被拒绝 / 不存在的限制
- **ai/限流配置页面**: 列表展示 / 编辑保存 / 非法输入禁止保存
- **operation-log/留痕字段取值与敏感信息脱敏**: 密码字段被脱敏 / 创建类操作的 target 为空 / 更新类操作记录目标 id / 嵌套资源的 target 取父级 id / 字符串路径参数作为 target
- **operation-log/运营写操作留痕**: 创建城市后异步留痕 / 业务方法失败时不留痕 / 登录不产生日志 / AI 场景创建留痕 / AI 测试类调用不留痕 / AI 生成记录评价留痕 / AI 限流配置修改留痕

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-ai-IT-034 | 发布模板产生 ai-prompt:publish 留痕 | ai/AI 管理写操作留痕#发布留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-management | IT | `test-evidence/ai-generation-records/TC-ai-IT-034/` | ✅ |
| TC-ai-IT-035 | 测试模板不产生留痕 | ai/AI 管理写操作留痕#测试不留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-management | IT | `test-evidence/ai-generation-records/TC-ai-IT-035/` | ✅ |
| TC-ai-IT-036 | MEMBER 角色可访问 AI 管理接口 | ai/AI 管理权限与菜单#MEMBER 可访问 AI 管理 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1page/get | ai-management | IT | `test-evidence/ai-limit-config/TC-ai-IT-036/` | ✅ |
| TC-ai-IT-037 | 未登录访问 AI 管理接口返回 401 | ai/AI 管理权限与菜单#未登录被拒绝 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1page/get | ai-management | IT | `test-evidence/ai-generation-records/TC-ai-IT-037/` | ✅ |
| TC-ai-IT-038 | POST /api/app/ai/generations 生成成功 | ai/App 生成接口#生成成功 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-limit-config/TC-ai-IT-038/` | ✅ |
| TC-ai-IT-050 | 重复提交返回原结果且不再调用模型 | ai/生成请求去重#重复提交返回原结果 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-limit-config/TC-ai-IT-050/` | ✅ |
| TC-ai-IT-055 | 匿名 ID 超出每日次数返回 429 | ai/生成接口每日额度#匿名 ID 超出每日次数 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-limit-config | IT | `test-evidence/ai-limit-config/TC-ai-IT-055/` | ✅ |
| TC-ai-IT-056 | 全站超出每日总量返回 429 | ai/生成接口每日额度#全站超出每日总量 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-limit-config | IT | `test-evidence/ai-limit-config/TC-ai-IT-056/` | ✅ |
| TC-ai-IT-057 | 北京时间昨日记录不计入今日额度 | ai/生成接口每日额度#昨日记录不计入 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-limit-config | IT | `test-evidence/ai-limit-config/TC-ai-IT-057/` | ✅ |
| TC-ai-IT-058 | 去重命中不占用额度 | ai/生成接口每日额度#去重不占额度 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-limit-config | IT | `test-evidence/ai-limit-config/TC-ai-IT-058/` | ✅ |
| TC-ai-IT-086 | MEMBER 角色可评价生成记录，未登录返回 401 | ai/AI 管理权限与菜单#MEMBER 可评价生成记录 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-086/` | ✅ |
| TC-ai-IT-088 | GET /api/admin/ai/limits 初始数据 | ai/生成额度配置#初始数据 | api-spec.json#/paths/~1api~1admin~1ai~1limits/get | ai-limit-config | IT | `test-evidence/ai-limit-config/TC-ai-IT-088/` | ✅ |
| TC-ai-IT-089 | PUT /api/admin/ai/limits/{key} 修改单项只记录该项修改人 | ai/生成额度配置#修改单项只记录该项修改人 | api-spec.json#/paths/~1api~1admin~1ai~1limits~1{key}/put | ai-limit-config | IT | `test-evidence/ai-limit-config/TC-ai-IT-089/` | ✅ |
| TC-ai-IT-090 | PUT /api/admin/ai/limits/{key} 非法取值返回 400 且数据不变 | ai/生成额度配置#非法取值被拒绝 | api-spec.json#/paths/~1api~1admin~1ai~1limits~1{key}/put | ai-limit-config | IT | `test-evidence/ai-limit-config/TC-ai-IT-090/` | ✅ |
| TC-ai-IT-091 | PUT /api/admin/ai/limits/{key} 取值边界 1 与 2147483647 通过 | ai/生成额度配置#修改单项只记录该项修改人 | api-spec.json#/paths/~1api~1admin~1ai~1limits~1{key}/put | ai-limit-config | IT | `test-evidence/ai-limit-config/TC-ai-IT-091/` | ✅ |
| TC-ai-IT-092 | PUT /api/admin/ai/limits/{key} 不存在的限制返回 404 | ai/生成额度配置#不存在的限制 | api-spec.json#/paths/~1api~1admin~1ai~1limits~1{key}/put | ai-limit-config | IT | `test-evidence/ai-limit-config/TC-ai-IT-092/` | ✅ |
| TC-ai-IT-093 | MEMBER 角色可查看与修改限流配置，未登录返回 401 | ai/AI 管理权限与菜单#MEMBER 可修改限流配置 | api-spec.json#/paths/~1api~1admin~1ai~1limits~1{key}/put | ai-limit-config | IT | `test-evidence/ai-limit-config/TC-ai-IT-093/` | ✅ |
| TC-ai-IT-094 | 修改限流配置产生 ai-limit:update 留痕 | ai/AI 管理写操作留痕#修改限流配置留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-limit-config | IT | `test-evidence/ai-limit-config/TC-ai-IT-094/` | ✅ |
| TC-ai-IT-095 | 两项额度均为 -1 时不做额度检查 | ai/生成接口每日额度#-1 不限制 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-limit-config | IT | `test-evidence/ai-limit-config/TC-ai-IT-095/` | ✅ |
| TC-ai-IT-096 | 后台修改额度后下一次请求即生效（无需重启 app） | ai/生成接口每日额度#修改即时生效 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-limit-config | IT | `test-evidence/ai-limit-config/TC-ai-IT-096/` | ✅ |
| TC-ai-IT-097 | 配置行缺失时生成接口返回 500 且不写记录 | ai/生成接口每日额度#配置行缺失 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-limit-config | IT | `test-evidence/ai-limit-config/TC-ai-IT-097/` | ✅ |
| TC-ai-WEB-001 | 侧栏「AI 管理」菜单及子菜单顺序 | ai/AI 管理权限与菜单#侧栏菜单 | - | ai-limit-config | WEB | `test-evidence/regression/ai/TC-ai-WEB-001/` | ⬜ |
| TC-ai-WEB-031 | 限流配置列表展示 | ai/限流配置页面#列表展示 | - | ai-limit-config | WEB | `test-evidence/regression/ai/TC-ai-WEB-031/` | ⬜ |
| TC-ai-WEB-032 | 编辑限流配置并保存 | ai/限流配置页面#编辑保存 | - | ai-limit-config | WEB | `test-evidence/regression/ai/TC-ai-WEB-032/` | ⬜ |
| TC-ai-WEB-033 | 非法输入禁止保存 | ai/限流配置页面#非法输入禁止保存 | - | ai-limit-config | WEB | `test-evidence/regression/ai/TC-ai-WEB-033/` | ⬜ |
| TC-operation-log-IT-001 | 创建城市后异步产生 city:create 留痕 | operation-log/运营写操作留痕#创建城市后异步留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/ai-generation-records/TC-operation-log-IT-001/` | ✅ |
| TC-operation-log-IT-002 | 业务校验失败（400）时不产生留痕 | operation-log/运营写操作留痕#业务方法失败时不留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/ai-generation-records/TC-operation-log-IT-002/` | ✅ |
| TC-operation-log-IT-003 | 登录不产生 auth:login 日志 | operation-log/运营写操作留痕#登录不产生日志 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/ai-management/TC-operation-log-IT-003/` | ✅ |
| TC-operation-log-IT-004 | 创建运营账号的 payload 中 password 被脱敏 | operation-log/留痕字段取值与敏感信息脱敏#密码字段被脱敏 | api-spec.json#/paths/~1api~1admin~1managers/post | baseline-auth-manager-banner-log-file | IT | `test-evidence/ai-limit-config/TC-operation-log-IT-004/` | ✅ |
| TC-operation-log-IT-005 | 创建类操作的 target 为 null | operation-log/留痕字段取值与敏感信息脱敏#创建类操作的 target 为空 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/ai-limit-config/TC-operation-log-IT-005/` | ✅ |
| TC-operation-log-IT-006 | 更新类操作的 target 为目标城市 id | operation-log/留痕字段取值与敏感信息脱敏#更新类操作记录目标 id | api-spec.json#/paths/~1api~1admin~1cities~1{id}/put | baseline-auth-manager-banner-log-file | IT | `test-evidence/ai-limit-config/TC-operation-log-IT-006/` | ✅ |
| TC-operation-log-IT-007 | 嵌套资源（商户评价）的 target 取父级商户 id | operation-log/留痕字段取值与敏感信息脱敏#嵌套资源的 target 取父级 id | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/ai-limit-config/TC-operation-log-IT-007/` | ✅ |
| TC-operation-log-IT-015 | 创建 AI 场景后异步产生 ai-scene:create 留痕 | operation-log/运营写操作留痕#AI 场景创建留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-management | IT | `test-evidence/ai-generation-records/TC-operation-log-IT-015/` | ✅ |
| TC-operation-log-IT-016 | AI 测试连接与模板测试不产生留痕 | operation-log/运营写操作留痕#AI 测试类调用不留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-management | IT | `test-evidence/ai-generation-records/TC-operation-log-IT-016/` | ✅ |
| TC-operation-log-IT-017 | 评价 AI 生成记录后异步产生 ai-generation:rate 留痕 | operation-log/运营写操作留痕#AI 生成记录评价留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-operation-log-IT-017/` | ✅ |
| TC-operation-log-IT-018 | 修改 AI 限流配置后异步产生 ai-limit:update 留痕 | operation-log/运营写操作留痕#AI 限流配置修改留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-limit-config | IT | `test-evidence/ai-limit-config/TC-operation-log-IT-018/` | ✅ |
| TC-operation-log-IT-019 | 字符串路径参数作为 target（AI 限流配置 key） | operation-log/留痕字段取值与敏感信息脱敏#字符串路径参数作为 target | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-limit-config | IT | `test-evidence/ai-limit-config/TC-operation-log-IT-019/` | ✅ |

## 覆盖核对

- ⚠ 未覆盖：operation-log/留痕字段取值与敏感信息脱敏#密码字段被脱敏 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：operation-log/留痕字段取值与敏感信息脱敏#创建类操作的 target 为空 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：operation-log/留痕字段取值与敏感信息脱敏#更新类操作记录目标 id 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：operation-log/留痕字段取值与敏感信息脱敏#嵌套资源的 target 取父级 id 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：operation-log/运营写操作留痕#登录不产生日志 无 WEB/APP 用例且无 UT(@scenario) 覆盖

## 测试统计
- 总数：37
- ✅ 通过：33 (89.2%)
- ❌ 失败：0
- ⬜ 未测：4

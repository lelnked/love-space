# 追溯矩阵（交付核对）：ai-generation-records

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change ai-generation-records`

## 需求与场景
- **ai/AI 管理写操作留痕**: 发布留痕 / 测试不留痕
- **ai/AI 管理权限与菜单**: MEMBER 可访问 AI 管理 / 未登录被拒绝 / 侧栏菜单 / MEMBER 可评价生成记录
- **ai/生成记录人工评价**: 首次评价 / 再次评价覆盖原评价 / 生成中的记录不能评价 / 失败记录可以评价 / 评价参数非法 / 评价留痕
- **ai/生成记录列表查询**: 默认最近 7 天 / 日期区间含两端整日 / 开始晚于结束被拒绝 / 按未评价筛选 / 按问题标签筛选 / 组合筛选与倒序 / 非法枚举被拒绝 / 列表不含正文
- **ai/生成记录列表页面**: 默认展示最近 7 天 / 时间范围互相限制 / 模板版本依赖场景 / 按未评价筛选 / 点击行进入详情
- **ai/生成记录详情**: 详情含调用明细 / 未评价记录的评价字段为空 / 记录不存在
- **ai/生成记录详情页面**: 未评价记录保存评价 / 修改已有评价 / 生成中记录评价区置灰 / 展开调用原始返回
- **operation-log/运营写操作留痕**: 创建城市后异步留痕 / 业务方法失败时不留痕 / 登录不产生日志 / AI 场景创建留痕 / AI 测试类调用不留痕 / AI 生成记录评价留痕

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-ai-IT-034 | 发布模板产生 ai-prompt:publish 留痕 | ai/AI 管理写操作留痕#发布留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-management | IT | `test-evidence/ai-generation-records/TC-ai-IT-034/` | ✅ |
| TC-ai-IT-035 | 测试模板不产生留痕 | ai/AI 管理写操作留痕#测试不留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-management | IT | `test-evidence/ai-generation-records/TC-ai-IT-035/` | ✅ |
| TC-ai-IT-036 | MEMBER 角色可访问 AI 管理接口 | ai/AI 管理权限与菜单#MEMBER 可访问 AI 管理 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1page/get | ai-management | IT | `test-evidence/ai-generation-records/TC-ai-IT-036/` | ✅ |
| TC-ai-IT-037 | 未登录访问 AI 管理接口返回 401 | ai/AI 管理权限与菜单#未登录被拒绝 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1page/get | ai-management | IT | `test-evidence/ai-generation-records/TC-ai-IT-037/` | ✅ |
| TC-ai-IT-038 | POST /api/app/ai/generations 生成成功 | ai/App 生成接口#生成成功 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-generation-records/TC-ai-IT-038/` | ✅ |
| TC-ai-IT-039 | 生成成功写完整生成记录与调用明细 | ai/生成记录与调用明细落库#成功生成写完整记录 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-generation-records/TC-ai-IT-039/` | ✅ |
| TC-ai-IT-065 | GET /api/admin/ai/generations/page 不传日期默认最近 7 天 | ai/生成记录列表查询#默认最近 7 天 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-065/` | ✅ |
| TC-ai-IT-066 | GET /api/admin/ai/generations/page 日期区间含两端整日（北京时间） | ai/生成记录列表查询#日期区间含两端整日 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-066/` | ✅ |
| TC-ai-IT-067 | GET /api/admin/ai/generations/page 只传一端按单边过滤 | ai/生成记录列表查询#日期区间含两端整日 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-067/` | ✅ |
| TC-ai-IT-068 | GET /api/admin/ai/generations/page 开始晚于结束被拒绝 | ai/生成记录列表查询#开始晚于结束被拒绝 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-068/` | ✅ |
| TC-ai-IT-069 | GET /api/admin/ai/generations/page 按未评价筛选 rating=NONE | ai/生成记录列表查询#按未评价筛选 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-069/` | ✅ |
| TC-ai-IT-070 | GET /api/admin/ai/generations/page 按问题标签筛选 | ai/生成记录列表查询#按问题标签筛选 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-070/` | ✅ |
| TC-ai-IT-071 | GET /api/admin/ai/generations/page 场景 + 状态组合筛选且按时间倒序 | ai/生成记录列表查询#组合筛选与倒序 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-071/` | ✅ |
| TC-ai-IT-072 | GET /api/admin/ai/generations/page requestId / templateId / model 精确匹配 | ai/生成记录列表查询#组合筛选与倒序 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-072/` | ✅ |
| TC-ai-IT-073 | GET /api/admin/ai/generations/page 非法日期与枚举返回 400 | ai/生成记录列表查询#非法枚举被拒绝 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-073/` | ✅ |
| TC-ai-IT-074 | GET /api/admin/ai/generations/page 列表条目仅十一个字段、不含正文 | ai/生成记录列表查询#列表不含正文 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1page/get | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-074/` | ✅ |
| TC-ai-IT-075 | GET /api/admin/ai/generations/{id} 详情含调用明细且查看不留痕 | ai/生成记录详情#详情含调用明细 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}/get | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-075/` | ✅ |
| TC-ai-IT-076 | GET /api/admin/ai/generations/{id} 未评价记录评价字段为空 | ai/生成记录详情#未评价记录的评价字段为空 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}/get | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-076/` | ✅ |
| TC-ai-IT-077 | GET /api/admin/ai/generations/{id} 记录不存在返回 404 | ai/生成记录详情#记录不存在 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}/get | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-077/` | ✅ |
| TC-ai-IT-078 | PUT /api/admin/ai/generations/{id}/rating 首次评价 | ai/生成记录人工评价#首次评价 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-078/` | ✅ |
| TC-ai-IT-079 | PUT /api/admin/ai/generations/{id}/rating remark 恰 500 字符与全部标签通过 | ai/生成记录人工评价#评价参数非法 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-079/` | ✅ |
| TC-ai-IT-080 | PUT /api/admin/ai/generations/{id}/rating 再次评价整体覆盖、不保留历史 | ai/生成记录人工评价#再次评价覆盖原评价 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-080/` | ✅ |
| TC-ai-IT-081 | PUT /api/admin/ai/generations/{id}/rating 已评价记录不能清除评价 | ai/生成记录人工评价#评价参数非法 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-081/` | ✅ |
| TC-ai-IT-082 | PUT /api/admin/ai/generations/{id}/rating 生成中记录返回 409 | ai/生成记录人工评价#生成中的记录不能评价 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-082/` | ✅ |
| TC-ai-IT-083 | PUT /api/admin/ai/generations/{id}/rating 失败记录可以评价 | ai/生成记录人工评价#失败记录可以评价 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-083/` | ✅ |
| TC-ai-IT-084 | PUT /api/admin/ai/generations/{id}/rating 参数非法返回 400 且评价不变 | ai/生成记录人工评价#评价参数非法 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-084/` | ✅ |
| TC-ai-IT-085 | PUT /api/admin/ai/generations/{id}/rating 记录不存在返回 404 | ai/生成记录人工评价#评价参数非法 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-085/` | ✅ |
| TC-ai-IT-086 | MEMBER 角色可评价生成记录，未登录返回 401 | ai/AI 管理权限与菜单#MEMBER 可评价生成记录 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}~1rating/put | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-086/` | ✅ |
| TC-ai-IT-087 | 评价生成记录产生 ai-generation:rate 留痕 | ai/生成记录人工评价#评价留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-ai-IT-087/` | ✅ |
| TC-ai-WEB-001 | 侧栏「AI 管理」菜单及子菜单顺序 | ai/AI 管理权限与菜单#侧栏菜单 | - | ai-generation-records | WEB | `test-evidence/regression/ai/TC-ai-WEB-001/` | ⬜ |
| TC-ai-WEB-021 | 生成记录列表默认展示最近 7 天 | ai/生成记录列表页面#默认展示最近 7 天 | - | ai-generation-records | WEB | `test-evidence/regression/ai/TC-ai-WEB-021/` | ⬜ |
| TC-ai-WEB-022 | 时间范围开始/结束互相限制可选日期 | ai/生成记录列表页面#时间范围互相限制 | - | ai-generation-records | WEB | `test-evidence/regression/ai/TC-ai-WEB-022/` | ⬜ |
| TC-ai-WEB-023 | 模板版本下拉依赖场景 | ai/生成记录列表页面#模板版本依赖场景 | - | ai-generation-records | WEB | `test-evidence/regression/ai/TC-ai-WEB-023/` | ⬜ |
| TC-ai-WEB-024 | 按未评价筛选并回到第 1 页 | ai/生成记录列表页面#按未评价筛选 | - | ai-generation-records | WEB | `test-evidence/regression/ai/TC-ai-WEB-024/` | ⬜ |
| TC-ai-WEB-025 | 点击列表行进入详情页 | ai/生成记录列表页面#点击行进入详情 | - | ai-generation-records | WEB | `test-evidence/regression/ai/TC-ai-WEB-025/` | ⬜ |
| TC-ai-WEB-026 | 未评价记录保存评价 | ai/生成记录详情页面#未评价记录保存评价 | - | ai-generation-records | WEB | `test-evidence/regression/ai/TC-ai-WEB-026/` | ⬜ |
| TC-ai-WEB-027 | 修改已有评价且不出现评价历史 | ai/生成记录详情页面#修改已有评价 | - | ai-generation-records | WEB | `test-evidence/regression/ai/TC-ai-WEB-027/` | ⬜ |
| TC-ai-WEB-028 | 生成中记录评价区置灰 | ai/生成记录详情页面#生成中记录评价区置灰 | - | ai-generation-records | WEB | `test-evidence/regression/ai/TC-ai-WEB-028/` | ⬜ |
| TC-ai-WEB-029 | 展开调用明细原始返回 | ai/生成记录详情页面#展开调用原始返回 | - | ai-generation-records | WEB | `test-evidence/regression/ai/TC-ai-WEB-029/` | ⬜ |
| TC-ai-WEB-030 | 详情页模板链接跳转模板详情 | ai/生成记录详情页面#未评价记录保存评价 | - | ai-generation-records | WEB | `test-evidence/regression/ai/TC-ai-WEB-030/` | ⬜ |
| TC-operation-log-IT-001 | 创建城市后异步产生 city:create 留痕 | operation-log/运营写操作留痕#创建城市后异步留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/ai-generation-records/TC-operation-log-IT-001/` | ✅ |
| TC-operation-log-IT-002 | 业务校验失败（400）时不产生留痕 | operation-log/运营写操作留痕#业务方法失败时不留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/ai-generation-records/TC-operation-log-IT-002/` | ✅ |
| TC-operation-log-IT-003 | 登录不产生 auth:login 日志 | operation-log/运营写操作留痕#登录不产生日志 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/ai-management/TC-operation-log-IT-003/` | ✅ |
| TC-operation-log-IT-015 | 创建 AI 场景后异步产生 ai-scene:create 留痕 | operation-log/运营写操作留痕#AI 场景创建留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-management | IT | `test-evidence/ai-generation-records/TC-operation-log-IT-015/` | ✅ |
| TC-operation-log-IT-016 | AI 测试连接与模板测试不产生留痕 | operation-log/运营写操作留痕#AI 测试类调用不留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-management | IT | `test-evidence/ai-generation-records/TC-operation-log-IT-016/` | ✅ |
| TC-operation-log-IT-017 | 评价 AI 生成记录后异步产生 ai-generation:rate 留痕 | operation-log/运营写操作留痕#AI 生成记录评价留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-generation-records | IT | `test-evidence/ai-generation-records/TC-operation-log-IT-017/` | ✅ |

## 覆盖核对

- ⚠ 未覆盖：operation-log/运营写操作留痕#登录不产生日志 无 WEB/APP 用例且无 UT(@scenario) 覆盖

## 测试统计
- 总数：46
- ✅ 通过：35 (76.1%)
- ❌ 失败：0
- ⬜ 未测：11

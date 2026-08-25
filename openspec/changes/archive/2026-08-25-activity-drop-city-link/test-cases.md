# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/activity/{it,web}.md`（activity 域「端」列为 web，故只产出 IT + WEB，无 APP）。

## 新增用例

- TC-activity-IT-021: GET /api/admin/activities/page 携带 cityId 不收窄结果（ADDED Scenario: activity/活动管理#活动列表不按城市过滤）
- TC-activity-IT-022: GET /api/app/activities/{id} 详情不受城市上架状态影响（ADDED Scenario: activity/App 端活动查询#城市上架状态不影响活动详情可见性）
- TC-activity-WEB-004: 活动表单无地图选项即可保存（ADDED Scenario: activity/web 端活动管理页面#活动表单无地图选项即可保存）

> 说明：Scenario「请求体携带 cityId 不影响创建」由既有 TC-activity-IT-004 改写覆盖（见下），不另开新用例。

## 修改用例

- TC-activity-IT-001: 创建请求不再传 cityId，断言响应不含该字段（MODIFIED: 活动管理去掉所属地图）
- TC-activity-IT-002: 删去「cityId 为不存在 UUID → 400」步骤，标题由「缺必填或城市不存在被拒绝」改为「缺必填被拒绝」（MODIFIED: 城市存在性校验被移除）
- TC-activity-IT-004: 由「cityId 不可变」改写为「请求体 cityId 被静默忽略」，关联需求改挂 `#请求体携带 cityId 不影响创建`（MODIFIED）
- TC-activity-IT-005: 删除后确认列表的请求去掉 `?cityId=`（MODIFIED: 列表不再支持城市过滤）
- TC-activity-IT-007: 由「上架城市活动列表」改为「全局上线活动列表」，请求不带参数，断言不因城市下架被筛掉（MODIFIED）
- TC-activity-IT-008: app 列表请求去掉 `?cityId=`（MODIFIED）
- TC-activity-IT-009: 增断言「响应不含 cityId」（MODIFIED）
- TC-activity-IT-020: 前置条件去掉「所属城市上架」（MODIFIED）
- TC-activity-WEB-001: 列断言去掉「所属城市」列，增断言筛选区无地图下拉（MODIFIED）
- TC-activity-WEB-002: 必填字段由「城市、标题、图片」改为「标题、图片」，前置去掉「存在上架城市」（MODIFIED）
- TC-activity-WEB-003: 前置去掉「存在上架城市」（MODIFIED）

## 需重测用例

上述全部「修改用例」均需重测（状态已重置为 ⬜ / ⚠️ 待重测）。此外行为未变但受实现影响、需回归确认：

- TC-activity-IT-003: 上下线切换（活动实体结构变更，回归确认未受影响）
- TC-activity-IT-006: 富文本 img src 签名 URL 往返（DTO 字段增删，回归确认序列化未受影响）

> TC-activity-WEB-002 保持既有 ⚠️ 环境阻塞标记（本地 OSS 占位符导致图片上传链路不通），本 change 不解除该阻塞。

## 执行汇总

<!-- runner 跑完后由编排 skill 填写：总数 / ✅ / ❌ / 未执行 -->

## Why

活动的产品信息里缺一个「景观」维度：运营在描述一场活动时，除了集合地/解散地/交通/签证这些行程要素，还需要单独说明这场活动看到的是什么景观（海岸线、火山地貌、雪山……），App 端详情页要独立展示这一项。当前只能塞进「活动简介」的自由文本里，无法在 App 端单独取用。

## What Changes

- `loves_activity` 表新增 `landscape text` 列（可空，无默认值）。
- admin 端活动创建/更新请求与详情响应新增 `landscape` 字段（纯文本，可空）。
- app 端活动**详情**响应新增 `landscape` 字段；活动列表不含该字段（列表只出图片/标题/标签/级别/周期，保持不变）。
- web 端活动表单新增「景观」文本输入框，创建与编辑均可填写并回显。
- `contracts/api-spec.json` 的 `ActivityUpsertRequest` 新增 `landscape` 属性。

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `activity`: 活动实体新增「景观」文本字段，贯通 admin 写入、admin 查询、app 详情查询与 web 表单。

## Impact

- 数据库：新增迁移 `019-add-activity-landscape.sql`（`ALTER TABLE loves_activity ADD COLUMN landscape text`），可加可逆。
- 代码：`love-space-admin` activity 模块（Entity / UpsertRequest / DetailResponse / Service）、`love-space-app` activity 模块（Entity / DetailResponse / QueryService）、`love-space-web`（`api/activities.ts`、`pages/Activities/Form.tsx`）。
- 契约：`contracts/api-spec.json` 的 `ActivityUpsertRequest`。
- 测试：`tests/activity/it.md` 新增 TC-activity-IT-020，`tests/activity/web.md` 新增 TC-activity-WEB-003。
- 兼容性：字段可空、不参与任何过滤或排序，既有活动数据与既有接口调用方不受影响。

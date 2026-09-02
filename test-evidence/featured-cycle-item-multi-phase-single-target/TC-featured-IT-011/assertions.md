# TC-featured-IT-011 断言明细

执行日期: 2026-09-02

- ✅ Step 1: ACTIVITY + 不存在 UUID 返回 400　— 实际 400: {"status":400,"error":"Bad Request","message":"关联活动不存在：16243e82-9e60-4bb8-a417-b38caf5f1ef4","path":"/api/admin/featured-cycle-items"}
- ✅ Step 1: ACTIVITY + 不存在 UUID message 按类型区分「关联活动不存在」　— '关联活动不存在：16243e82-9e60-4bb8-a417-b38caf5f1ef4'
- ✅ Step 1: ACTIVITY + 不存在 UUID 文案不与唯一冲突「已存在周期推荐」混淆　— '关联活动不存在：16243e82-9e60-4bb8-a417-b38caf5f1ef4'
- ✅ Step 2: ROUTE + 不存在 UUID 返回 400　— 实际 400: {"status":400,"error":"Bad Request","message":"关联路线不存在：16243e82-9e60-4bb8-a417-b38caf5f1ef4","path":"/api/admin/featured-cycle-items"}
- ✅ Step 2: ROUTE + 不存在 UUID message 按类型区分「关联路线不存在」　— '关联路线不存在：16243e82-9e60-4bb8-a417-b38caf5f1ef4'
- ✅ Step 2: ROUTE + 不存在 UUID 文案不与唯一冲突「已存在周期推荐」混淆　— '关联路线不存在：16243e82-9e60-4bb8-a417-b38caf5f1ef4'
- ✅ Step 3: ARTICLE + 不存在 UUID 返回 400　— 实际 400: {"status":400,"error":"Bad Request","message":"关联文章不存在：16243e82-9e60-4bb8-a417-b38caf5f1ef4","path":"/api/admin/featured-cycle-items"}
- ✅ Step 3: ARTICLE + 不存在 UUID message 按类型区分「关联文章不存在」　— '关联文章不存在：16243e82-9e60-4bb8-a417-b38caf5f1ef4'
- ✅ Step 3: ARTICLE + 不存在 UUID 文案不与唯一冲突「已存在周期推荐」混淆　— '关联文章不存在：16243e82-9e60-4bb8-a417-b38caf5f1ef4'
- ✅ Step 4: ACTIVITY + 文章 id（跨表不命中） 返回 400　— 实际 400: {"status":400,"error":"Bad Request","message":"关联活动不存在：01a0622c-338d-7791-976a-4e296213855f","path":"/api/admin/featured-cycle-items"}
- ✅ Step 4: ACTIVITY + 文章 id（跨表不命中） message 按类型区分「关联活动不存在」　— '关联活动不存在：01a0622c-338d-7791-976a-4e296213855f'
- ✅ Step 4: ACTIVITY + 文章 id（跨表不命中） 文案不与唯一冲突「已存在周期推荐」混淆　— '关联活动不存在：01a0622c-338d-7791-976a-4e296213855f'
- ✅ 条目均未创建　— 实际 0

结论: ✅ 通过（13/13 断言通过）

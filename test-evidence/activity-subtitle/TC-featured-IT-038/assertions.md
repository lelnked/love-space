# TC-featured-IT-038 断言明细

执行时间: 2026-09-02　结论: ✅ 通过（9/9 断言通过，1 项契约提示 ⚠️ 不判失败）

| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | step 2 状态码 | 200 | 200 | ✅ |
| 2 | step 2 响应头 Content-Type | 含 `application/json` | `application/json` | ✅ |
| 3 | 条目被下发（id 命中） | 数组含 `01a06095-d8f0-713f-9f1e-e77a85416296` | 命中（数组共 2 条） | ✅ |
| 4 | `target` 非 null | 非 null 对象 | 对象 | ✅ |
| 5 | `target.subtitle` 为 null（不回落为活动标题） | `null` | `null` | ✅ |
| 6 | `target.subtitle` ≠ 活动标题（回落防护显式核对） | ≠ "无副标题活动-0902" | null | ✅ |
| 7 | `target.title` = 活动标题 | "无副标题活动-0902" | "无副标题活动-0902" | ✅ |
| 8 | 条目自身 `description` 不被 target 覆盖 | "限时开团" | "限时开团" | ✅ |
| 9 | `target.id` = 活动 id | 01a06095-c3a6-7f38-91b4-7d44a22624b0 | 同 | ✅ |

## 前置步骤断言

| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| P1 | 登录 200 且 token 为三段式 JWT | 3 段 | 3 段（len 251） | ✅ |
| P2 | 创建活动 200 且 `subtitle` 未填 | null | null | ✅ |
| P3 | 创建条目 200 且 `description`="限时开团"、`subtitle`=null | 是 | 是 | ✅ |

## 契约核对

- 请求契约自检：`FeaturedCycleItemUpsertRequest`（phase/type/targetId/banner/sortOrder/online/description）字段与类型符合，`targetId` 必填已提供。
- ⚠️ 契约提示（不判失败）：`api-spec.json#/paths/~1api~1app~1featured-cycle-items/get` 只声明 `summary`/`parameters`，**未声明 `responses`**，无法做响应 schema 机器校验。已按 summary 中的文字契约人工核对：ACTIVITY 形状 `{id,title,subtitle,cover,level}` 与实际返回完全一致，无多余/缺失字段。

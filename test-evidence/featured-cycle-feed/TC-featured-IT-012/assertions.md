# TC-featured-IT-012 断言明细

执行日期: 2026-08-20 ｜ 判定: ✅ 通过（含 1 项 ⚠️ 契约漂移，不判失败）
关联契约: `api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}/put`
本轮按修正后的步骤定义执行（步骤 2 保留合法 activityId，其余必填字段按原类型 ACTIVITY 形态提供）。

## Step 1 前置创建
| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1.1 | 状态码 | 200 | 200 | ✅ |
| 1.2 | Content-Type | application/json | application/json | ✅ |
| 1.3 | phase | MENSTRUAL | MENSTRUAL | ✅ |
| 1.4 | type | ACTIVITY | ACTIVITY | ✅ |
| 1.5 | description | 原说明 | 原说明 | ✅ |
| 1.6 | 请求体契约自检（FeaturedCycleItemUpsertRequest：phase/type/banner 必填，type=ACTIVITY 需 activityId + description） | 合规 | 合规 | ✅ |

## Step 2 PUT 更新
| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 2.1 | 请求体契约自检（宽 record：phase/type/banner 必填；ACTIVITY 形态 activityId+description 齐备；articleId/title 属 ARTICLE 字段应被忽略） | 合规 | 合规 | ✅ |
| 2.2 | 状态码 | 200 | 200 | ✅ |
| 2.3 | Content-Type | application/json | application/json | ✅ |
| 2.4 | phase 未被改写 | MENSTRUAL | MENSTRUAL | ✅ |
| 2.5 | type 未被改写 | ACTIVITY | ACTIVITY | ✅ |
| 2.6 | description 按提交值更新 | 改后的说明 | 改后的说明 | ✅ |
| 2.7 | articleId 未落库 | null | null | ✅ |
| 2.8 | title 未落库 | null | null | ✅ |
| 2.9 | activityId 保持原关联 | 01a01f6c-2cce-7249-bc5c-082116a400c2 | 同左 | ✅ |
| 2.10 | updatedAt 已推进 | > createdAt | 13:50:16 > 13:50:07 | ✅ |

## Step 3 GET 详情复查
| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 3.1 | 状态码 | 200 | 200 | ✅ |
| 3.2 | Content-Type | application/json | application/json | ✅ |
| 3.3 | phase | MENSTRUAL | MENSTRUAL | ✅ |
| 3.4 | type | ACTIVITY | ACTIVITY | ✅ |
| 3.5 | description | 改后的说明 | 改后的说明 | ✅ |
| 3.6 | articleId / title | null / null | null / null | ✅ |
| 3.7 | routeId | null | null | ✅ |

## 契约校验
| # | 项 | 结果 |
|---|---|---|
| C.1 | 请求体符合 `FeaturedCycleItemUpsertRequest` | ✅ |
| C.2 | 响应体 schema 校验 | ⚠️ 契约漂移：api-spec.json 中该 operation（PUT `/api/admin/featured-cycle-items/{id}`）未声明 `responses`，无 200 响应 schema 可比对；实际返回结构与 GET 详情一致（含 relatedTitle、banner{id,url} 等）。仅记录，不判失败。 |

汇总：断言 23 项，✅ 22，⚠️ 1（契约漂移），❌ 0。

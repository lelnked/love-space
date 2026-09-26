## ADDED Requirements

### Requirement: app 端爱女大使只读查询
app 端 SHALL 提供爱女大使只读查询：列表 `GET /api/app/ambassadors` 仅返回 `online=true` 的大使，按 `weight` 倒序、同权重按 `createdAt` 倒序，最多返回 `limit` 条；`limit` 为**可选**参数，缺省值 SHALL 为 **2000**，上限 SHALL 为 **2000**。`limit` 为空或小于 1 时 SHALL 回落缺省值、大于上限时 SHALL 收敛到上限，均不返回 400（与 `PageQuery` 的非法值回落口径一致）。详情 `GET /api/app/ambassadors/{id}` 仅返回上线大使，下线或不存在 SHALL 返回 404。

#### Scenario: 不传 limit 返回全部上线大使
- **GIVEN** 存在 25 位 `online=true` 的大使与若干 `online=false` 的大使
- **WHEN** 请求 `GET /api/app/ambassadors`（不传 `limit`）
- **THEN** 返回 200，包含全部 25 位上线大使，按 weight 倒序，下线大使不出现

#### Scenario: limit 生效并在 2000 处收敛
- **GIVEN** 存在多位上线大使
- **WHEN** 请求 `limit=5`
- **THEN** 返回 200，最多 5 条，首条为 weight 最大者
- **WHEN** 请求 `limit=9999`
- **THEN** 返回 200，条数按上限 2000 收敛（实际返回全部上线大使），不返回 400

#### Scenario: limit 非法值回落缺省
- **GIVEN** 存在多位上线大使
- **WHEN** 请求 `limit=0` 或 `limit=-1`
- **THEN** 返回 200，按缺省值 2000 返回（即全部上线大使），不返回 400

#### Scenario: 大使详情可见性不变
- **GIVEN** 一位下线大使
- **WHEN** 请求其 `GET /api/app/ambassadors/{id}`
- **THEN** 返回 404

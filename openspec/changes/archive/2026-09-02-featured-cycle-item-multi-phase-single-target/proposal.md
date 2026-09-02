## Why

周期推荐当前是「一条条目 = 一个周期」，运营要把同一个活动/路线/文章投放到多个周期，就得在 4 个周期 tab 下各建一条重复条目（banner、文案各填一遍），维护成本和不一致风险都高；app 端的 `period` 数组还要靠「同一 target 的多条条目」反向聚合才能算出来，逻辑绕且脆弱。改为一条条目直接勾选多个周期，并约束一个关联实体全局只允许一条推荐，模型与运营心智一致，app 端也不再需要聚合。

## What Changes

- **BREAKING**（数据模型）：周期字段由单值 `phase` 改为多值 `phases`，一条条目 SHALL 至少勾选一个周期，可勾选多个。
- **BREAKING**（业务约束）：同一关联实体 `(type, targetId)` 全局唯一——一个活动/路线/文章只能存在一条周期推荐条目。违反时创建/更新返回 400。
- **BREAKING**（admin API）：创建/更新请求体 `phase: Period` → `phases: Period[]`；列表响应与详情响应同步改为 `phases`。列表的 `phase` 查询参数改为「包含该周期」语义。
- 周期由「创建后不可变」放宽为**可修改**——多选后限制不可变已无意义（原本不可变是为了防止条目在 tab 间漂移）。`type` 仍为创建后不可变。
- **web**：周期推荐页去掉 4 个周期 tab，改为单一列表；列表新增「周期」列展示该条目勾选的全部周期标签；表单里周期改为多选勾选框组（必选至少一个），且编辑时可改。
- **app API**：响应字段名与形状**不变**（`period` 仍为周期枚举数组），但取值来源改为条目自身的 `phases`，不再跨条目聚合；`period` 查询参数语义改为「条目 `phases` 包含该值」。App 客户端无需改动。
- 存量数据迁移：同一 `(type, targetId)` 的多条条目合并为一条，`phases` 取并集，banner 与文案保留 `created_at` 最早的那条，其余物理删除。

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `featured`: 「周期推荐条目管理」由单周期归属改为多周期勾选 + 关联实体全局唯一 + 周期可修改；「App 端周期推荐查询」的 `period` 数组来源与 `period` 查询参数语义改变；新增「web 端周期推荐页面」的单列表 + 多选表单要求。

## Impact

- **DB**：`loves_featured_cycle_item` — 新增 `phases text[] NOT NULL`，回填后删除 `phase`；新增唯一约束 `(type, target_id)`；迁移前先做重复条目合并。新增 Liquibase changeset。
- **admin 后端**：`modules/featuredcycle` 的 entity / DTO / service / controller / repository；新增唯一性校验与中文错误文案。
- **app 后端**：`modules/featuredcycle` 查询与 `period` 聚合逻辑删除，改为直读 `phases`；过滤条件改为数组包含。
- **web**：`src/pages/FeaturedCycleItems/{List,Form}.tsx`、`src/api/featuredCycleItems.ts` 类型。
- **契约**：`contracts/api-spec.json`、`love-space-app/docs/openapi.json`。
- **测试**：`tests/featured/{it,web}.md` 中周期推荐相关用例需改写。

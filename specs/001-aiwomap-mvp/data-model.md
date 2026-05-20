# Phase 1 Data Model — 爱女地图 MVP

> 数据库：PostgreSQL 16；主键统一 UUIDv7（`uuid` 类型），由应用层 `@PrePersist` 赋值。
> 迁移工具：**Liquibase**（按 Clarifications 2026-05-20 决议）。
> **不创建任何 `FOREIGN KEY` 约束**；引用关系仅以 `xxxId UUID` 字段持有，引用完整性由 service 层校验。
> 列名 `snake_case`；字段名禁止缩写。所有表仅保留时间审计列：
> `created_at TIMESTAMPTZ NOT NULL`、`updated_at TIMESTAMPTZ`；
> **不记录 `created_by` / `updated_by`**（按 Clarifications 2026-05-20 决议）。

## 通用基类

`BaseAuditEntity`（`@MappedSuperclass`）：

- `id: UUID`（主键，UUIDv7，`@PrePersist` 时生成）
- `createdAt: OffsetDateTime`（JPA Auditing 自动写入）
- `updatedAt: OffsetDateTime`

## 实体清单

### 1. User（运营用户）—— admin 后端

| 列 | 类型 | 约束 |
|---|---|---|
| id | uuid | PK, UUIDv7 |
| username | text | NOT NULL, UNIQUE |
| password | text | NOT NULL, BCrypt 哈希 |
| nickname | text | NULL 允许 |
| role | text | NOT NULL, 枚举 {ADMIN, MEMBER} |
| enable | boolean | NOT NULL, 默认 true |
| 审计列 | — | 同 BaseAuditEntity |

索引：`UNIQUE(username)`；`INDEX(role, enable)` 用于列表过滤。

### 2. City（城市）—— admin / app 共享表

| 列 | 类型 | 约束 |
|---|---|---|
| id | uuid | PK |
| chinese_name | text | NOT NULL, UNIQUE |
| english_name | text | NOT NULL |
| chinese_province | text | NOT NULL |
| english_province | text | NOT NULL |
| background_image | text | NULL（URL） |
| banner_sort_order | integer | NOT NULL, 默认 0；取值约束 `>= 0`（后端拒绝负数）；`> 0` 表示该城市进入 explore banner，数值即 banner 排序键 |
| online | boolean | NOT NULL, 默认 false |

索引：`UNIQUE(chinese_name)`；`INDEX(online, banner_sort_order)`（覆盖 banner 查询）。

**Banner 语义**：App 端 `/explore` 的 banner 列表 = 当前已上线（`online=true`）且 `banner_sort_order > 0` 的城市，
按 `banner_sort_order ASC` 排序；banner 展示字段直接复用 City 自身（`backgroundImage` / `chineseName` 等），
不再独立维护 banner 子资源。

**列表排序**：admin 城市列表页按 `created_at DESC` 排序，**不**使用 `banner_sort_order` 作为列表排序键。

### 3. Category（分类）—— MVP 预留

| 列 | 类型 | 约束 |
|---|---|---|
| id | uuid | PK |
| name | text | NOT NULL, UNIQUE, ≤10 汉字（服务端校验） |

列表排序：按 `created_at DESC`。
删除规则：删除分类时，service 层将 `merchant.category_id = ?` 的商户 `online = false`。

### 4. Tag（爱女标签）

| 列 | 类型 | 约束 |
|---|---|---|
| id | uuid | PK |
| name | text | NOT NULL, UNIQUE, ≤6 汉字 |
| online | boolean | NOT NULL, 默认 true |

列表排序：按 `created_at DESC`。

### 5. Merchant（商户）

| 列 | 类型 | 约束 |
|---|---|---|
| id | uuid | PK |
| name | text | NOT NULL, ≤15 汉字（不唯一） |
| logo | text | NOT NULL（URL） |
| address | text | NOT NULL |
| longitude | numeric(9,6) | NULL |
| latitude | numeric(8,6) | NULL |
| city_id | uuid | NOT NULL（无 FK） |
| category_id | uuid | NULL（无 FK） |
| safety_environment_score | smallint | NOT NULL, 0..30 |
| business_rights_score | smallint | NOT NULL, 0..25 |
| experience_friendly_score | smallint | NOT NULL, 0..25 |
| social_contribution_score | smallint | NOT NULL, 0..20 |
| story | text | NULL, ≤5000 字 |
| weight | integer | NOT NULL, 默认 0 |
| online | boolean | NOT NULL, 默认 false |

索引：`INDEX(city_id, online, weight DESC, created_at DESC)`；
`INDEX(category_id)`；CHECK 约束保证四维评分在各自上限内。

### 6. MerchantImage（商户图片）

| 列 | 类型 | 约束 |
|---|---|---|
| id | uuid | PK |
| merchant_id | uuid | NOT NULL（无 FK） |
| url | text | NOT NULL |
| sort_order | integer | NOT NULL |

索引：`INDEX(merchant_id, sort_order)`。

### 7. MerchantPeriod（商户推荐周期，多对多枚举）

| 列 | 类型 | 约束 |
|---|---|---|
| merchant_id | uuid | NOT NULL（无 FK） |
| period | text | NOT NULL, 枚举 {MENSTRUAL, FOLLICULAR, OVULATION, LUTEAL} |

PK：`(merchant_id, period)`；索引 `INDEX(period, merchant_id)`。

### 8. MerchantTag（商户—标签关联）

| 列 | 类型 | 约束 |
|---|---|---|
| merchant_id | uuid | NOT NULL（无 FK） |
| tag_id | uuid | NOT NULL（无 FK） |
| created_at | timestamptz | NOT NULL（用于"按时间排序") |

PK：`(merchant_id, tag_id)`；索引 `INDEX(tag_id)`、`INDEX(merchant_id, created_at)`。

### 9. MerchantReview（用户评价）

| 列 | 类型 | 约束 |
|---|---|---|
| id | uuid | PK |
| merchant_id | uuid | NOT NULL（无 FK） |
| nickname | text | NOT NULL |
| title | text | NOT NULL |
| content | text | NOT NULL（PostgreSQL `TEXT` 默认 UTF-8 编码，原生支持 emoji；无需 `utf8mb4` 配置） |
| sort_order | integer | NOT NULL |

索引：`INDEX(merchant_id, sort_order)`。

### 10. OperationLog（操作日志）

| 列 | 类型 | 约束 |
|---|---|---|
| id | uuid | PK |
| user_id | uuid | NOT NULL（无 FK，可能为系统） |
| username | text | NOT NULL（冗余便于查询） |
| module | text | NOT NULL |
| action | text | NOT NULL |
| target | text | NULL |
| payload | jsonb | NULL |
| created_at | timestamptz | NOT NULL |

索引：`INDEX(created_at DESC)`；`INDEX(username, created_at DESC)`；`INDEX(module, created_at DESC)`。

## 关键校验规则汇总

1. `username` 唯一；密码 BCrypt 哈希后落库。
2. `chineseName` 唯一；`englishName` / 省份字段必填。
3. 分类 / 标签名称 ≤10 / ≤6 汉字且不重名。
4. 商户：`name ≤15 汉字`、`logo` 必填、`images ≥1`、`story ≤5000 字`、四维评分各自 ≤ 上限、`recommendedPeriods`
   多选（4 选 N）。
5. 删除分类 → 串联将关联商户 `online = false`。
6. 标签下架仅影响 App 端隐藏，不联动商户状态。
7. 操作日志的 `payload` 仅记录关键字段，避免存放敏感信息（如密码原文）。

## 状态机

- **City**: `online ∈ {true, false}`；切换由 admin 控制。
- **Tag**: `online ∈ {true, false}`；下架不联动商户。
- **Merchant**: `online ∈ {true, false}`；初始 false；分类删除 → 强制 false。
- **User**: `enable ∈ {true, false}`；停用后不可登录。

# Phase 1 — Data Model: Banner Module

## 实体

### Banner (admin 后端 `com.loves.space.modules.banner.entity.Banner`)

| 字段 | Java 类型 | 列定义 | 约束 / 说明 |
|------|-----------|--------|-------------|
| id | `UUID` | `uuid PRIMARY KEY` | UUIDv7，应用层生成（`@PrePersist`） |
| name | `String` | `varchar(128) NOT NULL` | 非空，长度 ≤ 128 |
| online | `Boolean` | `boolean NOT NULL DEFAULT false` | 默认 false；FR-010 |
| type | `BannerType` enum | `varchar(32) NOT NULL` | 当前取值 `CITY`，预留扩展 |
| imageUrls | `List<String>` | `image_urls jsonb NOT NULL DEFAULT '[]'::jsonb` | 至少 1 张；JSON 字符串数组 |
| linkedEntityId | `UUID` | `linked_entity_id uuid NOT NULL` | type=CITY 时为 `loves_city.id`；JSON 序列化为 `link` 字段 |
| createdAt | `Instant` | `created_at timestamptz NOT NULL` | `@PrePersist` 设置 |
| updatedAt | `Instant` | `updated_at timestamptz NOT NULL` | `@PreUpdate` 刷新 |

**索引**：
- `idx_loves_banner_type_online` on `(type, online)` — 列表过滤
- `idx_loves_banner_linked_entity_id` on `(linked_entity_id)` — 事件监听器批量更新

**无外键约束**（宪法 II）。`linked_entity_id` 指向 `loves_city.id` 但只是值引用。

**枚举 `BannerType`**：`CITY`（首期唯一值）。

### Banner (app 后端 `com.space.app.modules.banner.entity.Banner`)

只读视图实体；字段与 admin 端一致；按宪法 IV 各自维护、不共享包。app 端无写操作。

### City（既有，本特性修改）

- **移除**字段：`bannerSortOrder`（Integer）。
- **不变**字段：`id`、`chineseName`、`englishName`、`province`、`backgroundImage`、`online`、
  `createdAt`、`updatedAt`。
- **新增行为**：`CityService.setOnline(id, online)` 在 `online` 状态实际变化时
  `applicationEventPublisher.publishEvent(new CityOnlineChangedEvent(id, oldOnline, online))`。

## 事件

### CityOnlineChangedEvent

```java
package com.loves.space.modules.city.event;
public record CityOnlineChangedEvent(UUID cityId, boolean previousOnline, boolean currentOnline) {}
```

- 仅当 `previousOnline != currentOnline` 时发布。
- 由 `CityService` 在城市状态字段已 flush 之后发布（仍在事务内）。
- `BannerEventListener.onCityOnlineChanged(CityOnlineChangedEvent)` 使用
  `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)` 处理。

## 状态转换

### Banner.online

| 当前 | 触发 | 新值 | 守卫 |
|------|------|------|------|
| false | 列表页切换 + 关联 city online=true | true | type=CITY 时 city.online MUST true，否则拒绝 |
| true | 列表页切换 | false | 无 |
| false | `CityOnlineChangedEvent(_, false, true)` | true | type=CITY 且 linkedEntityId=cityId |
| true | `CityOnlineChangedEvent(_, true, false)` | false | type=CITY 且 linkedEntityId=cityId |

编辑接口（`PUT /api/admin/banners/{id}`）MUST NOT 改写 online。

## 校验规则（FR-020 等）

- `name`：非空、trim 后长度 ≥ 1、≤ 128。
- `imageUrls`：至少 1 项，每项为合法 URL 字符串（HTTP/HTTPS）。
- `type`：枚举范围内。
- 当 `type=CITY` 时：`linkedEntityId` 非空且 `loves_city` 表存在该 id 的记录（service 层
  显式校验，无 FK）。
- 启用动作（`POST /api/admin/banners/{id}/online` with `online=true`）：当
  `type=CITY` 时关联城市 online MUST true。

## 关系图（概念）

```
loves_city (id, online, ...)            loves_banner (id, type, linked_entity_id, online, ...)
        ▲                                       │
        │  linked_entity_id 值引用              │
        └────────  (CITY type)  ────────────────┘

CityService ── publish ──► CityOnlineChangedEvent ── AFTER_COMMIT ──► BannerEventListener
                                                                       └─► CriteriaUpdate
                                                                              Banner.online
```

## 数据迁移

由 Liquibase changelog 一次性执行：

1. `003-create-loves-banner.sql`
   - 创建 `loves_banner` 表 + 索引。
   - 从 `loves_city` 反向回填：所有 `banner_sort_order > 0 AND background_image IS NOT NULL`
     的城市生成对应 `BannerType.CITY` 记录（`online` 取自城市的 `online`，`image_urls` 用单
     元素 jsonb 数组包裹 `background_image`，`name` 取 `chinese_name`，`linked_entity_id`
     取城市 id）。
2. `004-drop-city-banner-sort-order.sql`
   - `ALTER TABLE loves_city DROP COLUMN banner_sort_order;`
   - 删除相关索引（若存在）。

迁移不可逆——若需回滚 changelog，需在 rollback 段还原列并清空 `loves_banner`（changelog 内
显式声明 rollback SQL）。

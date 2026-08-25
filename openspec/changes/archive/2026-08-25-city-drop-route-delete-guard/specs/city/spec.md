## ADDED Requirements

### Requirement: 地图删除
admin 端删除地图（城市）SHALL 为物理删除，且 SHALL NOT 有任何前置校验——路线、活动均已与地图解耦，不构成删除阻塞。删除提交后 SHALL 异步级联：将该城市的 CITY 类型 Banner 全部下架、将该城市下全部商户全部下架；两者均只下架、不删除，商户的 `cityId` 不清空。删除不存在的地图 SHALL 视作幂等成功。

#### Scenario: 删除地图
- **GIVEN** 一个已创建的地图
- **WHEN** admin 端删除该地图
- **THEN** 返回 200；再查该地图详情返回 400 及中文业务错误（admin 端「资源不存在」全局口径）

#### Scenario: 有路线的地图可以直接删除
- **GIVEN** 系统中存在路线（路线仅持有自由文本地图名，与地图无关联）
- **WHEN** admin 端删除任一地图
- **THEN** 返回 200，删除不被拒绝；路线记录不受影响

#### Scenario: 删除地图连带下架 Banner 与商户
- **GIVEN** 一个地图下有 1 个上架商户，且存在一个指向该地图的上架 CITY Banner
- **WHEN** admin 端删除该地图
- **THEN** 返回 200；事务提交后该商户与该 Banner 均变为下架状态，且两者记录仍存在

## REMOVED Requirements

### Requirement: 城市下存在路线时禁止删除
**Reason**: 该约束的唯一理由是「防止路线的 `cityId` 悬空」。`route-remove-city-id`（2026-08-23 归档）已删除 `loves_route.city_id` 列与实体字段，路线不再持有任何城市关联（仅保留自由文本 `city_name`），前提消失。`CityService.delete()` 中的对应校验也已在该次变更中一并删除，实现侧早已不再拒绝删除。本次仅清理遗留的 living spec 与其孤儿测试。
**Migration**: 无需迁移。删除有路线的城市自 `route-remove-city-id` 起就返回 200，用户可见行为不变。运营侧无需调整操作习惯；若日后需要「地图名被路线引用时告警」，应作为新 Requirement 按 `city_name` 文本匹配另行提出，而非恢复本条。

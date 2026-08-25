--liquibase formatted sql

--changeset love-space:020-remove-activity-city-id
--comment: 移除活动表城市关联字段与索引，活动不再关联地图（rollback 只恢复列结构，原 city_id 数据不可恢复）
ALTER TABLE loves_activity DROP COLUMN IF EXISTS city_id;
DROP INDEX IF EXISTS ix_loves_activity_city;
--rollback
--rollback ALTER TABLE loves_activity ADD COLUMN city_id uuid;
--rollback CREATE INDEX ix_loves_activity_city ON loves_activity (city_id);

--liquibase formatted sql

--changeset love-space:016-remove-route-city-id
--comment: 移除路线表城市关联字段与索引，路线不再关联地图
ALTER TABLE loves_route DROP COLUMN IF EXISTS city_id;
DROP INDEX IF EXISTS ix_loves_route_city_sort;
--rollback
--rollback ALTER TABLE loves_route ADD COLUMN city_id uuid NOT NULL;
--rollback CREATE INDEX ix_loves_route_city_sort ON loves_route (city_id, sort_order);

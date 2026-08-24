--liquibase formatted sql

--changeset love-space:017-add-route-city-name
--comment: 路线表新增所属城市名字段，创建/编辑时写入，不再关联地图
ALTER TABLE loves_route ADD COLUMN IF NOT EXISTS city_name text;
--rollback ALTER TABLE loves_route DROP COLUMN IF EXISTS city_name;

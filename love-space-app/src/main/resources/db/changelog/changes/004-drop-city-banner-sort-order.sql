--liquibase formatted sql

-- 002-banner-module: 移除 loves_city 表上的 banner 相关字段与索引
-- admin / app 两端本文件 MUST 字节一致。

--changeset love-space:004-drop-city-banner-sort-order
--comment: 删除 loves_city.banner_sort_order 字段与对应索引/约束（已迁移至 loves_banner）
DROP INDEX IF EXISTS ix_loves_city_online_banner_sort;
ALTER TABLE loves_city DROP CONSTRAINT IF EXISTS ck_loves_city_banner_sort_nonneg;
ALTER TABLE loves_city DROP COLUMN IF EXISTS banner_sort_order;
CREATE INDEX IF NOT EXISTS ix_loves_city_online ON loves_city (online);
--rollback DROP INDEX IF EXISTS ix_loves_city_online;
--rollback ALTER TABLE loves_city ADD COLUMN banner_sort_order integer NOT NULL DEFAULT 0;
--rollback ALTER TABLE loves_city ADD CONSTRAINT ck_loves_city_banner_sort_nonneg CHECK (banner_sort_order >= 0);
--rollback CREATE INDEX ix_loves_city_online_banner_sort ON loves_city (online, banner_sort_order);

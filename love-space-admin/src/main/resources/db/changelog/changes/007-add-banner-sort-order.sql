--liquibase formatted sql

--changeset love-space:007-add-banner-sort-order
--comment: loves_banner 新增 sort_order（排序权重，越小越靠前）列；建索引供 App 查询按位置+上架+排序检索
ALTER TABLE loves_banner ADD COLUMN sort_order integer NOT NULL DEFAULT 0;
CREATE INDEX ix_loves_banner_position_online_sort ON loves_banner (position_code, online, sort_order);
--rollback DROP INDEX ix_loves_banner_position_online_sort;
--rollback ALTER TABLE loves_banner DROP COLUMN sort_order;

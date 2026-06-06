--liquibase formatted sql

--changeset love-space:006-add-category-sort-online
--comment: loves_category 新增 sort_order（排序权重，越小越靠前）与 online（是否上架）列；建索引供 App 分类菜单查询
ALTER TABLE loves_category ADD COLUMN sort_order integer NOT NULL DEFAULT 0;
ALTER TABLE loves_category ADD COLUMN online boolean NOT NULL DEFAULT false;
CREATE INDEX ix_loves_category_online_sort ON loves_category (online, sort_order, created_at);
--rollback DROP INDEX ix_loves_category_online_sort;
--rollback ALTER TABLE loves_category DROP COLUMN online;
--rollback ALTER TABLE loves_category DROP COLUMN sort_order;

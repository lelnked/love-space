--liquibase formatted sql

--changeset love-space:013-create-featured-item
--comment: 精选·地图上新推荐表 loves_featured_item（关联城市单选，与 Banner 独立，无外键）
CREATE TABLE loves_featured_item (
    id uuid PRIMARY KEY,
    city_id uuid NOT NULL,
    banner text NOT NULL,
    description text,
    online boolean NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
CREATE INDEX ix_loves_featured_item_city ON loves_featured_item (city_id);
--rollback DROP TABLE loves_featured_item;

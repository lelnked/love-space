--liquibase formatted sql

--changeset love-space:014-create-featured-cycle-item
--comment: 精选·周期推荐表 loves_featured_cycle_item（单表三类型：type 判别，关联 id 与文案列平铺可空，全局配置不关联城市，无外键）
CREATE TABLE loves_featured_cycle_item (
    id uuid PRIMARY KEY,
    phase text NOT NULL,
    type text NOT NULL,
    sort_order integer NOT NULL DEFAULT 0,
    online boolean NOT NULL DEFAULT false,
    activity_id uuid,
    route_id uuid,
    article_id uuid,
    title text,
    subtitle text,
    description text,
    note text,
    banner text NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
CREATE INDEX ix_loves_featured_cycle_item_phase ON loves_featured_cycle_item (phase, sort_order);
--rollback DROP TABLE loves_featured_cycle_item;

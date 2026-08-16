--liquibase formatted sql

--changeset love-space:010-create-ambassador-route
--comment: 爱女大使表 loves_ambassador 与路线表 loves_route（标签/图片/地点内联 jsonb，无外键，关联一致性由 service 层保证）
CREATE TABLE loves_ambassador (
    id uuid PRIMARY KEY,
    avatar text NOT NULL,
    name text NOT NULL,
    tags jsonb NOT NULL DEFAULT '[]'::jsonb,
    online boolean NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);

CREATE TABLE loves_route (
    id uuid PRIMARY KEY,
    city_id uuid NOT NULL,
    sort_order integer NOT NULL DEFAULT 0,
    title text NOT NULL,
    ambassador_note text,
    thumbnail text NOT NULL,
    images jsonb NOT NULL DEFAULT '[]'::jsonb,
    travel_time text,
    season text,
    travel_status text,
    ambassador_id uuid NOT NULL,
    spots jsonb NOT NULL DEFAULT '[]'::jsonb,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
CREATE INDEX ix_loves_route_city_sort ON loves_route (city_id, sort_order);
CREATE INDEX ix_loves_route_ambassador ON loves_route (ambassador_id);
--rollback DROP TABLE loves_route;
--rollback DROP TABLE loves_ambassador;

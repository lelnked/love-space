--liquibase formatted sql

--changeset love-space:011-create-activity
--comment: 活动表 loves_activity（图片/标签/周期/路线子条目内联 jsonb，详情富文本存 HTML，无外键）
CREATE TABLE loves_activity (
    id uuid PRIMARY KEY,
    city_id uuid NOT NULL,
    images jsonb NOT NULL DEFAULT '[]'::jsonb,
    title text NOT NULL,
    tags jsonb NOT NULL DEFAULT '[]'::jsonb,
    periods jsonb NOT NULL DEFAULT '[]'::jsonb,
    level text,
    introduction text,
    editor_note text,
    gathering_place text,
    dismissal_place text,
    transportation text,
    visa text,
    itinerary jsonb NOT NULL DEFAULT '[]'::jsonb,
    detail_html text,
    online boolean NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
CREATE INDEX ix_loves_activity_city ON loves_activity (city_id);
--rollback DROP TABLE loves_activity;

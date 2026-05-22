--liquibase formatted sql

-- 002-banner-module: 新增独立 Banner 模块
-- 约束：主键 uuid（应用层 UUIDv7）；统一审计列；无外键约束；列名 snake_case；表名加 loves_ 前缀。
-- admin / app 两端本文件 MUST 字节一致。

--changeset love-space:003-create-loves-banner-table
--comment: 创建 loves_banner 表（独立 Banner 模块）
CREATE TABLE loves_banner (
    id uuid PRIMARY KEY,
    name text NOT NULL,
    online boolean NOT NULL DEFAULT false,
    type text NOT NULL,
    image_urls jsonb NOT NULL DEFAULT '[]'::jsonb,
    linked_entity_id uuid NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
ALTER TABLE loves_banner ADD CONSTRAINT ck_loves_banner_type CHECK (type IN ('CITY'));
CREATE INDEX ix_loves_banner_type_online ON loves_banner (type, online);
CREATE INDEX ix_loves_banner_linked_entity_id ON loves_banner (linked_entity_id);
--rollback DROP TABLE loves_banner;

--changeset love-space:003-migrate-city-banner-to-loves-banner
--comment: 一次性将旧 loves_city 中 banner_sort_order>0 且 background_image 非空的行迁入 loves_banner（CITY 类型）
INSERT INTO loves_banner (id, name, online, type, image_urls, linked_entity_id, created_at, updated_at)
SELECT
    gen_random_uuid(),
    c.chinese_name,
    c.online,
    'CITY',
    jsonb_build_array(c.background_image),
    c.id,
    now(),
    now()
FROM loves_city c
WHERE c.banner_sort_order > 0
  AND c.background_image IS NOT NULL
  AND c.background_image <> '';
--rollback DELETE FROM loves_banner WHERE type = 'CITY';

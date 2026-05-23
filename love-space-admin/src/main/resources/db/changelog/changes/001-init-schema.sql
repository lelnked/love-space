--liquibase formatted sql

-- 爱女地图 初始 schema（formatted-SQL），按当前实体类生成。
-- 约束：
--   * 所有表主键 uuid（应用层 UUIDv7 生成）；
--   * 统一审计列 created_at(TIMESTAMPTZ NOT NULL) / updated_at(TIMESTAMPTZ)；
--   * 不创建任何 FOREIGN KEY 约束；
--   * 不创建任何 CHECK 约束（取值集合在应用层枚举/校验中保证）；
--   * 列名 snake_case，字段不缩写；业务表统一 loves_ 前缀。
-- admin / app 两端本文件 MUST 字节一致。

--changeset love-space:001-create-loves-manager
--comment: loves_manager 运营账号表
CREATE TABLE loves_manager (
    id uuid PRIMARY KEY,
    username text NOT NULL,
    password text NOT NULL,
    nickname text,
    role text NOT NULL,
    enable boolean NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
ALTER TABLE loves_manager ADD CONSTRAINT ux_loves_manager_username UNIQUE (username);
CREATE INDEX ix_loves_manager_role_enable ON loves_manager (role, enable);
--rollback DROP TABLE loves_manager;

--changeset love-space:001-create-loves-city
--comment: loves_city 城市表
CREATE TABLE loves_city (
    id uuid PRIMARY KEY,
    chinese_name text NOT NULL,
    english_name text NOT NULL,
    chinese_province text NOT NULL,
    english_province text NOT NULL,
    background_image text,
    online boolean NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
ALTER TABLE loves_city ADD CONSTRAINT ux_loves_city_chinese_name UNIQUE (chinese_name);
CREATE INDEX ix_loves_city_online ON loves_city (online);
--rollback DROP TABLE loves_city;

--changeset love-space:001-create-loves-category
--comment: loves_category 商户分类表
CREATE TABLE loves_category (
    id uuid PRIMARY KEY,
    name text NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
ALTER TABLE loves_category ADD CONSTRAINT ux_loves_category_name UNIQUE (name);
--rollback DROP TABLE loves_category;

--changeset love-space:001-create-loves-tag
--comment: loves_tag 标签表
CREATE TABLE loves_tag (
    id uuid PRIMARY KEY,
    name text NOT NULL,
    online boolean NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
ALTER TABLE loves_tag ADD CONSTRAINT ux_loves_tag_name UNIQUE (name);
--rollback DROP TABLE loves_tag;

--changeset love-space:001-create-loves-merchant
--comment: loves_merchant 商户表（images / periods 内联为 jsonb）
CREATE TABLE loves_merchant (
    id uuid PRIMARY KEY,
    name text NOT NULL,
    logo text NOT NULL,
    address text NOT NULL,
    longitude numeric(9,6),
    latitude numeric(8,6),
    city_id uuid NOT NULL,
    category_id uuid,
    images jsonb NOT NULL DEFAULT '[]'::jsonb,
    periods jsonb NOT NULL DEFAULT '[]'::jsonb,
    safety_environment_score smallint NOT NULL,
    business_rights_score smallint NOT NULL,
    experience_friendly_score smallint NOT NULL,
    social_contribution_score smallint NOT NULL,
    story text,
    weight integer NOT NULL DEFAULT 0,
    online boolean NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
CREATE INDEX ix_loves_merchant_city_online_weight_created ON loves_merchant (city_id, online, weight DESC, created_at DESC);
CREATE INDEX ix_loves_merchant_category ON loves_merchant (category_id);
--rollback DROP TABLE loves_merchant;

--changeset love-space:001-create-loves-merchant-tag
--comment: loves_merchant_tag 商户-标签关联表（单列 id 主键 + (merchant_id, tag_id) 唯一）
CREATE TABLE loves_merchant_tag (
    id uuid PRIMARY KEY,
    merchant_id uuid NOT NULL,
    tag_id uuid NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);
ALTER TABLE loves_merchant_tag ADD CONSTRAINT ux_loves_merchant_tag_merchant_tag UNIQUE (merchant_id, tag_id);
CREATE INDEX ix_loves_merchant_tag_tag ON loves_merchant_tag (tag_id);
CREATE INDEX ix_loves_merchant_tag_merchant_created ON loves_merchant_tag (merchant_id, created_at);
--rollback DROP TABLE loves_merchant_tag;

--changeset love-space:001-create-loves-merchant-review
--comment: loves_merchant_review 商户评价表
CREATE TABLE loves_merchant_review (
    id uuid PRIMARY KEY,
    merchant_id uuid NOT NULL,
    nickname text NOT NULL,
    title text NOT NULL,
    content text NOT NULL,
    sort_order integer NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
CREATE INDEX ix_loves_merchant_review_merchant_sort ON loves_merchant_review (merchant_id, sort_order);
--rollback DROP TABLE loves_merchant_review;

--changeset love-space:001-create-loves-operation-log
--comment: loves_operation_log 运营操作日志表
CREATE TABLE loves_operation_log (
    id uuid PRIMARY KEY,
    manager_id uuid NOT NULL,
    username text NOT NULL,
    module text NOT NULL,
    action text NOT NULL,
    target text,
    payload jsonb,
    created_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX ix_loves_operation_log_created ON loves_operation_log (created_at DESC);
CREATE INDEX ix_loves_operation_log_username_created ON loves_operation_log (username, created_at DESC);
CREATE INDEX ix_loves_operation_log_module_created ON loves_operation_log (module, created_at DESC);
--rollback DROP TABLE loves_operation_log;

--changeset love-space:001-create-loves-banner
--comment: loves_banner Banner 模块表
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
CREATE INDEX ix_loves_banner_type_online ON loves_banner (type, online);
CREATE INDEX ix_loves_banner_linked_entity_id ON loves_banner (linked_entity_id);
--rollback DROP TABLE loves_banner;

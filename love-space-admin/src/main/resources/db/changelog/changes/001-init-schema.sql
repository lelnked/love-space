--liquibase formatted sql

-- 爱女地图 MVP 初始 schema（formatted-SQL）
-- 约束：所有表主键 uuid（应用层生成 UUIDv7）；统一审计列 created_at/updated_at；
--       不创建任何 FOREIGN KEY 约束；列名 snake_case，字段名禁止缩写；
--       所有业务表统一加 loves_ 前缀（单数 snake_case）。
-- admin / app 两端本文件 MUST 字节一致。

--changeset love-space:001-create-loves-manager-table
--comment: 创建 loves_manager 表（运营 Manager 账号）
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
ALTER TABLE loves_manager ADD CONSTRAINT ck_loves_manager_role CHECK (role IN ('ADMIN','MEMBER'));
--rollback DROP TABLE loves_manager;

--changeset love-space:002-create-loves-city-table
--comment: 创建 loves_city 表
CREATE TABLE loves_city (
    id uuid PRIMARY KEY,
    chinese_name text NOT NULL,
    english_name text NOT NULL,
    chinese_province text NOT NULL,
    english_province text NOT NULL,
    background_image text,
    banner_sort_order integer NOT NULL DEFAULT 0,
    online boolean NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
ALTER TABLE loves_city ADD CONSTRAINT ux_loves_city_chinese_name UNIQUE (chinese_name);
CREATE INDEX ix_loves_city_online_banner_sort ON loves_city (online, banner_sort_order);
ALTER TABLE loves_city ADD CONSTRAINT ck_loves_city_banner_sort_nonneg CHECK (banner_sort_order >= 0);
--rollback DROP TABLE loves_city;

--changeset love-space:003-create-loves-category-table
--comment: 创建 loves_category 表（MVP 预留）
CREATE TABLE loves_category (
    id uuid PRIMARY KEY,
    name text NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
ALTER TABLE loves_category ADD CONSTRAINT ux_loves_category_name UNIQUE (name);
--rollback DROP TABLE loves_category;

--changeset love-space:004-create-loves-tag-table
--comment: 创建 loves_tag 表（爱女标签）
CREATE TABLE loves_tag (
    id uuid PRIMARY KEY,
    name text NOT NULL,
    online boolean NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
ALTER TABLE loves_tag ADD CONSTRAINT ux_loves_tag_name UNIQUE (name);
--rollback DROP TABLE loves_tag;

--changeset love-space:005-create-loves-merchant-table
--comment: 创建 loves_merchant 表（含四维评分 CHECK 约束）
CREATE TABLE loves_merchant (
    id uuid PRIMARY KEY,
    name text NOT NULL,
    logo text NOT NULL,
    address text NOT NULL,
    longitude numeric(9,6),
    latitude numeric(8,6),
    city_id uuid NOT NULL,
    category_id uuid,
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
ALTER TABLE loves_merchant
    ADD CONSTRAINT ck_loves_merchant_score_safety CHECK (safety_environment_score BETWEEN 0 AND 30),
    ADD CONSTRAINT ck_loves_merchant_score_business CHECK (business_rights_score BETWEEN 0 AND 25),
    ADD CONSTRAINT ck_loves_merchant_score_experience CHECK (experience_friendly_score BETWEEN 0 AND 25),
    ADD CONSTRAINT ck_loves_merchant_score_social CHECK (social_contribution_score BETWEEN 0 AND 20);
--rollback DROP TABLE loves_merchant;

--changeset love-space:006-create-loves-merchant-image-table
--comment: 创建 loves_merchant_image 表
CREATE TABLE loves_merchant_image (
    id uuid PRIMARY KEY,
    merchant_id uuid NOT NULL,
    url text NOT NULL,
    sort_order integer NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
CREATE INDEX ix_loves_merchant_image_merchant_sort ON loves_merchant_image (merchant_id, sort_order);
--rollback DROP TABLE loves_merchant_image;

--changeset love-space:007-create-loves-merchant-period-table
--comment: 创建 loves_merchant_period 表（商户推荐周期，多对多枚举）
CREATE TABLE loves_merchant_period (
    merchant_id uuid NOT NULL,
    period text NOT NULL,
    CONSTRAINT pk_loves_merchant_period PRIMARY KEY (merchant_id, period)
);
CREATE INDEX ix_loves_merchant_period_period_merchant ON loves_merchant_period (period, merchant_id);
ALTER TABLE loves_merchant_period ADD CONSTRAINT ck_loves_merchant_period_value CHECK (period IN ('MENSTRUAL','FOLLICULAR','OVULATION','LUTEAL'));
--rollback DROP TABLE loves_merchant_period;

--changeset love-space:008-create-loves-merchant-tag-table
--comment: 创建 loves_merchant_tag 表（商户—标签关联）
CREATE TABLE loves_merchant_tag (
    merchant_id uuid NOT NULL,
    tag_id uuid NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_loves_merchant_tag PRIMARY KEY (merchant_id, tag_id)
);
CREATE INDEX ix_loves_merchant_tag_tag ON loves_merchant_tag (tag_id);
CREATE INDEX ix_loves_merchant_tag_merchant_created ON loves_merchant_tag (merchant_id, created_at);
--rollback DROP TABLE loves_merchant_tag;

--changeset love-space:009-create-loves-merchant-review-table
--comment: 创建 loves_merchant_review 表（用户评价，content 支持 emoji）
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

--changeset love-space:010-create-loves-operation-log-table
--comment: 创建 loves_operation_log 表（manager_id 取代旧 user_id 字段）
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

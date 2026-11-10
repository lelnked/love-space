--liquibase formatted sql

--changeset love-space:030-create-system-config
--comment: 系统配置 key/value，一行一个配置项；运营在后台维护，app 按 key 实时读取。value 为任意合法 JSON
CREATE TABLE loves_system_config (
    id           uuid PRIMARY KEY,
    config_key   text        NOT NULL,
    name         text        NOT NULL,
    config_value jsonb       NOT NULL,
    description  text,
    created_at   TIMESTAMPTZ NOT NULL,
    updated_at   TIMESTAMPTZ,
    CONSTRAINT uk_loves_system_config_key UNIQUE (config_key)
);
--rollback DROP TABLE loves_system_config;

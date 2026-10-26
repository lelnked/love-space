--liquibase formatted sql

--changeset love-space:028-create-ai-limit
--comment: 生成接口每日额度，一种限制一行；运营在后台修改，app 每次生成实时读取。-1 表示不限制
CREATE TABLE loves_ai_limit (
    limit_key   text PRIMARY KEY,
    name        text NOT NULL,
    limit_value int  NOT NULL CHECK (limit_value = -1 OR limit_value > 0),
    updated_by  text,
    updated_at  TIMESTAMPTZ
);
INSERT INTO loves_ai_limit (limit_key, name, limit_value) VALUES
    ('PER_ANON_DAILY', '单个匿名 ID 每日上限', 50),
    ('GLOBAL_DAILY', '全站每日上限', 5000);
--rollback DROP TABLE loves_ai_limit;

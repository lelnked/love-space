--liquibase formatted sql

--changeset love-space:018-add-ambassador-weight
--comment: 爱女大使表新增排序权重字段，app 端列表按 weight DESC, created_at DESC 排序
ALTER TABLE loves_ambassador ADD COLUMN IF NOT EXISTS weight integer NOT NULL DEFAULT 0;
CREATE INDEX IF NOT EXISTS ix_loves_ambassador_online_weight_created ON loves_ambassador (online, weight DESC, created_at DESC);
--rollback DROP INDEX IF EXISTS ix_loves_ambassador_online_weight_created;
--rollback ALTER TABLE loves_ambassador DROP COLUMN IF EXISTS weight;

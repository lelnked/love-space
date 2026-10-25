--liquibase formatted sql

--changeset love-space:027-add-ai-generation-rating
--comment: 生成记录人工评价：每条记录一个评价，覆盖保存（app 实体不映射这些列，rating_tags 靠 DEFAULT 满足 NOT NULL）
ALTER TABLE loves_ai_generation
    ADD COLUMN rating text,
    ADD COLUMN rating_tags jsonb NOT NULL DEFAULT '[]',
    ADD COLUMN rating_remark text,
    ADD COLUMN rated_by text,
    ADD COLUMN rated_at TIMESTAMPTZ;
CREATE INDEX ix_loves_ai_generation_rating ON loves_ai_generation (rating);
--rollback DROP INDEX ix_loves_ai_generation_rating; ALTER TABLE loves_ai_generation DROP COLUMN rating, DROP COLUMN rating_tags, DROP COLUMN rating_remark, DROP COLUMN rated_by, DROP COLUMN rated_at;

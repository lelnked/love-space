--liquibase formatted sql

--changeset love-space:021-add-article-cover-title-intro-tags
--comment: 文章表新增封面标题（列表展示，可空）、引言（详情导语，可空）、标签（jsonb 字符串数组）；存量行不迁移，tags 由默认值补空数组
ALTER TABLE loves_article ADD COLUMN IF NOT EXISTS cover_title text;
ALTER TABLE loves_article ADD COLUMN IF NOT EXISTS intro text;
ALTER TABLE loves_article ADD COLUMN IF NOT EXISTS tags jsonb NOT NULL DEFAULT '[]'::jsonb;
--rollback ALTER TABLE loves_article DROP COLUMN IF EXISTS tags;
--rollback ALTER TABLE loves_article DROP COLUMN IF EXISTS intro;
--rollback ALTER TABLE loves_article DROP COLUMN IF EXISTS cover_title;

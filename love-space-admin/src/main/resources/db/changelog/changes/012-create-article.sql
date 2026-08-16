--liquibase formatted sql

--changeset love-space:012-create-article
--comment: 文章栏目表 loves_article_category 与文章表 loves_article（关联栏目内联 jsonb 数组，内容富文本存 HTML，无外键）
CREATE TABLE loves_article_category (
    id uuid PRIMARY KEY,
    name text NOT NULL,
    icon text NOT NULL,
    sort_order integer NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
CREATE TABLE loves_article (
    id uuid PRIMARY KEY,
    image text NOT NULL,
    title text NOT NULL,
    subtitle text,
    content_html text,
    sort_order integer NOT NULL DEFAULT 0,
    category_ids jsonb NOT NULL DEFAULT '[]'::jsonb,
    online boolean NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
--rollback DROP TABLE loves_article; DROP TABLE loves_article_category;

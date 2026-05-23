--liquibase formatted sql

--changeset love-space:005-refactor-merchant-inline-images-periods
--comment: 将 loves_merchant_image / loves_merchant_period 子表内联为 loves_merchant 的 images / periods jsonb 数组列
ALTER TABLE loves_merchant ADD COLUMN images jsonb NOT NULL DEFAULT '[]'::jsonb;
ALTER TABLE loves_merchant ADD COLUMN periods jsonb NOT NULL DEFAULT '[]'::jsonb;
DROP TABLE IF EXISTS loves_merchant_image;
DROP TABLE IF EXISTS loves_merchant_period;
--rollback CREATE TABLE loves_merchant_image (id uuid PRIMARY KEY, merchant_id uuid NOT NULL, url text NOT NULL, sort_order integer NOT NULL, created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ);
--rollback CREATE TABLE loves_merchant_period (merchant_id uuid NOT NULL, period text NOT NULL, CONSTRAINT pk_loves_merchant_period PRIMARY KEY (merchant_id, period));
--rollback ALTER TABLE loves_merchant DROP COLUMN periods;
--rollback ALTER TABLE loves_merchant DROP COLUMN images;

--changeset love-space:006-refactor-merchant-tag-id-pk
--comment: loves_merchant_tag 由复合主键 (merchant_id, tag_id) 改为单列 id PK，匹配实体 @Id UUID
ALTER TABLE loves_merchant_tag DROP CONSTRAINT pk_loves_merchant_tag;
ALTER TABLE loves_merchant_tag ADD COLUMN id uuid;
UPDATE loves_merchant_tag SET id = gen_random_uuid() WHERE id IS NULL;
ALTER TABLE loves_merchant_tag ALTER COLUMN id SET NOT NULL;
ALTER TABLE loves_merchant_tag ADD CONSTRAINT pk_loves_merchant_tag PRIMARY KEY (id);
ALTER TABLE loves_merchant_tag ADD CONSTRAINT ux_loves_merchant_tag_merchant_tag UNIQUE (merchant_id, tag_id);
--rollback ALTER TABLE loves_merchant_tag DROP CONSTRAINT ux_loves_merchant_tag_merchant_tag;
--rollback ALTER TABLE loves_merchant_tag DROP CONSTRAINT pk_loves_merchant_tag;
--rollback ALTER TABLE loves_merchant_tag DROP COLUMN id;
--rollback ALTER TABLE loves_merchant_tag ADD CONSTRAINT pk_loves_merchant_tag PRIMARY KEY (merchant_id, tag_id);

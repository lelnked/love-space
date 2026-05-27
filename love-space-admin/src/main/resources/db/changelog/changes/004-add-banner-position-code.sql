--liquibase formatted sql

--changeset love-space:004-add-banner-position-code
--comment: loves_banner 新增 position_code（展示位置标识码）列
ALTER TABLE loves_banner ADD COLUMN position_code text NOT NULL DEFAULT '';
ALTER TABLE loves_banner ALTER COLUMN position_code DROP DEFAULT;
--rollback ALTER TABLE loves_banner DROP COLUMN position_code;

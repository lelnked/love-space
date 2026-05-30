--liquibase formatted sql

--changeset love-space:005-add-banner-name-unique
--comment: loves_banner.name 增加唯一约束（Banner 名称不可重复）
ALTER TABLE loves_banner ADD CONSTRAINT ux_loves_banner_name UNIQUE (name);
--rollback ALTER TABLE loves_banner DROP CONSTRAINT ux_loves_banner_name;

--liquibase formatted sql

--changeset love-space:025-add-city-secondary-background-image
--comment: loves_city 新增 secondary_background_image（第二背景图）列
ALTER TABLE loves_city ADD COLUMN secondary_background_image text;
--rollback ALTER TABLE loves_city DROP COLUMN secondary_background_image;

--liquibase formatted sql

--changeset love-space:019-add-activity-landscape
--comment: loves_activity 新增 landscape（景观）列
ALTER TABLE loves_activity ADD COLUMN landscape text;
--rollback ALTER TABLE loves_activity DROP COLUMN landscape;

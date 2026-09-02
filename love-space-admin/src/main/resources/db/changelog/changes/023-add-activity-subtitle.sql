--liquibase formatted sql

--changeset love-space:023-add-activity-subtitle
--comment: loves_activity 新增 subtitle（副标题）列，可空
ALTER TABLE loves_activity ADD COLUMN subtitle text;
--rollback ALTER TABLE loves_activity DROP COLUMN subtitle;

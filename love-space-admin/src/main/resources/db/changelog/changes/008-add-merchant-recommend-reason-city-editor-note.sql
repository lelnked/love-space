--liquibase formatted sql

--changeset love-space:008-add-merchant-recommend-reason-city-editor-note
--comment: loves_merchant 新增 recommend_reason（编辑推荐理由，≤2000 字）；loves_city 新增 editor_note（地图编辑说，≤200 字）；长度由应用层 @Size 校验
ALTER TABLE loves_merchant ADD COLUMN recommend_reason varchar(2000);
ALTER TABLE loves_city ADD COLUMN editor_note varchar(200);
--rollback ALTER TABLE loves_city DROP COLUMN editor_note;
--rollback ALTER TABLE loves_merchant DROP COLUMN recommend_reason;

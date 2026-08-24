--liquibase formatted sql

--changeset love-space:015-recommend-list-status
--comment: recommend_list 增加 status 字段，默认 ONLINE；后续商户下架级联使用
ALTER TABLE loves_recommend_list ADD COLUMN status varchar(32) NOT NULL DEFAULT 'ONLINE';
CREATE INDEX ix_loves_recommend_list_status ON loves_recommend_list (status);
--rollback ALTER TABLE loves_recommend_list DROP COLUMN status;

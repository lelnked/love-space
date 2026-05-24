--liquibase formatted sql

--changeset love-space:003-add-merchant-review-recommended
--comment: loves_merchant_review 新增 recommended（是否推荐）列
ALTER TABLE loves_merchant_review ADD COLUMN recommended boolean NOT NULL DEFAULT false;
--rollback ALTER TABLE loves_merchant_review DROP COLUMN recommended;

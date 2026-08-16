--liquibase formatted sql

--changeset love-space:009-create-recommend-list
--comment: 推荐清单主表 loves_recommend_list 与商户关联表 loves_recommend_list_merchant（无外键，关联一致性由 service 层保证）
CREATE TABLE loves_recommend_list (
    id uuid PRIMARY KEY,
    title text NOT NULL,
    introduction text,
    city_id uuid NOT NULL,
    sort_order integer NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
CREATE INDEX ix_loves_recommend_list_city_sort ON loves_recommend_list (city_id, sort_order);

CREATE TABLE loves_recommend_list_merchant (
    id uuid PRIMARY KEY,
    recommend_list_id uuid NOT NULL,
    merchant_id uuid NOT NULL,
    sort_order integer NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
ALTER TABLE loves_recommend_list_merchant ADD CONSTRAINT ux_loves_rlm_list_merchant UNIQUE (recommend_list_id, merchant_id);
CREATE INDEX ix_loves_rlm_list_sort ON loves_recommend_list_merchant (recommend_list_id, sort_order);
--rollback DROP TABLE loves_recommend_list_merchant;
--rollback DROP TABLE loves_recommend_list;

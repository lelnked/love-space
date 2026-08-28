--liquibase formatted sql

--changeset love-space:022-merge-featured-cycle-item-target-id
--comment: 周期推荐三个关联 id 列合并为单列 target_id（指向哪张表由 type 判别）；三列中恒有且仅有一列非空，全空的脏行会让 SET NOT NULL 失败并中止迁移，此为刻意行为
ALTER TABLE loves_featured_cycle_item ADD COLUMN target_id uuid;
UPDATE loves_featured_cycle_item SET target_id = COALESCE(activity_id, route_id, article_id);
ALTER TABLE loves_featured_cycle_item ALTER COLUMN target_id SET NOT NULL;
ALTER TABLE loves_featured_cycle_item DROP COLUMN activity_id;
ALTER TABLE loves_featured_cycle_item DROP COLUMN route_id;
ALTER TABLE loves_featured_cycle_item DROP COLUMN article_id;
--rollback ALTER TABLE loves_featured_cycle_item ADD COLUMN activity_id uuid;
--rollback ALTER TABLE loves_featured_cycle_item ADD COLUMN route_id uuid;
--rollback ALTER TABLE loves_featured_cycle_item ADD COLUMN article_id uuid;
--rollback UPDATE loves_featured_cycle_item SET activity_id = CASE WHEN type = 'ACTIVITY' THEN target_id END, route_id = CASE WHEN type = 'ROUTE' THEN target_id END, article_id = CASE WHEN type = 'ARTICLE' THEN target_id END;
--rollback ALTER TABLE loves_featured_cycle_item DROP COLUMN target_id;

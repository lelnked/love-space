--liquibase formatted sql

--changeset love-space:024-featured-cycle-item-multi-phase
--comment: 周期推荐单周期 phase 改多周期 phases（jsonb 字符串数组，与 loves_merchant.periods 同构），并对 (type, target_id) 加唯一约束——一个关联实体只能有一条推荐。存量同一 (type,target_id) 的多条条目合并为一条：phases 取组内并集，banner 与文案保留 created_at 最早那条，其余条目**物理删除且不可恢复**（rollback 只能还原列结构，救不回被删条目）。若去重有漏导致唯一索引建不起来，整个 changeset 回滚，不留半迁移状态，此为刻意行为。
ALTER TABLE loves_featured_cycle_item ADD COLUMN phases jsonb NOT NULL DEFAULT '[]'::jsonb;

UPDATE loves_featured_cycle_item t
SET phases = g.phases
FROM (
    SELECT type,
           target_id,
           jsonb_agg(phase ORDER BY CASE phase
                                        WHEN 'MENSTRUAL' THEN 1
                                        WHEN 'FOLLICULAR' THEN 2
                                        WHEN 'OVULATION' THEN 3
                                        WHEN 'LUTEAL' THEN 4
                                        ELSE 5
                                    END) AS phases
    FROM (SELECT DISTINCT type, target_id, phase FROM loves_featured_cycle_item) d
    GROUP BY type, target_id
) g
WHERE t.type = g.type AND t.target_id = g.target_id;

DELETE FROM loves_featured_cycle_item t
WHERE EXISTS (SELECT 1
              FROM loves_featured_cycle_item o
              WHERE o.type = t.type
                AND o.target_id = t.target_id
                AND (o.created_at, o.id) < (t.created_at, t.id));

ALTER TABLE loves_featured_cycle_item DROP COLUMN phase;

CREATE UNIQUE INDEX ux_loves_featured_cycle_item_target ON loves_featured_cycle_item (type, target_id);
--rollback DROP INDEX ux_loves_featured_cycle_item_target;
--rollback ALTER TABLE loves_featured_cycle_item ADD COLUMN phase text;
--rollback UPDATE loves_featured_cycle_item SET phase = phases->>0;
--rollback ALTER TABLE loves_featured_cycle_item ALTER COLUMN phase SET NOT NULL;
--rollback ALTER TABLE loves_featured_cycle_item DROP COLUMN phases;
--rollback CREATE INDEX ix_loves_featured_cycle_item_phase ON loves_featured_cycle_item (phase, sort_order);

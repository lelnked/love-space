--liquibase formatted sql

--changeset love-space:029-add-ai-scene-thinking
--comment: 场景深度思考开关，默认关闭；生成调用方舟时显式传 thinking
ALTER TABLE loves_ai_scene ADD COLUMN thinking_enabled boolean NOT NULL DEFAULT false;
--rollback ALTER TABLE loves_ai_scene DROP COLUMN thinking_enabled;

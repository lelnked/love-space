--liquibase formatted sql

--changeset love-space:031-seed-ios-llm-popup-config
--comment: 预置系统配置 ios_llm_popup（iOS LLM 弹窗开关），默认开启
INSERT INTO loves_system_config (id, config_key, name, config_value, description, created_at, updated_at)
VALUES (
    '019794b6-b400-7000-8000-000000000031'::uuid,
    'ios_llm_popup',
    'iOS LLM 弹窗开关',
    '{"enabled": true}'::jsonb,
    '控制 iOS 系统调用 LLM 时是否需要弹窗',
    now(),
    now()
)
ON CONFLICT (config_key) DO NOTHING;
--rollback DELETE FROM loves_system_config WHERE config_key = 'ios_llm_popup';

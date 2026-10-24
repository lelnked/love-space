--liquibase formatted sql

--changeset love-space:026-create-ai
--comment: AI 管理四表：业务场景、提示词模板、生成记录、调用明细（无外键）
CREATE TABLE loves_ai_scene (
    id uuid PRIMARY KEY,
    scene_id text NOT NULL,
    name text NOT NULL,
    description text,
    enabled boolean NOT NULL DEFAULT true,
    primary_model text,
    backup_model text,
    active_template_id uuid,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    CONSTRAINT ux_loves_ai_scene_scene_id UNIQUE (scene_id)
);

CREATE TABLE loves_ai_prompt_template (
    id uuid PRIMARY KEY,
    scene_id text NOT NULL,
    seq integer NOT NULL,
    name text NOT NULL,
    system_prompt text NOT NULL,
    user_prompt_template text NOT NULL,
    variables jsonb NOT NULL DEFAULT '[]',
    remark text,
    tested boolean NOT NULL DEFAULT false,
    last_test_inputs jsonb,
    last_test_output text,
    last_test_content jsonb,
    last_test_model text,
    last_test_success boolean,
    last_test_error text,
    last_test_latency_ms integer,
    last_test_at TIMESTAMPTZ,
    last_test_by text,
    published_at TIMESTAMPTZ,
    published_by text,
    created_by text,
    updated_by text,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    version integer NOT NULL DEFAULT 0,
    CONSTRAINT ux_loves_ai_prompt_template_scene_seq UNIQUE (scene_id, seq)
);

CREATE TABLE loves_ai_generation (
    id uuid PRIMARY KEY,
    client_request_id text NOT NULL,
    anonymous_user_id text NOT NULL,
    scene_id text NOT NULL,
    template_id uuid NOT NULL,
    template_seq integer NOT NULL,
    app_version text,
    inputs jsonb NOT NULL,
    request_messages jsonb NOT NULL,
    request_params jsonb NOT NULL,
    status text NOT NULL,
    error_code text,
    error_message text,
    final_model text,
    attempt_count integer NOT NULL DEFAULT 0,
    raw_output text,
    content jsonb,
    prompt_tokens integer,
    completion_tokens integer,
    latency_ms integer,
    created_at TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ,
    CONSTRAINT ux_loves_ai_generation_client_request_id UNIQUE (client_request_id)
);
CREATE INDEX ix_loves_ai_generation_created_at ON loves_ai_generation (created_at DESC);
CREATE INDEX ix_loves_ai_generation_scene_created_at ON loves_ai_generation (scene_id, created_at DESC);
CREATE INDEX ix_loves_ai_generation_anonymous_created_at ON loves_ai_generation (anonymous_user_id, created_at);

CREATE TABLE loves_ai_generation_attempt (
    id uuid PRIMARY KEY,
    generation_id uuid NOT NULL,
    seq integer NOT NULL,
    trigger_reason text NOT NULL,
    model text NOT NULL,
    status text NOT NULL,
    error_code text,
    error_message text,
    provider_request_id text,
    raw_response text,
    parsed_content jsonb,
    validation_error text,
    prompt_tokens integer,
    completion_tokens integer,
    usage jsonb,
    started_at TIMESTAMPTZ,
    finished_at TIMESTAMPTZ,
    latency_ms integer
);
CREATE INDEX ix_loves_ai_generation_attempt_generation_seq ON loves_ai_generation_attempt (generation_id, seq);
--rollback DROP TABLE loves_ai_generation_attempt; DROP TABLE loves_ai_generation; DROP TABLE loves_ai_prompt_template; DROP TABLE loves_ai_scene;

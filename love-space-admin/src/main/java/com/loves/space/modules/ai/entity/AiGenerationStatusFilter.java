package com.loves.space.modules.ai.entity;

/** 列表按状态筛选：生成中 + AI 基础库的 7 个最终状态。 */
public enum AiGenerationStatusFilter {
    PROCESSING, SUCCESS, REFUSED, EMPTY, FORMAT_ERROR, TIMEOUT, RATE_LIMITED, UPSTREAM_ERROR
}

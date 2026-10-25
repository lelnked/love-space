package com.loves.space.modules.ai.entity;

/** 列表按评价筛选：三个评价值 + NONE（未评价）。NONE 只用于查询，不入库。 */
public enum AiRatingFilter {
    USABLE, NEEDS_IMPROVEMENT, UNUSABLE, NONE
}

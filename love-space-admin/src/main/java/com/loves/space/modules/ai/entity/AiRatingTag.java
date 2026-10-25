package com.loves.space.modules.ai.entity;

/** 评价问题标签。 */
public enum AiRatingTag {
    /** 忽略输入 */
    IGNORED_INPUT,
    /** 周期关联牵强 */
    WEAK_CYCLE_LINK,
    /** 语气不合适 */
    BAD_TONE,
    /** 内容空泛 */
    VAGUE,
    /** 建议不适用 */
    INAPPLICABLE_ADVICE,
    /** 过度医疗化 */
    OVER_MEDICALIZED,
    /** 格式错误 */
    FORMAT_ERROR,
    /** 其他 */
    OTHER
}

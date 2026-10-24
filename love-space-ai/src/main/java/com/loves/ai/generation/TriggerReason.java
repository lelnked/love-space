package com.loves.ai.generation;

/** 一次模型调用的触发原因。 */
public enum TriggerReason {
    /** 首次 */
    INITIAL,
    /** 同模型重试 */
    RETRY,
    /** 切到备用模型的首次 */
    FALLBACK
}

package com.loves.ai.generation;

/** 单次调用 / 最终生成状态。只有 SUCCESS 计为「AI 生成成功」，其余都会触发重试 / 切备用。 */
public enum AiStatus {
    /** 内容非空且是合法 JSON（对象或数组） */
    SUCCESS,
    /** 模型拒答（finish_reason=content_filter） */
    REFUSED,
    /** 返回内容为空 */
    EMPTY,
    /** 不是合法 JSON */
    FORMAT_ERROR,
    /** 超时 */
    TIMEOUT,
    /** 限流（HTTP 429） */
    RATE_LIMITED,
    /** 其他供应商 / 网络异常 */
    UPSTREAM_ERROR
}

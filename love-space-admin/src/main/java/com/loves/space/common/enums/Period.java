package com.loves.space.common.enums;

/**
 * 生理周期枚举（商户推荐周期，多选）。
 * <p>对应数据库 {@code merchant_period.period} 列，PostgreSQL 通过 CHECK 约束限制取值。
 */
public enum Period {
    /** 月经期。 */
    MENSTRUAL,
    /** 卵泡期。 */
    FOLLICULAR,
    /** 排卵期。 */
    OVULATION,
    /** 黄体期。 */
    LUTEAL
}

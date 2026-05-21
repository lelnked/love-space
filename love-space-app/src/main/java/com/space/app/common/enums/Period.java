package com.space.app.common.enums;

/**
 * 生理周期枚举（商户推荐周期，多选）。
 * <p>取值与 admin 端 {@code com.loves.space.common.enums.Period} 保持一致，
 * 通过数据库 CHECK 约束限制 {@code merchant_period.period} 列。
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

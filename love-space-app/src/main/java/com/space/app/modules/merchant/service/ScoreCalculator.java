package com.space.app.modules.merchant.service;

import com.space.app.modules.merchant.dto.LoveIndexView;
import com.space.app.modules.merchant.dto.ScoreView;
import org.springframework.stereotype.Component;

/**
 * 评分换算工具。
 * <ul>
 *   <li>百分制：{@code percent = round(raw * 100.0 / max)}（四舍五入到整数）；</li>
 *   <li>爱女指数总分：{@code total = S + L + E + I}（满分 100）；</li>
 *   <li>等级：{@code level = clamp(ceil(total / 10.0), 1, 10)}。</li>
 * </ul>
 */
@Component
public class ScoreCalculator {

    /** 安全环境原始分满分。 */
    public static final int SAFETY_ENVIRONMENT_MAX = 30;
    /** 经营权益原始分满分。 */
    public static final int BUSINESS_RIGHTS_MAX = 25;
    /** 体验友好原始分满分。 */
    public static final int EXPERIENCE_FRIENDLY_MAX = 25;
    /** 社会贡献原始分满分。 */
    public static final int SOCIAL_CONTRIBUTION_MAX = 20;

    /** 四维原始分换算为百分制视图。 */
    public ScoreView toScoreView(short safety, short business, short experience, short social) {
        return new ScoreView(
                percent(safety, SAFETY_ENVIRONMENT_MAX),
                percent(business, BUSINESS_RIGHTS_MAX),
                percent(experience, EXPERIENCE_FRIENDLY_MAX),
                percent(social, SOCIAL_CONTRIBUTION_MAX));
    }

    /** 计算爱女指数：total 为四维原始分之和；level 由 total 派生。 */
    public LoveIndexView toLoveIndex(short safety, short business, short experience, short social) {
        // 各项分数已由 DB CHECK 约束保证非负，这里再做一次防御
        requireNonNegative(safety, business, experience, social);
        int total = safety + business + experience + social;
        int level = (int) Math.min(10, Math.max(1, Math.ceil(total / 10.0)));
        return new LoveIndexView(total, level);
    }

    /** 百分制换算：四舍五入到整数。 */
    public int percent(short raw, int max) {
        if (raw < 0) {
            throw new IllegalArgumentException("score must be non-negative: " + raw);
        }
        if (max <= 0) {
            throw new IllegalArgumentException("max must be positive: " + max);
        }
        return (int) Math.round(raw * 100.0 / max);
    }

    private static void requireNonNegative(short... values) {
        for (short v : values) {
            if (v < 0) {
                throw new IllegalArgumentException("score must be non-negative: " + v);
            }
        }
    }
}

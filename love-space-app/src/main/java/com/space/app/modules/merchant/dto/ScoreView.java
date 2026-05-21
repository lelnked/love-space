package com.space.app.modules.merchant.dto;

/**
 * 四维评分百分制视图（已由原始分换算为 0–100 整数）。
 *
 * @param safetyEnvironmentPercent  安全环境百分制（原始满分 30）
 * @param businessRightsPercent     经营权益百分制（原始满分 25）
 * @param experienceFriendlyPercent 体验友好百分制（原始满分 25）
 * @param socialContributionPercent 社会贡献百分制（原始满分 20）
 */
public record ScoreView(
        int safetyEnvironmentPercent,
        int businessRightsPercent,
        int experienceFriendlyPercent,
        int socialContributionPercent
) {
}

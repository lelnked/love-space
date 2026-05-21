package com.loves.space.modules.merchant.dto;

import com.loves.space.common.enums.Period;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 商户创建/更新请求（upsert）。
 * <p>本对象在 service 层一次性写入：商户主记录 + 图片 + 推荐周期 + 标签 + 评价。
 *
 * @param name                    商户名称（≤ 15 个字符）
 * @param logo                    商户 LOGO URL
 * @param address                 详细地址
 * @param longitude               经度（可空）
 * @param latitude                纬度（可空）
 * @param cityId                  所属城市 ID（必填）
 * @param categoryId              所属分类 ID（可空）
 * @param safetyEnvironmentScore  安全环境分（0-30）
 * @param businessRightsScore     经营权益分（0-25）
 * @param experienceFriendlyScore 体验友好分（0-25）
 * @param socialContributionScore 社会贡献分（0-20）
 * @param story                   商户故事（≤ 5000 字符，可空）
 * @param weight                  排序权重（默认 0）
 * @param online                  是否上架（默认 false）
 * @param recommendedPeriods      推荐生理周期列表（可空，service 视为空集合）
 * @param tagIds                  关联标签 ID 列表（可空）
 * @param images                  商户图片列表（至少 1 张）
 * @param reviews                 商户评价列表（可空）
 */
public record MerchantUpsertRequest(
        @NotBlank String name,
        @NotBlank String logo,
        @NotBlank String address,
        BigDecimal longitude,
        BigDecimal latitude,
        @NotNull UUID cityId,
        UUID categoryId,
        @NotNull @Min(0) @Max(30) Short safetyEnvironmentScore,
        @NotNull @Min(0) @Max(25) Short businessRightsScore,
        @NotNull @Min(0) @Max(25) Short experienceFriendlyScore,
        @NotNull @Min(0) @Max(20) Short socialContributionScore,
        String story,
        Integer weight,
        Boolean online,
        List<Period> recommendedPeriods,
        List<UUID> tagIds,
        @Valid List<ImageItem> images,
        @Valid List<ReviewUpsertItem> reviews
) {
    /**
     * 商户图片输入项。
     *
     * @param url       图片 URL
     * @param sortOrder 排序序号
     */
    public record ImageItem(
            @NotBlank String url,
            @NotNull @Min(0) Integer sortOrder
    ) {
    }
}

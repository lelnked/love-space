package com.loves.space.modules.merchant.dto;

import com.loves.space.common.enums.Period;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 商户创建/更新请求（upsert）。
 * <p>主记录与 tag 子表在 service 层一次性写入；images / periods 现已内联在主表；评价由独立 controller 维护。
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
 * @param periods                 推荐生理周期列表（可空，service 视为空集合）
 * @param tagIds                  关联标签 ID 列表（可空）
 * @param images                  商户图片 URL 列表（至少 1 张，按数组顺序展示）
 */
public record MerchantUpsertRequest(
        @NotBlank(message = "商户名称不能为空") String name,
        @NotBlank(message = "商户 LOGO 不能为空")
        @Pattern(regexp = "^(images|bound)/[\\w-]+\\.(png|jpg|webp)$",
                message = "logo 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）")
        String logo,
        @NotBlank(message = "详细地址不能为空") String address,
        BigDecimal longitude,
        BigDecimal latitude,
        @NotNull(message = "所属城市不能为空") UUID cityId,
        UUID categoryId,
        @NotNull(message = "安全环境分不能为空") @Min(value = 0, message = "安全环境分不能小于 0") @Max(value = 30, message = "安全环境分不能超过 30") Short safetyEnvironmentScore,
        @NotNull(message = "经营权益分不能为空") @Min(value = 0, message = "经营权益分不能小于 0") @Max(value = 25, message = "经营权益分不能超过 25") Short businessRightsScore,
        @NotNull(message = "体验友好分不能为空") @Min(value = 0, message = "体验友好分不能小于 0") @Max(value = 25, message = "体验友好分不能超过 25") Short experienceFriendlyScore,
        @NotNull(message = "社会贡献分不能为空") @Min(value = 0, message = "社会贡献分不能小于 0") @Max(value = 20, message = "社会贡献分不能超过 20") Short socialContributionScore,
        String story,
        Integer weight,
        Boolean online,
        List<Period> periods,
        List<UUID> tagIds,
        List<@NotBlank(message = "商户图片不能为空") @Pattern(regexp = "^(images|bound)/[\\w-]+\\.(png|jpg|webp)$",
                message = "images 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）") String> images
) {
}

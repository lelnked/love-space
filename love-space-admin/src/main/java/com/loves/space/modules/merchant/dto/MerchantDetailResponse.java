package com.loves.space.modules.merchant.dto;

import com.loves.space.common.enums.Period;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 商户详情（运营后台）。
 *
 * @param id                      商户 ID
 * @param name                    名称
 * @param logo                    LOGO URL
 * @param address                 地址
 * @param longitude               经度（可空）
 * @param latitude                纬度（可空）
 * @param cityId                  所属城市 ID
 * @param categoryId              所属分类 ID（可空）
 * @param safetyEnvironmentScore  安全环境原始分
 * @param businessRightsScore     经营权益原始分
 * @param experienceFriendlyScore 体验友好原始分
 * @param socialContributionScore 社会贡献原始分
 * @param story                   商户故事
 * @param weight                  排序权重
 * @param online                  是否上架
 * @param recommendedPeriods      推荐生理周期列表
 * @param tagIds                  关联标签 ID 列表
 * @param images                  图片列表（按 sortOrder 升序）
 * @param reviews                 评价列表（按 sortOrder 升序）
 * @param createdAt               创建时间
 * @param updatedAt               更新时间
 */
public record MerchantDetailResponse(
        UUID id,
        String name,
        String logo,
        String address,
        BigDecimal longitude,
        BigDecimal latitude,
        UUID cityId,
        UUID categoryId,
        Short safetyEnvironmentScore,
        Short businessRightsScore,
        Short experienceFriendlyScore,
        Short socialContributionScore,
        String story,
        Integer weight,
        boolean online,
        List<Period> recommendedPeriods,
        List<UUID> tagIds,
        List<ImageItem> images,
        List<ReviewItem> reviews,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    /**
     * 商户图片详情项。
     *
     * @param id        图片 ID
     * @param url       图片 URL
     * @param sortOrder 排序序号
     */
    public record ImageItem(UUID id, String url, Integer sortOrder) {
    }

    /**
     * 商户评价详情项。
     *
     * @param id        评价 ID
     * @param nickname  评价昵称
     * @param title     评价标题
     * @param content   评价内容
     * @param sortOrder 排序序号
     */
    public record ReviewItem(UUID id, String nickname, String title, String content, Integer sortOrder) {
    }
}

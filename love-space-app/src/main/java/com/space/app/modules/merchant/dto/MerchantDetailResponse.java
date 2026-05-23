package com.space.app.modules.merchant.dto;

import com.space.app.common.dto.ImageResponse;
import com.space.app.common.enums.Period;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 商户详情 Response。
 *
 * @param id                 商户 ID
 * @param name               商户名称
 * @param logo               LOGO URL
 * @param images             图片 URL 列表（按 sortOrder 升序）
 * @param address            详细地址
 * @param longitude          经度，可空
 * @param latitude           纬度，可空
 * @param recommendedPeriods 推荐周期（按枚举原序）
 * @param tags               上架标签集合
 * @param scores             四维百分制评分
 * @param loveIndex          爱女指数（total + level）
 * @param reviews            评价列表
 * @param story              商户故事（≤5000 字），可空
 */
public record MerchantDetailResponse(
        UUID id,
        String name,
        ImageResponse logo,
        List<ImageResponse> images,
        String address,
        BigDecimal longitude,
        BigDecimal latitude,
        List<Period> recommendedPeriods,
        List<TagItemResponse> tags,
        ScoreView scores,
        LoveIndexView loveIndex,
        List<ReviewItemResponse> reviews,
        String story
) {
}

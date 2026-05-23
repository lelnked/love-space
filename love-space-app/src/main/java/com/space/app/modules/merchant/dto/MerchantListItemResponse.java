package com.space.app.modules.merchant.dto;

import com.space.app.common.dto.ImageResponse;

import java.util.List;
import java.util.UUID;

/**
 * 商户列表项 Response。
 *
 * @param id        商户 ID
 * @param name      商户名称
 * @param logo      LOGO URL
 * @param address   详细地址
 * @param tags      上架标签集合
 * @param scores    四维百分制评分
 * @param loveIndex 爱女指数（total + level）
 */
public record MerchantListItemResponse(
        UUID id,
        String name,
        ImageResponse logo,
        String address,
        List<TagItemResponse> tags,
        ScoreView scores,
        LoveIndexView loveIndex
) {
}

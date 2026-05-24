package com.loves.space.modules.merchant.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 商户评价响应。
 */
public record MerchantReviewResponse(
        UUID id,
        UUID merchantId,
        String nickname,
        String title,
        String content,
        Integer sortOrder,
        boolean recommended,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}

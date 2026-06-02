package com.space.app.modules.banner.dto;

import com.space.app.common.dto.ImageResponse;
import com.space.app.modules.banner.entity.BannerType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * App 端 Banner 展示项 Response。
 *
 * <p>响应字段顺序固定为 {@code id, name, type, image, data}，
 * {@code data} 形态由 {@link #type} 决定：
 * <ul>
 *   <li>{@link BannerType#CITY}：{@code {id, chineseName, englishName, chineseProvince,
 *       englishProvince}}（城市名以 {@code chineseName} 为准）</li>
 *   <li>其它类型：由对应业务装配，结构与前端约定</li>
 * </ul>
 *
 * @param id    banner 主键 UUID
 * @param name  banner 名称
 * @param type  banner 类型
 * @param image 图片 URL 列表
 * @param data  类型相关的附加数据（CITY 时为 {@code {id, chineseName, englishName,
 *              chineseProvince, englishProvince}}）
 */
public record BannerItemResponse(
        UUID id,
        String name,
        BannerType type,
        List<ImageResponse> image,
        Map<String, Object> data
) {
}

package com.loves.space.modules.banner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.loves.space.modules.banner.entity.BannerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * Banner 创建请求体。
 *
 * @param name      banner 名称（必填，长度 ≤ 128）
 * @param type      banner 类型（必填，首期仅 {@code CITY}）
 * @param imageUrls 图片 URL 列表（必填，至少 1 张）
 * @param link      关联实体 UUID（{@code type=CITY} 时为城市 ID）；JSON 字段名 {@code link}
 * @param sortOrder 排序权重（必填，≥0，越小越靠前）
 */
public record BannerCreateRequest(
        @NotBlank(message = "banner 名称不能为空") @Size(max = 128, message = "banner 名称长度不能超过 128 个字符") String name,
        @NotBlank(message = "展示位编码不能为空") @Size(max = 64, message = "展示位编码长度不能超过 64 个字符") String positionCode,
        @NotNull(message = "banner 类型不能为空") BannerType type,
        @NotEmpty(message = "至少上传一张图片") List<@NotBlank(message = "图片不能为空") @Pattern(regexp = "^(images|bound)/[\\w-]+\\.(png|jpg|webp)$",
                message = "imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）") String> imageUrls,
        @JsonProperty("link") @NotNull(message = "请选择关联城市") UUID linkedEntityId,
        @NotNull(message = "排序值不能为空") @PositiveOrZero(message = "排序值不能为负") Integer sortOrder
) {
}

package com.loves.space.modules.banner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.loves.space.modules.banner.entity.BannerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
 */
public record BannerCreateRequest(
        @NotBlank @Size(max = 128) String name,
        @NotBlank @Size(max = 64) String positionCode,
        @NotNull BannerType type,
        @NotEmpty List<@NotBlank @Pattern(regexp = "^(images|bound)/[\\w-]+\\.(png|jpg|webp)$",
                message = "imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）") String> imageUrls,
        @JsonProperty("link") @NotNull UUID linkedEntityId
) {
}

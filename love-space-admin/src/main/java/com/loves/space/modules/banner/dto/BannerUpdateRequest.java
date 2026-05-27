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
 * Banner 更新请求体（全字段覆盖，但不允许携带 {@code online}）。
 * <p>上下架操作走独立接口 {@code POST /api/admin/banners/{id}/online}，
 * 若请求体中出现 {@code online} 字段，service 层抛 400（FR-009）。
 */
public record BannerUpdateRequest(
        @NotBlank @Size(max = 128) String name,
        @NotBlank @Size(max = 64) String positionCode,
        @NotNull BannerType type,
        @NotEmpty List<@NotBlank @Pattern(regexp = "^(images|bound)/[\\w-]+\\.(png|jpg|webp)$",
                message = "imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）") String> imageUrls,
        @JsonProperty("link") @NotNull UUID linkedEntityId,
        /** 仅为捕获非法字段使用：JSON 中若显式传入会被 service 抛错；正常请求不传。 */
        Boolean online
) {
}

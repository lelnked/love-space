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
 * Banner 更新请求体（全字段覆盖，但不允许携带 {@code online}）。
 * <p>上下架操作走独立接口 {@code POST /api/admin/banners/{id}/online}，
 * 若请求体中出现 {@code online} 字段，service 层抛 400（FR-009）。
 */
public record BannerUpdateRequest(
        @NotBlank(message = "banner 名称不能为空") @Size(max = 128, message = "banner 名称长度不能超过 128 个字符") String name,
        @NotBlank(message = "展示位编码不能为空") @Size(max = 64, message = "展示位编码长度不能超过 64 个字符") String positionCode,
        @NotNull(message = "banner 类型不能为空") BannerType type,
        @NotEmpty(message = "至少上传一张图片") List<@NotBlank(message = "图片不能为空") @Pattern(regexp = "^(images|bound)/[\\w-]+\\.(png|jpg|webp|gif)$",
                message = "imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）") String> imageUrls,
        @JsonProperty("link") @NotNull(message = "请选择关联城市") UUID linkedEntityId,
        @NotNull(message = "排序值不能为空") @PositiveOrZero(message = "排序值不能为负") Integer sortOrder,
        /** 仅为捕获非法字段使用：JSON 中若显式传入会被 service 抛错；正常请求不传。 */
        Boolean online
) {
}

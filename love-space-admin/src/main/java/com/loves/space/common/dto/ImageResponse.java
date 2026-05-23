package com.loves.space.common.dto;

import jakarta.validation.constraints.NotNull;

public record ImageResponse(@NotNull String id, @NotNull String url) {
}

package com.loves.space.modules.ai.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 修改一项额度（0 由 service 拒绝）。
 * 用 BigDecimal 而非 Integer：Jackson 会把 1.5 静默截断成 1，这里要让小数和超出 int 的值都返回 400。
 *
 * @param value 上限，必填，-1 表示不限制，其余须为正整数
 */
public record AiLimitUpdateRequest(
        @NotNull(message = "取值须为 -1 或正整数")
        @Digits(integer = 10, fraction = 0, message = "取值须为 -1 或正整数")
        @Min(value = -1, message = "取值须为 -1 或正整数")
        @Max(value = Integer.MAX_VALUE, message = "取值须为 -1 或正整数")
        BigDecimal value
) {
}

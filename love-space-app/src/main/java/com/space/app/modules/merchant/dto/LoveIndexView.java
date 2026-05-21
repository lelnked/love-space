package com.space.app.modules.merchant.dto;

/**
 * 爱女指数视图。
 *
 * @param total 四维原始分之和（满分 100）
 * @param level 等级：{@code clamp(ceil(total/10), 1, 10)}
 */
public record LoveIndexView(int total, int level) {
}

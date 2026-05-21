package com.space.app.modules.merchant.dto;

/**
 * 商户评价视图。
 *
 * @param nickname 昵称
 * @param title    标题
 * @param content  内容
 */
public record ReviewItemResponse(String nickname, String title, String content) {
}

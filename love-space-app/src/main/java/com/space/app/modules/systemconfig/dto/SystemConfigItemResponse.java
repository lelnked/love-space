package com.space.app.modules.systemconfig.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.space.app.modules.systemconfig.entity.SystemConfig;

/**
 * App 端系统配置出参。
 *
 * @param key   配置 key
 * @param value 配置值，JSON 原值（对象、数组、字符串、数字或布尔）
 */
public record SystemConfigItemResponse(
        String key,
        @JsonRawValue String value
) {

    public static SystemConfigItemResponse from(SystemConfig c) {
        return new SystemConfigItemResponse(c.getKey(), c.getValue());
    }
}

package com.loves.space.infrastructure.storage;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * test profile 专用的 {@link ObjectKeyValidator}：联调/测试实例没有真实 OSS，
 * 只做正则校验并直接返回 bound key，跳过 headObject / copyObject。
 * 生产（非 test profile）仍由 {@link AliyunOssObjectKeyValidator} 生效。
 */
@Component
@Profile("test")
public class StubObjectKeyValidator implements ObjectKeyValidator {

    private static final Pattern OBJECT_KEY_PATTERN =
            Pattern.compile("^(images|bound)/([\\w-]+)\\.(png|jpg|webp)$");

    @Override
    public String validateAndBind(String rawObjectKey) {
        if (rawObjectKey == null || rawObjectKey.isBlank()) {
            throw new IllegalArgumentException(AliyunOssObjectKeyValidator.UNAVAILABLE_MESSAGE);
        }
        Matcher matcher = OBJECT_KEY_PATTERN.matcher(rawObjectKey);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(AliyunOssObjectKeyValidator.UNAVAILABLE_MESSAGE);
        }
        return "bound/" + matcher.group(2) + "." + matcher.group(3);
    }
}

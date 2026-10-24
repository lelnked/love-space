package com.loves.ai.prompt;

/** 模板校验/渲染失败。继承 IllegalArgumentException，两后端现有 400 映射直接生效。 */
public class PromptValidationException extends IllegalArgumentException {

    public PromptValidationException(String message) {
        super(message);
    }
}

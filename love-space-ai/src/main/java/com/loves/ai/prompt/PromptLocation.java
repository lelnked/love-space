package com.loves.ai.prompt;

/** 变量引用所在位置。 */
public enum PromptLocation {
    /** 系统提示词 */
    SYSTEM_PROMPT("系统提示词"),
    /** 输入模板 */
    USER_PROMPT_TEMPLATE("输入模板");

    private final String label;

    PromptLocation(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}

package com.loves.ai.prompt;

import java.util.ArrayList;
import java.util.List;

/**
 * 模板校验结果。
 *
 * @param declarationErrors 变量声明错误（名称非法/重复），中文描述
 * @param undeclared        未声明变量
 */
public record PromptValidationResult(List<String> declarationErrors, List<UndeclaredVariable> undeclared) {

    public boolean valid() {
        return declarationErrors.isEmpty() && undeclared.isEmpty();
    }

    /** 拼成面向运营的中文提示。 */
    public String message() {
        List<String> parts = new ArrayList<>(declarationErrors);
        for (UndeclaredVariable u : undeclared) {
            parts.add(u.location().label() + "引用了未声明变量 {{" + u.name() + "}}");
        }
        return String.join("；", parts);
    }
}

package com.loves.ai.prompt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.loves.ai.generation.ChatMessage;

class PromptRendererTest {

    private final PromptRenderer renderer = new PromptRenderer();

    // @scenario: ai/提示词变量校验与渲染#未声明变量被指出位置
    @Test
    void undeclaredVariablesReportedWithLocation() {
        PromptValidationResult result = renderer.validate("语气{{tone}}", "{{age}}{{mood}}",
                List.of(new PromptVariable("age", true, null)));

        assertThat(result.undeclared()).containsExactly(
                new UndeclaredVariable("tone", PromptLocation.SYSTEM_PROMPT),
                new UndeclaredVariable("mood", PromptLocation.USER_PROMPT_TEMPLATE));
        assertThat(result.message()).contains("输入模板引用了未声明变量 {{mood}}").contains("系统提示词");
    }

    // @scenario: ai/提示词变量校验与渲染#必填变量缺失渲染失败
    @Test
    void missingRequiredVariableFails() {
        assertThatThrownBy(() -> renderer.render("s", "{{age}}",
                List.of(new PromptVariable("age", true, null)), Map.of()))
                .isInstanceOf(PromptValidationException.class)
                .hasMessageContaining("age");
    }

    // @scenario: ai/提示词变量校验与渲染#选填变量缺失替换为空串
    @Test
    void missingOptionalVariableBecomesEmpty() {
        List<ChatMessage> messages = renderer.render("sys", "说明：{{user_input}}",
                List.of(new PromptVariable("user_input", false, null)), null);

        assertThat(messages).containsExactly(new ChatMessage("system", "sys"), new ChatMessage("user", "说明："));
    }

    // @scenario: ai/提示词变量校验与渲染#非字符串值按 JSON 文本代入
    @Test
    void nonStringValueRenderedAsJson() {
        List<ChatMessage> messages = renderer.render("", "{{tags}}|{{ age }}",
                List.of(new PromptVariable("tags", false, null), new PromptVariable("age", false, null)),
                Map.of("tags", List.of("a", "b"), "age", 28));

        assertThat(messages.get(1).content()).isEqualTo("[\"a\",\"b\"]|28");
    }

    @Test
    void replacementTextIsLiteral() {
        List<ChatMessage> messages = renderer.render("", "{{x}}",
                List.of(new PromptVariable("x", true, null)), Map.of("x", "$1\\n"));

        assertThat(messages.get(1).content()).isEqualTo("$1\\n");
    }

    // @scenario: ai/提示词变量校验与渲染#变量名非法或重复为声明错误
    @Test
    void invalidOrDuplicateNamesAreDeclarationErrors() {
        PromptValidationResult illegal = renderer.validate("", "", List.of(new PromptVariable("1abc", false, null)));
        PromptValidationResult duplicate = renderer.validate("", "",
                List.of(new PromptVariable("age", false, null), new PromptVariable("age", true, null)));

        assertThat(illegal.declarationErrors()).containsExactly("变量名不合法：1abc");
        assertThat(duplicate.declarationErrors()).containsExactly("变量名重复：age");
        assertThat(illegal.valid()).isFalse();
    }
}

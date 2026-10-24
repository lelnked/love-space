package com.loves.ai.prompt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.loves.ai.generation.ChatMessage;

import tools.jackson.databind.ObjectMapper;

/** 提示词变量校验与渲染。变量引用写法 {@code {{name}}}。 */
public class PromptRenderer {

    public static final Pattern VARIABLE_NAME = Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*$");
    private static final Pattern REFERENCE = Pattern.compile("\\{\\{\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*}}");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 校验变量声明与引用。
     *
     * @param systemPrompt       系统提示词
     * @param userPromptTemplate 输入模板
     * @param variables          变量声明，可空
     */
    public PromptValidationResult validate(String systemPrompt, String userPromptTemplate, List<PromptVariable> variables) {
        List<String> errors = new ArrayList<>();
        Set<String> declared = new HashSet<>();
        for (PromptVariable v : variables == null ? List.<PromptVariable>of() : variables) {
            String name = v.name();
            if (name == null || !VARIABLE_NAME.matcher(name).matches()) {
                errors.add("变量名不合法：" + name);
            } else if (!declared.add(name)) {
                errors.add("变量名重复：" + name);
            }
        }
        List<UndeclaredVariable> undeclared = new ArrayList<>();
        collectUndeclared(systemPrompt, PromptLocation.SYSTEM_PROMPT, declared, undeclared);
        collectUndeclared(userPromptTemplate, PromptLocation.USER_PROMPT_TEMPLATE, declared, undeclared);
        return new PromptValidationResult(errors, undeclared);
    }

    /** 校验，不通过抛 {@link PromptValidationException}。 */
    public void requireValid(String systemPrompt, String userPromptTemplate, List<PromptVariable> variables) {
        PromptValidationResult result = validate(systemPrompt, userPromptTemplate, variables);
        if (!result.valid()) {
            throw new PromptValidationException(result.message());
        }
    }

    /**
     * 渲染成 [system, user] 两条消息。必填变量缺失（未传或 null）抛异常；选填缺失替换为空串；
     * 非字符串值按 JSON 文本代入。
     *
     * @param inputs 变量取值，可空
     */
    public List<ChatMessage> render(String systemPrompt, String userPromptTemplate,
                                    List<PromptVariable> variables, Map<String, Object> inputs) {
        Map<String, Object> values = inputs == null ? Map.of() : inputs;
        List<String> missing = new ArrayList<>();
        for (PromptVariable v : variables == null ? List.<PromptVariable>of() : variables) {
            if (v.required() && values.get(v.name()) == null) {
                missing.add(v.name());
            }
        }
        if (!missing.isEmpty()) {
            throw new PromptValidationException("缺少必填变量：" + String.join("、", missing));
        }
        return List.of(
                new ChatMessage("system", substitute(systemPrompt, values)),
                new ChatMessage("user", substitute(userPromptTemplate, values)));
    }

    /** 变量值转成代入文本：字符串原样，其余按 JSON 文本，null 为空串。 */
    public static String toText(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof String s) {
            return s;
        }
        return MAPPER.writeValueAsString(value);
    }

    private static String substitute(String template, Map<String, Object> values) {
        if (template == null) {
            return "";
        }
        Matcher m = REFERENCE.matcher(template);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            m.appendReplacement(sb, Matcher.quoteReplacement(toText(values.get(m.group(1)))));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static void collectUndeclared(String text, PromptLocation location, Set<String> declared,
                                          List<UndeclaredVariable> out) {
        if (text == null) {
            return;
        }
        Set<String> seen = new LinkedHashSet<>();
        Matcher m = REFERENCE.matcher(text);
        while (m.find()) {
            if (!declared.contains(m.group(1)) && seen.add(m.group(1))) {
                out.add(new UndeclaredVariable(m.group(1), location));
            }
        }
    }
}

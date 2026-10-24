package com.loves.ai.generation;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.loves.ai.ark.ArkClient;
import com.loves.ai.ark.ArkResponse;
import com.loves.ai.config.AiProperties;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * 生成调用：主模型首次 + N 次重试 → 仍失败且有备用 → 备用首次 + N 次重试；任一次 SUCCESS 即停。
 * 成功 = 方舟正常返回 + 内容非空 + 剥离代码块后能解析为 JSON 对象或数组。
 */
public class AiGenerator {

    private static final ObjectMapper STRICT = JsonMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS).build();
    private static final Pattern FENCE = Pattern.compile("^\\s*```[A-Za-z0-9_-]*\\s*\\n?(.*?)\\n?\\s*```\\s*$", Pattern.DOTALL);

    private final AiProperties properties;
    private final ArkClient arkClient;

    public AiGenerator(AiProperties properties, ArkClient arkClient) {
        this.properties = properties;
        this.arkClient = arkClient;
    }

    /**
     * 生成。
     *
     * @param messages     渲染后的消息
     * @param primaryModel 主模型
     * @param backupModel  备用模型，可空
     */
    public AiResult generate(List<ChatMessage> messages, String primaryModel, String backupModel) {
        Instant start = Instant.now();
        List<AiAttempt> attempts = new ArrayList<>();
        boolean done = runModel(messages, primaryModel, TriggerReason.INITIAL, attempts);
        if (!done && backupModel != null && !backupModel.isBlank()) {
            runModel(messages, backupModel, TriggerReason.FALLBACK, attempts);
        }
        AiAttempt last = attempts.getLast();
        boolean success = last.status() == AiStatus.SUCCESS;
        return new AiResult(
                last.status(),
                success ? last.parsedContent() : null,
                last.content(),
                last.model(),
                sum(attempts, true),
                sum(attempts, false),
                Duration.between(start, Instant.now()).toMillis(),
                success ? null : last.status().name(),
                success ? null : failureMessage(last),
                params(primaryModel, backupModel),
                List.copyOf(attempts));
    }

    /** 连接测试：发一条极短请求，方舟正常返回即可达，不校验 JSON。 */
    public PingResult ping(String model) {
        Instant start = Instant.now();
        ArkResponse response = arkClient.chat(model, List.of(new ChatMessage("user", "ping")), 1);
        long latency = Duration.between(start, Instant.now()).toMillis();
        return response.failure() == null
                ? new PingResult(true, latency, "可达")
                : new PingResult(false, latency, response.errorMessage());
    }

    /** 参数快照（不含密钥）。 */
    public Map<String, Object> params(String primaryModel, String backupModel) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("primaryModel", primaryModel);
        params.put("backupModel", backupModel == null || backupModel.isBlank() ? null : backupModel);
        params.put("temperature", properties.temperature());
        params.put("topP", properties.topP());
        params.put("maxTokens", properties.maxTokens());
        params.put("timeoutSeconds", properties.timeoutSeconds());
        params.put("maxRetries", properties.maxRetries());
        return params;
    }

    /** 一个模型：首次 + maxRetries 次重试。返回是否已成功。 */
    // ponytail: 立即重试无退避；RATE_LIMITED 多发时再加固定退避
    private boolean runModel(List<ChatMessage> messages, String model, TriggerReason first, List<AiAttempt> attempts) {
        for (int i = 0; i <= properties.maxRetries(); i++) {
            AiAttempt attempt = call(messages, model, i == 0 ? first : TriggerReason.RETRY, attempts.size() + 1);
            attempts.add(attempt);
            if (attempt.status() == AiStatus.SUCCESS) {
                return true;
            }
        }
        return false;
    }

    private AiAttempt call(List<ChatMessage> messages, String model, TriggerReason reason, int seq) {
        Instant startedAt = Instant.now();
        ArkResponse r = arkClient.chat(model, messages, null);
        Instant finishedAt = Instant.now();
        AiStatus status = r.failure();
        String errorCode = r.errorCode();
        String errorMessage = r.errorMessage();
        JsonNode parsed = null;
        String validationError = null;
        if (status == null) {
            if ("content_filter".equals(r.finishReason())) {
                status = AiStatus.REFUSED;
                errorMessage = "模型拒答";
            } else if (r.content() == null || r.content().isBlank()) {
                status = AiStatus.EMPTY;
                errorMessage = "模型返回内容为空";
            } else {
                try {
                    parsed = parseJson(r.content());
                    status = AiStatus.SUCCESS;
                } catch (Exception e) {
                    status = AiStatus.FORMAT_ERROR;
                    validationError = e.getMessage();
                    errorMessage = "模型输出不是合法 JSON";
                }
            }
        }
        JsonNode usage = r.usage();
        return new AiAttempt(seq, reason, model, status, errorCode, errorMessage, r.providerRequestId(),
                r.rawResponse(), r.content(), parsed, validationError,
                intOrNull(usage, "prompt_tokens"), intOrNull(usage, "completion_tokens"), usage,
                startedAt, finishedAt, Duration.between(startedAt, finishedAt).toMillis());
    }

    /** 剥离整段代码块包裹后解析；必须是对象或数组。 */
    static JsonNode parseJson(String content) {
        Matcher m = FENCE.matcher(content);
        String json = m.matches() ? m.group(1) : content;
        JsonNode node = STRICT.readTree(json);
        if (node == null || !(node.isObject() || node.isArray())) {
            throw new IllegalArgumentException("顶层不是 JSON 对象或数组");
        }
        return node;
    }

    private static String failureMessage(AiAttempt a) {
        return a.errorCode() == null ? a.errorMessage() : a.errorMessage() + "（" + a.errorCode() + "）";
    }

    private static Integer intOrNull(JsonNode usage, String field) {
        return usage != null && usage.hasNonNull(field) ? usage.get(field).asInt() : null;
    }

    private static Integer sum(List<AiAttempt> attempts, boolean prompt) {
        Integer total = null;
        for (AiAttempt a : attempts) {
            Integer v = prompt ? a.promptTokens() : a.completionTokens();
            if (v != null) {
                total = (total == null ? 0 : total) + v;
            }
        }
        return total;
    }
}

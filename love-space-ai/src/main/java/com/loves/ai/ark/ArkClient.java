package com.loves.ai.ark;

import java.net.SocketTimeoutException;
import java.net.http.HttpClient;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.loves.ai.config.AiProperties;
import com.loves.ai.generation.AiStatus;
import com.loves.ai.generation.ChatMessage;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/** 火山方舟 Chat Completions（OpenAI 兼容）HTTP 适配。只做一次调用，不判定内容。 */
public class ArkClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final AiProperties properties;
    private final RestClient restClient;

    public ArkClient(AiProperties properties) {
        this.properties = properties;
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(properties.timeoutSeconds()));
        this.restClient = RestClient.builder().baseUrl(properties.baseUrl()).requestFactory(factory).build();
    }

    /**
     * 调用一次 chat/completions。
     *
     * @param maxTokens 覆盖 AI_MAX_TOKENS，可空（为空时用配置值）
     * @param thinking  深度思考开关，可空（为空时不传，用模型默认）
     */
    public ArkResponse chat(String model, List<ChatMessage> messages, Integer maxTokens, Boolean thinking) {
        if (!properties.hasApiKey()) {
            return new ArkResponse(AiStatus.UPSTREAM_ERROR, "NO_API_KEY", "未配置 ARK_API_KEY",
                    null, null, null, null, null);
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("messages", messages);
        putIfNotNull(body, "temperature", properties.temperature());
        putIfNotNull(body, "top_p", properties.topP());
        putIfNotNull(body, "max_tokens", maxTokens != null ? maxTokens : properties.maxTokens());
        if (thinking != null) {
            body.put("thinking", Map.of("type", thinking ? "enabled" : "disabled"));
        }
        try {
            return restClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + properties.apiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(MAPPER.writeValueAsString(body))
                    .exchange((request, response) -> {
                        int code = response.getStatusCode().value();
                        String raw = response.bodyTo(String.class);
                        if (code == 429) {
                            return new ArkResponse(AiStatus.RATE_LIMITED, "429", "方舟限流", raw, null, null, null, null);
                        }
                        if (code < 200 || code >= 300) {
                            return new ArkResponse(AiStatus.UPSTREAM_ERROR, String.valueOf(code),
                                    "方舟返回 HTTP " + code, raw, null, null, null, null);
                        }
                        return parse(raw);
                    });
        } catch (Exception e) {
            if (isTimeout(e)) {
                return new ArkResponse(AiStatus.TIMEOUT, "TIMEOUT",
                        "调用超时（" + properties.timeoutSeconds() + " 秒）", null, null, null, null, null);
            }
            return new ArkResponse(AiStatus.UPSTREAM_ERROR, e.getClass().getSimpleName(),
                    String.valueOf(e.getMessage()), null, null, null, null, null);
        }
    }

    private static ArkResponse parse(String raw) {
        JsonNode root;
        try {
            root = MAPPER.readTree(raw == null ? "" : raw);
        } catch (Exception e) {
            return new ArkResponse(AiStatus.UPSTREAM_ERROR, "BAD_RESPONSE", "方舟响应不是 JSON", raw, null, null, null, null);
        }
        JsonNode choice = root.path("choices").path(0);
        return new ArkResponse(null, null, null, raw,
                textOrNull(choice.path("message").path("content")),
                textOrNull(choice.path("finish_reason")),
                textOrNull(root.path("id")),
                root.has("usage") ? root.get("usage") : null);
    }

    private static String textOrNull(JsonNode node) {
        return node == null || node.isMissingNode() || node.isNull() ? null : node.asString();
    }

    private static boolean isTimeout(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof HttpTimeoutException || t instanceof SocketTimeoutException) {
                return true;
            }
        }
        return false;
    }

    private static void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}

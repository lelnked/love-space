package com.loves.ai.ark;

import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpTimeoutException;
import java.nio.channels.ClosedChannelException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLException;

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
        long startNanos = System.nanoTime();
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
                        if (code == 408 || code == 504) {
                            return new ArkResponse(AiStatus.TIMEOUT, String.valueOf(code),
                                    "调用超时（方舟返回 HTTP " + code + "）", raw, null, null, null, null);
                        }
                        if (code < 200 || code >= 300) {
                            return new ArkResponse(AiStatus.UPSTREAM_ERROR, String.valueOf(code),
                                    "方舟返回 HTTP " + code, raw, null, null, null, null);
                        }
                        return parse(raw);
                    });
        } catch (Exception e) {
            long elapsedMs = Duration.ofNanos(System.nanoTime() - startNanos).toMillis();
            if (isTimeout(e)) {
                return new ArkResponse(AiStatus.TIMEOUT, "TIMEOUT",
                        "调用超时（" + properties.timeoutSeconds() + " 秒）", null, null, null, null, null);
            }
            if (isReadDisconnectAfterWait(e, elapsedMs, properties.timeoutSeconds())) {
                long secs = Math.max(1L, Math.round(elapsedMs / 1000.0));
                return new ArkResponse(AiStatus.TIMEOUT, "TIMEOUT",
                        "调用超时，等待模型响应期间连接被断开（耗时 " + secs + " 秒）", null, null, null, null, null);
            }
            Throwable root = rootCause(e);
            return new ArkResponse(AiStatus.UPSTREAM_ERROR, root.getClass().getSimpleName(),
                    formatUpstreamErrorMessage(root), null, null, null, null, null);
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
            if (t instanceof HttpTimeoutException || t instanceof SocketTimeoutException || t instanceof TimeoutException) {
                return true;
            }
            String msg = t.getMessage();
            if (msg != null) {
                String lower = msg.toLowerCase(Locale.ROOT);
                if (lower.contains("timed out") || lower.contains("timeout")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 长耗时非流式调用期间，远端网关或中间网络因空闲超时主动掐断连接（表现为 EOF / no bytes / reset），
     * 并非即时连接失败，应归类为 TIMEOUT。
     */
    private static boolean isReadDisconnectAfterWait(Throwable e, long elapsedMs, int timeoutSeconds) {
        long thresholdMs = Math.min(5_000L, Math.max(500L, timeoutSeconds * 500L));
        if (elapsedMs < thresholdMs) {
            return false;
        }
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof ConnectException || t instanceof UnknownHostException || t instanceof SSLException) {
                return false;
            }
        }
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof EOFException || t instanceof ClosedChannelException) {
                return true;
            }
            if (t instanceof SocketException || t instanceof IOException) {
                String msg = t.getMessage();
                if (msg != null) {
                    String lower = msg.toLowerCase(Locale.ROOT);
                    if (lower.contains("received no bytes")
                            || lower.contains("eof")
                            || lower.contains("connection reset")
                            || lower.contains("broken pipe")
                            || lower.contains("closed")
                            || lower.contains("goaway")
                            || lower.contains("rst_stream")
                            || lower.contains("cancelled")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Throwable rootCause(Throwable e) {
        Throwable curr = e;
        while (curr.getCause() != null && curr.getCause() != curr) {
            curr = curr.getCause();
        }
        return curr;
    }

    private static String formatUpstreamErrorMessage(Throwable root) {
        String detail = root.getMessage();
        if (root instanceof ConnectException) {
            return "无法连接模型服务" + (detail != null ? "：" + detail : "");
        }
        if (root instanceof UnknownHostException) {
            return "模型服务域名解析失败" + (detail != null ? "：" + detail : "");
        }
        if (root instanceof EOFException || root instanceof ClosedChannelException || root instanceof SocketException) {
            return "模型服务连接中断" + (detail != null ? "：" + detail : "");
        }
        return detail != null ? detail : root.getClass().getSimpleName();
    }

    private static void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}

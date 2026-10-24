package com.loves.ai.generation;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import tools.jackson.databind.ObjectMapper;

/** 测试用假方舟：按模型名排队预设响应，队列取完后重复最后一个。 */
class FakeArk implements AutoCloseable {

    record Reply(int status, String body, long delayMs) {
        static Reply content(String content) {
            return finish(content, "stop");
        }

        static Reply finish(String content, String finishReason) {
            String json = new ObjectMapper().writeValueAsString(Map.of(
                    "id", "req-1",
                    "choices", java.util.List.of(Map.of(
                            "message", Map.of("role", "assistant", "content", content),
                            "finish_reason", finishReason)),
                    "usage", Map.of("prompt_tokens", 10, "completion_tokens", 5)));
            return new Reply(200, json, 0);
        }

        static Reply http(int status) {
            return new Reply(status, "{\"error\":{\"code\":\"x\"}}", 0);
        }

        static Reply hang(long delayMs) {
            return new Reply(200, "{}", delayMs);
        }
    }

    private final HttpServer server;
    private final Map<String, Deque<Reply>> replies = new HashMap<>();
    final AtomicInteger calls = new AtomicInteger();
    volatile String lastAuthorization;
    volatile String lastBody;

    FakeArk() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/chat/completions", this::handle);
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();
    }

    String baseUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

    FakeArk on(String model, Reply... rs) {
        replies.computeIfAbsent(model, k -> new ArrayDeque<>()).addAll(java.util.List.of(rs));
        return this;
    }

    private void handle(HttpExchange exchange) throws IOException {
        calls.incrementAndGet();
        lastAuthorization = exchange.getRequestHeaders().getFirst("Authorization");
        lastBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String model = new ObjectMapper().readTree(lastBody).path("model").asString();
        Deque<Reply> queue = replies.get(model);
        Reply reply = queue == null ? Reply.http(404) : (queue.size() > 1 ? queue.poll() : queue.peek());
        try {
            if (reply.delayMs() > 0) {
                Thread.sleep(reply.delayMs());
            }
            byte[] bytes = reply.body().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(reply.status(), bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (InterruptedException | IOException ignored) {
            // 客户端超时断开
        }
    }

    @Override
    public void close() {
        server.stop(0);
    }
}

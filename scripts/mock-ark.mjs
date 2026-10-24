#!/usr/bin/env node
// 假火山方舟（OpenAI 兼容 POST /chat/completions），供 admin / app 的 IT 与 WEB 测试复现 AI 行为。
// 行为由请求里的 model 决定，零依赖（仅 node 内置 http）。不进生产。
//
// 启动：node scripts/mock-ark.mjs [port=18080]
// 后端配套环境变量：
//   ARK_BASE_URL=http://localhost:18080 ARK_API_KEY=test AI_TIMEOUT_SECONDS=3 \
//   AI_MODELS=mock-ok,mock-fenced,mock-format-error,mock-empty,mock-refused,mock-429,mock-500,mock-timeout,mock-flaky,mock-slow
//
// | model             | 行为                                                   |
// |-------------------|--------------------------------------------------------|
// | mock-ok           | 200，content = {"echo": <user 消息>}                    |
// | mock-fenced       | 200，content 用 ```json 代码块包裹                      |
// | mock-format-error | 200，content = not json                                |
// | mock-empty        | 200，content = ""                                      |
// | mock-refused      | 200，finish_reason = content_filter                    |
// | mock-429/mock-500 | 对应 HTTP 状态                                         |
// | mock-timeout      | 挂起 MOCK_HANG_SECONDS（默认 8）秒后才返回              |
// | mock-flaky        | 同一 messages 第 1 次非 JSON、第 2 次成功（内存计数）   |
// | mock-slow         | 2 秒后成功（制造 PROCESSING 窗口；需小于 AI_TIMEOUT_SECONDS）|
import http from "node:http";

const port = Number(process.argv[2] ?? 18080);
const hangSeconds = Number(process.env.MOCK_HANG_SECONDS ?? 8);
const flakyCounts = new Map();

function completion(content, finishReason = "stop") {
  return {
    id: `mock-${Date.now()}`,
    choices: [{ index: 0, message: { role: "assistant", content }, finish_reason: finishReason }],
    usage: { prompt_tokens: 12, completion_tokens: 6, total_tokens: 18 },
  };
}

const server = http.createServer((req, res) => {
  let raw = "";
  req.on("data", (c) => (raw += c));
  req.on("end", () => {
    const send = (status, body, delayMs = 0) =>
      setTimeout(() => {
        if (res.destroyed) return;
        res.writeHead(status, { "Content-Type": "application/json" });
        res.end(JSON.stringify(body));
      }, delayMs);

    if (req.method !== "POST" || !req.url.endsWith("/chat/completions")) return send(404, { error: "not found" });
    let body;
    try {
      body = JSON.parse(raw);
    } catch {
      return send(400, { error: "bad json" });
    }
    const user = (body.messages ?? []).find((m) => m.role === "user")?.content ?? "";
    const ok = JSON.stringify({ echo: user });
    switch (body.model) {
      case "mock-ok": return send(200, completion(ok));
      case "mock-fenced": return send(200, completion("```json\n" + ok + "\n```"));
      case "mock-format-error": return send(200, completion("not json"));
      case "mock-empty": return send(200, completion(""));
      case "mock-refused": return send(200, completion("", "content_filter"));
      case "mock-429": return send(429, { error: { code: "RateLimitExceeded" } });
      case "mock-500": return send(500, { error: { code: "InternalServiceError" } });
      case "mock-timeout": return send(200, completion(ok), hangSeconds * 1000);
      case "mock-slow": return send(200, completion(ok), 2000);
      case "mock-flaky": {
        const key = JSON.stringify(body.messages);
        const n = (flakyCounts.get(key) ?? 0) + 1;
        flakyCounts.set(key, n);
        return send(200, completion(n % 2 === 1 ? "not json" : ok));
      }
      default: return send(404, { error: { code: "ModelNotFound", message: `unknown model ${body.model}` } });
    }
  });
});

server.listen(port, () => console.log(`mock-ark listening on http://localhost:${port}`));

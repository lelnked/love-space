package com.loves.ai.generation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.loves.ai.ark.ArkClient;
import com.loves.ai.config.AiProperties;
import com.loves.ai.generation.FakeArk.Reply;

class AiGeneratorTest {

    private static final List<ChatMessage> MESSAGES = List.of(new ChatMessage("system", "s"), new ChatMessage("user", "u"));

    private FakeArk ark;

    @BeforeEach
    void setUp() throws Exception {
        ark = new FakeArk();
    }

    @AfterEach
    void tearDown() {
        ark.close();
    }

    private AiGenerator generator(int maxRetries) {
        return generator(maxRetries, "test-key");
    }

    private AiGenerator generator(int maxRetries, String apiKey) {
        AiProperties properties = new AiProperties(apiKey, ark.baseUrl(), List.of("p", "b"), null, null, null, 1, maxRetries);
        return new AiGenerator(properties, new ArkClient(properties));
    }

    // @scenario: ai/生成重试、切备用与结果判定#主模型首次成功
    @Test
    void primarySucceedsFirstTime() {
        ark.on("p", Reply.content("{\"a\":1}"));

        AiResult result = generator(1).generate(MESSAGES, "p", "b");

        assertThat(result.status()).isEqualTo(AiStatus.SUCCESS);
        assertThat(result.content().get("a").asInt()).isEqualTo(1);
        assertThat(result.finalModel()).isEqualTo("p");
        assertThat(result.attempts()).extracting(AiAttempt::triggerReason).containsExactly(TriggerReason.INITIAL);
        assertThat(result.errorCode()).isNull();
        assertThat(ark.lastAuthorization).isEqualTo("Bearer test-key");
        assertThat(ark.lastBody).doesNotContain("temperature").doesNotContain("max_tokens");
    }

    // @scenario: ai/生成重试、切备用与结果判定#主模型失败后重试成功
    @Test
    void retrySucceeds() {
        ark.on("p", Reply.content("not json"), Reply.content("{\"a\":2}"));

        AiResult result = generator(1).generate(MESSAGES, "p", null);

        assertThat(result.status()).isEqualTo(AiStatus.SUCCESS);
        assertThat(result.attempts()).extracting(AiAttempt::triggerReason, AiAttempt::status).containsExactly(
                org.assertj.core.groups.Tuple.tuple(TriggerReason.INITIAL, AiStatus.FORMAT_ERROR),
                org.assertj.core.groups.Tuple.tuple(TriggerReason.RETRY, AiStatus.SUCCESS));
        assertThat(result.promptTokens()).isEqualTo(20);
        assertThat(result.completionTokens()).isEqualTo(10);
    }

    // @scenario: ai/生成重试、切备用与结果判定#主模型耗尽后切备用
    @Test
    void fallsBackAfterPrimaryExhausted() {
        ark.on("p", Reply.hang(2500)).on("b", Reply.content("[1]"));

        AiResult result = generator(1).generate(MESSAGES, "p", "b");

        assertThat(result.status()).isEqualTo(AiStatus.SUCCESS);
        assertThat(result.finalModel()).isEqualTo("b");
        assertThat(result.attempts()).extracting(AiAttempt::triggerReason)
                .containsExactly(TriggerReason.INITIAL, TriggerReason.RETRY, TriggerReason.FALLBACK);
        assertThat(result.attempts().get(0).status()).isEqualTo(AiStatus.TIMEOUT);
        assertThat(result.attempts()).extracting(AiAttempt::seq).containsExactly(1, 2, 3);
    }

    // @scenario: ai/生成重试、切备用与结果判定#无备用模型时全部失败
    @Test
    void allFailWithoutBackup() {
        ark.on("p", Reply.http(429));

        AiResult result = generator(1).generate(MESSAGES, "p", null);

        assertThat(result.status()).isEqualTo(AiStatus.RATE_LIMITED);
        assertThat(result.attempts()).hasSize(2);
        assertThat(result.content()).isNull();
        assertThat(result.errorCode()).isEqualTo("RATE_LIMITED");
        assertThat(result.errorMessage()).contains("429");
    }

    // @scenario: ai/生成重试、切备用与结果判定#各失败状态判定
    @Test
    void failureStatuses() {
        ark.on("refused", Reply.finish("{}", "content_filter"))
                .on("empty", Reply.content("  "))
                .on("text", Reply.content("hello"))
                .on("scalar", Reply.content("\"hello\""))
                .on("trailing", Reply.content("{\"a\":1} tail"))
                .on("err", Reply.http(500));
        AiGenerator generator = generator(0);

        assertThat(generator.generate(MESSAGES, "refused", null).status()).isEqualTo(AiStatus.REFUSED);
        assertThat(generator.generate(MESSAGES, "empty", null).status()).isEqualTo(AiStatus.EMPTY);
        assertThat(generator.generate(MESSAGES, "text", null).status()).isEqualTo(AiStatus.FORMAT_ERROR);
        assertThat(generator.generate(MESSAGES, "scalar", null).status()).isEqualTo(AiStatus.FORMAT_ERROR);
        assertThat(generator.generate(MESSAGES, "trailing", null).status()).isEqualTo(AiStatus.FORMAT_ERROR);
        assertThat(generator.generate(MESSAGES, "err", null).status()).isEqualTo(AiStatus.UPSTREAM_ERROR);
    }

    // @scenario: ai/生成重试、切备用与结果判定#代码块包裹的 JSON 视为成功
    @Test
    void fencedJsonIsSuccess() {
        String fenced = "```json\n{\"a\":1}\n```";
        ark.on("p", Reply.content(fenced));

        AiResult result = generator(0).generate(MESSAGES, "p", null);

        assertThat(result.status()).isEqualTo(AiStatus.SUCCESS);
        assertThat(result.content().toString()).isEqualTo("{\"a\":1}");
        assertThat(result.rawOutput()).isEqualTo(fenced);
    }

    @Test
    void missingApiKeyFailsWithoutCalling() {
        AiResult result = generator(1, "").generate(MESSAGES, "p", null);

        assertThat(result.status()).isEqualTo(AiStatus.UPSTREAM_ERROR);
        assertThat(result.errorMessage()).contains("ARK_API_KEY");
        assertThat(ark.calls.get()).isZero();
    }

    @Test
    void pingReportsReachability() {
        ark.on("p", Reply.content("x")).on("down", Reply.http(500));
        AiGenerator generator = generator(0);

        PingResult ok = generator.ping("p");
        PingResult fail = generator.ping("down");

        assertThat(ok.ok()).isTrue();
        assertThat(ark.lastBody).contains("\"max_tokens\":1");
        assertThat(fail.ok()).isFalse();
        assertThat(fail.message()).contains("500");
    }

    @Test
    void paramsSnapshotHasNoKey() {
        assertThat(generator(1).params("p", "").toString()).doesNotContain("test-key")
                .contains("primaryModel=p").contains("backupModel=null");
    }
}

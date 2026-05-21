package com.loves.space.infrastructure.log;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.modules.operationlog.service.OperationLogService;
import com.loves.space.security.OperatingContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 操作日志切面：环绕带 {@link OperationLog} 注解的方法，方法执行成功后调用
 * {@link OperationLogService#asyncSave} 异步落库。
 * <p>关键约束：
 * <ul>
 *   <li>未登录（无 userId）时跳过落库（{@code user_id} 列 NOT NULL）；</li>
 *   <li>payload 序列化中对键名匹配 {@code (?i)password|secret|token} 的字段做脱敏，值替换为 {@code [REDACTED]}；</li>
 *   <li>序列化或落库异常一律 WARN，不向上抛出，不影响主业务返回；</li>
 *   <li>方法抛异常时不落日志（保持原有行为）。</li>
 * </ul>
 */
@Aspect
@Component
public class OperationLogAspect {

    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

    /** 敏感字段键名匹配规则（不区分大小写）。 */
    private static final Pattern SENSITIVE_KEY_PATTERN = Pattern.compile("(?i).*(password|secret|token).*");

    /** 脱敏后用于替换敏感值的占位文本。 */
    private static final String REDACTED = "[REDACTED]";

    private final OperatingContext operatingContext;
    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;

    public OperationLogAspect(OperatingContext operatingContext,
                              OperationLogService operationLogService,
                              ObjectMapper objectMapper) {
        this.operatingContext = operatingContext;
        this.operationLogService = operationLogService;
        this.objectMapper = objectMapper;
    }

    /**
     * 切面环绕逻辑：
     * <ol>
     *   <li>先执行被代理方法获取返回值；</li>
     *   <li>方法成功后解析 module/action，提取 target 与 payload，异步落库；</li>
     *   <li>方法抛异常时直接向上传播，不落日志。</li>
     * </ol>
     */
    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint pjp, OperationLog operationLog) throws Throwable {
        Object result = pjp.proceed();
        try {
            UUID userId = operatingContext.currentUserId().orElse(null);
            if (userId == null) {
                // user_id NOT NULL：缺失上下文时不写日志（例如 /auth/login 前阶段）
                return result;
            }
            String username = operatingContext.currentUsername().orElse(null);
            String value = operationLog.value();
            int colonIndex = value.indexOf(':');
            String module = colonIndex >= 0 ? value.substring(0, colonIndex) : value;
            String action = colonIndex >= 0 ? value.substring(colonIndex + 1) : "";

            Object[] args = pjp.getArgs();
            String target = extractTarget(args);
            String payloadJson = extractPayloadJson(args);

            operationLogService.asyncSave(userId, username, module, action, target, payloadJson);
        } catch (Exception e) {
            // 任何切面侧异常都不影响主业务
            log.warn("operation log dispatch failed", e);
        }
        return result;
    }

    /** 取方法参数中第一个 {@link UUID} 类型参数的字符串形式作为 target。 */
    private String extractTarget(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object arg : args) {
            if (arg instanceof UUID id) {
                return id.toString();
            }
        }
        return null;
    }

    /** 取方法参数中第一个非 UUID/简单类型的参数作为 payload 并序列化（脱敏后）。 */
    private String extractPayloadJson(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }
            if (arg instanceof UUID) {
                continue;
            }
            if (arg instanceof CharSequence || arg instanceof Number || arg instanceof Boolean) {
                continue;
            }
            return sanitizeAndSerialize(arg);
        }
        return null;
    }

    /**
     * 将对象序列化为 JSON，遍历过程中对敏感字段值做脱敏替换。
     * <p>序列化异常返回 {@code null} 并 WARN，不影响主业务。
     */
    private String sanitizeAndSerialize(Object payload) {
        try {
            JsonNode node = objectMapper.valueToTree(payload);
            redact(node);
            return objectMapper.writeValueAsString(node);
        } catch (Exception e) {
            log.warn("operation log payload serialize failed", e);
            return null;
        }
    }

    /** 递归脱敏：对象节点匹配键替换为 {@link #REDACTED}，数组节点逐项递归。 */
    private void redact(JsonNode node) {
        if (node instanceof ObjectNode objectNode) {
            List<String> sensitiveKeys = new ArrayList<>();
            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.properties().iterator();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                if (SENSITIVE_KEY_PATTERN.matcher(key).matches()) {
                    sensitiveKeys.add(key);
                } else {
                    redact(value);
                }
            }
            for (String key : sensitiveKeys) {
                objectNode.put(key, REDACTED);
            }
        } else if (node instanceof ArrayNode arrayNode) {
            for (JsonNode child : arrayNode) {
                redact(child);
            }
        }
    }
}

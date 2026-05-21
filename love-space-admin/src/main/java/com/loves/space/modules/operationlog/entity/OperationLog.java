package com.loves.space.modules.operationlog.entity;

import com.loves.space.common.util.UuidV7Generator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 操作日志实体。
 * <p>对应表 {@code loves_operation_log}（见 db/changelog 001-init-schema 中创建表语句）。
 * <p>由 {@code OperationLogAspect} 在被 {@code @OperationLog} 注解的方法成功执行后异步写入。
 * <p>表只含 {@code created_at}，不含 {@code updated_at}，因此本实体不继承 {@code BaseAuditEntity}。
 */
@Entity
@Table(name = "loves_operation_log")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class OperationLog {

    /** 主键，UUIDv7（时间有序），在 {@link #onPrePersist()} 中生成。 */
    @Id
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    /** 操作者管理员主键，非空。 */
    @Column(name = "manager_id", nullable = false)
    private UUID managerId;

    /** 操作者用户名（冗余存储以便归档后仍可读）。 */
    @Column(name = "username", nullable = false)
    private String username;

    /** 模块标识，例如 {@code city}、{@code manager}、{@code merchant}。 */
    @Column(name = "module", nullable = false)
    private String module;

    /** 动作标识，例如 {@code create}、{@code update}、{@code delete}、{@code reset-password}。 */
    @Column(name = "action", nullable = false)
    private String action;

    /** 操作目标标识（通常为目标实体的主键字符串），可空。 */
    @Column(name = "target")
    private String target;

    /**
     * 请求载荷（已做敏感字段脱敏）的 JSON 字符串。
     * <p>底层列类型为 {@code jsonb}，由 Hibernate 通过 {@link SqlTypes#JSON} 完成 String &lt;-&gt; jsonb 转换。
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    private String payloadJson;

    /** 创建时间，由 JPA Auditing 自动填充。 */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /** 在持久化前生成 UUIDv7 主键（与 {@code BaseAuditEntity} 保持一致策略）。 */
    @PrePersist
    void onPrePersist() {
        if (id == null) {
            id = UuidV7Generator.next();
        }
    }
}

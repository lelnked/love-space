package com.space.app.modules.merchant.entity;

import com.space.app.common.enums.Period;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * 商户推荐周期实体：对应 {@code merchant_period} 表。
 * <p>复合主键 (merchant_id, period)；用于 App 端按周期筛选商户。
 */
@Entity
@Table(name = "merchant_period")
@IdClass(MerchantPeriodId.class)
@Getter
@Setter
public class MerchantPeriod {

    /** 关联商户 ID。 */
    @Id
    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    /** 推荐周期。 */
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "period", nullable = false)
    private Period period;
}

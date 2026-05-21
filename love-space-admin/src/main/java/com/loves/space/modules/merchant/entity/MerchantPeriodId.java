package com.loves.space.modules.merchant.entity;

import com.loves.space.common.enums.Period;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * {@link MerchantPeriod} 的复合主键：merchantId + period。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MerchantPeriodId implements Serializable {

    /** 商户 ID。 */
    private UUID merchantId;

    /** 推荐周期。 */
    private Period period;
}

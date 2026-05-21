package com.space.app.modules.merchant.entity;

import com.space.app.common.enums.Period;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * {@link MerchantPeriod} 复合主键（merchantId + period）。
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

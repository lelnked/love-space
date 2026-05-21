package com.loves.space.modules.merchant.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * {@link MerchantTag} 的复合主键：merchantId + tagId。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MerchantTagId implements Serializable {

    /** 商户 ID。 */
    private UUID merchantId;

    /** 标签 ID。 */
    private UUID tagId;
}

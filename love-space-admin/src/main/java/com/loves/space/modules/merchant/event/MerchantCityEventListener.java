package com.loves.space.modules.merchant.event;

import com.loves.space.modules.city.event.CityOnlineChangedEvent;
import com.loves.space.modules.merchant.repository.MerchantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 城市下线 → 级联下架该城市下全部商户。
 *
 * <p>事务阶段：{@link TransactionPhase#AFTER_COMMIT}，城市持久化已落库后再批量下架商户，
 * 避免半成功（城市未提交而商户已下架）。
 * <p>仅在城市变为下线（{@code currentOnline == false}）时触发；城市上线不会自动上架商户。
 * <p>失败策略：捕获并仅 {@code log.error}，不再抛出，以免影响已成功提交的城市变更主流程。
 */
@Component
public class MerchantCityEventListener {

    private static final Logger log = LoggerFactory.getLogger(MerchantCityEventListener.class);

    private final MerchantRepository merchantRepository;

    public MerchantCityEventListener(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    /**
     * 监听 {@link CityOnlineChangedEvent}，城市下线时批量下架该城市下全部商户。
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onCityOnlineChanged(CityOnlineChangedEvent event) {
        if (event.currentOnline()) {
            return;
        }
        try {
            int affected = merchantRepository.offlineAllByCityId(event.cityId());
            log.info("City {} went offline, offlined {} merchant(s)", event.cityId(), affected);
        } catch (Exception e) {
            log.error("Failed to offline merchants for city {}: {}",
                    event.cityId(), e.getMessage(), e);
        }
    }
}

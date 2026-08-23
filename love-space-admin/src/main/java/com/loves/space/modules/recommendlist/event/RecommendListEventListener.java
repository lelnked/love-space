package com.loves.space.modules.recommendlist.event;

import com.loves.space.modules.merchant.event.MerchantOnlineChangedEvent;
import com.loves.space.modules.recommendlist.repository.RecommendListMerchantRepository;
import com.loves.space.modules.recommendlist.repository.RecommendListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

/**
 * 推荐清单级联监听器：商户下架时，将该商户关联的 ONLINE 推荐清单批量置为 OFFLINE。
 */
@Component
public class RecommendListEventListener {

    private static final Logger log = LoggerFactory.getLogger(RecommendListEventListener.class);

    private final RecommendListRepository recommendListRepository;
    private final RecommendListMerchantRepository recommendListMerchantRepository;

    public RecommendListEventListener(RecommendListRepository recommendListRepository,
                                      RecommendListMerchantRepository recommendListMerchantRepository) {
        this.recommendListRepository = recommendListRepository;
        this.recommendListMerchantRepository = recommendListMerchantRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMerchantOnlineChanged(MerchantOnlineChangedEvent event) {
        if (event.currentOnline()) {
            return;
        }
        try {
            List<UUID> recommendListIds = recommendListMerchantRepository.findDistinctRecommendListIdByMerchantId(event.merchantId());
            int affected = 0;
            for (UUID recommendListId : recommendListIds) {
                int updated = recommendListRepository.offlineByRecommendListIdAndStatus(recommendListId, "ONLINE");
                affected += updated;
            }
            log.info("Merchant {} went offline, offlined {} recommend list(s)", event.merchantId(), affected);
        } catch (Exception e) {
            log.error("Failed to offline recommend lists for merchant {}: {}", event.merchantId(), e.getMessage(), e);
        }
    }
}

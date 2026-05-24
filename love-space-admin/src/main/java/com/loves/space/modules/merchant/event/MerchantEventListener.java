package com.loves.space.modules.merchant.event;

import com.loves.space.modules.category.event.CategoryDeletedEvent;
import com.loves.space.modules.city.event.CityDeletedEvent;
import com.loves.space.modules.city.event.CityOnlineChangedEvent;
import com.loves.space.modules.merchant.repository.MerchantRepository;
import com.loves.space.modules.merchant.repository.MerchantTagRepository;
import com.loves.space.modules.tag.event.TagDeletedEvent;
import com.loves.space.modules.tag.event.TagOnlineChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 商户级联监听器：统一处理城市、分类、标签的删除与下架对商户及其关联数据的影响。
 *
 * <p>事务阶段：{@link TransactionPhase#AFTER_COMMIT}，源实体变更已落库后再处理商户，
 * 避免半成功；传播：{@link Propagation#REQUIRES_NEW}，独立事务不影响已提交的源变更主流程。
 * <p>失败策略：捕获并仅 {@code log.error}，不再抛出。
 * <p>级联规则：
 * <ul>
 *   <li>城市下线 / 删除 → 下架该城市下全部商户（城市为商户必填项，删除时仅下架不清空）。</li>
 *   <li>分类删除 → 清空商户 categoryId 并下架该分类下全部商户。</li>
 *   <li>标签删除 / 下架 → 清除该标签的全部 loves_merchant_tag 关联数据；不影响商户上架状态。</li>
 * </ul>
 */
@Component
public class MerchantEventListener {

    private static final Logger log = LoggerFactory.getLogger(MerchantEventListener.class);

    private final MerchantRepository merchantRepository;
    private final MerchantTagRepository merchantTagRepository;

    public MerchantEventListener(MerchantRepository merchantRepository,
                                 MerchantTagRepository merchantTagRepository) {
        this.merchantRepository = merchantRepository;
        this.merchantTagRepository = merchantTagRepository;
    }

    /** 城市下线时批量下架该城市下全部商户；城市上线不会自动上架商户。 */
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

    /** 城市删除时批量下架该城市下全部商户（cityId 为必填项，不清空）。 */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onCityDeleted(CityDeletedEvent event) {
        try {
            int affected = merchantRepository.offlineAllByCityId(event.cityId());
            log.info("City {} deleted, offlined {} merchant(s)", event.cityId(), affected);
        } catch (Exception e) {
            log.error("Failed to offline merchants for deleted city {}: {}",
                    event.cityId(), e.getMessage(), e);
        }
    }

    /** 分类删除时清空该分类下全部商户的 categoryId 并下架。 */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onCategoryDeleted(CategoryDeletedEvent event) {
        try {
            int affected = merchantRepository.offlineAndDetachCategory(event.categoryId());
            log.info("Category {} deleted, detached and offlined {} merchant(s)",
                    event.categoryId(), affected);
        } catch (Exception e) {
            log.error("Failed to detach merchants for deleted category {}: {}",
                    event.categoryId(), e.getMessage(), e);
        }
    }

    /** 标签删除时清除该标签的全部商户关联数据（不影响商户上架状态）。 */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onTagDeleted(TagDeletedEvent event) {
        try {
            int affected = merchantTagRepository.deleteAllByTagId(event.tagId());
            log.info("Tag {} deleted, removed {} merchant-tag association(s)",
                    event.tagId(), affected);
        } catch (Exception e) {
            log.error("Failed to remove merchant-tag associations for deleted tag {}: {}",
                    event.tagId(), e.getMessage(), e);
        }
    }

    /** 标签下线时清除该标签的全部商户关联数据；标签上线不做处理。 */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onTagOnlineChanged(TagOnlineChangedEvent event) {
        if (event.currentOnline()) {
            return;
        }
        try {
            int affected = merchantTagRepository.deleteAllByTagId(event.tagId());
            log.info("Tag {} went offline, removed {} merchant-tag association(s)",
                    event.tagId(), affected);
        } catch (Exception e) {
            log.error("Failed to remove merchant-tag associations for offlined tag {}: {}",
                    event.tagId(), e.getMessage(), e);
        }
    }
}

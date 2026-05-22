package com.loves.space.modules.banner.event;

import com.loves.space.modules.banner.entity.Banner;
import com.loves.space.modules.banner.entity.BannerType;
import com.loves.space.modules.banner.entity.Banner_;
import com.loves.space.modules.city.event.CityOnlineChangedEvent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 城市上架状态变更 → 同步关联 CITY banner 上架状态。
 *
 * <p>事务阶段：{@link TransactionPhase#AFTER_COMMIT}，城市持久化已落库后再批量更新 banner，
 * 避免半成功（城市未提交而 banner 已变更）。
 * <p>字段引用：全部通过 {@code Banner_} 元模型（宪法 VI），禁止字符串字面量。
 * <p>失败策略：捕获并仅 {@code log.error}，不再抛出，以免影响已成功提交的城市变更主流程。
 */
@Component
public class BannerEventListener {

    private static final Logger log = LoggerFactory.getLogger(BannerEventListener.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 监听 {@link CityOnlineChangedEvent}，批量更新关联 CITY banner 的上架状态。
     *
     * <p>SQL 等价：{@code UPDATE loves_banner SET online=? WHERE linked_entity_id=? AND type='CITY'}。
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onCityOnlineChanged(CityOnlineChangedEvent event) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaUpdate<Banner> update = cb.createCriteriaUpdate(Banner.class);
            Root<Banner> root = update.from(Banner.class);
            update.set(root.get(Banner_.online), event.currentOnline());
            update.where(
                    cb.equal(root.get(Banner_.linkedEntityId), event.cityId()),
                    cb.equal(root.get(Banner_.type), BannerType.CITY)
            );
            int affected = entityManager.createQuery(update).executeUpdate();
            log.info("City {} online state changed to {}, synced {} banner(s)",
                    event.cityId(), event.currentOnline(), affected);
        } catch (Exception e) {
            log.error("Failed to sync banner online state for city {}: {}",
                    event.cityId(), e.getMessage(), e);
        }
    }
}

package com.space.app.modules.banner.service.resolver;

import com.space.app.modules.banner.entity.Banner;
import com.space.app.modules.banner.entity.BannerType;

import java.util.List;
import java.util.Map;

/**
 * 按 {@link BannerType} 装配 banner 的 {@code data} 字段的策略。
 *
 * <p>每种类型一个实现（{@code @Component}）；新增类型只需新增实现，
 * {@code BannerQueryService} 无需改动。
 *
 * <p>批量预加载在 {@link #prepare(List)} 中完成，避免逐条查询导致 N+1：
 * 框架先把同类型的 banner 聚合后整体传入，实现返回一个 {@link Prepared}，
 * 内部以闭包持有预加载结果，再对每条 banner 逐个装配。
 */
public interface BannerDataResolver {

    /** 该解析器负责的 banner 类型。 */
    BannerType type();

    /**
     * 对一批同类型 banner 做批量预加载，返回逐条装配器。
     *
     * @param banners 同类型的 banner 列表（已过滤 {@code online=true}）
     */
    Prepared prepare(List<Banner> banners);

    /** 已完成预加载、可对单条 banner 装配 {@code data} 的装配器。 */
    @FunctionalInterface
    interface Prepared {

        /**
         * 装配单条 banner 的 {@code data}。
         *
         * @return data map；返回 {@code null} 表示该 banner 应从结果中剔除
         *         （如关联城市离线或已删除）
         */
        Map<String, Object> dataFor(Banner banner);
    }
}

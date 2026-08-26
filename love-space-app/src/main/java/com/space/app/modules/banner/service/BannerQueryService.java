package com.space.app.modules.banner.service;

import com.space.app.common.util.ImageResponses;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.banner.dto.BannerItemResponse;
import com.space.app.modules.banner.entity.Banner;
import com.space.app.modules.banner.entity.BannerType;
import com.space.app.modules.banner.entity.Banner_;
import com.space.app.modules.banner.repository.BannerRepository;
import com.space.app.modules.banner.repository.BannerSpecifications;
import com.space.app.modules.banner.service.resolver.BannerDataResolver;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * App 端 Banner 查询服务（只读）。
 *
 * <p>规则：
 * <ul>
 *   <li>仅返回 {@code online=true} 的 banner。</li>
 *   <li>按 {@code sortOrder ASC} 排序（越小越靠前），同序号则按 {@code createdAt DESC}（新的在前）。</li>
 *   <li>{@code data} 字段按 {@link BannerType} 由对应 {@link BannerDataResolver} 装配，
 *       解析器负责批量预加载关联实体（防 N+1）并决定是否剔除条目
 *       （如 CITY 关联城市离线/缺失）。</li>
 * </ul>
 * 新增 banner 类型只需新增 {@link BannerDataResolver} 实现，无需改动本类。
 * Specification 全部走 {@code Banner_} 元模型（宪法 VI）。
 */
@Service
@Transactional(readOnly = true)
public class BannerQueryService {

    private final BannerRepository bannerRepository;
    private final ImageUrlSigner imageUrlSigner;
    private final Map<BannerType, BannerDataResolver> resolversByType;

    public BannerQueryService(BannerRepository bannerRepository,
                              ImageUrlSigner imageUrlSigner,
                              List<BannerDataResolver> resolvers) {
        this.bannerRepository = bannerRepository;
        this.imageUrlSigner = imageUrlSigner;
        this.resolversByType = resolvers.stream()
                .collect(Collectors.toMap(BannerDataResolver::type, r -> r,
                        (a, b) -> { throw new IllegalStateException("duplicate BannerDataResolver for " + a.type()); },
                        () -> new EnumMap<>(BannerType.class)));
    }

    /**
     * 查询 App 端 banner 列表。
     *
     * @param positionCode   展示位置标识码（必填，精确匹配）
     * @param linkedEntityId 关联实体过滤（可选）；为 null 时不过滤
     * @return banner 列表（已剔除关联实体不可见的条目，如 CITY 关联城市离线/缺失）
     */
    public List<BannerItemResponse> list(String positionCode, UUID linkedEntityId) {
        Specification<Banner> spec = Specification.allOf(
                BannerSpecifications.onlineTrue(),
                BannerSpecifications.hasPositionCode(positionCode),
                BannerSpecifications.linkedTo(linkedEntityId)
        );
        List<Banner> banners = bannerRepository.findAll(spec,
                Sort.by(Sort.Order.asc(Banner_.SORT_ORDER), Sort.Order.desc(Banner_.CREATED_AT)));

        // 同类型聚合后整体交给解析器批量预加载，避免逐条 N+1。
        Map<BannerType, BannerDataResolver.Prepared> preparedByType = new EnumMap<>(BannerType.class);
        banners.stream()
                .collect(Collectors.groupingBy(Banner::getType))
                .forEach((type, sameType) -> {
                    BannerDataResolver resolver = resolversByType.get(type);
                    if (resolver != null) {
                        preparedByType.put(type, resolver.prepare(sameType));
                    }
                });

        List<BannerItemResponse> result = new ArrayList<>(banners.size());
        for (Banner b : banners) {
            BannerDataResolver.Prepared prepared = preparedByType.get(b.getType());
            // 无对应解析器的类型：data 为空 map，不剔除。
            Map<String, Object> data = prepared == null ? Map.of() : prepared.dataFor(b);
            if (data == null) {
                continue; // 解析器要求剔除该条
            }
            result.add(new BannerItemResponse(
                    b.getId(), b.getName(), b.getType(),
                    ImageResponses.fromList(b.getImageUrls(), imageUrlSigner), data));
        }
        return result;
    }
}

package com.space.app.modules.banner.repository;

import com.space.app.modules.banner.entity.Banner;
import com.space.app.modules.banner.entity.BannerType;
import com.space.app.modules.banner.entity.Banner_;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/**
 * Banner Specification 工厂（app 端只读）。
 * <p>所有过滤器使用 {@code Banner_} 元模型引用属性，禁止字符串字面量（宪法 VI）。
 */
public final class BannerSpecifications {

    private BannerSpecifications() {
    }

    /** 仅查询 {@code online=true} 的 banner。 */
    public static Specification<Banner> onlineTrue() {
        return (root, query, cb) -> cb.isTrue(root.get(Banner_.online));
    }

    /**
     * 按类型过滤；{@code type} 为 null 时不增加约束。
     *
     * @param type 目标类型
     */
    public static Specification<Banner> hasType(BannerType type) {
        return (root, query, cb) -> type == null ? null : cb.equal(root.get(Banner_.type), type);
    }

    /**
     * 按 {@code linkedEntityId} 过滤；{@code linkedEntityId} 为 null 时不增加约束。
     *
     * @param linkedEntityId 关联实体 UUID
     */
    public static Specification<Banner> linkedTo(UUID linkedEntityId) {
        return (root, query, cb) -> linkedEntityId == null
                ? null
                : cb.equal(root.get(Banner_.linkedEntityId), linkedEntityId);
    }
}

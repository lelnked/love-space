package com.space.app.modules.banner.repository;

import com.space.app.modules.banner.entity.Banner;
import com.space.app.modules.banner.entity.Banner_;
import org.springframework.data.jpa.domain.Specification;

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
     * 按展示位置标识码精确过滤。
     *
     * @param positionCode 展示位置标识码（必填）
     */
    public static Specification<Banner> hasPositionCode(String positionCode) {
        return (root, query, cb) -> cb.equal(root.get(Banner_.positionCode), positionCode);
    }
}

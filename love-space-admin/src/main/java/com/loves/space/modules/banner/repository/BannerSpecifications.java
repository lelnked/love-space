package com.loves.space.modules.banner.repository;

import com.loves.space.modules.banner.entity.Banner;
import com.loves.space.modules.banner.entity.BannerType;
import com.loves.space.modules.banner.entity.Banner_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * Banner 查询 Specification 工厂。
 * <p>所有谓词均通过 hibernate-jpamodelgen 生成的 {@link Banner_} 元模型引用属性
 * （宪法 VI），<b>禁止</b>使用 {@code root.get("name")} 之类的字段名字符串字面量。
 */
public final class BannerSpecifications {

    private BannerSpecifications() {
    }

    /** 名称模糊匹配（{@code %keyword%}）；空白字符串返回恒真。 */
    public static Specification<Banner> nameContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        String pattern = "%" + keyword.trim() + "%";
        return (root, cq, cb) -> cb.like(root.get(Banner_.name), pattern);
    }

    /** 类型精确匹配；{@code null} 返回恒真。 */
    public static Specification<Banner> hasType(BannerType type) {
        if (type == null) {
            return null;
        }
        return (root, cq, cb) -> cb.equal(root.get(Banner_.type), type);
    }

    /** 上下架状态精确匹配；{@code null} 返回恒真。 */
    public static Specification<Banner> onlineEquals(Boolean online) {
        if (online == null) {
            return null;
        }
        return (root, cq, cb) -> cb.equal(root.get(Banner_.online), online);
    }

    /** 关联实体 ID 精确匹配。 */
    public static Specification<Banner> linkedTo(UUID linkedEntityId) {
        if (linkedEntityId == null) {
            return null;
        }
        return (root, cq, cb) -> cb.equal(root.get(Banner_.linkedEntityId), linkedEntityId);
    }
}

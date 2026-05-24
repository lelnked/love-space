package com.loves.space.common.page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 分页参数规范化工具。
 * <p>分页参数由 Spring Data 的 {@link Pageable} 解析器接收（见
 * {@code WebMvcConfig#pageableResolverCustomizer}，page 为 1 基）；本类只负责把解析出的
 * {@link Pageable} 校正到业务允许的范围并附加固定排序：
 * <ul>
 *   <li>page：负数按第 1 页（0 基索引 0）处理；</li>
 *   <li>size：仅允许 {@link #DEFAULT_SIZE} / {@link #ALT_SIZE}，其他值统一按 {@link #DEFAULT_SIZE} 处理。</li>
 * </ul>
 */
public final class PageQuery {

    /** 默认每页大小。 */
    public static final int DEFAULT_SIZE = 20;
    /** 备选每页大小。 */
    public static final int ALT_SIZE = 30;

    private PageQuery() {
    }

    /**
     * 将解析得到的 {@link Pageable} 校正 size 白名单并替换为指定排序。
     *
     * @param pageable Spring Data 解析出的分页（page 已是 0 基）
     * @param sort     业务固定排序，传 {@link Sort#unsorted()} 表示不排序
     */
    public static Pageable normalize(Pageable pageable, Sort sort) {
        int safePage = Math.max(pageable.getPageNumber(), 0);
        int size = pageable.getPageSize();
        int safeSize = size == DEFAULT_SIZE || size == ALT_SIZE ? size : DEFAULT_SIZE;
        return PageRequest.of(safePage, safeSize, sort);
    }
}

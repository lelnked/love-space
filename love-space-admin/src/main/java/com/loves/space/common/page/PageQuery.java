package com.loves.space.common.page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 通用分页查询参数。
 * <p>默认 size=20，可选 20/30；超出范围时取默认值 20。
 *
 * @param page 页码，从 1 开始（前端语义）；为 null 或 &lt; 1 时按 1 处理
 * @param size 每页大小；仅允许 20 或 30，其他值统一按 20 处理
 */
public record PageQuery(Integer page, Integer size) {

    /** 默认页码（1 基）。 */
    public static final int DEFAULT_PAGE = 1;
    /** 默认每页大小。 */
    public static final int DEFAULT_SIZE = 20;
    /** 备选每页大小。 */
    public static final int ALT_SIZE = 30;

    /**
     * 将本对象转换为 Spring Data {@link Pageable}（0 基页码）。
     *
     * @param sort 排序，传 {@link Sort#unsorted()} 表示不排序
     */
    public Pageable toPageable(Sort sort) {
        int safePage = page == null || page < 1 ? DEFAULT_PAGE : page;
        int safeSize = size != null && (size == DEFAULT_SIZE || size == ALT_SIZE) ? size : DEFAULT_SIZE;
        return PageRequest.of(safePage - 1, safeSize, sort);
    }
}

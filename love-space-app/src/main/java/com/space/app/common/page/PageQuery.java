package com.space.app.common.page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 通用分页查询参数。
 * <p>默认 size=20，可选 20/30；非法值统一回落到 20。
 *
 * @param page 页码，从 1 开始
 * @param size 每页大小：20 或 30
 */
public record PageQuery(Integer page, Integer size) {

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_SIZE = 20;
    public static final int ALT_SIZE = 30;

    public Pageable toPageable(Sort sort) {
        int safePage = page == null || page < 1 ? DEFAULT_PAGE : page;
        int safeSize = size != null && (size == DEFAULT_SIZE || size == ALT_SIZE) ? size : DEFAULT_SIZE;
        return PageRequest.of(safePage - 1, safeSize, sort);
    }
}

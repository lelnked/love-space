package com.space.app.common.page;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 通用分页响应（贴合 App 接口契约：content + page + size + totalElements + totalPages）。
 *
 * @param content       当前页数据
 * @param page          页码（1 基）
 * @param size          每页大小
 * @param totalElements 总条数
 * @param totalPages    总页数
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    /** 从 Spring Data {@link Page} 构造。 */
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}

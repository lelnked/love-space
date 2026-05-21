package com.loves.space.common.page;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * 分页响应包装工具：把 Spring Data {@link Page} 转为前端契约结构。
 */
public final class PageResponseMapper {

    private PageResponseMapper() {
    }

    /**
     * 将 {@link Page} 的每个元素映射后包装为 {@link PageResponse}。
     *
     * @param page    原始分页
     * @param mapper  元素映射函数
     * @param <S>     原始元素类型
     * @param <T>     目标元素类型
     */
    public static <S, T> PageResponse<T> map(Page<S> page, Function<S, T> mapper) {
        List<T> items = page.getContent().stream().map(mapper).toList();
        return new PageResponse<>(items, page.getNumber() + 1, page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    /**
     * 分页响应统一结构。
     *
     * @param items      当前页元素
     * @param page       当前页（1 基）
     * @param size       每页大小
     * @param total      总记录数
     * @param totalPages 总页数
     */
    public record PageResponse<T>(List<T> items, int page, int size, long total, int totalPages) {
    }
}

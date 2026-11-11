package com.tradesphere.ai.shared.query;

import java.util.List;
import java.util.function.Function;

/** A page of results with totals, as returned by every list endpoint. */
public record PageResult<T>(List<T> items, int page, int size, long totalElements, int totalPages) {

    public static <T> PageResult<T> of(List<T> items, PageQuery query, long total) {
        int pages = (int) Math.ceil(total / (double) query.size());
        return new PageResult<>(List.copyOf(items), query.page(), query.size(), total, pages);
    }

    public <R> PageResult<R> map(Function<T, R> mapper) {
        return new PageResult<>(items.stream().map(mapper).toList(), page, size, totalElements, totalPages);
    }
}

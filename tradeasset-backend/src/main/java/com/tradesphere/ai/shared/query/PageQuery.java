package com.tradesphere.ai.shared.query;

import com.tradesphere.ai.shared.domain.error.ValidationException;

import java.util.Locale;
import java.util.Map;

/**
 * Server-side pagination and sorting request (REQ-UI-002). The sort property is a public API name
 * that each adapter maps to a column through a whitelist, so it is never spliced into SQL raw.
 */
public record PageQuery(int page, int size, String sortProperty, boolean ascending) {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 200;
    /** Upper bound for server-side exports (REQ-ANL-004), which reuse the list query. */
    public static final int EXPORT_MAX_ROWS = 10_000;

    public PageQuery {
        if (page < 0) {
            throw new ValidationException("page", "page must be zero or positive");
        }
        if (size < 1 || size > EXPORT_MAX_ROWS) {
            throw new ValidationException("size", "size must be between 1 and " + MAX_SIZE);
        }
    }

    /**
     * @param sort "property" or "property,asc|desc"
     */
    public static PageQuery of(Integer page, Integer size, String sort, String defaultSort) {
        String effective = (sort == null || sort.isBlank()) ? defaultSort : sort;
        String property = effective;
        boolean asc = true;
        int comma = effective.indexOf(',');
        if (comma >= 0) {
            property = effective.substring(0, comma).trim();
            String dir = effective.substring(comma + 1).trim().toLowerCase(Locale.ROOT);
            if (!dir.equals("asc") && !dir.equals("desc")) {
                throw new ValidationException("sort", "sort direction must be asc or desc");
            }
            asc = dir.equals("asc");
        }
        int effectiveSize = size == null ? DEFAULT_SIZE : size;
        if (effectiveSize < 1 || effectiveSize > MAX_SIZE) {
            throw new ValidationException("size", "size must be between 1 and " + MAX_SIZE);
        }
        return new PageQuery(page == null ? 0 : page, effectiveSize, property.trim(), asc);
    }

    /** A query covering every row for exports, capped to keep memory bounded. */
    public static PageQuery exportAll(String sort, String defaultSort) {
        PageQuery base = of(0, 1, sort, defaultSort);
        return new PageQuery(0, EXPORT_MAX_ROWS, base.sortProperty(), base.ascending());
    }

    public int offset() {
        return page * size;
    }

    /** Resolves the ORDER BY clause from a whitelist of property to column expression. */
    public String orderBy(Map<String, String> allowed) {
        String column = allowed.get(sortProperty);
        if (column == null) {
            throw new ValidationException("sort", "Unsupported sort property: " + sortProperty
                    + ". Allowed: " + allowed.keySet());
        }
        return column + (ascending ? " ASC" : " DESC") + " NULLS LAST";
    }
}

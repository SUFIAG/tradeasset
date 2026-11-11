/**
 * useServerTable — list state (page, size, sort, filters) kept in the URL so a refresh, a back
 * button or a shared link reproduces the exact view. Everything is resolved server-side
 * (REQ-UI-002).
 *
 *   const table = useServerTable({ defaultSort: { id: 'createdAt', direction: 'desc' } });
 *   const query = useQuery({
 *     queryKey: ['lc', 'list', table.params],
 *     queryFn: ({ signal }) => api.get<RawPage<Lc>>('/api/v1/lcs', table.params, signal).then(normalisePage),
 *     placeholderData: keepPreviousData,
 *   });
 *   <FilterBar values={table.filters} onChange={table.setFilter} onReset={table.resetFilters} … />
 *   <DataTable state={table.state} onSortChange={table.setSort} onPageChange={table.setPage} … />
 */

import { useCallback, useMemo } from 'react';
import { useSearchParams } from 'react-router-dom';

export type SortDirection = 'asc' | 'desc';

export interface SortState {
  id: string;
  direction: SortDirection;
}

export interface TableState {
  page: number;
  size: number;
  sort: SortState | null;
}

export interface UseServerTableOptions {
  defaultSize?: number;
  defaultSort?: SortState | null;
  /** Filter ids this table owns; other URL params are left alone. */
  filterKeys?: readonly string[];
  /** Prefix when two tables share a page, e.g. `docs.` */
  prefix?: string;
}

export const PAGE_SIZE_OPTIONS = [10, 20, 50, 100] as const;

const RESERVED = ['page', 'size', 'sort'];

export function useServerTable(options: UseServerTableOptions = {}) {
  const { defaultSize = 20, defaultSort = null, filterKeys, prefix = '' } = options;
  const [searchParams, setSearchParams] = useSearchParams();
  const k = useCallback((name: string) => `${prefix}${name}`, [prefix]);

  const state: TableState = useMemo(() => {
    const page = Number.parseInt(searchParams.get(k('page')) ?? '0', 10);
    const size = Number.parseInt(searchParams.get(k('size')) ?? String(defaultSize), 10);
    const rawSort = searchParams.get(k('sort'));
    let sort = defaultSort;
    if (rawSort) {
      const [id, direction] = rawSort.split(',');
      if (id) sort = { id, direction: direction === 'asc' ? 'asc' : 'desc' };
    }
    return {
      page: Number.isFinite(page) && page >= 0 ? page : 0,
      size: PAGE_SIZE_OPTIONS.includes(size as (typeof PAGE_SIZE_OPTIONS)[number]) ? size : defaultSize,
      sort,
    };
  }, [searchParams, k, defaultSize, defaultSort]);

  const filters: Record<string, string> = useMemo(() => {
    const out: Record<string, string> = {};
    searchParams.forEach((value, key) => {
      if (!key.startsWith(prefix)) return;
      const name = key.slice(prefix.length);
      if (RESERVED.includes(name)) return;
      if (filterKeys && !filterKeys.includes(name)) return;
      out[name] = value;
    });
    return out;
  }, [searchParams, prefix, filterKeys]);

  const update = useCallback(
    (mutate: (next: URLSearchParams) => void) => {
      setSearchParams(
        (current) => {
          const next = new URLSearchParams(current);
          mutate(next);
          return next;
        },
        { replace: true },
      );
    },
    [setSearchParams],
  );

  const setPage = useCallback(
    (page: number) => update((next) => (page > 0 ? next.set(k('page'), String(page)) : next.delete(k('page')))),
    [update, k],
  );

  const setSize = useCallback(
    (size: number) =>
      update((next) => {
        next.set(k('size'), String(size));
        next.delete(k('page'));
      }),
    [update, k],
  );

  const setSort = useCallback(
    (sort: SortState | null) =>
      update((next) => {
        if (sort) next.set(k('sort'), `${sort.id},${sort.direction}`);
        else next.delete(k('sort'));
        next.delete(k('page'));
      }),
    [update, k],
  );

  const setFilter = useCallback(
    (id: string, value: string | null) =>
      update((next) => {
        if (value) next.set(k(id), value);
        else next.delete(k(id));
        next.delete(k('page'));
      }),
    [update, k],
  );

  const resetFilters = useCallback(
    () =>
      update((next) => {
        Object.keys(filters).forEach((id) => next.delete(k(id)));
        next.delete(k('page'));
      }),
    [update, filters, k],
  );

  /** Ready-to-send query params: Spring style `page`, `size`, `sort=field,dir` plus filters. */
  const params = useMemo(
    () => ({
      page: state.page,
      size: state.size,
      sort: state.sort ? `${state.sort.id},${state.sort.direction}` : undefined,
      ...filters,
    }),
    [state, filters],
  );

  return { state, filters, params, setPage, setSize, setSort, setFilter, resetFilters };
}

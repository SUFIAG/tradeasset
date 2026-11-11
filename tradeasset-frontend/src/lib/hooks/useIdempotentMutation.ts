/**
 * useIdempotentMutation — the only way a screen should submit a mutation.
 *
 *   const issue = useIdempotentMutation({
 *     scope: `lc:${id}:ISSUE`,
 *     mutationFn: (body, { idempotencyKey }) => api.post(`/api/v1/lcs/${id}/issue`, body, { idempotencyKey }),
 *     invalidate: [['lc', id]],
 *   });
 *   <Button inFlight={issue.isPending} onClick={() => issue.submit(body)}>…</Button>
 *
 * Guarantees:
 *   - an Idempotency-Key header on every call, persisted per scope+body in sessionStorage, so a
 *     double tap, a retry, or a page refresh mid-submit cannot create a second record;
 *   - a synchronous in-flight lock (double taps inside one frame are dropped);
 *   - the key is kept after a timeout / network error / 5xx (outcome unknown) so a retry replays,
 *     and cleared after success or a definitive 4xx.
 */

import { useCallback, useRef } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import type { QueryKey, UseMutationOptions } from '@tanstack/react-query';
import { ApiError, toApiError } from '../api/errors';
import { clearIdempotencyKey, idempotencyKeyFor } from '../api/idempotency';

export interface IdempotentContext {
  idempotencyKey: string;
}

export interface UseIdempotentMutationOptions<TData, TVariables>
  extends Omit<UseMutationOptions<TData, ApiError, TVariables>, 'mutationFn'> {
  /** Unique per form or per (record, action), e.g. `lc:create` or `lc:123:APPROVE`. */
  scope: string;
  mutationFn: (variables: TVariables, context: IdempotentContext) => Promise<TData>;
  /** Query keys to invalidate on success. */
  invalidate?: QueryKey[];
}

export function useIdempotentMutation<TData = unknown, TVariables = void>(
  options: UseIdempotentMutationOptions<TData, TVariables>,
) {
  const { scope, mutationFn, invalidate, onSuccess, onError, ...rest } = options;
  const queryClient = useQueryClient();
  const lock = useRef(false);

  const mutation = useMutation<TData, ApiError, TVariables>({
    ...rest,
    mutationFn: async (variables) => {
      const idempotencyKey = idempotencyKeyFor(scope, variables ?? null);
      try {
        return await mutationFn(variables, { idempotencyKey });
      } catch (error) {
        throw toApiError(error);
      }
    },
    onSuccess: async (data, variables, onMutateResult, context) => {
      clearIdempotencyKey(scope);
      if (invalidate?.length) {
        await Promise.all(invalidate.map((queryKey) => queryClient.invalidateQueries({ queryKey })));
      }
      await onSuccess?.(data, variables, onMutateResult, context);
    },
    onError: async (error, variables, onMutateResult, context) => {
      const definitive4xx = error.status !== null && error.status >= 400 && error.status < 500;
      if (definitive4xx) clearIdempotencyKey(scope);
      await onError?.(error, variables, onMutateResult, context);
    },
    // Never auto-retry a mutation silently; the user retries with the same key.
    retry: false,
  });

  const { mutateAsync } = mutation;

  /** Submit; resolves with data, or undefined when dropped as a duplicate tap or on error. */
  const submit = useCallback(
    async (variables: TVariables): Promise<TData | undefined> => {
      if (lock.current) return undefined;
      lock.current = true;
      try {
        return await mutateAsync(variables);
      } catch {
        return undefined; // error is on mutation.error; render it with <ErrorState> or a toast
      } finally {
        lock.current = false;
      }
    },
    [mutateAsync],
  );

  return { ...mutation, submit };
}

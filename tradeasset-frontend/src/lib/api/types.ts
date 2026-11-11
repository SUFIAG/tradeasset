/**
 * Shared API contract shapes. Module-specific DTOs live in their module folder.
 */

/**
 * A monetary value exactly as the server sends it (REQ-MON-001/005).
 * `amount` is a decimal STRING at the currency's scale; never parse it into a number for maths.
 * `formatted` is the server-formatted display value for the request locale.
 */
export interface MoneyDto {
  amount: string;
  currency: string;
  formatted?: string;
}

/** An action the server says is legal for this record right now (REQ-STA-002). */
export interface AllowedActionDto {
  /** Stable action code, e.g. `SUBMIT`, `APPROVE`, `REJECT`, `ISSUE`, `AMEND`. */
  action: string;
  /** Server hint: the action needs a reason (rejections, cancellations). */
  requiresReason?: boolean;
  /** Server hint: the action is a maker-checker approval step. */
  requiresApproval?: boolean;
  /** Present when the action is visible but not currently permitted, e.g. `SAME_MAKER`. */
  disabledReasonCode?: string;
}

/** Accept both `['APPROVE']` and `[{action:'APPROVE'}]`. */
export type AllowedActionsInput = ReadonlyArray<string | AllowedActionDto>;

export function normaliseAllowedActions(input: AllowedActionsInput | null | undefined): AllowedActionDto[] {
  if (!input) return [];
  return input.map((entry) => (typeof entry === 'string' ? { action: entry } : entry));
}

export interface AuditEntryDto {
  id: string;
  /** ISO-8601 instant (UTC). Rendered in Asia/Riyadh. */
  occurredAt: string;
  actor: { id?: string; displayName: string; role?: string };
  /** Stable action code, translated via `audit:actions.<code>` with fallback to the raw code. */
  action: string;
  fromState?: string;
  toState?: string;
  comment?: string;
  sourceAddress?: string;
  changes?: Array<{ field: string; before?: string | null; after?: string | null }>;
}

export interface DocumentRefDto {
  id: string;
  fileName: string;
  documentType: string;
  status?: string;
  /** Size in bytes (an integer count, not money). */
  sizeBytes?: number;
  uploadedAt?: string;
  uploadedBy?: string;
  downloadUrl?: string;
}

/** Normalised page used by every list (REQ-UI-002). */
export interface Page<T> {
  items: T[];
  /** Zero-based page index. */
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

type RawPage<T> = {
  content?: T[];
  items?: T[];
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
  page?: number | { number?: number; size?: number; totalElements?: number; totalPages?: number };
  total?: number;
};

/** Accepts Spring `Page`, Spring `PagedModel` (VIA_DTO) and `{items,total}` shapes. */
export function normalisePage<T>(raw: RawPage<T> | T[]): Page<T> {
  if (Array.isArray(raw)) {
    return { items: raw, page: 0, size: raw.length, totalElements: raw.length, totalPages: 1 };
  }
  const items = raw.content ?? raw.items ?? [];
  const meta = typeof raw.page === 'object' && raw.page !== null ? raw.page : undefined;
  const page = meta?.number ?? (typeof raw.page === 'number' ? raw.page : raw.number) ?? 0;
  const size = meta?.size ?? raw.size ?? items.length;
  const totalElements = meta?.totalElements ?? raw.totalElements ?? raw.total ?? items.length;
  const totalPages = meta?.totalPages ?? raw.totalPages ?? (size > 0 ? Math.ceil(totalElements / size) : 1);
  return { items, page, size, totalElements, totalPages };
}

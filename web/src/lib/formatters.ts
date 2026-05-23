// ── Shared formatters ────────────────────────────────────────────────────────

export function fmtClp(n: number): string {
  return `$${Math.round(n).toLocaleString('es-CL')}`;
}

export function fmtRut(raw: string): string {
  const clean = raw.replace(/[.\-]/g, '');
  if (clean.length < 2) return raw;
  const verifier = clean.slice(-1).toUpperCase();
  const digits   = clean.slice(0, -1);
  return `${digits.replace(/\B(?=(\d{3})+(?!\d))/g, '.')}-${verifier}`;
}

export function fmtDate(iso: string | null | undefined): string {
  if (!iso) return '—';
  return new Date(iso).toLocaleDateString('es-CL', {
    day: '2-digit', month: '2-digit', year: 'numeric',
  });
}

export function fmtDateTime(iso: string | null | undefined): string {
  if (!iso) return '—';
  return new Date(iso).toLocaleString('es-CL', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  });
}

export function fullName(u: {
  firstName: string;
  middleName?: string | null;
  lastName: string;
  secondLastName?: string | null;
}): string {
  return [u.firstName, u.middleName, u.lastName, u.secondLastName]
    .filter(Boolean).join(' ');
}

// ── Date presets ──────────────────────────────────────────────────────────────

export type DatePreset = '24h' | '7d' | '30d' | '90d' | 'all' | 'custom';

export const DATE_PRESETS: { key: DatePreset; label: string }[] = [
  { key: '24h',    label: 'Últimas 24h' },
  { key: '7d',     label: '7 días' },
  { key: '30d',    label: '30 días' },
  { key: '90d',    label: '90 días' },
  { key: 'all',    label: 'Histórico' },
  { key: 'custom', label: 'Personalizado' },
];

/** Returns [fromDate, toDate] for a given preset. Both null means no filter. */
export function presetToDates(
  preset: DatePreset,
  customFrom: string,
  customTo: string,
): [Date | null, Date | null] {
  const now = new Date();
  if (preset === 'all') return [null, null];
  if (preset === 'custom') {
    return [
      customFrom ? new Date(customFrom) : null,
      customTo   ? new Date(customTo + 'T23:59:59') : null,
    ];
  }
  const days = ({ '24h': 1, '7d': 7, '30d': 30, '90d': 90 } as Record<string, number>)[preset]!;
  return [new Date(now.getTime() - days * 86_400_000), now];
}

/** ISO date string for N days ago (YYYY-MM-DD). */
export function isoFrom(preset: DatePreset): string | undefined {
  if (preset === 'all' || preset === 'custom') return undefined;
  const days = ({ '24h': 1, '7d': 7, '30d': 30, '90d': 90 } as Record<string, number>)[preset]!;
  const d = new Date(Date.now() - days * 86_400_000);
  return d.toISOString().slice(0, 10);
}

/** Returns { startDate?, endDate? } as ISO strings for use as API query params. */
export function analyticsDateParams(
  preset: DatePreset,
  customFrom: string,
  customTo: string,
): { startDate?: string; endDate?: string } {
  if (preset === 'all') return {};
  if (preset === 'custom') {
    return { startDate: customFrom || undefined, endDate: customTo || undefined };
  }
  const days = ({ '24h': 1, '7d': 7, '30d': 30, '90d': 90 } as Record<string, number>)[preset]!;
  const now  = new Date();
  const from = new Date(now.getTime() - days * 86_400_000);
  const fmt  = (d: Date) => d.toISOString().slice(0, 10);
  return { startDate: fmt(from), endDate: fmt(now) };
}

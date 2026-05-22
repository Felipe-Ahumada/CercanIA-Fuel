import { useEffect, useMemo, useState } from 'react';
import { Plus, Pencil, Power, PowerOff, Tag, RefreshCw, ChevronDown, ChevronRight, Search, Hash, CheckCircle, Droplets, BarChart2 } from 'lucide-react';
import { toast } from 'sonner';
import { discountsApi } from '../../api/discounts';
import { catalogApi } from '../../api/catalog';
import { analyticsApi } from '../../api/analytics';
import type { DiscountResponse } from '../../types';
import { DiscountModal } from './DiscountModal';
import { DAY_NAMES } from '../../data/chile';

// ── helpers ──────────────────────────────────────────────────────────────────

function fmtValue(d: DiscountResponse) {
  if (d.discountType === 'FIXED_PER_LITER') return `$${d.discountValue}/L`;
  if (d.discountType === 'FIXED_AMOUNT')    return `$${d.discountValue}`;
  return `${d.discountValue}%`;
}

function dayLabel(day: number | null) {
  if (!day) return 'Todos los días';
  return DAY_NAMES[day] ?? '—';
}

const CARD_TYPE_LABEL: Record<string, string> = {
  CREDIT: 'Crédito',
  DEBIT:  'Débito',
  PREPAID:'Prepago',
};

const CARD_TYPE_COLOR: Record<string, string> = {
  CREDIT:  'bg-blue-100 text-blue-700',
  DEBIT:   'bg-emerald-100 text-emerald-700',
  PREPAID: 'bg-amber-100 text-amber-700',
};

// ── data grouping ─────────────────────────────────────────────────────────────

type CardGroup = {
  cardProductId: number | null;
  cardProductName: string;
  cardType: string | null;
  discounts: DiscountResponse[];
};

type BankGroup = {
  bankName: string;
  cards: CardGroup[];
  totalActive: number;
};

type StatusFilter = 'all' | 'active' | 'inactive';

function buildGroups(discounts: DiscountResponse[], status: StatusFilter): BankGroup[] {
  const visible =
    status === 'active'   ? discounts.filter((d) =>  d.active) :
    status === 'inactive' ? discounts.filter((d) => !d.active) :
    discounts;
  const bankMap = new Map<string, Map<string, DiscountResponse[]>>();

  for (const d of visible) {
    const bank = d.bankName ?? 'Sin banco';
    const card = d.cardProductName ?? 'Sin tarjeta';
    if (!bankMap.has(bank)) bankMap.set(bank, new Map());
    const cardMap = bankMap.get(bank)!;
    if (!cardMap.has(card)) cardMap.set(card, []);
    cardMap.get(card)!.push(d);
  }

  return [...bankMap.entries()]
    .map(([bankName, cardMap]) => ({
      bankName,
      cards: [...cardMap.entries()].map(([cardName, ds]) => ({
        cardProductId:   ds[0].cardProductId,
        cardProductName: cardName,
        cardType:        ds[0].cardType,
        discounts:       ds.sort((a, b) => a.brandName.localeCompare(b.brandName)),
      })),
      totalActive: [...cardMap.values()].flat().filter((d) => d.active).length,
    }))
    .sort((a, b) => a.bankName.localeCompare(b.bankName));
}

// ── sub-components ────────────────────────────────────────────────────────────

interface DiscountRowProps {
  d: DiscountResponse;
  toggling: number | null;
  onDeactivate: (d: DiscountResponse) => void;
  onReactivate: (d: DiscountResponse) => void;
  onEdit: (d: DiscountResponse) => void;
}

const TODAY = new Date().toISOString().slice(0, 10);

function DiscountRow({ d, toggling, onDeactivate, onReactivate, onEdit }: DiscountRowProps) {
  const isExpired = d.endDate != null && d.endDate < TODAY;
  return (
    <div className={`flex items-center gap-3 px-4 py-2.5 border-b border-gray-100 last:border-0 transition-colors ${
      d.active ? 'hover:bg-gray-50' : 'bg-gray-50 opacity-60'
    }`}>
      {/* Bencinera */}
      <span className="w-28 text-sm font-medium text-gray-900 shrink-0">{d.brandName}</span>

      {/* Día */}
      <span className="w-32 text-xs text-gray-500 shrink-0">{dayLabel(d.dayOfWeek)}</span>

      {/* Valor */}
      <span className="w-24 text-sm font-semibold text-blue-700 shrink-0">{fmtValue(d)}</span>

      {/* Combustible */}
      {d.fuelTypeName && (
        <span className="text-xs bg-gray-100 text-gray-600 px-2 py-0.5 rounded shrink-0">
          {d.fuelTypeName}
        </span>
      )}

      {/* Estado */}
      <span className={`ml-auto inline-flex items-center gap-1 px-2 py-0.5 rounded text-xs font-medium shrink-0 ${
        d.active ? 'bg-emerald-100 text-emerald-700' : 'bg-gray-100 text-gray-500'
      }`}>
        <span className={`w-1.5 h-1.5 rounded-full ${d.active ? 'bg-emerald-500' : 'bg-gray-400'}`} />
        {d.active ? 'Activo' : 'Inactivo'}
      </span>

      {/* Acciones */}
      <div className="flex items-center gap-1 shrink-0">
        {d.active && (
          <button onClick={() => onEdit(d)}
            className="p-1.5 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
            title="Editar">
            <Pencil size={13} />
          </button>
        )}
        {d.active ? (
          <button onClick={() => onDeactivate(d)} disabled={toggling === d.id}
            className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors disabled:opacity-50"
            title="Desactivar">
            <PowerOff size={13} />
          </button>
        ) : !isExpired ? (
          <button onClick={() => onReactivate(d)} disabled={toggling === d.id}
            className="p-1.5 text-gray-400 hover:text-emerald-600 hover:bg-emerald-50 rounded-lg transition-colors disabled:opacity-50"
            title="Reactivar">
            <Power size={13} />
          </button>
        ) : (
          <span className="px-2 py-0.5 text-[10px] text-red-400 bg-red-50 rounded font-medium" title="Fecha de término vencida">
            Expirado
          </span>
        )}
      </div>
    </div>
  );
}

interface CardSectionProps {
  card: CardGroup;
  toggling: number | null;
  onDeactivate: (d: DiscountResponse) => void;
  onReactivate: (d: DiscountResponse) => void;
  onEdit: (d: DiscountResponse) => void;
}

function CardSection({ card, toggling, onDeactivate, onReactivate, onEdit }: CardSectionProps) {
  const [open, setOpen] = useState(true);
  const activeCount = card.discounts.filter((d) => d.active).length;

  return (
    <div className="border border-gray-200 rounded-lg overflow-hidden mb-3">
      {/* Card header */}
      <button
        onClick={() => setOpen((v) => !v)}
        className="w-full flex items-center gap-3 px-4 py-2.5 bg-gray-50 hover:bg-gray-100 transition-colors text-left"
      >
        {open ? <ChevronDown size={14} className="text-gray-400 shrink-0" /> : <ChevronRight size={14} className="text-gray-400 shrink-0" />}
        <span className="text-sm font-medium text-gray-800 flex-1">{card.cardProductName}</span>
        {card.cardType && (
          <span className={`text-xs px-2 py-0.5 rounded font-medium ${CARD_TYPE_COLOR[card.cardType] ?? 'bg-gray-100 text-gray-600'}`}>
            {CARD_TYPE_LABEL[card.cardType] ?? card.cardType}
          </span>
        )}
        <span className="text-xs text-gray-400 ml-2">{activeCount} bencinera{activeCount !== 1 ? 's' : ''}</span>
      </button>

      {/* Discount rows */}
      {open && (
        <div>
          <div className="flex items-center gap-3 px-4 py-1.5 bg-white border-b border-gray-100">
            <span className="w-28 text-xs font-medium text-gray-400">Bencinera</span>
            <span className="w-32 text-xs font-medium text-gray-400">Día válido</span>
            <span className="w-24 text-xs font-medium text-gray-400">Descuento</span>
          </div>
          {card.discounts.map((d) => (
            <DiscountRow key={d.id} d={d} toggling={toggling}
              onDeactivate={onDeactivate} onReactivate={onReactivate} onEdit={onEdit} />
          ))}
        </div>
      )}
    </div>
  );
}

// ── main page ─────────────────────────────────────────────────────────────────

export function DiscountsPage() {
  const [discounts, setDiscounts]   = useState<DiscountResponse[]>([]);
  const [brands, setBrands]         = useState<{ id: number; name: string }[]>([]);
  const [totalDiscountUses, setTotalDiscountUses] = useState<number | null>(null);
  const [loading, setLoading]       = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [editTarget, setEditTarget] = useState<DiscountResponse | null>(null);
  const [toggling, setToggling]     = useState<number | null>(null);
  const [statusFilter, setStatusFilter] = useState<StatusFilter>('active');
  const [search, setSearch]         = useState('');

  const load = async () => {
    setLoading(true);
    try {
      const [d, b, analytics] = await Promise.all([
        discountsApi.listAllAdmin(),
        catalogApi.brands(),
        analyticsApi.get(),
      ]);
      setDiscounts(d);
      setBrands(b);
      setTotalDiscountUses(analytics.totalDiscountUses);
    } catch {
      toast.error('Error al cargar los descuentos');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const handleDeactivate = async (d: DiscountResponse) => {
    setToggling(d.id);
    try {
      await discountsApi.delete(d.id);
      toast.success('Descuento desactivado');
      setDiscounts((prev) => prev.map((x) => x.id === d.id ? { ...x, active: false } : x));
    } catch { toast.error('Error al desactivar'); }
    finally { setToggling(null); }
  };

  const handleReactivate = async (d: DiscountResponse) => {
    setToggling(d.id);
    try {
      await discountsApi.reactivate(d.id);
      toast.success('Descuento reactivado');
      setDiscounts((prev) => prev.map((x) => x.id === d.id ? { ...x, active: true } : x));
    } catch { toast.error('Error al reactivar'); }
    finally { setToggling(null); }
  };

  const activeCount   = discounts.filter((d) => d.active).length;
  const inactiveCount = discounts.length - activeCount;

  const avgDiscount = (() => {
    const perLiter = discounts.filter((d) => d.active && d.discountType === 'FIXED_PER_LITER');
    if (!perLiter.length) return null;
    return perLiter.reduce((s, d) => s + d.discountValue, 0) / perLiter.length;
  })();

  const groups = useMemo(() => {
    const g = buildGroups(discounts, statusFilter);
    if (!search.trim()) return g;
    const q = search.toLowerCase();
    return g.filter((b) => b.bankName.toLowerCase().includes(q));
  }, [discounts, statusFilter, search]);

  return (
    <div className="p-6 space-y-5">
      {/* Header */}
      <div className="flex items-center justify-between gap-4 flex-wrap">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Descuentos</h1>
          <p className="text-sm text-gray-500 mt-0.5">
            {loading ? '...' : `${activeCount} activos · ${inactiveCount} inactivos`}
          </p>
        </div>
        <div className="flex items-center gap-2 flex-wrap">
          {/* Search by bank */}
          <div className="relative">
            <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Buscar banco..."
              className="pl-8 pr-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 w-44"
            />
          </div>

          <div className="flex items-center gap-1 border border-gray-200 rounded-lg p-0.5 bg-gray-50">
            {([
              { key: 'all',      label: 'Todos' },
              { key: 'active',   label: 'Activos' },
              { key: 'inactive', label: 'Inactivos' },
            ] as const).map(({ key, label }) => (
              <button
                key={key}
                onClick={() => setStatusFilter(key)}
                className={`px-3 py-1.5 text-xs rounded-md transition-colors ${
                  statusFilter === key
                    ? 'bg-white text-gray-700 shadow-sm font-medium'
                    : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                {label}
                {!loading && key !== 'all' && (
                  <span className={`ml-1.5 px-1.5 py-0.5 rounded-full text-[10px] font-medium ${
                    key === 'active'
                      ? statusFilter === 'active' ? 'bg-emerald-100 text-emerald-700' : 'bg-gray-100 text-gray-500'
                      : statusFilter === 'inactive' ? 'bg-red-100 text-red-600' : 'bg-gray-100 text-gray-500'
                  }`}>
                    {key === 'active' ? activeCount : inactiveCount}
                  </span>
                )}
              </button>
            ))}
          </div>

          <button onClick={load}
            className="p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
            title="Refrescar">
            <RefreshCw size={16} />
          </button>

          <button onClick={() => setShowCreate(true)}
            className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors">
            <Plus size={16} />
            Crear Descuento
          </button>
        </div>
      </div>

      {/* KPIs */}
      <div className="grid grid-cols-2 xl:grid-cols-4 gap-4">
        {[
          {
            label: 'Total Descuentos',
            value: loading ? '—' : discounts.length.toLocaleString('es-CL'),
            sub: null,
            Icon: Hash,
            color: 'bg-blue-600',
          },
          {
            label: 'Descuentos Activos',
            value: loading ? '—' : activeCount.toLocaleString('es-CL'),
            sub: null,
            Icon: CheckCircle,
            color: 'bg-emerald-600',
          },
          {
            label: 'Descuento Promedio',
            value: loading || avgDiscount === null ? '—' : `$${avgDiscount.toFixed(0)}/L`,
            sub: 'Por litro de combustible',
            Icon: Droplets,
            color: 'bg-amber-500',
          },
          {
            label: 'Usos Totales',
            value: loading || totalDiscountUses === null
              ? '—'
              : totalDiscountUses.toLocaleString('es-CL'),
            sub: null,
            Icon: BarChart2,
            color: 'bg-purple-600',
          },
        ].map(({ label, value, sub, Icon, color }) => (
          <div key={label} className="bg-white rounded-xl border border-gray-200 p-5 shadow-sm">
            <div className="flex items-center justify-between mb-3">
              <p className="text-xs font-medium text-gray-500 leading-tight">{label}</p>
              <div className={`p-2 rounded-lg ${color} shrink-0`}>
                <Icon size={16} className="text-white" />
              </div>
            </div>
            {loading
              ? <div className="h-7 w-20 bg-gray-200 rounded animate-pulse" />
              : <p className="text-2xl font-bold text-gray-900">{value}</p>}
            {sub && !loading && (
              <p className="text-xs text-gray-400 mt-1">{sub}</p>
            )}
          </div>
        ))}
      </div>

      {/* Content */}
      {loading ? (
        <div className="space-y-4">
          {[1, 2, 3].map((i) => (
            <div key={i} className="bg-white border border-gray-200 rounded-xl p-4 animate-pulse">
              <div className="h-5 bg-gray-200 rounded w-48 mb-3" />
              <div className="h-4 bg-gray-100 rounded w-full mb-2" />
              <div className="h-4 bg-gray-100 rounded w-3/4" />
            </div>
          ))}
        </div>
      ) : groups.length === 0 ? (
        <div className="bg-white border border-gray-200 rounded-xl py-20 flex flex-col items-center text-gray-400">
          <Tag size={40} className="mb-3 text-gray-300" />
          <p className="text-sm">
            {search
              ? `Sin bancos que coincidan con "${search}"`
              : statusFilter === 'inactive'
              ? 'No hay descuentos inactivos'
              : statusFilter === 'active'
              ? 'No hay descuentos activos'
              : 'Sin descuentos registrados'}
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {groups.map((bank) => (
            <BankSection
              key={bank.bankName}
              bank={bank}
              toggling={toggling}
              onDeactivate={handleDeactivate}
              onReactivate={handleReactivate}
              onEdit={setEditTarget}
            />
          ))}
        </div>
      )}

      {showCreate && (
        <DiscountModal onClose={() => setShowCreate(false)} onSaved={load} brands={brands} />
      )}
      {editTarget && (
        <DiscountModal discount={editTarget} onClose={() => setEditTarget(null)} onSaved={load} brands={brands} />
      )}
    </div>
  );
}

// ── bank section (defined after DiscountsPage to avoid hoisting issues) ───────

interface BankSectionProps {
  bank: BankGroup;
  toggling: number | null;
  onDeactivate: (d: DiscountResponse) => void;
  onReactivate: (d: DiscountResponse) => void;
  onEdit: (d: DiscountResponse) => void;
}

function BankSection({ bank, toggling, onDeactivate, onReactivate, onEdit }: BankSectionProps) {
  const [open, setOpen] = useState(true);

  return (
    <div className="bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm">
      {/* Bank header */}
      <button
        onClick={() => setOpen((v) => !v)}
        className="w-full flex items-center gap-3 px-5 py-4 hover:bg-gray-50 transition-colors text-left border-b border-gray-100"
      >
        {open
          ? <ChevronDown size={16} className="text-gray-400 shrink-0" />
          : <ChevronRight size={16} className="text-gray-400 shrink-0" />}
        <h3 className="text-base font-semibold text-gray-900 flex-1">{bank.bankName}</h3>
        <span className="text-xs bg-blue-50 text-blue-700 font-medium px-2.5 py-1 rounded-full">
          {bank.totalActive} descuento{bank.totalActive !== 1 ? 's' : ''}
        </span>
      </button>

      {/* Card groups */}
      {open && (
        <div className="p-4 space-y-3">
          {bank.cards.map((card) => (
            <CardSection
              key={`${card.cardProductId ?? 'none'}-${card.cardProductName}`}
              card={card}
              toggling={toggling}
              onDeactivate={onDeactivate}
              onReactivate={onReactivate}
              onEdit={onEdit}
            />
          ))}
        </div>
      )}
    </div>
  );
}

import { useEffect, useState } from 'react';
import { Plus, Pencil, Power, PowerOff, Tag, RefreshCw } from 'lucide-react';
import { toast } from 'sonner';
import { discountsApi } from '../../api/discounts';
import type { DiscountResponse } from '../../types';
import { DiscountModal } from './DiscountModal';
import { SkeletonRow } from '../common/SkeletonRow';
import { BENCINERAS, DAY_NAMES } from '../../data/chile';

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('es-CL', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  });
}

function formatValue(d: DiscountResponse) {
  if (d.discountType === 'FIXED_PER_LITER') return `$${d.discountValue}/litro`;
  if (d.discountType === 'FIXED_AMOUNT') return `$${d.discountValue} fijo`;
  return `${d.discountValue}%`;
}

function dayLabel(dayOfWeek: number | null) {
  if (!dayOfWeek) return 'Todos los días';
  return DAY_NAMES[dayOfWeek] ?? '—';
}

export function DiscountsPage() {
  const [discounts, setDiscounts] = useState<DiscountResponse[]>([]);
  const [brands, setBrands] = useState<{ id: number; name: string }[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [editTarget, setEditTarget] = useState<DiscountResponse | null>(null);
  const [toggling, setToggling] = useState<number | null>(null);

  const load = async () => {
    setLoading(true);
    try {
      const d = await discountsApi.listAll();
      setDiscounts(d);
      const brandMap = new Map<number, string>();
      d.forEach((disc) => brandMap.set(disc.brandId, disc.brandName));
      if (brandMap.size === 0) {
        setBrands(BENCINERAS.map((n, i) => ({ id: i + 1, name: n })));
      } else {
        setBrands([...brandMap.entries()].map(([id, name]) => ({ id, name })));
      }
    } catch {
      // silent
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const handleDeactivate = async (d: DiscountResponse) => {
    setToggling(d.id);
    try {
      await discountsApi.delete(d.id);
      toast.success(`Descuento desactivado`);
      setDiscounts((prev) =>
        prev.map((item) => (item.id === d.id ? { ...item, active: false } : item))
      );
    } catch {
      toast.error('Error al desactivar el descuento');
    } finally {
      setToggling(null);
    }
  };

  const activeCount = discounts.filter((d) => d.active).length;
  const inactiveCount = discounts.length - activeCount;

  return (
    <div className="p-6 space-y-5">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Descuentos</h1>
          <p className="text-sm text-gray-500 mt-0.5">
            {loading
              ? '...'
              : `${activeCount} activos · ${inactiveCount} inactivos`}
          </p>
        </div>
        <div className="flex gap-2">
          <button
            onClick={load}
            className="p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
            title="Refrescar"
          >
            <RefreshCw size={16} />
          </button>
          <button
            onClick={() => setShowCreate(true)}
            className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors"
          >
            <Plus size={16} />
            Crear Descuento
          </button>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-200 bg-gray-50">
                <th className="text-left px-4 py-3 font-medium text-gray-600">Bencinera</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Banco</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Descuento</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Día válido</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Vigencia</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Estado</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <SkeletonRow cols={7} rows={8} />
              ) : discounts.length === 0 ? (
                <tr>
                  <td colSpan={7} className="text-center py-16 text-gray-400">
                    <Tag size={40} className="mx-auto mb-3 text-gray-300" />
                    <p>No hay descuentos registrados</p>
                  </td>
                </tr>
              ) : (
                discounts.map((d) => (
                  <tr
                    key={d.id}
                    className="border-b border-gray-100 hover:bg-gray-50 transition-colors"
                  >
                    <td className="px-4 py-3 font-medium text-gray-900">{d.brandName}</td>
                    <td className="px-4 py-3 text-gray-600">
                      {d.bankName ? (
                        <div>
                          <span>{d.bankName}</span>
                          {d.cardProductName && (
                            <span className="block text-xs text-gray-400">{d.cardProductName}</span>
                          )}
                        </div>
                      ) : (
                        <span className="text-gray-400 text-xs">Todos</span>
                      )}
                    </td>
                    <td className="px-4 py-3">
                      <span className="font-semibold text-blue-700">{formatValue(d)}</span>
                    </td>
                    <td className="px-4 py-3 text-gray-600">{dayLabel(d.dayOfWeek)}</td>
                    <td className="px-4 py-3 text-gray-500 text-xs">
                      <span>{formatDate(d.startDate)}</span>
                      {d.endDate && (
                        <span className="block">{formatDate(d.endDate)}</span>
                      )}
                    </td>
                    <td className="px-4 py-3">
                      <span
                        className={`inline-flex items-center gap-1 px-2 py-0.5 rounded text-xs font-medium ${
                          d.active
                            ? 'bg-emerald-100 text-emerald-700'
                            : 'bg-gray-100 text-gray-500'
                        }`}
                      >
                        <span
                          className={`w-1.5 h-1.5 rounded-full ${
                            d.active ? 'bg-emerald-500' : 'bg-gray-400'
                          }`}
                        />
                        {d.active ? 'Activo' : 'Inactivo'}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-1">
                        <button
                          onClick={() => setEditTarget(d)}
                          className="p-1.5 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                          title="Editar"
                        >
                          <Pencil size={14} />
                        </button>
                        {d.active ? (
                          <button
                            onClick={() => handleDeactivate(d)}
                            disabled={toggling === d.id}
                            className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors disabled:opacity-50"
                            title="Desactivar"
                          >
                            <PowerOff size={14} />
                          </button>
                        ) : (
                          <button
                            disabled
                            className="p-1.5 text-gray-300 rounded-lg cursor-not-allowed"
                            title="No se puede reactivar desde aquí"
                          >
                            <Power size={14} />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {showCreate && (
        <DiscountModal
          onClose={() => setShowCreate(false)}
          onSaved={load}
          brands={brands}
        />
      )}

      {editTarget && (
        <DiscountModal
          discount={editTarget}
          onClose={() => setEditTarget(null)}
          onSaved={load}
          brands={brands}
        />
      )}
    </div>
  );
}

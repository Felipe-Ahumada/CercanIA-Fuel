import { useEffect, useState } from 'react';
import { Loader2 } from 'lucide-react';
import { toast } from 'sonner';
import { Modal } from '../common/Modal';
import { discountsApi } from '../../api/discounts';
import { banksApi } from '../../api/banks';
import { cardProductsApi } from '../../api/cardProducts';
import type {
  BankResponse,
  CardProductResponse,
  DiscountCreateRequest,
  DiscountResponse,
  DiscountType,
} from '../../types';
import { DIAS_SEMANA } from '../../data/chile';

interface Props {
  discount?: DiscountResponse; // present = edit mode
  onClose: () => void;
  onSaved: () => void;
  brands: { id: number; name: string }[];
}

interface FormState {
  brandId: string;
  bankId: string;
  cardProductId: string;
  discountType: DiscountType;
  discountValue: string;
  maxCap: string;
  dayOfWeek: string; // '' = all days
  description: string;
  startDate: string;
  endDate: string;
}

interface Errors {
  brandId?: string;
  discountValue?: string;
  startDate?: string;
}

const TYPE_LABELS: Record<DiscountType, string> = {
  FIXED_PER_LITER: '$/litro',
  FIXED_AMOUNT: '$ fijo',
  PERCENTAGE: '%',
};

export function DiscountModal({ discount, onClose, onSaved, brands }: Props) {
  const isEdit = Boolean(discount);

  const [form, setForm] = useState<FormState>({
    brandId: discount ? String(discount.brandId) : '',
    bankId: '',
    cardProductId: discount ? String(discount.cardProductId ?? '') : '',
    discountType: discount?.discountType ?? 'FIXED_PER_LITER',
    discountValue: discount ? String(discount.discountValue) : '',
    maxCap: discount?.maxCap ? String(discount.maxCap) : '',
    dayOfWeek: discount?.dayOfWeek ? String(discount.dayOfWeek) : '',
    description: discount?.description ?? '',
    startDate: discount?.startDate ?? new Date().toISOString().split('T')[0],
    endDate: discount?.endDate ?? '',
  });

  const [banks, setBanks] = useState<BankResponse[]>([]);
  const [cardProducts, setCardProducts] = useState<CardProductResponse[]>([]);
  const [errors, setErrors] = useState<Errors>({});
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    banksApi.list().then((p) => setBanks(p.content)).catch(() => {});
  }, []);

  useEffect(() => {
    if (form.bankId) {
      cardProductsApi.list(Number(form.bankId))
        .then(setCardProducts)
        .catch(() => setCardProducts([]));
    } else {
      setCardProducts([]);
    }
    setForm((f) => ({ ...f, cardProductId: '' }));
  }, [form.bankId]);

  // Pre-fill bankId from discount
  useEffect(() => {
    if (discount?.bankName && banks.length > 0) {
      const found = banks.find((b) => b.name === discount.bankName);
      if (found) setForm((f) => ({ ...f, bankId: String(found.id) }));
    }
  }, [banks, discount]);

  const set =
    (field: keyof FormState) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) =>
      setForm((f) => ({ ...f, [field]: e.target.value }));

  const validate = (): boolean => {
    const e: Errors = {};
    if (!form.brandId) e.brandId = 'Selecciona una bencinera';
    const val = parseFloat(form.discountValue);
    if (isNaN(val) || val < 10 || val > 500) {
      e.discountValue = 'El descuento debe ser entre $10 y $500';
    }
    if (!form.startDate) e.startDate = 'Fecha de inicio requerida';
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    setSaving(true);
    try {
      if (isEdit && discount) {
        await discountsApi.update(discount.id, {
          cardProductId: form.cardProductId ? Number(form.cardProductId) : undefined,
          dayOfWeek: form.dayOfWeek ? Number(form.dayOfWeek) : undefined,
          discountType: form.discountType,
          discountValue: parseFloat(form.discountValue),
          maxCap: form.maxCap ? parseFloat(form.maxCap) : undefined,
          description: form.description || undefined,
          startDate: form.startDate,
          endDate: form.endDate || undefined,
        });
        toast.success('Descuento actualizado');
      } else {
        const req: DiscountCreateRequest = {
          brandId: Number(form.brandId),
          cardProductId: form.cardProductId ? Number(form.cardProductId) : undefined,
          dayOfWeek: form.dayOfWeek ? Number(form.dayOfWeek) : undefined,
          discountType: form.discountType,
          discountValue: parseFloat(form.discountValue),
          maxCap: form.maxCap ? parseFloat(form.maxCap) : undefined,
          description: form.description || undefined,
          startDate: form.startDate,
          endDate: form.endDate || undefined,
        };
        await discountsApi.create(req);
        toast.success('Descuento creado exitosamente');
      }
      onSaved();
      onClose();
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        'Error al guardar el descuento';
      toast.error(msg);
    } finally {
      setSaving(false);
    }
  };

  const selectedBrand = brands.find((b) => String(b.id) === form.brandId);
  const selectedCard = cardProducts.find((c) => String(c.id) === form.cardProductId);
  const selectedDay = DIAS_SEMANA.find((d) => String(d.value) === form.dayOfWeek);

  const preview =
    form.discountValue && selectedBrand
      ? `Descuento de ${form.discountType === 'FIXED_PER_LITER' ? `$${form.discountValue} por litro` : form.discountType === 'FIXED_AMOUNT' ? `$${form.discountValue} fijo` : `${form.discountValue}%`} en ${selectedBrand.name}${selectedCard ? ` con tarjeta ${selectedCard.name}` : ''}${selectedDay ? `, los ${selectedDay.label}` : ''}`
      : null;

  return (
    <Modal
      title={isEdit ? 'Editar Descuento' : 'Crear Descuento'}
      onClose={onClose}
      width="max-w-xl"
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Brand */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Bencinera <span className="text-red-500">*</span>
          </label>
          <select
            value={form.brandId}
            onChange={set('brandId')}
            disabled={isEdit}
            className={`w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 ${errors.brandId ? 'border-red-400' : 'border-gray-300'}`}
          >
            <option value="">Seleccionar bencinera</option>
            {brands.map((b) => (
              <option key={b.id} value={String(b.id)}>
                {b.name}
              </option>
            ))}
          </select>
          {errors.brandId && <p className="text-red-500 text-xs mt-1">{errors.brandId}</p>}
        </div>

        {/* Bank + Card Product */}
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Banco</label>
            <select
              value={form.bankId}
              onChange={set('bankId')}
              className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Todos los bancos</option>
              {banks.map((b) => (
                <option key={b.id} value={String(b.id)}>
                  {b.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Tarjeta</label>
            <select
              value={form.cardProductId}
              onChange={set('cardProductId')}
              disabled={!form.bankId}
              className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-400"
            >
              <option value="">
                {form.bankId ? 'Todas las tarjetas' : 'Selecciona banco'}
              </option>
              {cardProducts.map((c) => (
                <option key={c.id} value={String(c.id)}>
                  {c.name}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Discount type + value */}
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Tipo de descuento
            </label>
            <select
              value={form.discountType}
              onChange={set('discountType')}
              className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              {Object.entries(TYPE_LABELS).map(([k, v]) => (
                <option key={k} value={k}>
                  {v}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Valor <span className="text-red-500">*</span>
            </label>
            <div className="relative">
              <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-sm">
                {form.discountType === 'PERCENTAGE' ? '' : '$'}
              </span>
              <input
                type="number"
                min={10}
                max={500}
                step="any"
                value={form.discountValue}
                onChange={set('discountValue')}
                className={`w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${form.discountType !== 'PERCENTAGE' ? 'pl-6' : ''} ${errors.discountValue ? 'border-red-400' : 'border-gray-300'}`}
                placeholder={form.discountType === 'PERCENTAGE' ? '5' : '50'}
              />
              {form.discountType === 'PERCENTAGE' && (
                <span className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 text-sm">%</span>
              )}
              {form.discountType === 'FIXED_PER_LITER' && (
                <span className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 text-xs">/L</span>
              )}
            </div>
            {errors.discountValue && (
              <p className="text-red-500 text-xs mt-1">{errors.discountValue}</p>
            )}
          </div>
        </div>

        {/* Day of week */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Día válido</label>
          <select
            value={form.dayOfWeek}
            onChange={set('dayOfWeek')}
            className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Todos los días</option>
            {DIAS_SEMANA.map((d) => (
              <option key={d.value} value={String(d.value)}>
                {d.label}
              </option>
            ))}
          </select>
        </div>

        {/* Dates */}
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Fecha inicio <span className="text-red-500">*</span>
            </label>
            <input
              type="date"
              value={form.startDate}
              onChange={set('startDate')}
              className={`w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.startDate ? 'border-red-400' : 'border-gray-300'}`}
            />
            {errors.startDate && <p className="text-red-500 text-xs mt-1">{errors.startDate}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Fecha fin <span className="text-gray-400 text-xs">(opcional)</span>
            </label>
            <input
              type="date"
              value={form.endDate}
              onChange={set('endDate')}
              min={form.startDate}
              className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>

        {/* Description */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Descripción</label>
          <textarea
            value={form.description}
            onChange={set('description')}
            rows={2}
            maxLength={255}
            className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
            placeholder="Descripción adicional del descuento..."
          />
        </div>

        {/* Preview */}
        {preview && (
          <div className="bg-blue-50 border border-blue-200 rounded-lg px-4 py-3">
            <p className="text-xs font-medium text-blue-700 mb-0.5">Vista previa</p>
            <p className="text-sm text-blue-900">{preview}</p>
          </div>
        )}

        <div className="flex gap-3 pt-1">
          <button
            type="button"
            onClick={onClose}
            className="flex-1 px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
          >
            Cancelar
          </button>
          <button
            type="submit"
            disabled={saving}
            className="flex-1 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 disabled:bg-blue-400 transition-colors flex items-center justify-center gap-2"
          >
            {saving && <Loader2 size={14} className="animate-spin" />}
            {saving ? 'Guardando...' : 'Guardar'}
          </button>
        </div>
      </form>
    </Modal>
  );
}

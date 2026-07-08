import { useEffect, useState } from 'react';
import { Loader2, Plus, X } from 'lucide-react';
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
  bankId: string;
  cardProductId: string;
  brandIds: number[];       // multi-select (create only)
  discountType: DiscountType;
  discountValue: string;
  maxCap: string;
  dayOfWeek: string;
  description: string;
  startDate: string;
  endDate: string;
}

interface Errors {
  bankId?: string;
  brandIds?: string;
  discountValue?: string;
  startDate?: string;
  endDate?: string;
}

const TYPE_LABELS: Record<DiscountType, string> = {
  FIXED_PER_LITER: '$/litro',
  FIXED_AMOUNT: '$ fijo',
  PERCENTAGE: '%',
};

const today = new Date().toISOString().slice(0, 10);

export function DiscountModal({ discount, onClose, onSaved, brands }: Props) {
  const isEdit = Boolean(discount);

  const [form, setForm] = useState<FormState>({
    bankId: '',
    cardProductId: discount ? String(discount.cardProductId ?? '') : '',
    brandIds: discount ? [discount.brandId] : [],
    discountType: discount?.discountType ?? 'FIXED_PER_LITER',
    discountValue: discount ? String(discount.discountValue) : '',
    maxCap: discount?.maxCap ? String(discount.maxCap) : '',
    dayOfWeek: discount?.dayOfWeek ? String(discount.dayOfWeek) : '',
    description: discount?.description ?? '',
    startDate: discount?.startDate ?? today,
    endDate: discount?.endDate ?? '',
  });

  const [banks, setBanks]               = useState<BankResponse[]>([]);
  const [cardProducts, setCardProducts] = useState<CardProductResponse[]>([]);
  const [errors, setErrors]             = useState<Errors>({});
  const [saving, setSaving]             = useState(false);

  // Inline bank creation
  const [newBankMode, setNewBankMode]   = useState(false);
  const [newBankName, setNewBankName]   = useState('');
  const [newBankCode, setNewBankCode]   = useState('');
  const [creatingBank, setCreatingBank] = useState(false);

  // Brands section
  const [showMoreBrands, setShowMoreBrands] = useState(false);

  // Inline card product creation
  const [newCardMode, setNewCardMode]   = useState(false);
  const [newCardName, setNewCardName]   = useState('');
  const [newCardType, setNewCardType]   = useState('CREDIT');
  const [creatingCard, setCreatingCard] = useState(false);

  useEffect(() => {
    banksApi.list().then((p) => setBanks(p.content)).catch(() => {});
  }, []);

  // Load card products when bank changes
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

  // Pre-fill bankId from discount in edit mode
  useEffect(() => {
    if (discount?.bankName && banks.length > 0) {
      const found = banks.find((b) => b.name === discount.bankName);
      if (found) setForm((f) => ({ ...f, bankId: String(found.id) }));
    }
  }, [banks, discount]);

  const handleCreateBank = async () => {
    if (!newBankName.trim() || !newBankCode.trim()) return;
    setCreatingBank(true);
    try {
      const created = await banksApi.create({ name: newBankName.trim(), code: newBankCode.trim().toUpperCase() });
      setBanks((prev) => [...prev, created]);
      setForm((f) => ({ ...f, bankId: String(created.id) }));
      setNewBankMode(false);
      setNewBankName('');
      setNewBankCode('');
      toast.success(`Banco "${created.name}" creado`);
    } catch {
      toast.error('Error al crear el banco');
    } finally {
      setCreatingBank(false);
    }
  };

  const handleCreateCard = async () => {
    if (!newCardName.trim() || !form.bankId) return;
    setCreatingCard(true);
    try {
      const created = await cardProductsApi.create({ bankId: Number(form.bankId), name: newCardName.trim(), cardType: newCardType });
      setCardProducts((prev) => [...prev, created]);
      setForm((f) => ({ ...f, cardProductId: String(created.id) }));
      setNewCardMode(false);
      setNewCardName('');
      toast.success(`Tarjeta "${created.name}" creada`);
    } catch {
      toast.error('Error al crear la tarjeta');
    } finally {
      setCreatingCard(false);
    }
  };

  const set =
    (field: keyof FormState) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) =>
      setForm((f) => ({ ...f, [field]: e.target.value }));

  const toggleBrand = (id: number) =>
    setForm((f) => ({
      ...f,
      brandIds: f.brandIds.includes(id)
        ? f.brandIds.filter((b) => b !== id)
        : [...f.brandIds, id],
    }));

  const validate = (): boolean => {
    const e: Errors = {};
    if (!isEdit && !form.bankId)
      e.bankId = 'Selecciona un banco';
    if (!isEdit && form.brandIds.length === 0)
      e.brandIds = 'Selecciona al menos una bencinera';
    const val = parseFloat(form.discountValue);
    if (isNaN(val) || val < 10 || val > 500)
      e.discountValue = 'El descuento debe ser entre $10 y $500';
    if (!form.startDate) e.startDate = 'Fecha de inicio requerida';
    if (!form.endDate)
      e.endDate = 'Fecha de término requerida';
    else if (form.endDate < form.startDate)
      e.endDate = 'Debe ser posterior a la fecha de inicio';
    else if (form.endDate < today)
      e.endDate = 'La fecha de término ya venció';
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
          endDate: form.endDate,
        });
        toast.success('Descuento actualizado');
      } else {
        // Create one record per selected brand in parallel
        const base: Omit<DiscountCreateRequest, 'brandId'> = {
          cardProductId: form.cardProductId ? Number(form.cardProductId) : undefined,
          dayOfWeek: form.dayOfWeek ? Number(form.dayOfWeek) : undefined,
          discountType: form.discountType,
          discountValue: parseFloat(form.discountValue),
          maxCap: form.maxCap ? parseFloat(form.maxCap) : undefined,
          description: form.description || undefined,
          startDate: form.startDate,
          endDate: form.endDate,
        };
        await Promise.all(
          form.brandIds.map((brandId) => discountsApi.create({ ...base, brandId }))
        );
        toast.success(
          form.brandIds.length === 1
            ? 'Descuento creado exitosamente'
            : `${form.brandIds.length} descuentos creados exitosamente`
        );
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

  const selectedCard = cardProducts.find((c) => String(c.id) === form.cardProductId);
  const selectedDay  = DIAS_SEMANA.find((d) => String(d.value) === form.dayOfWeek);
  const selectedBrandNames = brands
    .filter((b) => form.brandIds.includes(b.id))
    .map((b) => b.name)
    .join(', ');

  const preview =
    form.discountValue && (isEdit ? true : form.brandIds.length > 0)
      ? `Descuento de ${
          form.discountType === 'FIXED_PER_LITER'
            ? `$${form.discountValue} por litro`
            : form.discountType === 'FIXED_AMOUNT'
            ? `$${form.discountValue} fijo`
            : `${form.discountValue}%`
        } en ${isEdit ? brands.find((b) => b.id === discount!.brandId)?.name ?? '' : selectedBrandNames}${
          selectedCard ? ` con tarjeta ${selectedCard.name}` : ''
        }${selectedDay ? `, los ${selectedDay.label}` : ''}`
      : null;

  return (
    <Modal
      title={isEdit ? 'Editar Descuento' : 'Crear Descuento'}
      onClose={onClose}
      width="max-w-xl"
    >
      <form onSubmit={handleSubmit} className="space-y-4">

        {/* ── Bank ── */}
        <div>
          <div className="flex items-center justify-between mb-1">
            <label className="text-sm font-medium text-gray-700">
              Banco {!isEdit && <span className="text-red-500">*</span>}
            </label>
            {!isEdit && !newBankMode && (
              <button
                type="button"
                onClick={() => setNewBankMode(true)}
                className="flex items-center gap-1 text-xs text-blue-600 hover:text-blue-700"
              >
                <Plus size={11} /> Nuevo banco
              </button>
            )}
          </div>

          {newBankMode ? (
            <div className="border border-blue-200 rounded-lg p-3 bg-blue-50 space-y-2">
              <div className="grid grid-cols-2 gap-2">
                <input
                  type="text"
                  placeholder="Nombre del banco"
                  value={newBankName}
                  onChange={(e) => setNewBankName(e.target.value)}
                  className="px-3 py-1.5 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <input
                  type="text"
                  placeholder="Código (ej. BCI)"
                  value={newBankCode}
                  onChange={(e) => setNewBankCode(e.target.value)}
                  maxLength={20}
                  className="px-3 py-1.5 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 uppercase"
                />
              </div>
              <div className="flex gap-2">
                <button
                  type="button"
                  onClick={handleCreateBank}
                  disabled={creatingBank || !newBankName.trim() || !newBankCode.trim()}
                  className="flex items-center gap-1 px-3 py-1.5 text-xs font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 disabled:bg-blue-300 transition-colors"
                >
                  {creatingBank ? <Loader2 size={11} className="animate-spin" /> : <Plus size={11} />}
                  Crear banco
                </button>
                <button
                  type="button"
                  onClick={() => { setNewBankMode(false); setNewBankName(''); setNewBankCode(''); }}
                  className="flex items-center gap-1 px-3 py-1.5 text-xs text-gray-600 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  <X size={11} /> Cancelar
                </button>
              </div>
            </div>
          ) : (
            <>
              <select
                value={form.bankId}
                onChange={set('bankId')}
                disabled={isEdit}
                className={`w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-500 ${errors.bankId ? 'border-red-400' : 'border-gray-300'}`}
              >
                <option value="">Seleccionar banco</option>
                {banks.map((b) => (
                  <option key={b.id} value={String(b.id)}>{b.name}</option>
                ))}
              </select>
              {errors.bankId && <p className="text-red-500 text-xs mt-1">{errors.bankId}</p>}
            </>
          )}
        </div>

        {/* ── Card product ── */}
        <div>
          <div className="flex items-center justify-between mb-1">
            <label className="text-sm font-medium text-gray-700">Tarjeta</label>
            {!newCardMode && form.bankId && (
              <button
                type="button"
                onClick={() => setNewCardMode(true)}
                className="flex items-center gap-1 text-xs text-blue-600 hover:text-blue-700"
              >
                <Plus size={11} /> Nueva tarjeta
              </button>
            )}
          </div>

          {newCardMode ? (
            <div className="border border-blue-200 rounded-lg p-3 bg-blue-50 space-y-2">
              <div className="grid grid-cols-2 gap-2">
                <input
                  type="text"
                  placeholder="Nombre de la tarjeta"
                  value={newCardName}
                  onChange={(e) => setNewCardName(e.target.value)}
                  className="px-3 py-1.5 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <select
                  value={newCardType}
                  onChange={(e) => setNewCardType(e.target.value)}
                  className="px-3 py-1.5 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="CREDIT">Crédito</option>
                  <option value="DEBIT">Débito</option>
                  <option value="PREPAID">Prepago</option>
                  <option value="APP">App</option>
                </select>
              </div>
              <div className="flex gap-2">
                <button
                  type="button"
                  onClick={handleCreateCard}
                  disabled={creatingCard || !newCardName.trim()}
                  className="flex items-center gap-1 px-3 py-1.5 text-xs font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 disabled:bg-blue-300 transition-colors"
                >
                  {creatingCard ? <Loader2 size={11} className="animate-spin" /> : <Plus size={11} />}
                  Crear tarjeta
                </button>
                <button
                  type="button"
                  onClick={() => { setNewCardMode(false); setNewCardName(''); }}
                  className="flex items-center gap-1 px-3 py-1.5 text-xs text-gray-600 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  <X size={11} /> Cancelar
                </button>
              </div>
            </div>
          ) : (
            <select
              value={form.cardProductId}
              onChange={set('cardProductId')}
              disabled={!form.bankId}
              className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-400"
            >
              <option value="">{form.bankId ? 'Todas las tarjetas' : 'Selecciona banco primero'}</option>
              {cardProducts.map((c) => (
                <option key={c.id} value={String(c.id)}>{c.name}</option>
              ))}
            </select>
          )}
        </div>

        {/* ── Bencineras (create: multi-select, edit: read-only) ── */}
        {(() => {
          const RECOMMENDED = ['copec', 'aramco', 'shell'];
          const recommended = brands.filter((b) => RECOMMENDED.includes(b.name.toLowerCase()));
          const others      = brands.filter((b) => !RECOMMENDED.includes(b.name.toLowerCase()));
          return (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Bencineras <span className="text-red-500">*</span>
                {!isEdit && (
                  <span className="ml-1.5 text-xs font-normal text-gray-400">— selecciona una o más</span>
                )}
              </label>

              {isEdit ? (
                <div className="px-3 py-2 text-sm bg-gray-50 border border-gray-200 rounded-lg text-gray-700">
                  {brands.find((b) => b.id === discount!.brandId)?.name ?? '—'}
                </div>
              ) : (
                <div className={`border rounded-lg p-3 space-y-3 ${errors.brandIds ? 'border-red-400' : 'border-gray-200'}`}>

                  {/* Recomendadas siempre visibles */}
                  <div className="flex flex-wrap gap-2">
                    {recommended.map((b) => {
                      const active = form.brandIds.includes(b.id);
                      return (
                        <button
                          key={b.id}
                          type="button"
                          onClick={() => toggleBrand(b.id)}
                          className={`px-4 py-1.5 rounded-full text-sm font-medium border transition-colors ${
                            active
                              ? 'bg-blue-600 text-white border-blue-600'
                              : 'bg-white text-gray-700 border-gray-300 hover:border-blue-400 hover:text-blue-600'
                          }`}
                        >
                          {b.name}
                        </button>
                      );
                    })}
                  </div>

                  {/* Otras bencineras (colapsable) */}
                  {others.length > 0 && (
                    <>
                      <button
                        type="button"
                        onClick={() => setShowMoreBrands((v) => !v)}
                        className="flex items-center gap-1 text-xs text-blue-600 hover:text-blue-700"
                      >
                        <Plus size={11} className={`transition-transform ${showMoreBrands ? 'rotate-45' : ''}`} />
                        {showMoreBrands ? 'Ocultar otras bencineras' : 'Agregar otra bencinera'}
                        {others.some((b) => form.brandIds.includes(b.id)) && (
                          <span className="ml-1 px-1.5 py-0.5 text-[10px] bg-blue-100 text-blue-700 rounded-full font-medium">
                            {others.filter((b) => form.brandIds.includes(b.id)).length} sel.
                          </span>
                        )}
                      </button>

                      {showMoreBrands && (
                        <div className="grid grid-cols-2 gap-2 pt-1 border-t border-gray-100">
                          {others.map((b) => (
                            <label key={b.id} className="flex items-center gap-2 cursor-pointer group">
                              <input
                                type="checkbox"
                                checked={form.brandIds.includes(b.id)}
                                onChange={() => toggleBrand(b.id)}
                                className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                              />
                              <span className="text-sm text-gray-700 group-hover:text-gray-900">{b.name}</span>
                            </label>
                          ))}
                        </div>
                      )}
                    </>
                  )}
                </div>
              )}
              {errors.brandIds && <p className="text-red-500 text-xs mt-1">{errors.brandIds}</p>}
            </div>
          );
        })()}

        {/* ── Discount type + value ── */}
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Tipo de descuento</label>
            <select
              value={form.discountType}
              onChange={set('discountType')}
              className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              {Object.entries(TYPE_LABELS).map(([k, v]) => (
                <option key={k} value={k}>{v}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Valor <span className="text-red-500">*</span>
            </label>
            <div className="relative">
              {form.discountType !== 'PERCENTAGE' && (
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-sm">$</span>
              )}
              <input
                type="number"
                min={10}
                max={500}
                step="any"
                value={form.discountValue}
                onChange={set('discountValue')}
                className={`w-full py-2 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  form.discountType !== 'PERCENTAGE' ? 'pl-6 pr-8' : 'px-3'
                } ${errors.discountValue ? 'border-red-400' : 'border-gray-300'}`}
                placeholder={form.discountType === 'PERCENTAGE' ? '5' : '50'}
              />
              {form.discountType === 'PERCENTAGE' && (
                <span className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 text-sm">%</span>
              )}
              {form.discountType === 'FIXED_PER_LITER' && (
                <span className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 text-xs">/L</span>
              )}
            </div>
            {errors.discountValue && <p className="text-red-500 text-xs mt-1">{errors.discountValue}</p>}
          </div>
        </div>

        {/* ── Day + MaxCap ── */}
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Día válido</label>
            <select
              value={form.dayOfWeek}
              onChange={set('dayOfWeek')}
              className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Todos los días</option>
              {DIAS_SEMANA.map((d) => (
                <option key={d.value} value={String(d.value)}>{d.label}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Tope máximo <span className="text-gray-400 text-xs">(opcional)</span>
            </label>
            <div className="relative">
              <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-sm">$</span>
              <input
                type="number"
                min={0}
                step="any"
                value={form.maxCap}
                onChange={set('maxCap')}
                className="w-full pl-6 pr-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="10000"
              />
            </div>
          </div>
        </div>

        {/* ── Dates ── */}
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Fecha inicio <span className="text-red-500">*</span>
            </label>
            <input
              type="date"
              value={form.startDate}
              max={today}
              onChange={set('startDate')}
              className={`w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.startDate ? 'border-red-400' : 'border-gray-300'}`}
            />
            {errors.startDate && <p className="text-red-500 text-xs mt-1">{errors.startDate}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Fecha fin <span className="text-red-500">*</span>
            </label>
            <input
              type="date"
              value={form.endDate}
              min={form.startDate || today}
              onChange={set('endDate')}
              className={`w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.endDate ? 'border-red-400' : 'border-gray-300'}`}
            />
            {errors.endDate && <p className="text-red-500 text-xs mt-1">{errors.endDate}</p>}
          </div>
        </div>

        {/* ── Description ── */}
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

        {/* ── Preview ── */}
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
            {saving ? 'Guardando...' : isEdit ? 'Guardar' : `Crear${form.brandIds.length > 1 ? ` (${form.brandIds.length})` : ''}`}
          </button>
        </div>
      </form>
    </Modal>
  );
}

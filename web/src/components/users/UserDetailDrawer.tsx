import { useEffect, useState } from 'react';
import { X, Car, Fuel, User, Loader2, ShoppingBag, Tag } from 'lucide-react';
import { usersApi } from '../../api/users';
import type { UserResponse, VehicleResponse, TransactionResponse, DiscountResponse } from '../../types';
import { fmtClp, fmtRut, fmtDate, fmtDateTime, fullName } from '../../lib/formatters';

type Tab = 'datos' | 'vehiculos' | 'cargas' | 'descuentos';

function Field({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <div>
      <p className="text-xs text-gray-400 mb-0.5">{label}</p>
      <p className="text-sm text-gray-900 font-medium">{value || '—'}</p>
    </div>
  );
}

// ── Tabs ──────────────────────────────────────────────────────────────────────

function DatosTab({ user }: { user: UserResponse }) {
  const name = fullName(user);

  return (
    <div className="space-y-5">
      <div className="grid grid-cols-2 gap-4">
        <Field label="Nombre completo" value={name} />
        <Field label="Email" value={user.email} />
        <Field label="RUT" value={fmtRut(user.rut)} />
        <Field label="Fecha de nacimiento" value={fmtDate(user.birthDate)} />
        <Field label="Rol" value={
          <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
            user.roleName === 'ADMIN' ? 'bg-purple-100 text-purple-700' : 'bg-gray-100 text-gray-600'
          }`}>{user.roleName}</span>
        } />
        <Field label="Estado" value={
          <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded text-xs font-medium ${
            user.active ? 'bg-emerald-100 text-emerald-700' : 'bg-gray-100 text-gray-500'
          }`}>
            <span className={`w-1.5 h-1.5 rounded-full ${user.active ? 'bg-emerald-500' : 'bg-gray-400'}`} />
            {user.active ? 'Activo' : 'Inactivo'}
          </span>
        } />
        <Field label="Fecha de registro" value={fmtDate(user.createdAt)} />
        <Field label="Última actualización" value={fmtDate(user.updatedAt)} />
      </div>

      <div className="border-t border-gray-100 pt-4 grid grid-cols-3 gap-3">
        {[
          { label: 'Vehículos', value: user.vehicleCount },
          { label: 'Cargas totales', value: user.totalTransactions },
          { label: 'Ahorro total', value: fmtClp(user.totalSavings) },
        ].map(({ label, value }) => (
          <div key={label} className="bg-gray-50 rounded-lg px-3 py-3 text-center">
            <p className="text-xs text-gray-400 mb-1">{label}</p>
            <p className="text-lg font-bold text-gray-900">{value}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

function VehiculosTab({ userId }: { userId: string }) {
  const [vehicles, setVehicles] = useState<VehicleResponse[]>([]);
  const [loading, setLoading]   = useState(true);

  useEffect(() => {
    usersApi.vehicles(userId)
      .then(setVehicles)
      .catch(() => setVehicles([]))
      .finally(() => setLoading(false));
  }, [userId]);

  if (loading) return (
    <div className="flex items-center justify-center py-16 text-gray-400">
      <Loader2 size={20} className="animate-spin mr-2" /> Cargando vehículos...
    </div>
  );

  if (vehicles.length === 0) return (
    <div className="flex flex-col items-center justify-center py-16 text-gray-400">
      <Car size={36} className="mb-3 text-gray-300" />
      <p className="text-sm">Sin vehículos registrados</p>
    </div>
  );

  return (
    <div className="space-y-3">
      {vehicles.map((v) => (
        <div key={v.id} className="border border-gray-200 rounded-lg px-4 py-3 flex items-center gap-4">
          <div className="w-9 h-9 rounded-full bg-blue-50 flex items-center justify-center flex-shrink-0">
            <Car size={16} className="text-blue-500" />
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium text-gray-900">
              {v.brandName} {v.modelName}
              {v.year && <span className="text-gray-400 font-normal ml-1">({v.year})</span>}
            </p>
            <p className="text-xs text-gray-500 mt-0.5">
              {v.fuelTypeName}
              {v.licensePlate && <span className="ml-2 font-mono uppercase">{v.licensePlate}</span>}
            </p>
          </div>
        </div>
      ))}
    </div>
  );
}

function CargasTab({ userId }: { userId: string }) {
  const [txs, setTxs]       = useState<TransactionResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    usersApi.transactions(userId, 0, 50)
      .then((p) => setTxs(p.content))
      .catch(() => setTxs([]))
      .finally(() => setLoading(false));
  }, [userId]);

  if (loading) return (
    <div className="flex items-center justify-center py-16 text-gray-400">
      <Loader2 size={20} className="animate-spin mr-2" /> Cargando cargas...
    </div>
  );

  if (txs.length === 0) return (
    <div className="flex flex-col items-center justify-center py-16 text-gray-400">
      <ShoppingBag size={36} className="mb-3 text-gray-300" />
      <p className="text-sm">Sin cargas registradas</p>
    </div>
  );

  return (
    <div className="space-y-2">
      {txs.map((t) => (
        <div key={t.id} className="border border-gray-200 rounded-lg px-4 py-3">
          <div className="flex items-start justify-between gap-2">
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-gray-900 truncate">{t.stationName}</p>
              <p className="text-xs text-gray-500 mt-0.5">
                {t.fuelTypeName} · {Number(t.liters).toFixed(2)} L · {fmtClp(Number(t.unitPrice))}/L
              </p>
              {t.cardProductName && (
                <p className="text-xs text-gray-400 mt-0.5">{t.cardProductName}</p>
              )}
            </div>
            <div className="text-right flex-shrink-0">
              <p className="text-sm font-semibold text-gray-900">{fmtClp(Number(t.finalAmount))}</p>
              {Number(t.discountAmount) > 0 && (
                <p className="text-xs text-emerald-600">-{fmtClp(Number(t.discountAmount))}</p>
              )}
              <p className="text-xs text-gray-400 mt-1">{fmtDateTime(t.transactionDate)}</p>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}

function DescuentosTab({ userId }: { userId: string }) {
  const [discounts, setDiscounts] = useState<DiscountResponse[]>([]);
  const [loading, setLoading]     = useState(true);

  useEffect(() => {
    usersApi.discounts(userId)
      .then(setDiscounts)
      .catch(() => setDiscounts([]))
      .finally(() => setLoading(false));
  }, [userId]);

  if (loading) return (
    <div className="flex items-center justify-center py-16 text-gray-400">
      <Loader2 size={20} className="animate-spin mr-2" /> Cargando descuentos...
    </div>
  );

  if (discounts.length === 0) return (
    <div className="flex flex-col items-center justify-center py-16 text-gray-400">
      <Tag size={36} className="mb-3 text-gray-300" />
      <p className="text-sm">Sin descuentos seleccionados</p>
    </div>
  );

  const DAY_LABELS: Record<number, string> = {
    1: 'Lun', 2: 'Mar', 3: 'Mié', 4: 'Jue', 5: 'Vie', 6: 'Sáb', 7: 'Dom',
  };

  return (
    <div className="space-y-3">
      {discounts.map((d) => (
        <div key={d.id} className="border border-gray-200 rounded-lg px-4 py-3 flex items-start gap-3">
          <div className="w-9 h-9 rounded-full bg-emerald-50 flex items-center justify-center flex-shrink-0 mt-0.5">
            <Tag size={15} className="text-emerald-500" />
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium text-gray-900">{d.brandName}</p>
            <p className="text-xs text-gray-500 mt-0.5">
              {d.bankName && <span>{d.bankName} · </span>}
              {d.cardProductName && <span>{d.cardProductName}</span>}
              {d.fuelTypeName && <span> · {d.fuelTypeName}</span>}
            </p>
            {d.description && (
              <p className="text-xs text-gray-400 mt-0.5 truncate">{d.description}</p>
            )}
          </div>
          <div className="text-right flex-shrink-0">
            <p className="text-sm font-semibold text-emerald-600">
              {d.discountType === 'PERCENTAGE'
                ? `${d.discountValue}%`
                : d.discountType === 'FIXED_PER_LITER'
                ? `$${d.discountValue}/L`
                : fmtClp(d.discountValue)}
            </p>
            {d.dayOfWeek && (
              <p className="text-xs text-gray-400">{DAY_LABELS[d.dayOfWeek] ?? `Día ${d.dayOfWeek}`}</p>
            )}
          </div>
        </div>
      ))}
    </div>
  );
}

// ── Main drawer ───────────────────────────────────────────────────────────────

interface Props {
  user: UserResponse;
  onClose: () => void;
}

export function UserDetailDrawer({ user, onClose }: Props) {
  const [tab, setTab] = useState<Tab>('datos');
  const name = fullName(user);

  const tabs: { id: Tab; label: string; Icon: typeof User }[] = [
    { id: 'datos',      label: 'Datos',      Icon: User },
    { id: 'vehiculos',  label: 'Vehículos',  Icon: Car  },
    { id: 'cargas',     label: 'Cargas',     Icon: Fuel },
    { id: 'descuentos', label: 'Descuentos', Icon: Tag  },
  ];

  return (
    <>
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-black/30 z-40 transition-opacity"
        onClick={onClose}
      />

      {/* Drawer */}
      <div className="fixed right-0 top-0 h-full w-full max-w-lg bg-white shadow-2xl z-50 flex flex-col">
        {/* Header */}
        <div className="px-6 py-4 border-b border-gray-200 flex items-start justify-between gap-4">
          <div className="min-w-0">
            <h2 className="text-lg font-semibold text-gray-900 truncate">{name}</h2>
            <p className="text-sm text-gray-500 truncate">{user.email}</p>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-lg transition-colors flex-shrink-0"
          >
            <X size={18} />
          </button>
        </div>

        {/* Tab bar */}
        <div className="flex border-b border-gray-200">
          {tabs.map(({ id, label, Icon }) => (
            <button
              key={id}
              onClick={() => setTab(id)}
              className={`flex-1 flex items-center justify-center gap-1.5 py-3 text-sm font-medium transition-colors border-b-2 ${
                tab === id
                  ? 'border-blue-600 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700'
              }`}
            >
              <Icon size={14} />
              {label}
            </button>
          ))}
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-6">
          {tab === 'datos'      && <DatosTab      user={user} />}
          {tab === 'vehiculos'  && <VehiculosTab  userId={user.id} />}
          {tab === 'cargas'     && <CargasTab     userId={user.id} />}
          {tab === 'descuentos' && <DescuentosTab userId={user.id} />}
        </div>
      </div>
    </>
  );
}

import { useCallback, useEffect, useMemo, useState } from 'react';
import { Users, Tag, MapPin, Fuel, TrendingUp, TrendingDown, Minus, Search, Calendar, ChevronDown } from 'lucide-react';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  LineChart, Line, PieChart, Pie, Cell, Legend,
} from 'recharts';
import { analyticsApi } from '../../api/analytics';
import { catalogApi } from '../../api/catalog';
import type { AnalyticsResponse, BrandDetail } from '../../types';
import { fmtClp, DATE_PRESETS, analyticsDateParams, type DatePreset } from '../../lib/formatters';

const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#ec4899', '#84cc16'];

function fmtLitros(n: number) {
  return n >= 1000
    ? `${(n / 1000).toFixed(1).replace('.', ',')} mil L`
    : `${n.toFixed(1).replace('.', ',')} L`;
}

// ── sub-components ────────────────────────────────────────────────────────────

interface KPICardProps {
  title: string; value: string | number; Icon: typeof Users;
  color: string; loading?: boolean;
}
function KPICard({ title, value, Icon, color, loading }: KPICardProps) {
  return (
    <div className="bg-white rounded-xl border border-gray-200 p-5 shadow-sm">
      <div className="flex items-center justify-between mb-3">
        <p className="text-sm font-medium text-gray-500">{title}</p>
        <div className={`p-2 rounded-lg ${color}`}><Icon size={18} className="text-white" /></div>
      </div>
      {loading
        ? <div className="h-8 w-24 bg-gray-200 rounded animate-pulse" />
        : <p className="text-2xl font-bold text-gray-900">{value}</p>}
    </div>
  );
}

function ChartCard({ title, subtitle, loading, children, fullWidth }: {
  title: string; subtitle?: string; loading: boolean;
  children: React.ReactNode; fullWidth?: boolean;
}) {
  return (
    <div className={`bg-white rounded-xl border border-gray-200 p-5 shadow-sm${fullWidth ? ' col-span-full' : ''}`}>
      <div className="mb-4">
        <h3 className="font-semibold text-gray-900">{title}</h3>
        {subtitle && <p className="text-xs text-gray-400 mt-0.5">{subtitle}</p>}
      </div>
      {loading ? <div className="h-64 bg-gray-100 rounded animate-pulse" /> : children}
    </div>
  );
}

function EmptyState({ message }: { message: string }) {
  return <div className="h-64 flex items-center justify-center text-gray-400 text-sm">{message}</div>;
}

function TrendBadge({ value }: { value: number }) {
  if (value === 0) return <span className="text-gray-400 text-xs flex items-center gap-0.5"><Minus size={12} />—</span>;
  const positive = value > 0;
  return (
    <span className={`inline-flex items-center gap-0.5 text-xs font-medium ${positive ? 'text-emerald-600' : 'text-red-500'}`}>
      {positive ? <TrendingUp size={12} /> : <TrendingDown size={12} />}
      {positive ? '+' : ''}{value.toFixed(1)}%
    </span>
  );
}

// ── Brand detail table ────────────────────────────────────────────────────────

function BrandTable({ rows, loading }: { rows: BrandDetail[]; loading: boolean }) {
  const [search, setSearch] = useState('');

  const filtered = useMemo(
    () => rows.filter((r) => r.brand.toLowerCase().includes(search.toLowerCase())),
    [rows, search]
  );

  return (
    <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
      <div className="px-5 py-4 border-b border-gray-100 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h3 className="font-semibold text-gray-900">Detalle por Bencinera</h3>
          <p className="text-xs text-gray-400 mt-0.5">Métricas completas de todas las redes</p>
        </div>
        <div className="relative w-full sm:w-64">
          <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Buscar bencinera..."
            className="w-full pl-8 pr-3 py-2 text-sm border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-gray-100 bg-gray-50">
              <th className="text-left px-5 py-3 font-medium text-gray-500">Bencinera</th>
              <th className="text-right px-5 py-3 font-medium text-gray-500">Recargas</th>
              <th className="text-right px-5 py-3 font-medium text-gray-500">Usuarios</th>
              <th className="text-right px-5 py-3 font-medium text-gray-500">Ahorro Generado</th>
              <th className="text-right px-5 py-3 font-medium text-gray-500">Litros</th>
              <th className="text-right px-5 py-3 font-medium text-gray-500">Tendencia</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              Array.from({ length: 6 }).map((_, i) => (
                <tr key={i} className="border-b border-gray-100">
                  {Array.from({ length: 6 }).map((__, j) => (
                    <td key={j} className="px-5 py-3">
                      <div className="h-4 bg-gray-100 rounded animate-pulse" />
                    </td>
                  ))}
                </tr>
              ))
            ) : filtered.length === 0 ? (
              <tr>
                <td colSpan={6} className="text-center py-12 text-gray-400">
                  {search ? `Sin resultados para "${search}"` : 'Sin datos'}
                </td>
              </tr>
            ) : (
              filtered.map((row) => (
                <tr key={row.brand} className="border-b border-gray-100 hover:bg-gray-50 transition-colors">
                  <td className="px-5 py-3 font-medium text-gray-900">{row.brand}</td>
                  <td className="px-5 py-3 text-right tabular-nums text-gray-700">
                    {row.recargas.toLocaleString('es-CL')}
                  </td>
                  <td className="px-5 py-3 text-right tabular-nums text-gray-700">
                    {row.uniqueUsers.toLocaleString('es-CL')}
                  </td>
                  <td className="px-5 py-3 text-right tabular-nums text-gray-700">
                    {fmtClp(row.ahorroTotal)}
                  </td>
                  <td className="px-5 py-3 text-right tabular-nums text-gray-700">
                    {fmtLitros(row.litrosTotal)}
                  </td>
                  <td className="px-5 py-3 text-right">
                    <TrendBadge value={row.tendencia} />
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {!loading && filtered.length > 0 && (
        <div className="px-5 py-3 border-t border-gray-100 text-xs text-gray-400">
          {search ? `${filtered.length} de ${rows.length} redes` : `${rows.length} redes en total`}
        </div>
      )}
    </div>
  );
}

// ── main component ────────────────────────────────────────────────────────────

export function AnalyticsDashboard() {
  const [data, setData]       = useState<AnalyticsResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState(false);

  // Filters
  const [datePreset, setDatePreset] = useState<DatePreset>('all');
  const [customFrom, setCustomFrom] = useState('');
  const [customTo, setCustomTo]     = useState('');
  const [regionId, setRegionId]     = useState<number | undefined>();
  const [regions, setRegions]       = useState<{ id: number; name: string }[]>([]);

  useEffect(() => {
    catalogApi.regions().then(setRegions).catch(() => {});
  }, []);

  const fetchData = useCallback(() => {
    setLoading(true);
    setError(false);
    const dates = analyticsDateParams(datePreset, customFrom, customTo);
    analyticsApi.get({ ...dates, regionId })
      .then(setData)
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, [datePreset, customFrom, customTo, regionId]);

  useEffect(() => { fetchData(); }, [fetchData]);

  if (error) {
    return (
      <div className="p-6 flex items-center justify-center h-64 text-gray-500">
        Error al cargar las estadísticas.
      </div>
    );
  }

  const transactionsByHour = Array.from({ length: 24 }, (_, h) => {
    const found = data?.transactionsByHour.find((t) => t.hour === h);
    return { hour: `${String(h).padStart(2, '0')}:00`, count: found?.count ?? 0 };
  });

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Estadísticas</h1>
          <p className="text-sm text-gray-500 mt-0.5">Métricas reales de la plataforma</p>
        </div>
      </div>

      {/* ── Filters ── */}
      <div className="bg-white rounded-xl border border-gray-200 shadow-sm p-4 space-y-3">
        <div className="flex items-center gap-4 flex-wrap">
          {/* Date presets */}
          <div className="flex items-center gap-2 flex-wrap">
            <span className="flex items-center gap-1 text-xs font-medium text-gray-500 shrink-0">
              <Calendar size={13} className="text-gray-400" />
              Período:
            </span>
            {DATE_PRESETS.map(({ key, label }) => (
              <button
                key={key}
                onClick={() => setDatePreset(key)}
                className={`px-3 py-1.5 text-xs rounded-lg border transition-colors ${
                  datePreset === key
                    ? 'bg-blue-600 text-white border-blue-600'
                    : 'text-gray-600 border-gray-200 hover:bg-gray-50'
                }`}
              >
                {label}
              </button>
            ))}
          </div>

          {/* Region filter */}
          <div className="flex items-center gap-2 ml-auto">
            <span className="flex items-center gap-1 text-xs font-medium text-gray-500 shrink-0">
              <MapPin size={13} className="text-gray-400" />
              Región:
            </span>
            <div className="relative">
              <select
                value={regionId ?? ''}
                onChange={(e) => setRegionId(e.target.value ? Number(e.target.value) : undefined)}
                className="text-sm border border-gray-300 rounded-lg pl-3 pr-8 py-1.5 appearance-none focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
              >
                <option value="">Todas las regiones</option>
                {regions.map((r) => (
                  <option key={r.id} value={r.id}>{r.name}</option>
                ))}
              </select>
              <ChevronDown size={13} className="absolute right-2.5 top-1/2 -translate-y-1/2 text-gray-400 pointer-events-none" />
            </div>
          </div>
        </div>

        {/* Custom date inputs */}
        {datePreset === 'custom' && (
          <div className="flex items-center gap-3 pt-3 border-t border-gray-100 flex-wrap">
            <span className="text-xs text-gray-500">Desde:</span>
            <input
              type="date"
              value={customFrom}
              max={new Date().toISOString().slice(0, 10)}
              onChange={(e) => setCustomFrom(e.target.value)}
              className="text-sm border border-gray-300 rounded-lg px-3 py-1.5 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <span className="text-xs text-gray-500">Hasta:</span>
            <input
              type="date"
              value={customTo}
              min={customFrom || undefined}
              max={new Date().toISOString().slice(0, 10)}
              onChange={(e) => setCustomTo(e.target.value)}
              className="text-sm border border-gray-300 rounded-lg px-3 py-1.5 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            {(customFrom || customTo) && (
              <button
                onClick={() => { setCustomFrom(''); setCustomTo(''); }}
                className="text-xs text-gray-400 hover:text-gray-600 underline"
              >
                Limpiar
              </button>
            )}
          </div>
        )}
      </div>

      {/* KPIs */}
      <div className="grid grid-cols-2 xl:grid-cols-3 gap-4">
        <KPICard title="Descuentos activos"   value={loading ? '—' : (data?.activeDiscounts ?? 0).toLocaleString('es-CL')} Icon={Tag}    color="bg-emerald-600" loading={loading} />
        <KPICard title="Bencineras activas"   value={loading ? '—' : (data?.totalStations ?? 0).toLocaleString('es-CL')}  Icon={MapPin} color="bg-amber-500"   loading={loading} />
        <KPICard title="Usuarios registrados" value={loading ? '—' : (data?.activeUsers ?? 0).toLocaleString('es-CL')}    Icon={Fuel}   color="bg-purple-600"  loading={loading} />
      </div>

      {/* Row 1 — Usuarios (full width) */}
      <div className="grid grid-cols-1">
        <ChartCard title="Usuarios Registrados por Mes" subtitle="Evolución histórica de registros" loading={loading} fullWidth>
          {(data?.usersByMonth.length ?? 0) === 0
            ? <EmptyState message="Sin datos de registro" />
            : (
              <ResponsiveContainer width="100%" height={220}>
                <LineChart data={data!.usersByMonth} margin={{ top: 4, right: 24, left: -10, bottom: 0 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#f3f4f6" />
                  <XAxis dataKey="month" tick={{ fontSize: 12 }} />
                  <YAxis tick={{ fontSize: 12 }} allowDecimals={false} />
                  <Tooltip formatter={(v) => [v, 'Registros']} />
                  <Line type="monotone" dataKey="count" stroke="#3b82f6" strokeWidth={2} dot={{ r: 4 }} name="Registros" />
                </LineChart>
              </ResponsiveContainer>
            )}
        </ChartCard>
      </div>

      {/* Row 2 — Bencineras más usadas + Distribución combustibles */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <ChartCard title="Bencineras Más Usadas" subtitle="Recargas registradas por marca" loading={loading}>
          {(data?.stationsByBrand.length ?? 0) === 0
            ? <EmptyState message="Sin recargas registradas aún" />
            : (
              <ResponsiveContainer width="100%" height={260}>
                <BarChart data={data!.stationsByBrand} layout="vertical" margin={{ top: 0, right: 20, left: 70, bottom: 0 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#f3f4f6" horizontal={false} />
                  <XAxis type="number" tick={{ fontSize: 11 }} />
                  <YAxis dataKey="name" type="category" tick={{ fontSize: 11 }} width={70} />
                  <Tooltip formatter={(v) => [v, 'Recargas']} />
                  <Bar dataKey="count" fill="#3b82f6" radius={[0, 4, 4, 0]} name="Recargas" />
                </BarChart>
              </ResponsiveContainer>
            )}
        </ChartCard>

        <ChartCard title="Distribución de Combustibles" subtitle="Recargas registradas por tipo" loading={loading}>
          {(data?.fuelDistribution.length ?? 0) === 0
            ? <EmptyState message="Sin recargas registradas aún" />
            : (
              <ResponsiveContainer width="100%" height={260}>
                <PieChart>
                  <Pie data={data!.fuelDistribution} cx="50%" cy="50%" outerRadius={90}
                    dataKey="count" nameKey="name"
                    label={({ name, percent }) => `${name} ${((percent ?? 0) * 100).toFixed(0)}%`}
                    labelLine={false}>
                    {data!.fuelDistribution.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                  </Pie>
                  <Tooltip formatter={(v) => [v, 'Recargas']} />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            )}
        </ChartCard>
      </div>

      {/* Row 3 — Descuentos más usados + Horarios pico */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <ChartCard title="Descuentos Más Usados" subtitle="Selecciones de descuento por usuario" loading={loading}>
          {(data?.topDiscountsByUsage.length ?? 0) === 0
            ? <EmptyState message="Sin datos de selección de descuentos" />
            : (
              <ResponsiveContainer width="100%" height={260}>
                <BarChart
                  data={data!.topDiscountsByUsage.map((d) => ({ label: `${d.brand} · ${d.bank}`, userCount: d.userCount }))}
                  margin={{ top: 0, right: 10, left: -10, bottom: 50 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#f3f4f6" />
                  <XAxis dataKey="label" tick={{ fontSize: 10 }} angle={-35} textAnchor="end" interval={0} />
                  <YAxis tick={{ fontSize: 11 }} allowDecimals={false} />
                  <Tooltip formatter={(v) => [v, 'Usuarios']} />
                  <Bar dataKey="userCount" fill="#10b981" radius={[4, 4, 0, 0]} name="Usuarios" />
                </BarChart>
              </ResponsiveContainer>
            )}
        </ChartCard>

        <ChartCard title="Horarios Pico de Uso" subtitle="Transacciones registradas por hora del día" loading={loading}>
          <ResponsiveContainer width="100%" height={260}>
            <BarChart data={transactionsByHour} margin={{ top: 0, right: 10, left: -10, bottom: 40 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f3f4f6" />
              <XAxis dataKey="hour" tick={{ fontSize: 9 }} angle={-45} textAnchor="end" interval={1} />
              <YAxis tick={{ fontSize: 11 }} allowDecimals={false} />
              <Tooltip formatter={(v) => [v, 'Transacciones']} />
              <Bar dataKey="count" fill="#f59e0b" radius={[4, 4, 0, 0]} name="Transacciones" />
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>
      </div>

      {/* Bottom — Detalle por Bencinera */}
      <BrandTable rows={data?.brandDetails ?? []} loading={loading} />
    </div>
  );
}

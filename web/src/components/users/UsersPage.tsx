import { useEffect, useMemo, useState } from 'react';
import {
  UserCircle, RefreshCw, Power, PowerOff, Car, Fuel,
  PiggyBank, Eye, Users, UserCheck, TrendingUp, Calendar, ArrowUpDown,
} from 'lucide-react';
import { toast } from 'sonner';
import { usersApi } from '../../api/users';
import type { UserResponse } from '../../types';
import { UserDetailDrawer } from './UserDetailDrawer';
import { SkeletonRow } from '../common/SkeletonRow';
import { fmtClp, fmtRut, fmtDate, fullName, DATE_PRESETS, presetToDates, type DatePreset } from '../../lib/formatters';

// ── component ─────────────────────────────────────────────────────────────────

export function UsersPage() {
  const [users, setUsers]         = useState<UserResponse[]>([]);
  const [totalUsers, setTotalUsers] = useState(0);
  const [loading, setLoading]     = useState(true);
  const [detailUser, setDetailUser] = useState<UserResponse | null>(null);
  const [toggling, setToggling]   = useState<string | null>(null);

  // Filters
  const [datePreset, setDatePreset] = useState<DatePreset>('all');
  const [customFrom, setCustomFrom] = useState('');
  const [customTo, setCustomTo]     = useState('');
  const [statusFilter, setStatusFilter] = useState<'all' | 'active' | 'inactive'>('active');
  const [roleFilter,   setRoleFilter]   = useState<'all' | 'ADMIN' | 'USER'>('all');
  const [sortBy, setSortBy] = useState<'name' | 'date-desc' | 'date-asc'>('name');

  // Pagination
  const PAGE_SIZE = 50;
  const [currentPage, setCurrentPage] = useState(0);


  const myEmail = (() => {
    try { return JSON.parse(localStorage.getItem('user') ?? '{}').email ?? ''; }
    catch { return ''; }
  })();

  const load = async () => {
    setLoading(true);
    try {
      const page = await usersApi.list(0, 1000);
      setUsers(page.content);
      setTotalUsers(page.totalElements);
    } catch {
      toast.error('Error al cargar los usuarios');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);
  useEffect(() => { setCurrentPage(0); }, [statusFilter, roleFilter, sortBy, datePreset, customFrom, customTo]);

  const handleToggleActive = async (u: UserResponse) => {
    setToggling(u.id);
    try {
      if (u.active) {
        await usersApi.deactivate(u.id);
        toast.success(`${u.firstName} desactivado`);
      } else {
        await usersApi.activate(u.id);
        toast.success(`${u.firstName} activado`);
      }
      setUsers((prev) => prev.map((x) => x.id === u.id ? { ...x, active: !u.active } : x));
    } catch {
      toast.error('Error al cambiar el estado del usuario');
    } finally {
      setToggling(null);
    }
  };

  // ── filtered set ────────────────────────────────────────────────────────────

  const filteredUsers = useMemo(() => {
    const [from, to] = presetToDates(datePreset, customFrom, customTo);
    const filtered = users.filter((u) => {
      if (statusFilter === 'active'   && !u.active) return false;
      if (statusFilter === 'inactive' &&  u.active) return false;
      if (roleFilter !== 'all' && u.roleName !== roleFilter) return false;
      if (!u.createdAt) return true;
      const d = new Date(u.createdAt);
      if (from && d < from) return false;
      if (to   && d > to)   return false;
      return true;
    });
    return filtered.sort((a, b) => {
      if (sortBy === 'name') {
        const na = `${a.lastName ?? ''} ${a.firstName ?? ''}`.trim().toLowerCase();
        const nb = `${b.lastName ?? ''} ${b.firstName ?? ''}`.trim().toLowerCase();
        return na.localeCompare(nb, 'es');
      }
      const ta = a.createdAt ? new Date(a.createdAt).getTime() : 0;
      const tb = b.createdAt ? new Date(b.createdAt).getTime() : 0;
      return sortBy === 'date-desc' ? tb - ta : ta - tb;
    });
  }, [users, datePreset, customFrom, customTo, statusFilter, roleFilter, sortBy]);

  // ── Pagination ──────────────────────────────────────────────────────────────

  const totalPages    = Math.max(1, Math.ceil(filteredUsers.length / PAGE_SIZE));
  const paginatedUsers = filteredUsers.slice(currentPage * PAGE_SIZE, (currentPage + 1) * PAGE_SIZE);

  // ── KPIs (from filtered set) ────────────────────────────────────────────────

  const activeCount       = filteredUsers.filter((u) => u.active).length;
  const inactiveCount     = filteredUsers.length - activeCount;
  const totalTransactions = filteredUsers.reduce((s, u) => s + (u.totalTransactions ?? 0), 0);
  const totalSavings      = filteredUsers.reduce((s, u) => s + (u.totalSavings ?? 0), 0);
  const avgTransactions   = filteredUsers.length ? totalTransactions / filteredUsers.length : 0;
  const avgSavings        = activeCount ? totalSavings / activeCount : 0;

  const isFiltered = datePreset !== 'all' || statusFilter !== 'all' || roleFilter !== 'all';

  return (
    <div className="p-6 space-y-5">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Usuarios</h1>
          <p className="text-sm text-gray-500 mt-0.5">
            {loading
              ? '...'
              : `${activeCount} activos · ${inactiveCount} inactivos${
                  isFiltered ? ` (${filteredUsers.length} de ${users.length} usuarios)` : ''
                }`}
          </p>
        </div>
        <button
          onClick={load}
          className="p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
          title="Refrescar"
        >
          <RefreshCw size={16} />
        </button>
      </div>

      {/* ── Filters ── */}
      <div className="bg-white rounded-xl border border-gray-200 shadow-sm p-4 space-y-3">
        <div className="flex items-center gap-4 flex-wrap">
          {/* Date preset buttons */}
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

          {/* Role filter */}
          <div className="flex items-center gap-1 border border-gray-200 rounded-lg p-0.5 bg-gray-50">
            {([
              { key: 'all',   label: 'Todos los roles' },
              { key: 'USER',  label: 'Usuario' },
              { key: 'ADMIN', label: 'Admin' },
            ] as const).map(({ key, label }) => (
              <button
                key={key}
                onClick={() => setRoleFilter(key)}
                className={`px-3 py-1.5 text-xs rounded-md transition-colors ${
                  roleFilter === key
                    ? 'bg-white text-gray-700 shadow-sm font-medium'
                    : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                {label}
              </button>
            ))}
          </div>

          {/* Status filter */}
          <div className="flex items-center gap-1 ml-auto border border-gray-200 rounded-lg p-0.5 bg-gray-50">
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
                    ? key === 'inactive'
                      ? 'bg-white text-gray-700 shadow-sm font-medium'
                      : 'bg-white text-gray-700 shadow-sm font-medium'
                    : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                {label}
                {!loading && key !== 'all' && (
                  <span className={`ml-1.5 px-1.5 py-0.5 rounded-full text-[10px] font-medium ${
                    key === 'active'
                      ? statusFilter === 'active'
                        ? 'bg-emerald-100 text-emerald-700'
                        : 'bg-gray-100 text-gray-500'
                      : statusFilter === 'inactive'
                        ? 'bg-red-100 text-red-600'
                        : 'bg-gray-100 text-gray-500'
                  }`}>
                    {key === 'active'
                      ? users.filter(u => u.active).length
                      : users.filter(u => !u.active).length}
                  </span>
                )}
              </button>
            ))}
          </div>
        </div>

        {/* Custom date range */}
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
      <div className="grid grid-cols-2 xl:grid-cols-5 gap-4">
        {[
          {
            label: 'Total Usuarios',
            value: loading ? '—' : totalUsers.toLocaleString('es-CL'),
            Icon: Users,
            color: 'bg-blue-600',
          },
          {
            label: 'Usuarios Activos',
            value: loading ? '—' : activeCount.toLocaleString('es-CL'),
            Icon: UserCheck,
            color: 'bg-emerald-600',
          },
          {
            label: 'Promedio Cargas por Usuario',
            value: loading ? '—' : avgTransactions.toLocaleString('es-CL', { maximumFractionDigits: 1 }),
            Icon: Fuel,
            color: 'bg-amber-500',
          },
          {
            label: 'Ahorro Promedio por Usuario',
            value: loading ? '—' : fmtClp(avgSavings),
            Icon: PiggyBank,
            color: 'bg-purple-600',
          },
          {
            label: 'Total Ahorrado (descuentos bancarios)',
            value: loading ? '—' : fmtClp(totalSavings),
            Icon: TrendingUp,
            color: 'bg-rose-600',
          },
        ].map(({ label, value, Icon, color }) => (
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
          </div>
        ))}
      </div>

      {/* Table */}
      <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-200 bg-gray-50">
                <th className="text-left px-4 py-3 font-medium text-gray-600">
                  <button
                    onClick={() => setSortBy('name')}
                    className="flex items-center gap-1 hover:text-blue-600 transition-colors group"
                    title="Ordenar alfabéticamente"
                  >
                    Nombre
                    <ArrowUpDown size={12} className={`transition-colors ${
                      sortBy === 'name' ? 'text-blue-500' : 'text-gray-300 group-hover:text-blue-400'
                    }`} />
                  </button>
                </th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Email</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">RUT</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Rol</th>
                <th className="text-center px-4 py-3 font-medium text-gray-600">
                  <span className="flex items-center justify-center gap-1"><Car size={13} />Vehículos</span>
                </th>
                <th className="text-center px-4 py-3 font-medium text-gray-600">
                  <span className="flex items-center justify-center gap-1"><Fuel size={13} />Cargas</span>
                </th>
                <th className="text-right px-4 py-3 font-medium text-gray-600">
                  <span className="flex items-center justify-end gap-1"><PiggyBank size={13} />Ahorro</span>
                </th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Estado</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">
                  <button
                    onClick={() => setSortBy((s) => s === 'date-desc' ? 'date-asc' : 'date-desc')}
                    className="flex items-center gap-1 hover:text-blue-600 transition-colors group"
                    title={sortBy === 'date-desc' ? 'Más reciente primero' : sortBy === 'date-asc' ? 'Más antiguo primero' : 'Ordenar por fecha'}
                  >
                    Registro
                    <ArrowUpDown size={12} className={`transition-colors ${
                      sortBy === 'date-desc' ? 'text-blue-500' :
                      sortBy === 'date-asc'  ? 'text-amber-500' :
                      'text-gray-300 group-hover:text-blue-400'
                    }`} />
                  </button>
                </th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <SkeletonRow cols={10} rows={6} />
              ) : filteredUsers.length === 0 ? (
                <tr>
                  <td colSpan={10} className="text-center py-16 text-gray-400">
                    <UserCircle size={40} className="mx-auto mb-3 text-gray-300" />
                    <p>
                      {statusFilter === 'inactive'
                        ? 'No hay usuarios inactivos'
                        : statusFilter === 'active'
                        ? 'No hay usuarios activos'
                        : isFiltered
                        ? 'Sin usuarios en el período seleccionado'
                        : 'No hay usuarios registrados'}
                    </p>
                  </td>
                </tr>
              ) : (
                paginatedUsers.map((u) => (
                  <tr
                    key={u.id}
                    className={`border-b border-gray-100 transition-colors ${
                      u.active ? 'hover:bg-gray-50' : 'bg-gray-50 opacity-60'
                    }`}
                  >
                    <td className="px-4 py-3 font-medium text-gray-900 whitespace-nowrap">{fullName(u)}</td>
                    <td className="px-4 py-3 text-gray-600 max-w-[180px] truncate">{u.email}</td>
                    <td className="px-4 py-3 text-gray-600 font-mono text-xs whitespace-nowrap">{fmtRut(u.rut)}</td>
                    <td className="px-4 py-3">
                      <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                        u.roleName === 'ADMIN' ? 'bg-purple-100 text-purple-700' : 'bg-gray-100 text-gray-600'
                      }`}>
                        {u.roleName}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-center tabular-nums text-gray-700">
                      {u.vehicleCount > 0
                        ? <span className="font-medium">{u.vehicleCount}</span>
                        : <span className="text-gray-300">—</span>}
                    </td>
                    <td className="px-4 py-3 text-center tabular-nums text-gray-700">
                      {u.totalTransactions > 0
                        ? <span className="font-medium">{u.totalTransactions.toLocaleString('es-CL')}</span>
                        : <span className="text-gray-300">—</span>}
                    </td>
                    <td className="px-4 py-3 text-right tabular-nums">
                      {u.totalSavings > 0
                        ? <span className="text-emerald-600 font-medium">{fmtClp(u.totalSavings)}</span>
                        : <span className="text-gray-300">—</span>}
                    </td>
                    <td className="px-4 py-3">
                      <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded text-xs font-medium ${
                        u.active ? 'bg-emerald-100 text-emerald-700' : 'bg-gray-100 text-gray-500'
                      }`}>
                        <span className={`w-1.5 h-1.5 rounded-full ${u.active ? 'bg-emerald-500' : 'bg-gray-400'}`} />
                        {u.active ? 'Activo' : 'Inactivo'}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-gray-500 text-xs whitespace-nowrap">
                      {fmtDate(u.createdAt)}
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-1">
                        <button
                          onClick={() => setDetailUser(u)}
                          className="p-1.5 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                          title="Ver detalles"
                        >
                          <Eye size={14} />
                        </button>
                        {u.email !== myEmail && (
                          u.active ? (
                            <button
                              onClick={() => handleToggleActive(u)}
                              disabled={toggling === u.id}
                              className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors disabled:opacity-50"
                              title="Desactivar"
                            >
                              <PowerOff size={14} />
                            </button>
                          ) : (
                            <button
                              onClick={() => handleToggleActive(u)}
                              disabled={toggling === u.id}
                              className="p-1.5 text-gray-400 hover:text-emerald-600 hover:bg-emerald-50 rounded-lg transition-colors disabled:opacity-50"
                              title="Activar"
                            >
                              <Power size={14} />
                            </button>
                          )
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {!loading && filteredUsers.length > 0 && (
          <div className="flex items-center justify-between px-4 py-3 border-t border-gray-100">
            <p className="text-xs text-gray-500">
              {filteredUsers.length === users.length
                ? `${filteredUsers.length} usuarios`
                : `${filteredUsers.length} de ${users.length} usuarios`}
              {' · '}página {currentPage + 1} de {totalPages}
            </p>
            <div className="flex items-center gap-1">
              <button
                onClick={() => setCurrentPage((p) => Math.max(0, p - 1))}
                disabled={currentPage === 0}
                className="px-3 py-1.5 text-xs rounded-lg border border-gray-200 text-gray-600 hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
              >
                ← Anterior
              </button>
              {Array.from({ length: Math.min(totalPages, 7) }, (_, i) => {
                const offset = Math.max(0, Math.min(currentPage - 3, totalPages - 7));
                const page = i + offset;
                return (
                  <button
                    key={page}
                    onClick={() => setCurrentPage(page)}
                    className={`w-8 h-8 text-xs rounded-lg border transition-colors ${
                      page === currentPage
                        ? 'bg-blue-600 text-white border-blue-600 font-medium'
                        : 'border-gray-200 text-gray-600 hover:bg-gray-50'
                    }`}
                  >
                    {page + 1}
                  </button>
                );
              })}
              <button
                onClick={() => setCurrentPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={currentPage >= totalPages - 1}
                className="px-3 py-1.5 text-xs rounded-lg border border-gray-200 text-gray-600 hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
              >
                Siguiente →
              </button>
            </div>
          </div>
        )}
      </div>

      {detailUser && (
        <UserDetailDrawer user={detailUser} onClose={() => setDetailUser(null)} />
      )}
    </div>
  );
}

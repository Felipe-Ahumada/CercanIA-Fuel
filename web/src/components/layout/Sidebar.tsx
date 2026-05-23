import { BarChart3, Tag, Users, LogOut, Fuel } from 'lucide-react';
import type { ActiveSection } from '../../types';

interface Props {
  active: ActiveSection;
  onChange: (s: ActiveSection) => void;
  onLogout: () => void;
  userEmail: string;
}

const NAV_ITEMS: { id: ActiveSection; label: string; Icon: typeof BarChart3 }[] = [
  { id: 'analytics', label: 'Analytics', Icon: BarChart3 },
  { id: 'users', label: 'Usuarios', Icon: Users },
  { id: 'discounts', label: 'Descuentos', Icon: Tag },
];

export function Sidebar({ active, onChange, onLogout, userEmail }: Props) {
  return (
    <>
      {/* Desktop sidebar */}
      <aside className="hidden md:flex flex-col w-60 bg-white border-r border-gray-200 min-h-screen fixed left-0 top-0 z-40">
        <div className="flex items-center gap-2 px-5 py-5 border-b border-gray-200">
          <div className="bg-blue-600 rounded-lg p-1.5">
            <Fuel size={20} className="text-white" />
          </div>
          <div>
            <p className="font-bold text-gray-900 text-sm leading-tight">CercanIA Fuel</p>
            <p className="text-xs text-gray-500">Admin</p>
          </div>
        </div>

        <nav className="flex-1 p-3 space-y-1">
          {NAV_ITEMS.map(({ id, label, Icon }) => (
            <button
              key={id}
              onClick={() => onChange(id)}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all ${
                active === id
                  ? 'bg-blue-50 text-blue-700'
                  : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
              }`}
            >
              <Icon size={18} />
              {label}
            </button>
          ))}
        </nav>

        <div className="p-3 border-t border-gray-200">
          <div className="px-3 py-2 mb-1">
            <p className="text-xs text-gray-500 truncate">{userEmail}</p>
          </div>
          <button
            onClick={onLogout}
            className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-gray-600 hover:bg-red-50 hover:text-red-600 transition-all"
          >
            <LogOut size={18} />
            Cerrar sesión
          </button>
        </div>
      </aside>

      {/* Mobile bottom nav */}
      <nav className="md:hidden fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 z-40 flex">
        {NAV_ITEMS.map(({ id, label, Icon }) => (
          <button
            key={id}
            onClick={() => onChange(id)}
            className={`flex-1 flex flex-col items-center gap-1 py-2 text-xs font-medium transition-colors ${
              active === id ? 'text-blue-600' : 'text-gray-500'
            }`}
          >
            <Icon size={20} />
            {label}
          </button>
        ))}
        <button
          onClick={onLogout}
          className="flex-1 flex flex-col items-center gap-1 py-2 text-xs font-medium text-gray-500"
        >
          <LogOut size={20} />
          Salir
        </button>
      </nav>
    </>
  );
}

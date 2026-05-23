import { useState } from 'react';
import { Fuel, Eye, EyeOff, Loader2 } from 'lucide-react';
import { toast } from 'sonner';
import { authApi } from '../api/auth';

interface Props {
  onLogin: (token: string, email: string) => void;
}

export function LoginPage({ onLogin }: Props) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPw, setShowPw] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !password) return;

    setLoading(true);
    try {
      const res = await authApi.login({ email, password });
      if (res.role !== 'ADMIN') {
        toast.error('Solo administradores pueden acceder a este panel');
        return;
      }
      localStorage.setItem('token', res.token);
      localStorage.setItem('user', JSON.stringify({ email: res.email, role: res.role }));
      onLogin(res.token, res.email);
      toast.success('Bienvenido al panel de administración');
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        'Credenciales incorrectas';
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-950 via-blue-900 to-blue-800 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-white/10 rounded-2xl mb-4">
            <Fuel size={32} className="text-white" />
          </div>
          <h1 className="text-3xl font-bold text-white">CercanIA Fuel</h1>
          <p className="text-blue-200 mt-1">Panel de Administración</p>
        </div>

        <div className="bg-white rounded-2xl shadow-2xl p-8">
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Iniciar sesión</h2>

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1.5">
                Correo electrónico
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="admin@ejemplo.cl"
                required
                className="w-full px-4 py-2.5 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1.5">
                Contraseña
              </label>
              <div className="relative">
                <input
                  type={showPw ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  required
                  className="w-full px-4 py-2.5 pr-10 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowPw(!showPw)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                >
                  {showPw ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white font-medium py-2.5 rounded-lg transition-colors flex items-center justify-center gap-2 text-sm"
            >
              {loading && <Loader2 size={16} className="animate-spin" />}
              {loading ? 'Iniciando sesión...' : 'Ingresar'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

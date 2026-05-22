import { useState } from 'react';
import { Loader2 } from 'lucide-react';
import { toast } from 'sonner';
import { Modal } from '../common/Modal';
import { usersApi } from '../../api/users';
import { CHILE_REGIONS } from '../../data/chile';
import type { UserCreateRequest } from '../../types';

interface Props {
  onClose: () => void;
  onCreated: () => void;
}

interface FormState {
  firstName: string;
  middleName: string;
  lastName: string;
  secondLastName: string;
  email: string;
  rut: string;
  birthDate: string;
  regionId: string;
}

interface Errors {
  firstName?: string;
  lastName?: string;
  secondLastName?: string;
  email?: string;
  rut?: string;
  birthDate?: string;
}

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const RUT_RE = /^\d{1,2}\.\d{3}\.\d{3}-[\dkK]$/;

export function CreateUserModal({ onClose, onCreated }: Props) {
  const [form, setForm] = useState<FormState>({
    firstName: '',
    middleName: '',
    lastName: '',
    secondLastName: '',
    email: '',
    rut: '',
    birthDate: '',
    regionId: '',
  });
  const [errors, setErrors] = useState<Errors>({});
  const [saving, setSaving] = useState(false);

  const set = (field: keyof FormState) => (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) =>
    setForm((f) => ({ ...f, [field]: e.target.value }));

  const validate = (): boolean => {
    const e: Errors = {};
    if (!form.firstName.trim()) e.firstName = 'Nombre requerido';
    if (!form.lastName.trim()) e.lastName = 'Apellido paterno requerido';
    if (!form.secondLastName.trim()) e.secondLastName = 'Apellido materno requerido';
    if (!EMAIL_RE.test(form.email)) e.email = 'Email inválido';
    if (!RUT_RE.test(form.rut)) e.rut = 'RUT inválido (ej: 12.345.678-9)';
    if (!form.birthDate) e.birthDate = 'Fecha de nacimiento requerida';
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    setSaving(true);
    try {
      const req: UserCreateRequest = {
        email: form.email,
        rut: form.rut,
        firstName: form.firstName,
        middleName: form.middleName || undefined,
        lastName: form.lastName,
        secondLastName: form.secondLastName,
        birthDate: form.birthDate,
        roleId: 2, // default user role
      };
      await usersApi.create(req);
      toast.success('Usuario creado exitosamente');
      onCreated();
      onClose();
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        'Error al crear el usuario';
      toast.error(msg);
    } finally {
      setSaving(false);
    }
  };

  const selectedRegion = CHILE_REGIONS.find((r) => String(r.id) === form.regionId);

  return (
    <Modal title="Crear Usuario" onClose={onClose}>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Nombre <span className="text-red-500">*</span>
            </label>
            <input
              value={form.firstName}
              onChange={set('firstName')}
              className={`w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.firstName ? 'border-red-400' : 'border-gray-300'}`}
              placeholder="Juan"
            />
            {errors.firstName && <p className="text-red-500 text-xs mt-1">{errors.firstName}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Segundo nombre</label>
            <input
              value={form.middleName}
              onChange={set('middleName')}
              className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Carlos (opcional)"
            />
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Apellido paterno <span className="text-red-500">*</span>
            </label>
            <input
              value={form.lastName}
              onChange={set('lastName')}
              className={`w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.lastName ? 'border-red-400' : 'border-gray-300'}`}
              placeholder="González"
            />
            {errors.lastName && <p className="text-red-500 text-xs mt-1">{errors.lastName}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Apellido materno <span className="text-red-500">*</span>
            </label>
            <input
              value={form.secondLastName}
              onChange={set('secondLastName')}
              className={`w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.secondLastName ? 'border-red-400' : 'border-gray-300'}`}
              placeholder="López"
            />
            {errors.secondLastName && (
              <p className="text-red-500 text-xs mt-1">{errors.secondLastName}</p>
            )}
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Email <span className="text-red-500">*</span>
          </label>
          <input
            type="email"
            value={form.email}
            onChange={set('email')}
            className={`w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.email ? 'border-red-400' : 'border-gray-300'}`}
            placeholder="juan.gonzalez@correo.cl"
          />
          {errors.email && <p className="text-red-500 text-xs mt-1">{errors.email}</p>}
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              RUT <span className="text-red-500">*</span>
            </label>
            <input
              value={form.rut}
              onChange={set('rut')}
              className={`w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.rut ? 'border-red-400' : 'border-gray-300'}`}
              placeholder="12.345.678-9"
            />
            {errors.rut && <p className="text-red-500 text-xs mt-1">{errors.rut}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Fecha de nacimiento <span className="text-red-500">*</span>
            </label>
            <input
              type="date"
              value={form.birthDate}
              onChange={set('birthDate')}
              max={new Date().toISOString().split('T')[0]}
              className={`w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.birthDate ? 'border-red-400' : 'border-gray-300'}`}
            />
            {errors.birthDate && <p className="text-red-500 text-xs mt-1">{errors.birthDate}</p>}
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Región</label>
            <select
              value={form.regionId}
              onChange={set('regionId')}
              className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Seleccionar región</option>
              {CHILE_REGIONS.map((r) => (
                <option key={r.id} value={String(r.id)}>
                  {r.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Comuna</label>
            <select
              disabled={!selectedRegion}
              className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-50 disabled:text-gray-400"
            >
              <option value="">
                {selectedRegion ? 'Seleccionar comuna' : 'Seleccione región primero'}
              </option>
              {selectedRegion?.communes.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="flex gap-3 pt-2">
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

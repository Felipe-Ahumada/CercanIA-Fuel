import type { ReactNode } from 'react';
import { Sidebar } from './Sidebar';
import type { ActiveSection } from '../../types';

interface Props {
  active: ActiveSection;
  onChange: (s: ActiveSection) => void;
  onLogout: () => void;
  userEmail: string;
  children: ReactNode;
}

export function Layout({ active, onChange, onLogout, userEmail, children }: Props) {
  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar active={active} onChange={onChange} onLogout={onLogout} userEmail={userEmail} />
      <main className="flex-1 md:ml-60 pb-16 md:pb-0 min-h-screen">
        {children}
      </main>
    </div>
  );
}

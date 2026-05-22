import { useState } from 'react';
import { Toaster } from 'sonner';
import { LoginPage } from './components/LoginPage';
import { Layout } from './components/layout/Layout';
import { AnalyticsDashboard } from './components/analytics/AnalyticsDashboard';
import { UsersPage } from './components/users/UsersPage';
import { DiscountsPage } from './components/discounts/DiscountsPage';
import type { ActiveSection } from './types';

function App() {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('token'));
  const [userEmail, setUserEmail] = useState<string>(() => {
    try {
      const u = localStorage.getItem('user');
      return u ? JSON.parse(u).email : '';
    } catch {
      return '';
    }
  });
  const [section, setSection] = useState<ActiveSection>('analytics');

  const handleLogin = (t: string, email: string) => {
    setToken(t);
    setUserEmail(email);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUserEmail('');
  };

  if (!token) {
    return (
      <>
        <Toaster position="top-right" richColors duration={3000} />
        <LoginPage onLogin={handleLogin} />
      </>
    );
  }

  return (
    <>
      <Toaster position="top-right" richColors duration={3000} />
      <Layout
        active={section}
        onChange={setSection}
        onLogout={handleLogout}
        userEmail={userEmail}
      >
        {section === 'analytics' && <AnalyticsDashboard />}
        {section === 'users' && <UsersPage />}
        {section === 'discounts' && <DiscountsPage />}
      </Layout>
    </>
  );
}

export default App;

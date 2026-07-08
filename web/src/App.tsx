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
  const VALID_SECTIONS: ActiveSection[] = ['analytics', 'users', 'discounts'];
  const [section, setSection] = useState<ActiveSection>(() => {
    const saved = localStorage.getItem('section') as ActiveSection;
    return VALID_SECTIONS.includes(saved) ? saved : 'analytics';
  });

  const handleLogin = (t: string, email: string) => {
    setToken(t);
    setUserEmail(email);
  };

  const handleSectionChange = (s: ActiveSection) => {
    localStorage.setItem('section', s);
    setSection(s);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('section');
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
        onChange={handleSectionChange}
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

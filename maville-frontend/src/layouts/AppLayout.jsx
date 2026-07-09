import { Outlet } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import Navbar from '../components/Navbar';

export default function AppLayout({ children }) {
  return (
    <div style={{
      display: 'flex',
      minHeight: '100vh',
      background: 'var(--color-background)',
    }}>
      <Sidebar />
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minWidth: 0 }}>
        <Navbar />
        <main style={{
          flex: 1,
          padding: '2rem 2.5rem',
          maxWidth: '72rem',
          width: '100%',
          margin: '0 auto',
        }}>
          {children || <Outlet />}
        </main>
      </div>
    </div>
  );
}
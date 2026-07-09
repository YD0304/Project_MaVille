import { NavLink } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import {
  Home, AlertTriangle, ClipboardList, Bell,
  User, LogOut, Briefcase, ChevronRight, ShieldCheck, Wrench
} from 'lucide-react';

// FIX 1: navConfig is now a function so residentId and companyNumber
// are resolved from the real user object at render time — not dead
// template literals baked into a static string.
function getNavConfig(user) {
  const residentId    = user?.id;
  const companyNumber = user?.companyNumber;

  return {
    resident: [
      // FIX 2: removed duplicate 'Signaler' entry (same action as 'Report a problem').
      // FIX 3: paths are clean React Router routes, not API endpoint paths.
      { path: '/resident',             label: 'Tableau de bord',     icon: Home },
      { path: '/resident/report',      label: 'Signaler un problème', icon: AlertTriangle },
      { path: '/resident/my-problems', label: 'Mes signalements',    icon: ClipboardList },
      { path: '/resident/projects',    label: 'Travaux publics',     icon: Bell },
      { path: '/resident/subscriptions', label: 'Abonnements',        icon: User },
    ],
    admin: [
      { path: '/admin',                  label: 'Tableau de bord',         icon: Home },
      { path: '/admin/signals',          label: 'Signalements en direct', icon: AlertTriangle },
      { path: '/admin/not-assigned',     label: 'À traiter',              icon: ClipboardList },
      { path: '/admin/assigned',         label: 'Fiches problèmes',       icon: Wrench },
      { path: '/admin/submitted-proposals', label: 'Candidatures',         icon: Briefcase },
    ],
    provider:[
      { path: '/provider',                  label: 'Tableau de bord',        icon: Home },
      { path: '/provider/assigned-problems', label: 'Fiches problèmes',      icon: AlertTriangle },
      { path: '/provider/my-projects',       label: 'Mes projets',           icon: ClipboardList },
],
  };
}

const roleLabel = {
  resident:    'Résident',
  admin:       'Administrateur',
  prestataire: 'Prestataire',
};

const roleBadge = {
  resident:    { color: '#86efac', bg: 'rgba(34,197,94,0.15)',  border: 'rgba(34,197,94,0.25)',  Icon: Home },
  admin:       { color: '#93c5fd', bg: 'rgba(59,130,246,0.15)', border: 'rgba(59,130,246,0.25)', Icon: ShieldCheck },
  prestataire: { color: '#fcd34d', bg: 'rgba(234,179,8,0.15)',  border: 'rgba(234,179,8,0.25)',  Icon: Wrench },
};


export default function Sidebar() {
  const { user, logout } = useAuth();
const role = user?.role?.toLowerCase();
  const items   = getNavConfig(user)[role] ?? [];  // ?? [] guards unknown roles
  const badge   = roleBadge[role];
  const BadgeIcon = badge?.Icon;

  return (
    <aside style={{
      width: '15.5rem',
      background: '#0f172a',
      height: '100vh',
      position: 'sticky',
      top: 0,
      display: 'flex',
      flexDirection: 'column',
      flexShrink: 0,
    }}>

      {/* ── Logo ── */}
      <div style={{
        padding: '1.25rem 1.25rem 1rem',
        borderBottom: '1px solid rgba(255,255,255,0.07)',
        display: 'flex',
        flexDirection: 'column',
        gap: '0.625rem',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.625rem' }}>
          <div style={{
            width: '2rem', height: '2rem',
            background: 'linear-gradient(135deg, #3b82f6, #1d4ed8)',
            borderRadius: '0.5rem',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            boxShadow: '0 0 16px rgba(59,130,246,0.4)',
            flexShrink: 0,
          }}>
            <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
              <path d="M8 1L15 8L8 15L1 8L8 1Z" fill="white" fillOpacity="0.9"/>
              <path d="M8 4L12 8L8 12L4 8L8 4Z" fill="white"/>
            </svg>
          </div>
          <span style={{
            fontSize: '1.0625rem',
            fontWeight: 700,
            letterSpacing: '-0.02em',
            color: '#f8fafc',
          }}>MaVille</span>
        </div>

        {/* Role badge */}
        {badge && (
          <span style={{
            display: 'inline-flex', alignItems: 'center', gap: '0.25rem',
            background: badge.bg, color: badge.color,
            border: `1px solid ${badge.border}`,
            borderRadius: '9999px', padding: '0.175rem 0.5rem',
            fontSize: '0.6875rem', fontWeight: 600, letterSpacing: '0.04em',
            alignSelf: 'flex-start',
          }}>
            {BadgeIcon && <BadgeIcon size={10} />} {roleLabel[role] || role}
          </span>
        )}
      </div>

      {/* ── Nav ── */}
      <nav style={{
        flex: 1, padding: '0.875rem 0.75rem',
        overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '0.125rem',
      }}>
        <p style={{
          fontSize: '0.6375rem', fontWeight: 700, textTransform: 'uppercase',
          letterSpacing: '0.1em', color: 'rgba(255,255,255,0.25)',
          padding: '0 0.625rem', marginBottom: '0.375rem',
        }}>Navigation</p>

        {items.map(item => (
          <NavLink
            key={item.path}
            to={item.path}
            end={['/admin', '/resident', '/provider'].includes(item.path)}
            style={({ isActive }) => ({
              display: 'flex',
              alignItems: 'center',
              gap: '0.625rem',
              padding: '0.5625rem 0.75rem',
              borderRadius: '0.5rem',
              textDecoration: 'none',
              fontSize: '0.875rem',
              fontWeight: isActive ? 600 : 400,
              color: isActive ? '#fff' : 'rgba(255,255,255,0.55)',
              background: isActive ? 'rgba(59,130,246,0.2)' : 'transparent',
              borderLeft: isActive ? '2.5px solid #3b82f6' : '2.5px solid transparent',
              transition: 'all 0.15s ease',
            })}
            onMouseEnter={e => {
              e.currentTarget.style.background = 'rgba(255,255,255,0.06)';
              e.currentTarget.style.color = 'rgba(255,255,255,0.85)';
            }}
            onMouseLeave={e => {
              e.currentTarget.style.background = '';
              e.currentTarget.style.color = '';
            }}
          >
            {({ isActive }) => (
              <>
                <item.icon size={17} style={{ flexShrink: 0, opacity: isActive ? 1 : 0.7 }} />
                <span style={{ flex: 1 }}>{item.label}</span>
                {isActive && <ChevronRight size={13} style={{ opacity: 0.5 }} />}
              </>
            )}
          </NavLink>
        ))}
      </nav>

      {/* ── Footer ── */}
      <div style={{
        padding: '0.875rem 0.75rem',
        borderTop: '1px solid rgba(255,255,255,0.07)',
      }}>
        {user?.name && (
          <div style={{
            display: 'flex', alignItems: 'center', gap: '0.625rem',
            padding: '0.5rem 0.75rem', marginBottom: '0.5rem',
          }}>
            <div style={{
              width: '1.875rem', height: '1.875rem', borderRadius: '9999px',
              background: 'linear-gradient(135deg, #3b82f6, #06b6d4)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontSize: '0.75rem', fontWeight: 700, color: '#fff', flexShrink: 0,
            }}>
              {user.name?.[0]?.toUpperCase() || 'U'}
            </div>
            <div style={{ minWidth: 0 }}>
              <p style={{ fontSize: '0.8125rem', fontWeight: 600, color: '#f1f5f9', lineHeight: 1.2, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                {user.name}
              </p>
              <p style={{ fontSize: '0.7rem', color: 'rgba(255,255,255,0.35)' }}>
                {user.email || ''}
              </p>
            </div>
          </div>
        )}
        <button
          onClick={logout}
          style={{
            display: 'flex', alignItems: 'center', gap: '0.625rem',
            width: '100%', padding: '0.5625rem 0.75rem',
            borderRadius: '0.5rem', border: 'none', background: 'transparent',
            color: '#f87171', fontSize: '0.875rem', fontWeight: 500,
            cursor: 'pointer', transition: 'background 0.15s',
            fontFamily: 'inherit',
          }}
          onMouseEnter={e => e.currentTarget.style.background = 'rgba(239,68,68,0.12)'}
          onMouseLeave={e => e.currentTarget.style.background = 'transparent'}
        >
          <LogOut size={16} /> Déconnexion
        </button>
      </div>
    </aside>
  );
}
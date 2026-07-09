import React, { useState, useEffect } from 'react';
import { useAuth } from '../../auth/AuthContext';
import { api } from '../../api/api';
import AppLayout from '../../layouts/AppLayout';
import { useNavigate } from 'react-router-dom';
import {
  AlertTriangle,
  ClipboardList,
  ChevronRight,
} from 'lucide-react';

function StatCard({ icon: Icon, label, value, color, to, navigate }) {
  return (
    <div
      className="card"
      onClick={() => navigate(to)}
      style={{
        cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '1rem',
        padding: '1.25rem', transition: 'border-color 0.15s, box-shadow 0.15s',
      }}
      onMouseEnter={e => { e.currentTarget.style.borderColor = 'var(--color-primary)'; e.currentTarget.style.boxShadow = '0 0 0 3px rgba(37,99,235,0.1)'; }}
      onMouseLeave={e => { e.currentTarget.style.borderColor = ''; e.currentTarget.style.boxShadow = 'none'; }}
    >
      <div style={{
        width: '2.75rem', height: '2.75rem', borderRadius: '0.625rem',
        background: `${color}15`, display: 'flex', alignItems: 'center',
        justifyContent: 'center', color: color, flexShrink: 0,
      }}>
        <Icon size={20} />
      </div>
      <div style={{ flex: 1, minWidth: 0 }}>
        <p style={{ fontSize: '0.75rem', color: 'var(--color-text-subtle)', margin: 0, textTransform: 'uppercase', letterSpacing: '0.05em' }}>{label}</p>
        <p style={{ fontSize: '1.5rem', fontWeight: 700, margin: '0.125rem 0 0', lineHeight: 1.2 }}>{value}</p>
      </div>
      <ChevronRight size={16} style={{ color: 'var(--color-text-subtle)', flexShrink: 0 }} />
    </div>
  );
}

export default function ProviderPanel() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [stats, setStats] = useState({ assigned: 0, myProjects: 0 });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const [assigned, myProjects] = await Promise.all([
          api.getAssignedProblems().catch(() => []),
          api.getMyProposals(user?.id || '').catch(() => []),
        ]);
        setStats({
          assigned: assigned?.length || 0,
          myProjects: myProjects?.length || 0,
        });
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    if (user?.id) load();
    else setLoading(false);
  }, [user]);

  return (
    <AppLayout>
      <div>
        <div className="page-header">
          <h2>Tableau de bord — Prestataire</h2>
          <p>Bienvenue, {user?.name || 'Prestataire'} !</p>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(14rem, 1fr))', gap: '1rem', marginBottom: '2rem' }}>
          <StatCard icon={AlertTriangle} label="Fiches problèmes disponibles" value={loading ? '...' : stats.assigned} color="#3b82f6" to="/provider/assigned-problems" navigate={navigate} />
          <StatCard icon={ClipboardList} label="Mes projets" value={loading ? '...' : stats.myProjects} color="#10b981" to="/provider/my-projects" navigate={navigate} />
        </div>

        <div>
          <h3 style={{ fontSize: '0.9375rem', fontWeight: 600, marginBottom: '0.75rem' }}>Actions rapides</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            <button className="btn btn-primary" onClick={() => navigate('/provider/assigned-problems')} style={{ justifyContent: 'flex-start', gap: '0.625rem', padding: '0.875rem 1rem', fontSize: '0.875rem' }}>
              <AlertTriangle size={16} /> Voir les fiches problèmes
            </button>
            <button className="btn btn-ghost" onClick={() => navigate('/provider/my-projects')} style={{ justifyContent: 'flex-start', gap: '0.625rem', padding: '0.875rem 1rem', fontSize: '0.875rem' }}>
              <ClipboardList size={16} /> Mes projets et candidatures
            </button>
          </div>
        </div>
      </div>
    </AppLayout>
  );
}

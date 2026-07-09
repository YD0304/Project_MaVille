import React, { useState, useEffect } from 'react';
import { useAuth } from '../../auth/AuthContext';
import { api } from '../../api/api';
import AppLayout from '../../layouts/AppLayout';
import { useNavigate } from 'react-router-dom';
import {
  AlertTriangle,
  ClipboardList,
  Wrench,
  Briefcase,
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

export default function AdminPanel() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [stats, setStats] = useState({ allSignals: 0, notAssigned: 0, assigned: 0, proposals: 0 });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const [all, notAssigned, assigned, proposals] = await Promise.all([
          api.getAllProblems().catch(() => []),
          api.getNotAssignedProblems().catch(() => []),
          api.getAssignedProblems().catch(() => []),
          api.getSubmittedProposals().catch(() => []),
        ]);
        setStats({
          allSignals: all?.length || 0,
          notAssigned: notAssigned?.length || 0,
          assigned: assigned?.length || 0,
          proposals: proposals?.length || 0,
        });
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  return (
    <AppLayout>
      <div>
        <div className="page-header">
          <h2>Tableau de bord — Agent STPM</h2>
          <p>Bienvenue, {user?.name || 'Agent'} !</p>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(14rem, 1fr))', gap: '1rem', marginBottom: '2rem' }}>
          <StatCard icon={AlertTriangle} label="Signalements reçus" value={loading ? '...' : stats.allSignals} color="#3b82f6" to="/admin/signals" navigate={navigate} />
          <StatCard icon={ClipboardList} label="À traiter" value={loading ? '...' : stats.notAssigned} color="#f59e0b" to="/admin/not-assigned" navigate={navigate} />
          <StatCard icon={Wrench} label="Fiches problèmes" value={loading ? '...' : stats.assigned} color="#10b981" to="/admin/assigned" navigate={navigate} />
          <StatCard icon={Briefcase} label="Candidatures reçues" value={loading ? '...' : stats.proposals} color="#8b5cf6" to="/admin/submitted-proposals" navigate={navigate} />
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
          <div>
            <h3 style={{ fontSize: '0.9375rem', fontWeight: 600, marginBottom: '0.75rem' }}>Actions rapides</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <button className="btn btn-primary" onClick={() => navigate('/admin/not-assigned')} style={{ justifyContent: 'flex-start', gap: '0.625rem', padding: '0.875rem 1rem', fontSize: '0.875rem' }}>
                <ClipboardList size={16} /> Traiter les signalements
              </button>
              <button className="btn btn-ghost" onClick={() => navigate('/admin/submitted-proposals')} style={{ justifyContent: 'flex-start', gap: '0.625rem', padding: '0.875rem 1rem', fontSize: '0.875rem' }}>
                <Briefcase size={16} /> Examiner les candidatures
              </button>
              <button className="btn btn-ghost" onClick={() => navigate('/admin/signals')} style={{ justifyContent: 'flex-start', gap: '0.625rem', padding: '0.875rem 1rem', fontSize: '0.875rem' }}>
                <AlertTriangle size={16} /> Voir tous les signalements
              </button>
            </div>
          </div>
          <div>
            <h3 style={{ fontSize: '0.9375rem', fontWeight: 600, marginBottom: '0.75rem' }}>Notifications en temps réel</h3>
            <div className="card" style={{ padding: '1.25rem', textAlign: 'center', color: 'var(--color-text-subtle)' }}>
              <p>Les nouveaux signalements et candidatures apparaissent automatiquement.</p>
            </div>
          </div>
        </div>
      </div>
    </AppLayout>
  );
}

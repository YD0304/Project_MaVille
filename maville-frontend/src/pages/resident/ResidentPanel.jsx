import React, { useState, useEffect } from 'react';
import { useAuth } from '../../auth/AuthContext';
import { api } from '../../api/api';
import AppLayout from '../../layouts/AppLayout';
import { useNavigate } from 'react-router-dom';
import {
  AlertTriangle,
  Wrench,
  Bell,
  MapPin,
  Clock,
  ChevronRight,
} from 'lucide-react';

const statusLabels = {
  PROPOSAL_SUBMITTED: 'Proposition soumise',
  PERMIT_ISSUED: 'Permis accordé (à venir)',
  PROJECT_ONGOING: 'En cours',
  PROJECT_DELAYED: 'Retardé',
  PROJECT_FINISHED: 'Terminé',
  PROPOSAL_REFUSED: 'Refusé',
};

const typeLabels = {
  TRAVAUX_ROUTIERS: 'Travaux routiers',
  TRAVAUX_GAZ_ELECTRICITE: 'Gaz ou électricité',
  CONSTRUCTION_RENOVATION: 'Construction',
  ENTRETIEN_PAYSAGER: 'Entretien paysager',
  TRANSPORTS_COMMUN: 'Transports en commun',
  SIGNALISATION_ECLAIRAGE: 'Signalisation',
  TRAVAUX_SOUTERRAINS: 'Travaux souterrains',
  TRAVAUX_RESIDENTIEL: 'Travaux résidentiel',
  ENTRETIEN_URBAIN: 'Entretien urbain',
  RESEAUX_TELECOMMUNICATION: 'Télécommunication',
};

const priorityColors = {
  FAIBLE: { color: '#10b981', bg: 'rgba(16,185,129,0.1)' },
  MOYENNE: { color: '#f59e0b', bg: 'rgba(245,158,11,0.1)' },
  ELEVEE: { color: '#ef4444', bg: 'rgba(239,68,68,0.1)' },
  NOT_ASSIGNED: { color: '#6b7280', bg: 'rgba(107,114,128,0.1)' },
};

function StatCard({ icon: Icon, label, value, color, to, navigate }) {
  return (
    <div
      className="card"
      onClick={() => navigate(to)}
      style={{
        cursor: 'pointer',
        display: 'flex', alignItems: 'center', gap: '1rem',
        padding: '1.25rem',
        transition: 'border-color 0.15s, box-shadow 0.15s',
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

export default function ResidentPanel() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [problemCount, setProblemCount] = useState(0);
  const [projectCount, setProjectCount] = useState(0);
  const [subscriptionCount, setSubscriptionCount] = useState(0);
  const [recentProblems, setRecentProblems] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadData = async () => {
      try {
        const [problems, projects, subs] = await Promise.all([
          api.getMyProblems(user.id).catch(() => []),
          api.filterProjects({ neighbourhood: user.neighbourhood || undefined }).catch(() => []),
          api.getResidentSubscriptions(user.id).catch(() => []),
        ]);
        setProblemCount(problems?.length || 0);
        setProjectCount(projects?.length || 0);
        setSubscriptionCount(subs?.length || 0);
        setRecentProblems((problems || []).slice(-3).reverse());
      } catch (err) {
        console.error('Error loading dashboard data:', err);
      } finally {
        setLoading(false);
      }
    };

    if (user?.id) loadData();
    else setLoading(false);
  }, [user]);

  return (
    <AppLayout>
      <div>
        <div className="page-header">
          <h2>Tableau de bord</h2>
          <p>Bienvenue, {user?.name || 'Résident'} !</p>
        </div>

        {/* Stats row */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(14rem, 1fr))', gap: '1rem', marginBottom: '2rem' }}>
          <StatCard icon={AlertTriangle} label="Mes signalements" value={loading ? '...' : problemCount} color="#ef4444" to="/resident/my-problems" navigate={navigate} />
          <StatCard icon={Wrench} label="Projets dans mon quartier" value={loading ? '...' : projectCount} color="#3b82f6" to="/resident/projects" navigate={navigate} />
          <StatCard icon={Bell} label="Abonnements actifs" value={loading ? '...' : subscriptionCount} color="#10b981" to="/resident/subscriptions" navigate={navigate} />
        </div>

        {/* Recent problems & quick links */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
          <div>
            <h3 style={{ fontSize: '0.9375rem', fontWeight: 600, marginBottom: '0.75rem' }}>Signalements récents</h3>
            {loading ? (
              <div className="card" style={{ textAlign: 'center', padding: '2rem', color: 'var(--color-text-subtle)' }}>Chargement...</div>
            ) : recentProblems.length === 0 ? (
              <div className="card" style={{ textAlign: 'center', padding: '2rem', color: 'var(--color-text-subtle)' }}>
                <p>Aucun signalement pour le moment.</p>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                {recentProblems.map(p => {
                  const pColor = priorityColors[p.prioriteType] || priorityColors.NOT_ASSIGNED;
                  return (
                    <div key={p.id} className="card" style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', padding: '0.875rem 1rem' }}>
                      <div style={{
                        width: '0.375rem', height: '2.25rem', borderRadius: '9999px',
                        background: pColor.color, flexShrink: 0,
                      }} />
                      <div style={{ flex: 1, minWidth: 0 }}>
                        <p style={{ fontSize: '0.8125rem', fontWeight: 500, margin: 0 }}>{p.street}</p>
                        <p style={{ fontSize: '0.75rem', color: 'var(--color-text-subtle)', margin: '0.125rem 0 0' }}>
                          {typeLabels[p.type] || p.type} — {p.neighbourhood}
                        </p>
                      </div>
                      <Clock size={13} style={{ color: 'var(--color-text-subtle)', flexShrink: 0 }} />
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          <div>
            <h3 style={{ fontSize: '0.9375rem', fontWeight: 600, marginBottom: '0.75rem' }}>Actions rapides</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <button className="btn btn-primary" onClick={() => navigate('/resident/report')} style={{ justifyContent: 'flex-start', gap: '0.625rem', padding: '0.875rem 1rem', fontSize: '0.875rem' }}>
                <AlertTriangle size={16} /> Signaler un problème
              </button>
              <button className="btn btn-ghost" onClick={() => navigate('/resident/projects')} style={{ justifyContent: 'flex-start', gap: '0.625rem', padding: '0.875rem 1rem', fontSize: '0.875rem' }}>
                <Wrench size={16} /> Voir les projets en cours
              </button>
              <button className="btn btn-ghost" onClick={() => navigate('/resident/subscriptions')} style={{ justifyContent: 'flex-start', gap: '0.625rem', padding: '0.875rem 1rem', fontSize: '0.875rem' }}>
                <Bell size={16} /> Gérer mes abonnements
              </button>
            </div>
          </div>
        </div>
      </div>
    </AppLayout>
  );
}
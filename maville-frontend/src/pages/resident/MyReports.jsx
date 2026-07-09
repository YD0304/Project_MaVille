import { useState, useEffect } from 'react';
import { api } from "../../api/api";
import AppLayout from '../../layouts/AppLayout';
import { useAuth } from '../../auth/AuthContext';
import { AlertTriangle, Clock, CheckCircle, MapPin, Wrench } from 'lucide-react';

const priorityColors = {
  NOT_ASSIGNED: { color: '#6b7280', bg: 'rgba(107,114,128,0.1)', label: 'Non assignée' },
  FAIBLE: { color: '#10b981', bg: 'rgba(16,185,129,0.1)', label: 'Faible' },
  MOYENNE: { color: '#f59e0b', bg: 'rgba(245,158,11,0.1)', label: 'Moyenne' },
  ELEVEE: { color: '#ef4444', bg: 'rgba(239,68,68,0.1)', label: 'Élevée' },
  REFUSED: { color: '#ef4444', bg: 'rgba(239,68,68,0.1)', label: 'Refusée' },
};

const typeLabels = {
  TRAVAUX_ROUTIERS: 'Travaux routiers',
  TRAVAUX_GAZ_ELECTRICITE: 'Travaux de gaz ou électricité',
  CONSTRUCTION_RENOVATION: 'Construction ou rénovation',
  ENTRETIEN_PAYSAGER: 'Entretien paysager',
  TRANSPORTS_COMMUN: 'Travaux transports en commun',
  SIGNALISATION_ECLAIRAGE: 'Signalisation et éclairage',
  TRAVAUX_SOUTERRAINS: 'Travaux souterrains',
  TRAVAUX_RESIDENTIEL: 'Travaux résidentiel',
  ENTRETIEN_URBAIN: 'Entretien urbain',
  RESEAUX_TELECOMMUNICATION: 'Réseaux télécommunication',
};

function ProblemCard({ problem }) {
  const priority = priorityColors[problem.prioriteType] || priorityColors.NOT_ASSIGNED;
  return (
    <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <p style={{ fontSize: '0.875rem', fontWeight: 600, margin: 0 }}>{problem.street}</p>
          <p style={{ fontSize: '0.8125rem', color: 'var(--color-text-subtle)', margin: '0.25rem 0 0' }}>
            {problem.neighbourhood}
          </p>
        </div>
        <span style={{
          display: 'inline-flex', alignItems: 'center', gap: '0.25rem',
          padding: '0.2rem 0.5rem', borderRadius: '9999px',
          fontSize: '0.6875rem', fontWeight: 600,
          background: priority.bg, color: priority.color,
        }}>
          {priority.label}
        </span>
      </div>
      <p style={{ fontSize: '0.8125rem', margin: 0, color: 'var(--color-text)' }}>{problem.description}</p>
      <div style={{ display: 'flex', gap: '1rem', fontSize: '0.75rem', color: 'var(--color-text-subtle)' }}>
        <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
          <Wrench size={12} /> {typeLabels[problem.type] || problem.type}
        </span>
        <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
          <Clock size={12} /> {problem.reportTime ? new Date(problem.reportTime).toLocaleDateString() : '—'}
        </span>
      </div>
    </div>
  );
}

export default function MyReports() {
  const { user } = useAuth();
  const [problems, setProblems] = useState([]);
  const [loaded, setLoaded] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (user?.id) {
      loadProblems(user.id);
    } else if (user === null) {
      setLoaded(true);
      setError("Vous devez être connecté pour voir vos signalements.");
    }
  }, [user]);

  const loadProblems = async (residentId) => {
    try {
      setLoaded(false);
      const data = await api.getMyProblems(residentId);
      setProblems(data || []);
      setError(null);
    } catch (err) {
      setError("Erreur lors du chargement des signalements.");
    } finally {
      setLoaded(true);
    }
  };

  return (
    <AppLayout>
      <div>
        <div className="page-header">
          <h2>Mes signalements</h2>
          <p>Problèmes que vous avez rapportés</p>
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
          {!loaded ? (
            <div className="empty" style={{ textAlign: 'center', padding: '2rem', color: 'var(--color-text-subtle)' }}>Chargement...</div>
          ) : error ? (
            <div className="empty error" style={{ textAlign: 'center', padding: '2rem', color: '#ef4444' }}>{error}</div>
          ) : problems.length === 0 ? (
            <div className="empty" style={{ textAlign: 'center', padding: '2rem', color: 'var(--color-text-subtle)' }}>
              <AlertTriangle size={24} style={{ margin: '0 auto 0.5rem', opacity: 0.4 }} />
              <p>Aucun signalement trouvé.</p>
            </div>
          ) : (
            problems.map(p => <ProblemCard key={p.id} problem={p} />)
          )}
        </div>
      </div>
    </AppLayout>
  );
}
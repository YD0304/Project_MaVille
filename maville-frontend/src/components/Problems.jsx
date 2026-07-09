import React, { useState, useEffect, useCallback } from 'react';
import { api } from '../api/api';
import Button from './Button';

// Labels for display (same as before)
const problemTypeLabels = {
  TRAVAUX_ROUTIERS: 'Travaux routiers',
  TRAVAUX_GAZ_ELECTRICITE: 'Travaux de gaz ou électricité',
  CONSTRUCTION_RENOVATION: 'Construction ou rénovation',
  ENTRETIEN_PAYSAGER: 'Entretien paysager',
  TRANSPORTS_COMMUN: 'Transports en commun',
  SIGNALISATION_ECLAIRAGE: 'Signalisation et éclairage',
  TRAVAUX_SOUTERRAINS: 'Travaux souterrains',
  TRAVAUX_RESIDENTIEL: 'Travaux résidentiel',
  ENTRETIEN_URBAIN: 'Entretien urbain',
  RESEAUX_TELECOMMUNICATION: 'Réseaux de télécommunication'
};

const priorityLabels = {
  FAIBLE: 'Faible',
  MOYENNE: 'Moyenne',
  ELEVEE: 'Élevée'
};

export function Problems() {
  const [problems, setProblems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchAssignedProblems = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      // Calls backend method: getAssignedProblems()
      const data = await api.getAssignedProblems();
      setProblems(data);
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchAssignedProblems();
  }, [fetchAssignedProblems]);

  return (
    <div className="resident-project-list">
      <h2>Fiches problèmes traitées par les agents STPM</h2>
      <p className="info-note">
        Ces signalements ont été analysés et une priorité leur a été attribuée.
      </p>

      {loading && <div>Chargement...</div>}
      {error && <div>Erreur : {error.message}</div>}
      {!loading && !error && (
        <div className="project-grid">
          {problems.length === 0 ? (
            <p>Aucune fiche problème traitée pour le moment.</p>
          ) : (
            problems.map(problem => <ProblemCard key={problem.id} problem={problem} />)
          )}
        </div>
      )}
    </div>
  );
}

function ProblemCard({ problem }) {
  const typeLabel = problemTypeLabels[problem.type] || problem.type;
  const priorityValue = problem.prioriteType;           // ← changed from 'priority'
  const priorityLabel = priorityLabels[priorityValue] || priorityValue || 'Non définie';

  return (
    <div className="project-card-horizontal">
      <div className="project-main">
        <h3>{problem.title || problem.description?.substring(0, 50)}</h3>
        <p>{problem.description}</p>
      </div>
      <div className="project-details">
        <p><strong>Rue :</strong> {problem.street || 'Non spécifié'}</p>
        <p><strong>Quartier :</strong> {problem.neighbourhood || 'Non spécifié'}</p>
        <p><strong>Type :</strong> {typeLabel}</p>
        <p><strong>Priorité :</strong> {priorityLabel}</p>
        {/* Only show status if the problem has a meaningful status field */}
        {problem.status && <p><strong>Statut :</strong> {problem.status}</p>}
      </div>
<Button 
  to={`/provider/create-proposal?problemId=${problem.id}`}
  style={{
    margin: '1rem',
    padding: '0.5rem 1rem',
    backgroundColor: '#3b82f6',
    color: 'white',
    border: 'none',
    borderRadius: '0.375rem',
    cursor: 'pointer',
  }}
>
  Soumettre une proposition
</Button> </div>);
}
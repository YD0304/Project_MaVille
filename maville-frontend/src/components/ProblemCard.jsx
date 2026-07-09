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
  RESEAUX_TELECOMMUNICATION: 'Réseaux de télécommunication',
};

const priorityLabels = {
  FAIBLE: 'Faible',
  MOYENNE: 'Moyenne',
  ELEVEE: 'Élevée',
  NOT_ASSIGNED: 'Non assignée',
  REFUSED: 'Refusé',
};

export function ProblemCard({ problem, actions }) {
  const typeLabel = problemTypeLabels[problem.type] || problem.type;
  const priorityLabel = priorityLabels[problem.prioriteType] || problem.prioriteType || 'Non définie';

  return (
    <div className="project-card-horizontal">
      <div className="project-main">
        <h3>{problem.title || problem.description?.substring(0, 50) || `Signalement #${problem.id}`}</h3>
        <p>{problem.description}</p>
      </div>
      <div className="project-details">
        <p><strong>Rue :</strong> {problem.street || 'Non spécifié'}</p>
        <p><strong>Quartier :</strong> {problem.neighbourhood || 'Non spécifié'}</p>
        <p><strong>Type :</strong> {typeLabel}</p>
        <p><strong>Priorité :</strong> {priorityLabel}</p>
        <p><strong>Date :</strong> {problem.reportTime ? new Date(problem.reportTime).toLocaleDateString('fr-CA') : 'N/A'}</p>
        {problem.status && <p><strong>Statut :</strong> {problem.status}</p>}
      </div>
      {actions && <div style={{ padding: '0.75rem' }}>{actions}</div>}
    </div>
  );
}

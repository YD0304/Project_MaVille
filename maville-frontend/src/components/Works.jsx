import React, { useState, useEffect, useCallback } from 'react';
import { api } from '../api/api';

// Types de travaux (enum backend)
const workTypes = [
  'TRAVAUX_ROUTIERS',
  'TRAVAUX_GAZ_ELECTRICITE',
  'CONSTRUCTION_RENOVATION',
  'ENTRETIEN_PAYSAGER',
  'TRANSPORTS_COMMUN',
  'SIGNALISATION_ECLAIRAGE',
  'TRAVAUX_SOUTERRAINS',
  'TRAVAUX_RESIDENTIEL',
  'ENTRETIEN_URBAIN',
  'RESEAUX_TELECOMMUNICATION'
];

const priorities = ['FAIBLE', 'MOYENNE', 'ELEVEE'];

// Libellés conviviaux
const workTypeLabels = {
  TRAVAUX_ROUTIERS: 'Travaux routiers',
  TRAVAUX_GAZ_ELECTRICITE: 'Travaux de gaz ou électricité',
  CONSTRUCTION_RENOVATION: 'Construction ou rénovation',
  ENTRETIEN_PAYSAGER: 'Entretien paysager',
  TRANSPORTS_COMMUN: 'Travaux liés aux transports en commun',
  SIGNALISATION_ECLAIRAGE: 'Travaux de signalisation et éclairage',
  TRAVAUX_SOUTERRAINS: 'Travaux souterrains',
  TRAVAUX_RESIDENTIEL: 'Travaux résidentiel',
  ENTRETIEN_URBAIN: 'Entretien urbain',
  RESEAUX_TELECOMMUNICATION: 'Entretien des réseaux de télécommunication'
};

const priorityLabels = {
  FAIBLE: 'Faible',
  MOYENNE: 'Moyenne',
  ELEVEE: 'Élevée'
};

// Statuts possibles (enum backend) et leurs libellés
const statusLabels = {
  PROPOSAL_SUBMITTED: 'Proposition soumise',
  PERMIT_ISSUED: 'Permis accordé (à venir)',
  PROJECT_ONGOING: 'En cours',
  PROJECT_DELAYED: 'Retardé',
  PROJECT_FINISHED: 'Terminé',
  PROPOSAL_REFUSED: 'Refusé'
};

export function Works() {
  const [filters, setFilters] = useState({
    street: '',
    neighbourhood: '',
    type: '',
    priority: '',
    status: '', // '' = tous, 'PROJECT_ONGOING' = en cours, 'PERMIT_ISSUED' = à venir
  });

  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchProjects = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params = {
        street: filters.street || undefined,
        neighbourhood: filters.neighbourhood || undefined,
        type: filters.type || undefined,
        priority: filters.priority || undefined,
      };

      if (filters.status) {
        params.status = filters.status;
      }

      const data = await api.filterProjects(params);
      setProjects(data);
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    fetchProjects();
  }, [fetchProjects]);

  const handleFilterChange = (key, value) => {
    setFilters(prev => ({ ...prev, [key]: value }));
  };

  const resetFilters = () => {
    setFilters({
      street: '',
      neighbourhood: '',
      type: '',
      priority: '',
      status: '',
    });
  };

  return (
    <div className="resident-project-list">
      <h2>Projets dans Montréal</h2>
      <div className="filters">
        <input
          type="text"
          placeholder="Rue (ex: Rue de l'Église)"
          value={filters.street}
          onChange={e => handleFilterChange('street', e.target.value)}
        />
        <input
          type="text"
          placeholder="Quartier (ex: Rosemont)"
          value={filters.neighbourhood}
          onChange={e => handleFilterChange('neighbourhood', e.target.value)}
        />
        <select
          value={filters.type}
          onChange={e => handleFilterChange('type', e.target.value)}
        >
          <option value="">Tous les types de travaux</option>
          {workTypes.map(type => (
            <option key={type} value={type}>{workTypeLabels[type] || type}</option>
          ))}
        </select>
        <select
          value={filters.priority}
          onChange={e => handleFilterChange('priority', e.target.value)}
        >
          <option value="">Toutes les priorités</option>
          {priorities.map(p => (
            <option key={p} value={p}>{priorityLabels[p] || p}</option>
          ))}
        </select>

        {/* Sélecteur de statut : permet de choisir soit "En cours", soit "À venir", soit tous */}
        <select
          value={filters.status}
          onChange={e => handleFilterChange('status', e.target.value)}
        >
          <option value="">Tous les statuts</option>
          <option value="PROJECT_ONGOING">En cours (PROJECT_ONGOING)</option>
          <option value="PERMIT_ISSUED">À venir (PERMIT_ISSUED)</option>
        </select>

        <button onClick={resetFilters} disabled={loading}>
          Réinitialiser
        </button>
      </div>

      {loading && <div>Chargement...</div>}
      {error && <div>Erreur : {error.message}</div>}
      {!loading && !error && (
        <div className="project-grid">
          {projects.length === 0 ? (
            <p>Aucun projet ne correspond aux critères.</p>
          ) : (
            projects.map(project => <ProjectCard key={project.id} project={project} />)
          )}
        </div>
      )}
    </div>
  );
}

function ProjectCard({ project }) {
  const typeLabel = workTypeLabels[project.type] || project.type;
  const priorityValue = project.problem?.prioriteType;
  const priorityLabel = priorityLabels[priorityValue] || priorityValue || 'Non définie';
  const statusLabel = statusLabels[project.status] || project.status;

  return (
    <div className="project-card-horizontal">
      <div className="project-main">
        <h3>{project.title}</h3>
        <p>{project.description}</p>
      </div>
      <div className="project-details">
        <p><strong>Rue :</strong> {project.problem?.street || 'Non spécifié'}</p>
        <p><strong>Quartier :</strong> {project.problem?.neighbourhood || 'Non spécifié'}</p>
        <p><strong>Type :</strong> {typeLabel}</p>
        <p><strong>Priorité :</strong> {priorityLabel}</p>
        <p><strong>Dates :</strong> {project.proposedStartDate} → {project.proposedEndDate}</p>
        <p><strong>Statut :</strong> {statusLabel}</p>
      </div>
    </div>
  );
}

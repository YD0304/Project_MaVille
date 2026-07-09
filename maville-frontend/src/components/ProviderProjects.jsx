import React, { useState, useEffect, useCallback } from 'react';
import { api } from '../api/api';
import { useAuth } from '../auth/AuthContext';

// Same labels as in Works.jsx (you can import from a shared constants file)
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

const statusLabels = {
  PROPOSAL_SUBMITTED: 'Proposition soumise – en attente',
  PERMIT_ISSUED: 'Permis accordé – à venir',
  PROJECT_ONGOING: 'En cours',
  PROJECT_DELAYED: 'Retardé',
  PROJECT_FINISHED: 'Terminé',
  PROPOSAL_REFUSED: 'Refusé'
};

export default function ProviderProjects() {
  const { user } = useAuth();
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  console.log('Full user object:', user);
console.log('Company number:', user?.companyNumber);

  // Inside fetchMyProjects
const fetchMyProjects = useCallback(async () => {
  if (!user?.id) return;   // ← changed from user?.companyNumber
  setLoading(true);
  try {
    const data = await api.getMyProposals(user.id); // user.id = company number
    setProjects(data);
  } catch (err) {
    setError(err);
  } finally {
    setLoading(false);
  }
}, [user]);

  // Initial load + real‑time polling every 10 seconds (agent decision tracking)
  useEffect(() => {
    fetchMyProjects();
    const interval = setInterval(fetchMyProjects, 10000); // refresh every 10s
    return () => clearInterval(interval);
  }, [fetchMyProjects]);

  if (loading && projects.length === 0) return <div>Chargement de vos projets...</div>;
  if (error) return <div>Erreur : {error.message}</div>;

  return (
    <div className="resident-project-list">
      <h2>Mes projets (prestataire)</h2>
      <p className="info-note">
        Suivez l'état de vos candidatures et projets en cours. Décisions de l'agent STPM actualisées en temps réel.
      </p>
      {projects.length === 0 ? (
        <p>Aucun projet pour le moment. Soumettez une proposition depuis les problèmes assignés.</p>
      ) : (
        <div className="project-grid">
          {projects.map(project => (
            <ProjectCard key={project.id} project={project} onUpdate={fetchMyProjects} />
          ))}
        </div>
      )}
    </div>
  );
}

// --- Reusable card (similar to Works.jsx but with modify actions) ---
function ProjectCard({ project, onUpdate }) {
  const { user } = useAuth();
  console.log('Provider company number:', user?.companyNumber);
  const [editing, setEditing] = useState(false);
  const [newDescription, setNewDescription] = useState(project.description || '');
  const [newEndDate, setNewEndDate] = useState(project.proposedEndDate || '');
  const [updating, setUpdating] = useState(false);

  const typeLabel = workTypeLabels[project.type] || project.type;
  const priorityValue = project.problem?.prioriteType;
  const priorityLabel = priorityLabels[priorityValue] || priorityValue || 'Non définie';
  const statusLabel = statusLabels[project.status] || project.status;

  const handleUpdateDescription = async () => {
    setUpdating(true);
    try {
      await api.updateProposalDescription(project.id, user.id, newDescription);
      alert('Description mise à jour');
      onUpdate(); // refresh list
      setEditing(false);
    } catch (err) {
      alert('Erreur : ' + err.message);
    } finally {
      setUpdating(false);
    }
  };

  const handleUpdateEndDate = async () => {
    setUpdating(true);
    try {
      await api.updateProposalEndDate(project.id, user.id, newEndDate);
      alert('Date de fin mise à jour');
      onUpdate();
      setEditing(false);
    } catch (err) {
      alert('Erreur : ' + err.message);
    } finally {
      setUpdating(false);
    }
  };

  const handleStatusChange = async (action) => {
    setUpdating(true);
    try {
      if (action === 'start') await api.startWork(project.id, user.id);
      else if (action === 'delay') await api.delayWork(project.id, user.id);
      else if (action === 'complete') await api.completeWork(project.id, user.id);
      alert('Statut mis à jour');
      onUpdate();
    } catch (err) {
      alert('Erreur : ' + err.message);
    } finally {
      setUpdating(false);
    }
  };

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
        <p><strong>Statut :</strong> <span className={`status-${project.status}`}>{statusLabel}</span></p>
      </div>
      

      {/* Modification panel – only if project is not finished/refused */}
      {project.status !== 'PROJECT_FINISHED' && project.status !== 'PROPOSAL_REFUSED' && (
        <div className="modify-actions">
          <button onClick={() => setEditing(!editing)} disabled={updating}>
            {editing ? 'Annuler' : 'Modifier le projet'}
          </button>

          {editing && (
            <div style={{ marginTop: '0.5rem' }}>
              <div>
                <label>Nouvelle description :</label>
                <textarea
                  value={newDescription}
                  onChange={(e) => setNewDescription(e.target.value)}
                  rows={2}
                  style={{ width: '100%' }}
                />
                <button onClick={handleUpdateDescription} disabled={updating}>Enregistrer description</button>
              </div>
              <div>
                <label>Nouvelle date de fin :</label>
                <input
                  type="date"
                  value={newEndDate}
                  onChange={(e) => setNewEndDate(e.target.value)}
                />
                <button onClick={handleUpdateEndDate} disabled={updating}>Modifier date</button>
              </div>
              <div>
                <label>Changer le statut :</label>
                <button onClick={() => handleStatusChange('start')} disabled={updating}>Commencer</button>
                <button onClick={() => handleStatusChange('delay')} disabled={updating}>Signaler un retard</button>
                <button onClick={() => handleStatusChange('complete')} disabled={updating}>Terminer</button>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
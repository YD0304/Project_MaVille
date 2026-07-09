import { useEffect, useState } from 'react';
import { api } from "../../api/api";
import { Alert } from '../../components/Alert';

const statusLabels = {
  PROPOSAL_SUBMITTED: 'Soumise',
  PERMIT_ISSUED: 'Permis accordé',
  PROJECT_ONGOING: 'En cours',
  PROJECT_DELAYED: 'Retardé',
  PROJECT_FINISHED: 'Terminé',
  PROPOSAL_REFUSED: 'Refusé',
};

export default function SubmittedProposals() {
  const [proposals, setProposals] = useState([]);
  const [alert, setAlert] = useState(null);
  const [rejectReason, setRejectReason] = useState({});

  const load = () => api.getSubmittedProposals().then(setProposals);
  useEffect(() => { load(); }, []);

  const handleAccept = async (projectId) => {
    try {
      await api.acceptProposal(projectId);
      setAlert({ message: 'Proposition acceptée — projet créé!', type: 'success' });
      load();
    } catch (err) {
      setAlert({ message: 'Erreur lors de l\'acceptation', type: 'error' });
    }
  };

  const handleReject = async (projectId) => {
    const reason = rejectReason[projectId];
    if (!reason || !reason.trim()) {
      setAlert({ message: 'Veuillez fournir une raison de refus', type: 'error' });
      return;
    }
    try {
      await api.rejectProposal(projectId, reason);
      setAlert({ message: 'Proposition refusée', type: 'success' });
      setRejectReason(prev => ({ ...prev, [projectId]: '' }));
      load();
    } catch (err) {
      setAlert({ message: 'Erreur lors du refus', type: 'error' });
    }
  };

  return (
    <div>
      <div className="page-header"><h2>Candidatures des prestataires</h2><p>Examinez et décidez</p></div>
      <Alert message={alert?.message} type={alert?.type} onClose={() => setAlert(null)} />
      <div className="problem-list">
        {proposals.length === 0 && <div className="empty">Aucune candidature pour le moment.</div>}
        {proposals.map(p => (
          <div key={p.id} className="project-card-horizontal">
            <div className="project-main">
              <h3>{p.title || 'Proposition #' + p.id}</h3>
              <p>{p.description}</p>
            </div>
            <div className="project-details">
              <p><strong>Prestataire :</strong> {p.provider?.companyName || p.provider?.name || 'Inconnu'}</p>
              <p><strong>Priorité :</strong> {p.problem?.prioriteType || 'N/A'}</p>
              <p><strong>Quartier :</strong> {p.problem?.neighbourhood || 'N/A'}</p>
              <p><strong>Dates :</strong> {p.proposedStartDate} → {p.proposedEndDate}</p>
              <p><strong>Coût :</strong> {p.proposedCost ? `${p.proposedCost} $` : 'N/A'}</p>
              <p><strong>Statut :</strong> <span className={`status-${p.status}`}>{statusLabels[p.status] || p.status}</span></p>
            </div>
            {p.status === 'PROPOSAL_SUBMITTED' && (
              <div style={{ marginTop: '0.75rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <button className="btn btn-success btn-sm" onClick={() => handleAccept(p.id)}>
                    Accepter
                  </button>
                  <button className="btn btn-danger btn-sm" onClick={() => handleReject(p.id)}>
                    Refuser
                  </button>
                </div>
                <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                  <input
                    style={{ flex: 1, padding: '0.375rem 0.5rem', fontSize: '0.8125rem' }}
                    placeholder="Motif du refus (obligatoire)"
                    value={rejectReason[p.id] || ''}
                    onChange={e => setRejectReason(prev => ({ ...prev, [p.id]: e.target.value }))}
                  />
                </div>
              </div>
            )}
            {p.rejectionReason && (
              <p style={{ marginTop: '0.5rem', color: '#dc2626', fontSize: '0.8125rem' }}>
                <strong>Motif du refus :</strong> {p.rejectionReason}
              </p>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

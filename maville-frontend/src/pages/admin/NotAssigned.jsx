import { useEffect, useState } from 'react';
import { api } from "../../api/api";
import { ProblemCard } from '../../components/ProblemCard';
import { Alert } from '../../components/Alert';

export const NotAssigned = () => {
  const [problems, setProblems] = useState([]);
  const [assignedProblems, setAssignedProblems] = useState([]);
  const [alert, setAlert] = useState(null);
  const [linkTargetId, setLinkTargetId] = useState({});

  const load = () => Promise.all([
    api.getNotAssignedProblems().then(setProblems),
    api.getAssignedProblems().then(setAssignedProblems),
  ]);
  useEffect(() => { load(); }, []);

  const assignPriority = async (id, priorite) => {
    const result = await api.assignPriority(id, priorite);
    if (result) { setAlert({ message: 'Priorité assignée!', type: 'success' }); load(); }
    else setAlert({ message: 'Erreur: déjà traité', type: 'error' });
  };

  const linkSignal = async (signalId) => {
    const parentId = linkTargetId[signalId];
    if (!parentId) { setAlert({ message: 'Sélectionnez une fiche cible', type: 'error' }); return; }
    const result = await api.linkSignalToParent(signalId, parentId);
    if (result) { setAlert({ message: 'Signalement lié à la fiche!', type: 'success' }); load(); }
    else setAlert({ message: 'Erreur: impossible de lier', type: 'error' });
  };

  return (
    <div>
      <div className="page-header"><h2>Signalements à traiter</h2><p>Assignez une priorité pour créer une fiche problème, ou liez à une fiche existante</p></div>
      <Alert message={alert?.message} type={alert?.type} onClose={() => setAlert(null)} />
      <div className="problem-list">
        {problems.map(p => {
          const actions = (
            <div style={{ marginTop: '8px', display: 'flex', flexDirection: 'column', gap: '6px' }}>
              <div style={{ display: 'flex', gap: '6px', alignItems: 'center' }}>
                <select id={`pri-${p.id}`} style={{ padding: '4px 8px' }}>
                  <option value="HIGH">Haute</option><option value="MEDIUM">Moyenne</option><option value="LOW">Faible</option><option value="REFUSED">Refuser</option>
                </select>
                <button className="btn btn-primary btn-sm" onClick={() => assignPriority(p.id, document.getElementById(`pri-${p.id}`).value)}>Assigner</button>
              </div>
              <div style={{ display: 'flex', gap: '6px', alignItems: 'center' }}>
                <select
                  style={{ padding: '4px 8px', flex: 1 }}
                  value={linkTargetId[p.id] || ''}
                  onChange={e => setLinkTargetId(prev => ({ ...prev, [p.id]: e.target.value }))}
                >
                  <option value="">— Lier à une fiche —</option>
                  {assignedProblems.map(fp => (
                    <option key={fp.id} value={fp.id}>#{fp.id} — {fp.street} ({fp.prioriteType})</option>
                  ))}
                </select>
                <button className="btn btn-secondary btn-sm" onClick={() => linkSignal(p.id)}>Lier</button>
              </div>
            </div>
          );
          return <ProblemCard key={p.id} problem={p} actions={actions} />;
        })}
        {problems.length === 0 && <div className="empty">Tous les signalements ont été traités.</div>}
      </div>
    </div>
  );
};
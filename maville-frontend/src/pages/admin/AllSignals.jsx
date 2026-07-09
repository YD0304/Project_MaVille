import { useEffect, useState } from 'react';
import { api } from "../../api/api";
import { ProblemCard } from '../../components/ProblemCard';

export const AllSignals = () => {
  const [problems, setProblems] = useState([]);
  useEffect(() => { api.getAllProblems().then(setProblems); }, []);
  const stats = { total: problems.length, notAssigned: problems.filter(p => p.prioriteType === 'NOT_ASSIGNED').length, high: problems.filter(p => p.prioriteType === 'HIGH').length };
  return (
    <div>
      <div className="page-header"><h2>Tous les signalements</h2><p>Vue en temps réel</p></div>
      <div className="stats-grid">
        <div className="stat-card"><div className="stat-label">Total</div><div className="stat-value">{stats.total}</div></div>
        <div className="stat-card"><div className="stat-label">À traiter</div><div className="stat-value" style={{ color: '#185FA5' }}>{stats.notAssigned}</div></div>
        <div className="stat-card"><div className="stat-label">Priorité haute</div><div className="stat-value" style={{ color: '#A32D2D' }}>{stats.high}</div></div>
      </div>
      <div className="problem-list">{problems.map(p => <ProblemCard key={p.id} problem={p} />)}</div>
    </div>
  );
};
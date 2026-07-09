import { useEffect, useState } from 'react';
import { api } from "../../api/api";
import { ProblemCard } from '../../components/ProblemCard';

export const AssignedProblems = () => {
  const [problems, setProblems] = useState([]);
  useEffect(() => { api.getAssignedProblems().then(setProblems); }, []);
  return (
    <div>
      <div className="page-header"><h2>Fiches problèmes</h2><p>Signalements ayant reçu une priorité</p></div>
      <div className="problem-list">{problems.map(p => <ProblemCard key={p.id} problem={p} />)}</div>
    </div>
  );
};
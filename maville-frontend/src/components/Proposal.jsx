import { useState, useEffect } from 'react';
import { useAuth } from '../auth/AuthContext';
import { api } from '../api/api'; // import the whole api object

const Proposal = ({ mode, initialProblemId }) => {
  const { user } = useAuth();
  const [proposals, setProposals] = useState([]);
  const [form, setForm] = useState({
    problemId: initialProblemId || '',
    title: '',
    description: '',
    startDate: '',
    endDate: '',
    cost: ''
  });
  const [updateForm, setUpdateForm] = useState({ projectId: '', status: '', description: '', newDate: '' });
  const [loading, setLoading] = useState(false);

  // Fetch existing proposals for this provider
  useEffect(() => {
    if (mode === 'view' && user?.companyNumber) {
      const fetchProposals = async () => {
        try {
          const data = await api.getMyProposals(user.companyNumber);
          setProposals(data);
        } catch (err) {
          console.error(err);
        }
      };
      fetchProposals();
    }
  }, [mode, user]);

  const handleSubmitProposal = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await api.submitProposal({
        problemId: form.problemId,
        title: form.title,
        description: form.description,
        proposedStartDate: form.startDate,
        proposedEndDate: form.endDate,
        proposedCost: form.cost
      });
      alert('Proposition soumise avec succès');
      // Reset form
      setForm({ problemId: '', title: '', description: '', startDate: '', endDate: '', cost: '' });
    } catch (err) {
      alert('Erreur lors de la soumission: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (projectId, action) => {
    try {
      if (action === 'start') await api.startWork(projectId, user.companyNumber);
      else if (action === 'delay') await api.delayWork(projectId, user.companyNumber);
      else if (action === 'complete') await api.completeWork(projectId, user.companyNumber);
      alert('Mise à jour effectuée');
      // Refresh list
      const data = await api.getMyProposals(user.companyNumber);
      setProposals(data);
    } catch (err) {
      alert('Erreur: ' + err.message);
    }
  };

  const handleDescriptionUpdate = async (projectId, newDescription) => {
    try {
      await api.updateProposalDescription(projectId, user.companyNumber, newDescription);
      alert('Description mise à jour');
      const data = await api.getMyProposals(user.companyNumber);
      setProposals(data);
    } catch (err) {
      alert('Erreur: ' + err.message);
    }
  };

  const handleEndDateUpdate = async (projectId, newEndDate) => {
    try {
      await api.updateProposalEndDate(projectId, user.companyNumber, newEndDate);
      alert('Date de fin mise à jour');
      const data = await api.getMyProposals(user.companyNumber);
      setProposals(data);
    } catch (err) {
      alert('Erreur: ' + err.message);
    }
  };

  if (mode === 'submit') {
    return (
      <div className="proposal-form">
        <h3>Soumettre une proposition</h3>
        <form onSubmit={handleSubmitProposal}>
          <input
            type="text"
            placeholder="ID du problème"
            value={form.problemId}
            onChange={(e) => setForm({ ...form, problemId: e.target.value })}
            required
            readOnly={!!initialProblemId}
          />
          <input
            placeholder="Titre du projet"
            value={form.title}
            onChange={(e) => setForm({ ...form, title: e.target.value })}
            required
          />
          <textarea
            placeholder="Description"
            value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
          />
          <input
            type="date"
            value={form.startDate}
            onChange={(e) => setForm({ ...form, startDate: e.target.value })}
            required
          />
          <input
            type="date"
            value={form.endDate}
            onChange={(e) => setForm({ ...form, endDate: e.target.value })}
            required
          />
          <input
            type="number"
            placeholder="Coût proposé"
            value={form.cost}
            onChange={(e) => setForm({ ...form, cost: e.target.value })}
            required
          />
          <button type="submit" disabled={loading}>
            {loading ? 'Envoi...' : 'Soumettre'}
          </button>
        </form>
      </div>
    );
  }

  return (
    <div className="proposals-list">
      <h3>Mes propositions</h3>
      {proposals.length === 0 && <p>Aucune proposition pour le moment.</p>}
      <ul>
        {proposals.map(p => (
          <li key={p.id}>
            <strong>{p.title}</strong> – Statut: {p.status}
            <p>{p.description}</p>
            <div>
              <button onClick={() => handleStatusUpdate(p.id, 'start')}>Commencer</button>
              <button onClick={() => handleStatusUpdate(p.id, 'delay')}>Retarder</button>
              <button onClick={() => handleStatusUpdate(p.id, 'complete')}>Terminer</button>
            </div>
            <div>
              <input
                placeholder="Nouvelle description"
                onChange={(e) => setUpdateForm({ ...updateForm, description: e.target.value })}
              />
              <button onClick={() => handleDescriptionUpdate(p.id, updateForm.description)}>
                Mettre à jour description
              </button>
            </div>
            <div>
              <input
                type="date"
                onChange={(e) => setUpdateForm({ ...updateForm, newDate: e.target.value })}
              />
              <button onClick={() => handleEndDateUpdate(p.id, updateForm.newDate)}>
                Changer date de fin
              </button>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Proposal;
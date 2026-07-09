import { useState, useEffect } from 'react';
import { useAuth } from '../context/RoleContext';
import { reportProblem, getMyReportedProblems } from '../api/api';

const ResidentProblems = () => {
  const { user } = useAuth();
  const [problems, setProblems] = useState([]);
  const [form, setForm] = useState({ neighbourhood: '', street: '', type: '', description: '' });

  useEffect(() => {
    if (user?.id) {
      getMyReportedProblems(user.id).then(res => setProblems(res.data)).catch(console.error);
    }
  }, [user]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });
  const handleSubmit = async (e) => {
    e.preventDefault();
    const problem = { resident: user, ...form };
    try {
      await reportProblem(problem);
      alert('Problem reported');
      const res = await getMyReportedProblems(user.id);
      setProblems(res.data);
      setForm({ neighbourhood: '', street: '', type: '', description: '' });
    } catch (err) {
      alert('Error reporting problem');
    }
  };

  return (
    <div>
      <h3>Report Problem</h3>
      <form onSubmit={handleSubmit}>
        <input name="neighbourhood" placeholder="Neighbourhood" onChange={handleChange} required />
        <input name="street" placeholder="Street" onChange={handleChange} required />
        <input name="type" placeholder="Type" onChange={handleChange} required />
        <textarea name="description" placeholder="Description" onChange={handleChange}></textarea>
        <button type="submit">Submit</button>
      </form>
      <h3>My Reported Problems</h3>
      <ul>{problems.map(p => <li key={p.id}>{p.type} – {p.street} (Priority: {p.priorite})</li>)}</ul>
    </div>
  );
};

export default ResidentProblems;
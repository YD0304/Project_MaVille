import { useRole } from '../context/RoleContext';

export const RoleSwitcher = () => {
  const { role, setRole } = useRole();
  const roles = [
    { key: 'resident', label: 'Résident' },
    { key: 'agent', label: 'Agent STPM' },
    { key: 'prestataire', label: 'Prestataire' },
  ];
  return (
    <div className="role-bar">
      <span style={{ fontSize: '13px', color: 'var(--color-text-secondary)', alignSelf: 'center', marginRight: '4px' }}>Role:</span>
      {roles.map(r => (
        <button key={r.key} className={`role-btn ${role === r.key ? 'active' : ''}`} onClick={() => setRole(r.key)}>
          {r.label}
        </button>
      ))}
    </div>
  );
};
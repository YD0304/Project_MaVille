import { useState, useEffect } from 'react';
import { api } from '../api/api';
import AppLayout from '../layouts/AppLayout';
import { useAuth } from '../auth/AuthContext';
import {
  Bell, Plus, Trash2, RotateCcw,
  MapPin, Map, Wrench, CheckCircle, XCircle,
} from 'lucide-react';

const SUBSCRIPTION_TYPES = [
  { value: 'QUARTIER', label: 'Quartier', placeholder: 'ex. Rosemont', icon: Map },
  { value: 'RUE', label: 'Rue', placeholder: 'ex. Rue Sainte-Catherine', icon: MapPin },
  { value: 'TYPE_PROBLEME', label: 'Type de travaux', placeholder: 'ex. TRAVAUX_ROUTIERS', icon: Wrench },
];

const inputStyle = {
  width: '100%',
  height: '2.5rem',
  paddingLeft: '2.5rem',
  paddingRight: '0.875rem',
  border: '1.5px solid var(--color-border-strong)',
  borderRadius: '0.5rem',
  background: 'var(--color-surface-2)',
  color: 'var(--color-text)',
  fontSize: '0.875rem',
  fontFamily: 'inherit',
  outline: 'none',
  transition: 'border-color 0.15s, box-shadow 0.15s',
};

export default function SubscriptionsPage() {
  const { user } = useAuth();
  const [subscriptions, setSubscriptions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [newType, setNewType] = useState('QUARTIER');
  const [newValue, setNewValue] = useState('');
  const [adding, setAdding] = useState(false);

  const loadSubscriptions = async () => {
    if (!user?.id) return;
    try {
      setLoading(true);
      const data = await api.getResidentSubscriptions(user.id);
      setSubscriptions(data || []);
      setError(null);
    } catch (err) {
      setError('Erreur lors du chargement des abonnements.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadSubscriptions();
  }, [user]);

  const handleAdd = async () => {
    if (!newValue.trim()) return;
    setAdding(true);
    try {
      await api.subscribeResident({
        residentId: user.id,
        type: newType,
        value: newValue.trim(),
      });
      setNewValue('');
      await loadSubscriptions();
    } catch (err) {
      setError("Erreur lors de l'ajout de l'abonnement.");
    } finally {
      setAdding(false);
    }
  };

  const handleRemove = async (id) => {
    try {
      await api.unsubscribeResident(id);
      setSubscriptions(prev => prev.map(s => s.id === id ? { ...s, active: false } : s));
    } catch (err) {
      setError('Erreur lors de la désactivation.');
    }
  };

  const handleReactivate = async (id) => {
    try {
      await api.reactivateResidentSubscription(id);
      setSubscriptions(prev => prev.map(s => s.id === id ? { ...s, active: true } : s));
    } catch (err) {
      setError('Erreur lors de la réactivation.');
    }
  };

  const selectedType = SUBSCRIPTION_TYPES.find(t => t.value === newType);
  const TypeIcon = selectedType?.icon || Map;

  return (
    <AppLayout>
      <div>
        <div className="page-header">
          <h2>Gérer mes abonnements</h2>
          <p>Abonnez-vous à des quartiers, rues ou types de travaux pour recevoir des notifications en temps réel.</p>
        </div>

        {error && (
          <div className="alert alert-error" style={{ marginBottom: '1rem' }}>
            <XCircle size={16} />
            <span>{error}</span>
            <button onClick={() => setError(null)} style={{ background: 'none', border: 'none', cursor: 'pointer', opacity: 0.6 }}>×</button>
          </div>
        )}

        {/* Add subscription */}
        <div className="card" style={{ maxWidth: '44rem', marginBottom: '1.5rem' }}>
          <h3 style={{ fontSize: '0.9375rem', fontWeight: 600, marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Plus size={16} /> Ajouter un abonnement
          </h3>
          <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap', alignItems: 'flex-end' }}>
            <div style={{ flex: '1 1 10rem', position: 'relative' }}>
              <div style={{
                position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)',
                color: 'var(--color-text-subtle)', pointerEvents: 'none',
              }}>
                <TypeIcon size={15} />
              </div>
              <select
                value={newType}
                onChange={e => setNewType(e.target.value)}
                style={{
                  ...inputStyle,
                  paddingRight: '2.5rem',
                  appearance: 'none',
                  cursor: 'pointer',
                  backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='14' viewBox='0 0 24 24' fill='none' stroke='%2364748b' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E")`,
                  backgroundRepeat: 'no-repeat',
                  backgroundPosition: 'right 0.75rem center',
                }}
              >
                {SUBSCRIPTION_TYPES.map(t => (
                  <option key={t.value} value={t.value}>{t.label}</option>
                ))}
              </select>
            </div>
            <div style={{ flex: '1 1 14rem', position: 'relative' }}>
              <div style={{
                position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)',
                color: 'var(--color-text-subtle)', pointerEvents: 'none',
              }}>
                <Bell size={15} />
              </div>
              <input
                type="text"
                placeholder={selectedType?.placeholder || 'Valeur'}
                value={newValue}
                onChange={e => setNewValue(e.target.value)}
                onKeyDown={e => e.key === 'Enter' && handleAdd()}
                style={inputStyle}
              />
            </div>
            <button className="btn btn-primary" onClick={handleAdd} disabled={adding || !newValue.trim()} style={{ height: '2.5rem' }}>
              {adding ? 'Ajout...' : 'Ajouter'}
            </button>
          </div>
        </div>

        {/* Current subscriptions */}
        <div style={{ maxWidth: '44rem' }}>
          <h3 style={{ fontSize: '0.9375rem', fontWeight: 600, marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Bell size={16} /> Mes abonnements
          </h3>

          {loading ? (
            <div className="empty" style={{ textAlign: 'center', padding: '2rem', color: 'var(--color-text-subtle)' }}>Chargement...</div>
          ) : subscriptions.length === 0 ? (
            <div className="empty" style={{ textAlign: 'center', padding: '2rem', color: 'var(--color-text-subtle)' }}>
              <Bell size={24} style={{ margin: '0 auto 0.5rem', opacity: 0.4 }} />
              <p>Aucun abonnement. Ajoutez-en un pour recevoir des notifications.</p>
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              {subscriptions.map(sub => {
                const typeDef = SUBSCRIPTION_TYPES.find(t => t.value === sub.type) || SUBSCRIPTION_TYPES[0];
                const Icon = typeDef.icon;
                return (
                  <div key={sub.id} className="card" style={{
                    display: 'flex', alignItems: 'center', gap: '0.75rem',
                    opacity: sub.active ? 1 : 0.5,
                  }}>
                    <div style={{
                      width: '2.25rem', height: '2.25rem', borderRadius: '0.5rem',
                      background: sub.active ? 'var(--color-primary-ghost)' : 'var(--color-surface-2)',
                      display: 'flex', alignItems: 'center', justifyContent: 'center',
                      flexShrink: 0, color: sub.active ? 'var(--color-primary)' : 'var(--color-text-subtle)',
                    }}>
                      <Icon size={15} />
                    </div>
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <p style={{ fontSize: '0.875rem', fontWeight: 500, margin: 0 }}>{sub.subscriptionValue}</p>
                      <p style={{ fontSize: '0.75rem', color: 'var(--color-text-subtle)', margin: '0.125rem 0 0' }}>{typeDef.label}</p>
                    </div>
                    {sub.active ? (
                      <span style={{
                        display: 'inline-flex', alignItems: 'center', gap: '0.25rem',
                        fontSize: '0.6875rem', fontWeight: 600, color: '#10b981',
                      }}>
                        <CheckCircle size={12} /> Actif
                      </span>
                    ) : (
                      <span style={{
                        display: 'inline-flex', alignItems: 'center', gap: '0.25rem',
                        fontSize: '0.6875rem', fontWeight: 600, color: '#ef4444',
                      }}>
                        <XCircle size={12} /> Inactif
                      </span>
                    )}
                    <div style={{ display: 'flex', gap: '0.375rem' }}>
                      {!sub.active && (
                        <button
                          className="btn btn-ghost"
                          onClick={() => handleReactivate(sub.id)}
                          title="Réactiver"
                          style={{ padding: '0.375rem', fontSize: '0.75rem' }}
                        >
                          <RotateCcw size={14} />
                        </button>
                      )}
                      <button
                        className="btn btn-ghost"
                        onClick={() => handleRemove(sub.id)}
                        title="Désactiver"
                        style={{ padding: '0.375rem', fontSize: '0.75rem', color: '#ef4444' }}
                      >
                        <Trash2 size={14} />
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>
    </AppLayout>
  );
}
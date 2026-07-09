import { useState, useEffect } from 'react';
import { api } from '../../api/api';
import AppLayout from '../../layouts/AppLayout';
import { useAuth } from '../../auth/AuthContext';
import {
  MapPin, Map, Wrench, FileText,
  Send, CheckCircle2, AlertCircle,
} from 'lucide-react';

const PROBLEM_TYPES = [
  { value: 'TRAVAUX_ROUTIERS',          label: 'Travaux routiers' },
  { value: 'TRAVAUX_GAZ_ELECTRICITE',   label: 'Travaux de gaz ou électricité' },
  { value: 'CONSTRUCTION_RENOVATION',   label: 'Construction ou rénovation' },
  { value: 'ENTRETIEN_PAYSAGER',        label: 'Entretien paysager' },
  { value: 'TRANSPORTS_COMMUN',         label: 'Travaux liés aux transports en commun' },
  { value: 'SIGNALISATION_ECLAIRAGE',   label: 'Travaux de signalisation et éclairage' },
  { value: 'TRAVAUX_SOUTERRAINS',       label: 'Travaux souterrains' },
  { value: 'TRAVAUX_RESIDENTIEL',       label: 'Travaux résidentiel' },
  { value: 'ENTRETIEN_URBAIN',          label: 'Entretien urbain' },
  { value: 'RESEAUX_TELECOMMUNICATION', label: 'Entretien des réseaux de télécommunication' }
];

const INITIAL_FORM = {
  street: '',
  neighbourhood: '',
  type: 'TRAVAUX_ROUTIERS',
  description: '',
};

function FieldIcon({ icon: Icon }) {
  return (
    <div style={{
      position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)',
      color: 'var(--color-text-subtle)', pointerEvents: 'none',
      display: 'flex', alignItems: 'center',
    }}>
      <Icon size={15} />
    </div>
  );
}

export default function ReportProblem() {
  const { user } = useAuth();
  const [form, setForm] = useState(INITIAL_FORM);
  const [alert, setAlert] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user?.neighbourhood) {
      setForm(prev => ({ ...prev, neighbourhood: user.neighbourhood }));
    }
  }, [user]);

  const set = (key) => (e) => setForm(f => ({ ...f, [key]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.street || !form.neighbourhood || !form.type || !form.description) {
      setAlert({ message: 'Veuillez remplir tous les champs obligatoires.', type: 'error' });
      return;
    }
    setLoading(true);
    // 🔧 Construction du payload avec l'ID du résident
    const payload = {
      street: form.street,
      neighbourhood: form.neighbourhood,
      type: form.type, 
      description: form.description,
      residentId: user.id, // ou residentId: user?.id, selon votre backend
    };
    console.log('[DEBUG] Payload :', payload);
    try {
      const result = await api.reportProblem(payload);
      if (result) {
        setAlert({ message: `Signalement #${result.id} soumis avec succès !`, type: 'success' });
        setForm(INITIAL_FORM);
        // Réapplique le quartier par défaut après réinitialisation
        if (user?.neighbourhood) {
          setForm(prev => ({ ...prev, neighbourhood: user.neighbourhood }));
        }
      } else {
        setAlert({ message: 'Une erreur est survenue. Veuillez réessayer.', type: 'error' });
      }
    } catch (err) {
      console.error('Erreur API détaillée :', err.response?.data || err.message);
      setAlert({ message: `Erreur : ${err.message || 'Problème serveur'}`, type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  // Styles (inchangés)
  const inputStyle = {
    width: '100%',
    height: '2.625rem',
    paddingLeft: '2.5rem',
    paddingRight: '0.875rem',
    border: '1.5px solid var(--color-border-strong)',
    borderRadius: '0.5rem',
    background: 'var(--color-surface-2)',
    color: 'var(--color-text)',
    fontSize: '0.9375rem',
    fontFamily: 'inherit',
    transition: 'border-color 0.15s, box-shadow 0.15s, background 0.15s',
    outline: 'none',
  };

  const inputFocus = (e) => {
    e.target.style.borderColor = 'var(--color-primary)';
    e.target.style.background = 'var(--color-surface)';
    e.target.style.boxShadow = '0 0 0 3px rgba(37,99,235,0.13)';
  };
  const inputBlur = (e) => {
    e.target.style.borderColor = 'var(--color-border-strong)';
    e.target.style.background = 'var(--color-surface-2)';
    e.target.style.boxShadow = 'none';
  };

  return (
    <AppLayout>
      <div>
        <div className="page-header">
          <h2>Signaler un problème</h2>
          <p>Soumettez un signalement dans votre quartier pour informer les équipes concernées.</p>
        </div>

        {alert && (
          <div className={`alert alert-${alert.type}`}>
            {alert.type === 'success' ? <CheckCircle2 size={18} style={{ flexShrink: 0 }} /> : <AlertCircle size={18} style={{ flexShrink: 0 }} />}
            <span style={{ flex: 1 }}>{alert.message}</span>
            <button onClick={() => setAlert(null)} style={{ background: 'none', border: 'none', cursor: 'pointer', opacity: 0.6, fontSize: '1rem', lineHeight: 1, padding: 0 }}>×</button>
          </div>
        )}

        <div className="card" style={{ maxWidth: '44rem' }}>
          <form onSubmit={handleSubmit}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.25rem', marginBottom: '1.25rem' }}>
              {/* Rue */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
                <label htmlFor="street">Rue <span style={{ color: 'var(--color-primary)' }}>*</span></label>
                <div style={{ position: 'relative' }}>
                  <FieldIcon icon={MapPin} />
                  <input
                    id="street"
                    type="text"
                    placeholder="ex. 123 rue Principale"
                    value={form.street}
                    onChange={set('street')}
                    style={inputStyle}
                    onFocus={inputFocus}
                    onBlur={inputBlur}
                  />
                </div>
              </div>

              {/* Quartier */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
                <label htmlFor="neighbourhood">Quartier <span style={{ color: 'var(--color-primary)' }}>*</span></label>
                <div style={{ position: 'relative' }}>
                  <FieldIcon icon={Map} />
                  <input
                    id="neighbourhood"
                    type="text"
                    placeholder="ex. Saint-Laurent"
                    value={form.neighbourhood}
                    onChange={set('neighbourhood')}
                    style={inputStyle}
                    onFocus={inputFocus}
                    onBlur={inputBlur}
                  />
                </div>
              </div>

              {/* Type */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
                <label htmlFor="type">Type de travaux <span style={{ color: 'var(--color-primary)' }}>*</span></label>
                <div style={{ position: 'relative' }}>
                  <FieldIcon icon={Wrench} />
                  <select
                    id="type"
                    value={form.type}
                    onChange={set('type')}
                    style={{
                      ...inputStyle,
                      paddingRight: '2.5rem',
                      backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%2364748b' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E")`,
                      backgroundRepeat: 'no-repeat',
                      backgroundPosition: 'right 0.75rem center',
                      appearance: 'none',
                      cursor: 'pointer',
                    }}
                    onFocus={inputFocus}
                    onBlur={inputBlur}
                  >
                    {PROBLEM_TYPES.map(t => (
                      <option key={t.value} value={t.value}>{t.label}</option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Description */}
              <div style={{ gridColumn: '1 / -1', display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
                <label htmlFor="description">Description <span style={{ color: 'var(--color-primary)' }}>*</span></label>
                <div style={{ position: 'relative' }}>
                  <div style={{
                    position: 'absolute', left: '0.75rem', top: '0.6875rem',
                    color: 'var(--color-text-subtle)', pointerEvents: 'none',
                  }}>
                    <FileText size={15} />
                  </div>
                  <textarea
                    id="description"
                    rows={4}
                    placeholder="Décrivez le problème en détail…"
                    value={form.description}
                    onChange={set('description')}
                    style={{
                      ...inputStyle,
                      height: 'auto',
                      paddingTop: '0.625rem',
                      paddingBottom: '0.625rem',
                      resize: 'vertical',
                      minHeight: '7rem',
                    }}
                    onFocus={inputFocus}
                    onBlur={inputBlur}
                  />
                </div>
              </div>
            </div>

            <div style={{ height: '1px', background: 'var(--color-border)', margin: '0 0 1.25rem' }} />

            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '1rem' }}>
              <p style={{ fontSize: '0.8125rem', color: 'var(--color-text-subtle)' }}>
                Les champs marqués <span style={{ color: 'var(--color-primary)' }}>*</span> sont obligatoires.
              </p>
              <button type="submit" className="btn btn-primary" disabled={loading} style={{ minWidth: '9rem', opacity: loading ? 0.7 : 1 }}>
                {loading ? (
                  <>
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ animation: 'spin 0.8s linear infinite' }}>
                      <path d="M21 12a9 9 0 1 1-6.219-8.56" />
                    </svg>
                    Envoi…
                  </>
                ) : (
                  <><Send size={15} /> Soumettre</>
                )}
              </button>
            </div>
          </form>
        </div>

        <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
      </div>
    </AppLayout>
  );
}
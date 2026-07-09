import { useAuth } from "../auth/AuthContext";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { ShieldCheck, Home, Wrench, LogIn, ArrowLeft } from "lucide-react";
import { api } from "../api/api";

const ROLES = [
  {
    value: "RESIDENT",
    label: "Résident",
    description: "Connectez-vous avec votre email",
    credentialLabel: "Email",
    credentialType: "email",
    credentialKey: "email",       // ← sent to api.login as { email, password }
    Icon: Home,
    color: "#10b981",
    activeBorder: "#10b981",
    border: "#10b981",
    bg: "rgba(16,185,129,0.08)",
  },
  {
    value: "PROVIDER",        // ← change from "PRESTATAIRE"
  label: "Prestataire",
  description: "Connectez-vous avec votre email",
  credentialLabel: "Email",
  credentialType: "email",
  credentialKey: "email",   // ← ensure you use email, not companyNumber
  Icon: Wrench,                         // change to "email"
    Icon: Wrench,
    color: "#f59e0b",
    activeBorder: "#f59e0b",
    border: "#f59e0b",
    bg: "rgba(245,158,11,0.08)",
  },
  {
    value: "ADMIN",
    label: "Administrateur",
    description: "Connectez-vous avec votre email",
    credentialLabel: "Email",
    credentialType: "email",
    credentialKey: "email",
    Icon: ShieldCheck,
    color: "#3b82f6",
    activeBorder: "#3b82f6",
    border: "#3b82f6",
    bg: "rgba(59,130,246,0.08)",
  },
];

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [step, setStep] = useState(1);
  const [role, setRole] = useState("RESIDENT");
  const [credential, setCredential] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const selected = ROLES.find((r) => r.value === role);

  const handleLogin = async () => {
    try {
      setLoading(true);

      const payload = {
        [selected.credentialKey]: credential,
        password,
      };

      const loginResponse = await api.login(payload);
      if (!loginResponse || !loginResponse.accessToken) {
        throw new Error('Invalid login response');
      }
      const { accessToken } = loginResponse;

      // Fetch user profile using the JWT we just received
      const userResponse = await fetch('/api/auth/me', {
        headers: { Authorization: `Bearer ${accessToken}` },
        credentials: 'include',
      });
      if (!userResponse.ok) {
        throw new Error(`Failed to fetch user profile: ${userResponse.status}`);
      }
      const userProfile = await userResponse.json();
      // 3. Build user object for AuthContext
      const userObject = {
        id: userProfile.id,
        email: userProfile.email,
        role: userProfile.role,
        name: userProfile.name,   // display name (you can use firstName if backend provides it)
        token: accessToken
      };

      // 4. Store in auth context
      login(userObject);

      // 5. Navigate based on role
      switch (userObject.role) {
        case "ADMIN":
          navigate("/admin");
          break;
        case "PROVIDER":
          navigate("/provider");
          break;
        default:
          navigate("/resident");
      }
    } catch (err) {
      alert("Identifiants incorrects");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };
  // ── shared input style ──────────────────────────────────────────
  const inputStyle = {
    width: "100%", padding: "0.75rem", borderRadius: "0.625rem",
    border: "1px solid rgba(255,255,255,0.1)",
    background: "rgba(255,255,255,0.04)", color: "#fff",
    fontSize: "0.9rem", outline: "none", boxSizing: "border-box",
  };

  return (
    <div style={{
      minHeight: "100vh", background: "#0f172a",
      display: "flex", alignItems: "center", justifyContent: "center",
      padding: "2rem", fontFamily: "inherit",
    }}>
      <div style={{ width: "100%", maxWidth: "26rem", position: "relative", zIndex: 1 }}>
        <div style={{
          background: "#1e293b", border: "1px solid rgba(255,255,255,0.08)",
          borderRadius: "1rem", overflow: "hidden",
          boxShadow: "0 24px 64px rgba(0,0,0,0.5)",
        }}>

          {/* Header */}
          <div style={{
            padding: "1.75rem 1.75rem 1.5rem",
            borderBottom: "1px solid rgba(255,255,255,0.07)",
          }}>
            <p style={{ fontSize: "1.0625rem", fontWeight: 700, color: "#f8fafc" }}>STPM</p>
            <h1 style={{ fontSize: "1.25rem", fontWeight: 700, color: "#f1f5f9", margin: "0.25rem 0 0" }}>
              {step === 1 ? "Qui êtes-vous ?" : `Connexion — ${selected.label}`}
            </h1>
          </div>

          <div style={{ padding: "1.5rem 1.75rem 1.75rem" }}>

            {/* ── STEP 1: role picker ── */}
            {step === 1 && (
              <>
                <div style={{ display: "flex", flexDirection: "column", gap: "0.625rem", marginBottom: "1.5rem" }}>
                  {ROLES.map((r) => {
                    const isActive = role === r.value;
                    return (
                      <button
                        key={r.value}
                        onClick={() => setRole(r.value)}
                        style={{
                          display: "flex", alignItems: "center", gap: "0.875rem",
                          padding: "0.875rem 1rem", borderRadius: "0.625rem",
                          border: isActive ? `1.5px solid ${r.activeBorder}` : "1.5px solid rgba(255,255,255,0.08)",
                          background: isActive ? r.bg : "rgba(255,255,255,0.03)",
                          cursor: "pointer", textAlign: "left", width: "100%",
                          outline: "none",
                        }}
                      >
                        <r.Icon size={16} color={isActive ? r.color : "rgba(255,255,255,0.35)"} />
                        <div style={{ flex: 1 }}>
                          <p style={{ fontSize: "0.875rem", fontWeight: isActive ? 600 : 500,
                            color: isActive ? "#f8fafc" : "rgba(255,255,255,0.6)", margin: 0 }}>
                            {r.label}
                          </p>
                          <p style={{ fontSize: "0.75rem",
                            color: isActive ? "rgba(255,255,255,0.5)" : "rgba(255,255,255,0.3)", margin: "0.2rem 0 0" }}>
                            {r.description}
                          </p>
                        </div>
                      </button>
                    );
                  })}
                </div>
                <button
                  onClick={() => setStep(2)}
                  style={{
                    width: "100%", padding: "0.75rem", borderRadius: "0.625rem",
                    border: "none",
                    background: "linear-gradient(135deg, #3b82f6, #1d4ed8)",
                    color: "#fff", fontSize: "0.9375rem", fontWeight: 600,
                    cursor: "pointer", display: "flex", alignItems: "center",
                    justifyContent: "center", gap: "0.5rem",
                  }}
                >
                  Continuer →
                </button>
              </>
            )}

            {/* ── STEP 2: credentials ── */}
            {step === 2 && (
              <>
                <button
                  onClick={() => setStep(1)}
                  style={{
                    background: "none", border: "none", color: "rgba(255,255,255,0.45)",
                    cursor: "pointer", fontSize: "0.8125rem", display: "flex",
                    alignItems: "center", gap: "0.4rem", padding: 0, marginBottom: "1.25rem",
                  }}
                >
                  <ArrowLeft size={14} /> Changer de rôle
                </button>

                <label style={{ fontSize: "0.8125rem", color: "rgba(255,255,255,0.5)", display: "block", marginBottom: "0.4rem" }}>
                  {selected.credentialLabel}
                </label>
                <input
                  type={selected.credentialType}
                  placeholder={selected.credentialLabel}
                  value={credential}
                  onChange={(e) => setCredential(e.target.value)}
                  style={{ ...inputStyle, marginBottom: "1rem" }}
                />

                <label style={{ fontSize: "0.8125rem", color: "rgba(255,255,255,0.5)", display: "block", marginBottom: "0.4rem" }}>
                  Mot de passe
                </label>
                <input
                  type="password"
                  placeholder="Mot de passe"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  onKeyDown={(e) => e.key === "Enter" && handleLogin()}
                  style={{ ...inputStyle, marginBottom: "1.5rem" }}
                />

                <button
                  onClick={handleLogin}
                  disabled={loading}
                  style={{
                    width: "100%", padding: "0.75rem", borderRadius: "0.625rem",
                    border: "none",
                    background: loading ? "rgba(59,130,246,0.5)" : "linear-gradient(135deg, #3b82f6, #1d4ed8)",
                    color: "#fff", fontSize: "0.9375rem", fontWeight: 600,
                    cursor: loading ? "not-allowed" : "pointer",
                    display: "flex", alignItems: "center", justifyContent: "center", gap: "0.5rem",
                  }}
                >
                  {loading ? "Connexion…" : <><LogIn size={16} /> Se connecter</>}
                </button>
              </>
            )}
          </div>
        </div>

        {/* test credentials hint */}
        <p style={{ textAlign: "center", marginTop: "1rem", fontSize: "0.7rem", color: "rgba(255,255,255,0.2)" }}>
          Résident: alice@example.com / alice123 · Prestataire: RW-001 / provider123
        </p>
      </div>
    </div>
  );
}
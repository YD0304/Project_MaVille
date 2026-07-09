import { useNavigate } from 'react-router-dom';
import { AlertTriangle } from 'lucide-react';  // ← changed from @phosphor-icons/react

export default function Button({ to, children }) {
  const navigate = useNavigate();
  return (
    <button onClick={() => navigate(to)}>
      {children}
    </button>
  );
}

//<Button to="/provider/assigned-problems">Go to Problems</Button>
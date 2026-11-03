import { useNavigate } from 'react-router-dom';
import { AlertTriangle } from 'lucide-react';  // ← changed from @phosphor-icons/react

export default function Button({ to, children, className, style }) {
  const navigate = useNavigate();
  return (
    <button onClick={() => navigate(to)} className={className} style={style}>
      {children}
    </button>
  );
}

//<Button to="/provider/assigned-problems">Go to Problems</Button>
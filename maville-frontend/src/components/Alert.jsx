import { useEffect } from 'react';

export const Alert = ({ message, type, onClose }) => {
  useEffect(() => {
    if (message) {
      const timer = setTimeout(onClose, 4000);
      return () => clearTimeout(timer);
    }
  }, [message, onClose]);

  if (!message) return null;
  const bgColor = type === 'success' ? '#EAF3DE' : type === 'error' ? '#FCEBEB' : '#E6F1FB';
  const color = type === 'success' ? '#27500A' : type === 'error' ? '#791F1F' : '#0C447C';
  return (
    <div style={{ padding: '10px 14px', borderRadius: '8px', marginBottom: '1rem', backgroundColor: bgColor, color, border: '0.5px solid #ccc' }}>
      {message}
    </div>
  );
};
import { useState, useRef, useEffect } from 'react';
import { Bell, Search, Check, X } from 'lucide-react';
import { useAuth } from '../auth/AuthContext';
import { useWebSocket } from '../context/WebSocketContext';
import { api } from '../api/api';

export default function Navbar() {
  const { user } = useAuth?.() || {};
  const { connected, notifications, unreadCount, markAsRead, clearUnread } = useWebSocket();
  const [open, setOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    const handler = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  const handleMarkRead = async (id) => {
    try {
      await api.markNotificationRead(id);
      markAsRead(id);
    } catch (err) {
      console.error('Failed to mark notification as read:', err);
    }
  };

  return (
    <header
      style={{
        height: '3.75rem',
        background: 'var(--color-surface)',
        borderBottom: '1px solid var(--color-border)',
        display: 'flex',
        alignItems: 'center',
        padding: '0 1.5rem',
        gap: '1rem',
        position: 'sticky',
        top: 0,
        zIndex: 20,
        boxShadow: 'var(--shadow-xs)',
      }}
    >
      {/* Search bar */}
      <div style={{
        flex: 1,
        maxWidth: '26rem',
        position: 'relative',
        display: 'flex',
        alignItems: 'center',
      }}>
        <Search
          size={15}
          style={{
            position: 'absolute',
            left: '0.75rem',
            color: 'var(--color-text-subtle)',
            pointerEvents: 'none',
          }}
        />
        <input
          type="search"
          placeholder="Rechercher…"
          style={{
            width: '100%',
            height: '2.25rem',
            paddingLeft: '2.25rem',
            paddingRight: '0.875rem',
            borderRadius: '0.5rem',
            border: '1.5px solid var(--color-border-strong)',
            background: 'var(--color-surface-2)',
            color: 'var(--color-text)',
            fontSize: '0.875rem',
            transition: 'border-color 0.15s, box-shadow 0.15s',
            outline: 'none',
          }}
          onFocus={e => {
            e.target.style.borderColor = 'var(--color-primary)';
            e.target.style.boxShadow = '0 0 0 3px rgba(37,99,235,0.12)';
          }}
          onBlur={e => {
            e.target.style.borderColor = 'var(--color-border-strong)';
            e.target.style.boxShadow = 'none';
          }}
        />
      </div>

      <div style={{ flex: 1 }} />

      {/* Notifications */}
      <div ref={dropdownRef} style={{ position: 'relative' }}>
        <button
          onClick={() => setOpen(!open)}
          style={{
            width: '2.25rem',
            height: '2.25rem',
            borderRadius: '0.5rem',
            border: open ? '1.5px solid var(--color-primary)' : '1.5px solid var(--color-border)',
            background: open ? 'var(--color-primary-ghost)' : 'var(--color-surface-2)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer',
            color: open ? 'var(--color-primary)' : 'var(--color-text-muted)',
            position: 'relative',
            transition: 'all 0.15s',
          }}
          onMouseEnter={e => {
            e.currentTarget.style.borderColor = 'var(--color-primary)';
            e.currentTarget.style.color = 'var(--color-primary)';
            e.currentTarget.style.background = 'var(--color-primary-ghost)';
          }}
          onMouseLeave={e => {
            if (!open) {
              e.currentTarget.style.borderColor = 'var(--color-border)';
              e.currentTarget.style.color = 'var(--color-text-muted)';
              e.currentTarget.style.background = 'var(--color-surface-2)';
            }
          }}
          aria-label="Notifications"
        >
          <Bell size={16} />
          {unreadCount > 0 && (
            <span style={{
              position: 'absolute', top: '0.125rem', right: '0.125rem',
              minWidth: '1rem', height: '1rem',
              borderRadius: '9999px', background: '#ef4444',
              border: '2px solid var(--color-surface)',
              fontSize: '0.625rem', fontWeight: 700, color: '#fff',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              lineHeight: 1,
            }}>
              {unreadCount > 9 ? '9+' : unreadCount}
            </span>
          )}
        </button>

        {/* Dropdown */}
        {open && (
          <div style={{
            position: 'absolute',
            top: 'calc(100% + 0.5rem)',
            right: 0,
            width: '22rem',
            maxHeight: '24rem',
            background: 'var(--color-surface)',
            border: '1px solid var(--color-border)',
            borderRadius: '0.75rem',
            boxShadow: '0 8px 32px rgba(0,0,0,0.15)',
            overflow: 'hidden',
            display: 'flex',
            flexDirection: 'column',
            zIndex: 100,
          }}>
            <div style={{
              padding: '0.875rem 1rem',
              borderBottom: '1px solid var(--color-border)',
              display: 'flex', justifyContent: 'space-between', alignItems: 'center',
            }}>
              <span style={{ fontSize: '0.875rem', fontWeight: 600 }}>
                Notifications {connected ? '' : '(déconnecté)'}
              </span>
              {unreadCount > 0 && (
                <button
                  onClick={clearUnread}
                  style={{ fontSize: '0.75rem', background: 'none', border: 'none', cursor: 'pointer', color: 'var(--color-primary)', padding: 0, fontFamily: 'inherit' }}
                >
                  Tout marquer lu
                </button>
              )}
            </div>

            <div style={{
              overflowY: 'auto', flex: 1,
              display: 'flex', flexDirection: 'column',
            }}>
              {notifications.length === 0 ? (
                <div style={{
                  padding: '2rem 1rem', textAlign: 'center',
                  color: 'var(--color-text-subtle)', fontSize: '0.8125rem',
                }}>
                  Aucune notification
                </div>
              ) : (
                notifications.map((notif) => (
                  <div
                    key={notif.id}
                    style={{
                      padding: '0.75rem 1rem',
                      borderBottom: '1px solid var(--color-border)',
                      background: notif.read ? 'transparent' : 'var(--color-primary-ghost)',
                      display: 'flex', gap: '0.625rem', alignItems: 'flex-start',
                      transition: 'background 0.15s',
                    }}
                    onMouseEnter={e => { e.currentTarget.style.background = 'var(--color-surface-2)'; }}
                    onMouseLeave={e => { e.currentTarget.style.background = notif.read ? 'transparent' : 'var(--color-primary-ghost)'; }}
                  >
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <p style={{
                        fontSize: '0.8125rem', margin: 0,
                        color: notif.read ? 'var(--color-text-subtle)' : 'var(--color-text)',
                        fontWeight: notif.read ? 400 : 500,
                        lineHeight: 1.4,
                      }}>
                        {notif.message}
                      </p>
                      <p style={{
                        fontSize: '0.6875rem', margin: '0.25rem 0 0',
                        color: 'var(--color-text-subtle)',
                      }}>
                        {notif.createdAt ? new Date(notif.createdAt).toLocaleString() : ''}
                      </p>
                    </div>
                    {!notif.read && (
                      <button
                        onClick={() => handleMarkRead(notif.id)}
                        style={{
                          background: 'none', border: 'none', cursor: 'pointer',
                          padding: '0.25rem', color: 'var(--color-text-subtle)',
                          borderRadius: '0.25rem', flexShrink: 0,
                        }}
                        title="Marquer lu"
                      >
                        <Check size={14} />
                      </button>
                    )}
                  </div>
                ))
              )}
            </div>
          </div>
        )}
      </div>

      {/* WebSocket status indicator */}
      <div style={{
        width: '0.5rem', height: '0.5rem', borderRadius: '9999px',
        background: connected ? '#10b981' : '#ef4444',
        flexShrink: 0,
      }} title={connected ? 'Connecté en temps réel' : 'Temps réel déconnecté'} />

      {/* Avatar */}
      {user && (
        <div style={{
          width: '2.25rem', height: '2.25rem', borderRadius: '9999px',
          background: 'linear-gradient(135deg, var(--color-primary-light), var(--color-accent))',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          fontSize: '0.8125rem', fontWeight: 700, color: '#fff',
          cursor: 'pointer', flexShrink: 0,
          boxShadow: '0 0 0 2px var(--color-surface), 0 0 0 3.5px var(--color-primary-light)',
        }}>
          {user?.name?.[0]?.toUpperCase() || 'U'}
        </div>
      )}
    </header>
  );
}

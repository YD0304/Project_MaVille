import { createContext, useContext, useState, useEffect, useRef, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useAuth } from '../auth/AuthContext';

const WebSocketContext = createContext();

const WS_URL = 'http://localhost:7070/ws';

export function WebSocketProvider({ children }) {
  const { user, token } = useAuth();
  const clientRef = useRef(null);
  const [connected, setConnected] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);

  const addNotification = useCallback((notif) => {
    setNotifications(prev => [notif, ...prev]);
    if (!notif.read) {
      setUnreadCount(prev => prev + 1);
    }
  }, []);

  const markAsRead = useCallback((notificationId) => {
    setNotifications(prev =>
      prev.map(n => n.id === notificationId ? { ...n, read: true } : n)
    );
    setUnreadCount(prev => Math.max(0, prev - 1));
  }, []);

  const clearUnread = useCallback(() => {
    setUnreadCount(0);
  }, []);

  useEffect(() => {
    if (!token || !user) {
      if (clientRef.current?.connected) {
        clientRef.current.deactivate();
      }
      setConnected(false);
      setNotifications([]);
      setUnreadCount(0);
      return;
    }

    const client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      connectHeaders: {
        'X-Authorization': `Bearer ${token}`,
      },
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      reconnectDelay: 5000,
      onConnect: () => {
        setConnected(true);

        const userType = user.role;
        const userId = user.email;

        client.subscribe(`/topic/notifications/${userType.toLowerCase()}/${userId}`, (message) => {
          try {
            const notif = JSON.parse(message.body);
            addNotification(notif);
          } catch (err) {
            console.error('Failed to parse notification:', err);
          }
        });

        if (userType === 'STPM') {
          client.subscribe('/topic/notifications/stpm/STPM_AGENT', (message) => {
            try {
              const notif = JSON.parse(message.body);
              addNotification(notif);
            } catch (err) {
              console.error('Failed to parse STPM notification:', err);
            }
          });
        }
      },
      onDisconnect: () => {
        setConnected(false);
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame.headers?.message);
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
      setConnected(false);
    };
  }, [token, user]);

  return (
    <WebSocketContext.Provider value={{
      connected,
      notifications,
      unreadCount,
      markAsRead,
      clearUnread,
    }}>
      {children}
    </WebSocketContext.Provider>
  );
}

export function useWebSocket() {
  const ctx = useContext(WebSocketContext);
  if (!ctx) throw new Error('useWebSocket must be used within WebSocketProvider');
  return ctx;
}

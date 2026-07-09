package ca.udem.maville.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ca.udem.maville.model.Notification;

@Service
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void pushNotification(Notification notification) {
        String destination = "/topic/notifications/" + notification.getUserType().toLowerCase()
                + "/" + notification.getUserId();
        messagingTemplate.convertAndSend(destination, notification);
    }
}

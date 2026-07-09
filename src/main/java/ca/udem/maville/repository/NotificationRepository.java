package ca.udem.maville.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.udem.maville.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndUserTypeOrderByCreatedAtDesc(String userId, String userType);
    List<Notification> findByUserIdAndUserTypeAndReadFalse(String userId, String userType);
}
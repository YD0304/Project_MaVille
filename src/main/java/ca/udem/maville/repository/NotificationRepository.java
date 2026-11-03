package ca.udem.maville.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.udem.maville.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndUserTypeOrderByCreatedAtDesc(String userId, String userType);
    List<Notification> findByUserIdAndUserTypeAndReadFalse(String userId, String userType);
}
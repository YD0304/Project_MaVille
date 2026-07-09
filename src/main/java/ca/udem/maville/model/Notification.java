package ca.udem.maville.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;      // e.g., resident email or provider companyNumber
    private String userType;    // "RESIDENT", "PROVIDER", "STPM"
    private String message;
    private Long relatedEntityId;   // optional: projectId, problemId, proposalId
    private boolean read;
    private LocalDateTime createdAt;

    // constructors, getters, setters
    public Notification() {}

    public Notification(String userId, String userType, String message, Long relatedEntityId) {
        this.userId = userId;
        this.userType = userType;
        this.message = message;
        this.relatedEntityId = relatedEntityId;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(Long relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public boolean isRead() {
        return read;
    }
    public void setRead(boolean read) {
        this.read = read;

    
    } public LocalDateTime getCreatedAt() {
        return createdAt;
    }   public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
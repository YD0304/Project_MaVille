package ca.udem.maville.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class ProviderSubscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Provider provider;
    
    @Enumerated(EnumType.STRING)
    private AbonnementType type;
    
   private String subscriptionValue;     // neighbourhood or problem type
    private boolean active;
    
    // Required no-arg constructor
    public ProviderSubscription() {}
    
    // Constructor matching the one used in the controller
    public ProviderSubscription(Provider provider, AbonnementType type, String subscriptionValue, boolean active) {
        this.provider = provider;
        this.type = type;
        this.subscriptionValue = subscriptionValue;
        this.active = active;
    }
    
    // Getters and setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }
    public AbonnementType getType() { return type; }
    public void setType(AbonnementType type) { this.type = type; }
    public String getSubscriptionValue() { return subscriptionValue; }
    public void setSubscriptionValue(String subscriptionValue) { this.subscriptionValue = subscriptionValue; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
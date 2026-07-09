package ca.udem.maville.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue; // assuming you have a Resident entity
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class ResidentSubscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Resident resident;
    private AbonnementType type; // Type d'abonnement (QUARTIER, RUE, TYPE_PROBLEME, etc.)
    private String subscriptionValue;    // or null if not set
    // getters/setters
    private boolean active;

    public ResidentSubscription() {}

    public ResidentSubscription(Resident resident, AbonnementType type, String value,
            boolean active) {
        this.resident = resident;
        this.type = type;
        this.subscriptionValue = value;
        this.active = active;
    }

    // Getters and Setters

    /**
     * Retourne l'identifiant unique de l'abonnement.
     * 
     * @return ID de l'abonnement
     */

    public Long getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique de l'abonnement.
     * 
     * @param id Nouvel ID de l'abonnement
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retourne le type d'abonnement.
     * 
     * @return Type d'abonnement
     */
    public AbonnementType getType() {
        return type;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
            this.resident = resident;
    }

    /**
     * Définit le type d'abonnement.
     * 
     * @param type Nouveau type d'abonnement
     */
    public void setType(AbonnementType type) {
        this.type = type;
    }

    /**
     * Retourne le sujet de l'abonnement.
     * 
     * @return Sujet spécifique (nom de quartier, rue, etc.)
     */
    public String getSubscriptionValue() {
        return subscriptionValue;
    }

    /**
     * Définit le sujet de l'abonnement.
     * 
     * @param subscriptionValue Nouvelle valeur d'abonnement
     */
    public void setSubscriptionValue(String subscriptionValue) {
        this.subscriptionValue = subscriptionValue;
    }

    
    /**
     * Vérifie si l'abonnement est actif.
     * 
     * @return true si l'abonnement est actif, false sinon
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Active ou désactive l'abonnement.
     * 
     * @param active Nouvel état d'activation
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}

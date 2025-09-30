package ca.udem.maville.model;

import ca.udem.maville.enums.AbonnementType;

/**
 * Représente un abonnement à un type d'information municipale.
 * 
 * <p>
 * Les abonnements permettent aux résidents et prestataires de recevoir
 * des notifications ciblées selon leurs intérêts.
 * </p>
 * 
 * <p>
 * Caractéristiques principales :
 * <ul>
 * <li>Type d'abonnement (quartier, rue, type de problème)</li>
 * <li>Sujet spécifique (ex: nom de rue, type de problème)</li>
 * <li>Association soit à un résident soit à un prestataire</li>
 * <li>État actif/inactif</li>
 * </ul>
 * 
 * <p>
 * Remarques d'implémentation :
 * <ul>
 * <li>Un abonnement est toujours associé soit à un résident (residentId) soit à
 * un prestataire (prestataireId)</li>
 * <li>Les champs residentId et prestataireId sont mutuellement exclusifs</li>
 * </ul>
 * 
 * @author Team 6
 * @version 1.0
 */
public class Abonnement {
    private int id;
    private AbonnementType type;
    private String sujet;
    private Integer residentId; // null for prestataire subscriptions
    private Integer prestataireId; // null for resident subscriptions
    private boolean active;

    /**
     * Constructeur sans paramètres requis pour la désérialisation JSON.
     */
    public Abonnement() {
        // constructeur vide requis pour Jackson
    }

    /**
     * Constructeur principal pour créer un abonnement.
     * 
     * @param type          Type d'abonnement (QUARTIER, RUE, etc.)
     * @param sujet         Sujet de l'abonnement (ex: "Plateau-Mont-Royal",
     *                      "TRAVAUX_ROUTIER")
     * @param residentId    ID du résident associé (null si prestataire)
     * @param prestataireId ID du prestataire associé (null si résident)
     * @param active        État d'activation de l'abonnement
     * 
     * @throws IllegalArgumentException Si residentId et prestataireId sont tous
     *                                  deux non-nuls ou tous deux nuls
     */
    public Abonnement(AbonnementType type, String sujet,
            Integer residentId, Integer prestataireId,
            boolean active) {
        this.type = type;
        this.sujet = sujet;
        this.residentId = residentId;
        this.prestataireId = prestataireId;
        this.active = active;
    }

    // Getters and Setters

    /**
     * Retourne l'identifiant unique de l'abonnement.
     * 
     * @return ID de l'abonnement
     */

    public int getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique de l'abonnement.
     * 
     * @param id Nouvel ID de l'abonnement
     */
    public void setId(int id) {
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
    public String getSujet() {
        return sujet;
    }

    /**
     * Définit le sujet de l'abonnement.
     * 
     * @param sujet Nouveau sujet d'abonnement
     */
    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    /**
     * Retourne l'ID du résident associé.
     * 
     * @return ID du résident ou null si abonnement prestataire
     */
    public Integer getResidentId() {
        return residentId;
    }

    /**
     * Définit l'ID du résident associé.
     * 
     * @param residentId Nouvel ID de résident (doit être null si prestataireId est
     *                   défini)
     */
    public void setResidentId(Integer residentId) {
        this.residentId = residentId;
    }

    /**
     * Retourne l'ID du prestataire associé.
     * 
     * @return ID du prestataire ou null si abonnement résident
     */
    public Integer getPrestataireId() {
        return prestataireId;
    }

    /**
     * Définit l'ID du prestataire associé.
     * 
     * @param prestataireId Nouvel ID de prestataire (doit être null si residentId
     *                      est défini)
     */
    public void setPrestataireId(Integer prestataireId) {
        this.prestataireId = prestataireId;
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

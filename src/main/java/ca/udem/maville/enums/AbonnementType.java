package ca.udem.maville.enums;

/**
 * Enumération représentant les types d'abonnements disponibles dans le système.
 * 
 * <p>
 * Ces abonnements permettent aux utilisateurs de recevoir des notifications
 * ciblées selon différents critères géographiques ou thématiques.
 * </p>
 * 
 * <p>
 * Types disponibles :
 * <ul>
 * <li><b>QUARTIER</b> : Abonnement au niveau du quartier</li>
 * <li><b>RUE</b> : Abonnement au niveau de la rue</li>
 * <li><b>TYPE_PROBLEME</b> : Abonnement par type de problème</li>
 * </ul>
 * 
 * @author Team 6
 * @version 1.0
 */
public enum AbonnementType {
    QUARTIER,
    RUE, 
    TYPE_PROBLEME
}
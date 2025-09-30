package ca.udem.maville.enums;

public enum TravauxType {
    TRAVAUX_ROUTIERS("Travaux routiers"),
    TRAVAUX_GAZ_ELECTRICITE("Travaux de gaz ou électricité"),
    CONSTRUCTION_RENOVATION("Construction ou rénovation"),
    ENTRETIEN_PAYSAGER("Entretien paysager"),
    TRANSPORTS_COMMUN("Travaux liés aux transports en commun"),
    SIGNALISATION_ECLAIRAGE("Travaux de signalisation et éclairage"),
    TRAVAUX_SOUTERRAINS("Travaux souterrains"),
    TRAVAUX_RESIDENTIEL("Travaux résidentiel"),
    ENTRETIEN_URBAIN("Entretien urbain"),
    RESEAUX_TELECOMMUNICATION("Entretien des réseaux de télécommunication");

    private final String description;

    TravauxType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
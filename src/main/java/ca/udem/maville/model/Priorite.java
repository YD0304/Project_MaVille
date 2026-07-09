package ca.udem.maville.model;

public enum Priorite {
    NOT_ASSIGNED("Non assignée"),
    REFUSED("Refusée"),
    FAIBLE("Faible"),
    MOYENNE("Moyenne"),
    ELEVEE("Élevée");

    private final String description;

    Priorite(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
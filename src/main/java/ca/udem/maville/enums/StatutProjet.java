// ProjectStatus.java
package ca.udem.maville.enums;

public enum StatutProjet {
    PROPOSAL_SUBMITED, PROPOSAL_REFUSED, PROPOSAL_ACCEPTED, PERMIT_ISSUED, PROJECT_FINISHED;

    public static StatutProjet fromApiValue(String apiStatus) {
        if (apiStatus == null) {
            return null;
        }
        switch (apiStatus) {
            case "Permis émis":
                return PERMIT_ISSUED;
            case "Fermé":
                return PROJECT_FINISHED;
            case "Projet futur":
                return PROPOSAL_ACCEPTED;
            default:
                // Return a default value or throw an exception for unknown statuses
                return null; // Or a specific UNKNOWN constant
        }
    }
}
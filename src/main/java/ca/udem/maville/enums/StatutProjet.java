// ProjectStatus.java
package ca.udem.maville.enums;

public enum StatutProjet {
    PROPOSAL_SUBMITED, PROPOSAL_REFUSED, PERMIT_ISSUED, PROJECT_FINISHED;

    public static StatutProjet fromApiValue(String apiStatus) {
        if (apiStatus == null) {
            return null;
        }
        else if ( apiStatus.equalsIgnoreCase("Permis émis")){
                return PERMIT_ISSUED;}
        else{
            throw new IllegalArgumentException("Unknown API status: " + apiStatus);
        }
}}
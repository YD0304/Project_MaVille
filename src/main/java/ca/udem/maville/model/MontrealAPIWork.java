package ca.udem.maville.model;

import com.fasterxml.jackson.annotation.JsonProperty;
public class MontrealAPIWork {
    @JsonProperty("_id")
    private String id;

    @JsonProperty("boroughid")
    private String boroughId;

    @JsonProperty("currentstatus")
    private String currentStatus;

    @JsonProperty("reason_category")
    private String reasonCategory;

    @JsonProperty("occupancy_name")
    private String occupancy_name;

    @JsonProperty("submittercategory")
    private String submitterCategory;

    @JsonProperty("organizationname")
    private String organizationName;

    @JsonProperty("duration_start_date")
    private String duration_start_date;

    @JsonProperty("duration_end_date")
    private String duration_end_date;

    public MontrealAPIWork() {
    }
    public MontrealAPIWork(String id, String boroughId, String occupancy_name, String currentStatus,
                          String reasonCategory, String submitterCategory,
                          String organizationName, String duration_start_date, String duration_end_date) {
        this.id = id;
        this.boroughId = boroughId;
        this.occupancy_name = occupancy_name;
        this.currentStatus = currentStatus;
        this.reasonCategory = reasonCategory;
        this.submitterCategory = submitterCategory;
        this.organizationName = organizationName;
        this.duration_start_date = duration_start_date;
        this.duration_end_date = duration_end_date;

    }

    public String getId() {
        return id;
    }
    public String getBoroughId() {
        return boroughId;
    }
    public String getCurrentStatus() {
        return currentStatus;
    }
    public String getReasonCategory() {
        return reasonCategory;
    }

    public String getOccupancy_name() {
        return occupancy_name;
    }

    public String getSubmitterCategory() {
        return submitterCategory;
    }
    public String getOrganizationName() {
        return organizationName;
    }
    public String getDuration_start_date() {
        return duration_start_date;
    }
    public String getDuration_end_date() {
        return duration_end_date;
    }

    public String getTitle() {
        return "Travaux ID: " + id + " dans l'arrondissement " + boroughId + " sur " + occupancy_name;
    }
    public String getDescription() {
        return "Travaux dans l'arrondissement " + boroughId + "sur "+ occupancy_name + " par " + organizationName +
                " pour " + reasonCategory + ". Statut: " + currentStatus;
    }

    @Override
    public String toString() {
        return "ID du travail: " + id + "\n" +
                "  Arrondissement: " + boroughId + "\n" +
                "  Rue/Emplacement: " + occupancy_name + "\n" +
                "  Organisation: " + organizationName + "\n" +
                "  Catégorie de prestataire: " + submitterCategory + "\n" +
                "  Motif du travail: " + reasonCategory + "\n" +
                "  Statut actuel: " + currentStatus + "\n" +
                "---------------------------------";
    }
}
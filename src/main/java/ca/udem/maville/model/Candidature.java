package ca.udem.maville.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import ca.udem.maville.enums.Priorite;
import ca.udem.maville.enums.StatutProjet;
import ca.udem.maville.enums.TravauxType;

public class Candidature {
    private int id;
    private String title;
    @JsonIgnore
    private Problem problem;
    private String description;
    private double cost;
    private String startDate;
    private String endDate;
    private StatutProjet status;
    private int reportedCount;
    private Prestataire prestataire;

    @JsonCreator
    public Candidature(
            @JsonProperty("id") int id,
            @JsonProperty("title") String title, // Corrected from "titre"
            @JsonProperty("problem") Problem problem,
            @JsonProperty("description") String description,
            @JsonProperty("cout") double cost,
            @JsonProperty("dateDebut") String startDate,
            @JsonProperty("dateFin") String endDate,
            @JsonProperty("status") StatutProjet status,
            @JsonProperty("prestataire") Prestataire prestataire) {
        this.id = id;
        this.title = title;
        this.problem = problem;
        this.description = description;
        this.cost = cost;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.prestataire = prestataire;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Problem getProblem() { return problem; }
    public void setProblem(Problem problem) { this.problem = problem; }

    public TravauxType getProblemType() { return problem.getProblemtype(); }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public StatutProjet getStatus() { return status; }
    public void setStatus(StatutProjet status) { this.status = status; }

    public Prestataire getPrestataire() { return prestataire;}
    public void setPrestataire(Prestataire prestataire) { this.prestataire = prestataire; }

    public String getPrestataireContact() {
        return prestataire != null ? prestataire.getContact() : "Unknown";
    }

    public int getReportedCount(){
        return reportedCount;
    }

    public String getLocation() {
        if (problem != null) {
            return problem.getNeigbourhood() + ", " + problem.getStreet();
        }
        return "Unknown";
    }

    public Priorite getPriority() {
        return problem != null ? problem.getPrioriteType() : null;
    }

    public TravauxType getWorkType() {
        return problem != null ? problem.getProblemtype() : null;
    }

    @Override
    public String toString() {
        return "ID: " + id +
               ", Title: " + title +
               ", Location: " + getLocation() +
               ", Status: " + status +
               ", Priority: " + getPriority() +
               ", Work Type: " + getWorkType() +
               ", Reports: " + getReportedCount() +
               ", Start: " + startDate +
               ", End: " + endDate +
               ", Prestataire: " + prestataire;
    }
}

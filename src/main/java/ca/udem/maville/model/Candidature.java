// package ca.udem.maville.model;

// import com.fasterxml.jackson.annotation.JsonCreator;
// import com.fasterxml.jackson.annotation.JsonProperty;
// import jakarta.persistence.*;
// import java.util.List;

// @Entity
// @Table(name = "candidatures")   // fixed table name
// public class Candidature {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private int id;              // no more static counter

//     private String title;
//     private String description;
//     private double cost;
//     private String startDate;
//     private String endDate;

//     @Enumerated(EnumType.STRING)
//     private StatutProjet status;

//     private int reportedCount;

//     // Relationships
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "problem_id")
//     private Problem problem;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "prestataire_id")
//     private Provider prestataire;

//     // JPA requires a no-arg constructor
//     public Candidature() {}

//     // Keep your @JsonCreator constructor, but remove manual ID assignment
//     @JsonCreator
//     public Candidature(@JsonProperty("title") String title,
//                        @JsonProperty("description") String description,
//                        @JsonProperty("cost") double cost,
//                        @JsonProperty("startDate") String startDate,
//                        @JsonProperty("endDate") String endDate,
//                        @JsonProperty("status") StatutProjet status,
//                        @JsonProperty("problem") Problem problem,
//                        @JsonProperty("prestataire") Provider prestataire) {
//         // Do NOT set id here – JPA will assign it
//         this.title = title;
//         this.description = description;
//         this.cost = cost;
//         this.startDate = startDate;
//         this.endDate = endDate;
//         this.status = status;
//         this.problem = problem;
//         this.prestataire = prestataire;
//     }

//     // Getters and setters (unchanged)
//     public int getId() { return id; }
//     public void setId(int id) { this.id = id; }

//     public String getTitle() { return title; }
//     public void setTitle(String title) { this.title = title; }

//     public String getDescription() { return description; }
//     public void setDescription(String description) { this.description = description; }

//     public double getCost() { return cost; }
//     public void setCost(double cost) { this.cost = cost; }

//     @JsonProperty("dateDebut")
//     public String getStartDate() { return startDate; }
//     public void setStartDate(String startDate) { this.startDate = startDate; }

//     @JsonProperty("dateFin")
//     public String getEndDate() { return endDate; }
//     public void setEndDate(String endDate) { this.endDate = endDate; }

//     public StatutProjet getStatus() { return status; }
//     public void setStatus(StatutProjet status) { this.status = status; }

//     public int getReportedCount() { return reportedCount; }
//     public void setReportedCount(int reportedCount) { this.reportedCount = reportedCount; }

//     public Problem getProblem() { return problem; }
//     public void setProblem(Problem problem) { this.problem = problem; }

//     public Provider getPrestataire() { return prestataire; }
//     public void setPrestataire(Provider prestataire) { this.prestataire = prestataire; }

//     // Remove synchroniserCompteurId() entirely

//     public boolean peutEtreModifiee() {
//         return status == StatutProjet.PROPOSAL_SUBMITED;
//     }

//     @Override
//     public String toString() {
//         return "Candidature #" + id + " par " + 
//                (prestataire != null ? prestataire.getCompanyName() : "N/A") + 
//                " - " + cost + "$ (" + 
//                (status != null ? status : "N/A") + ")";
//     }
// }
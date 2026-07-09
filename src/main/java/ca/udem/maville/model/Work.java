// package ca.udem.maville.model;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;

// import jakarta.persistence.Entity;
// import jakarta.persistence.EnumType;
// import jakarta.persistence.Enumerated;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.JoinTable;
// import jakarta.persistence.ManyToMany;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.OneToOne;
// import jakarta.persistence.PreUpdate;
// import jakarta.persistence.Table;

// @Entity
// @Table(name = "works")
// public class Work {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     private String title;
//     private String description;
//     private double cost;

//     private WorkType category;          
//     private String neighbourhood;
//     private String street;

//     private LocalDate startDate;
//     private LocalDate endDate;


//     @Enumerated(EnumType.STRING)
//     private StatutProjet status;

//     @ManyToOne
//     @JoinColumn(name = "prestataire_id")
//     private Provider provider;

//     @OneToOne
//     @JoinColumn(name = "candidature_id")
//     private Candidature candidature;   // candidature acceptée à l’origine du projet

//     @ManyToMany
//     @JoinTable(name = "work_problems",
//                joinColumns = @JoinColumn(name = "work_id"),
//                inverseJoinColumns = @JoinColumn(name = "problem_id"))
//     private List<Problem> problems = new ArrayList<>();

//     private LocalDateTime lastUpdate;

//     // Constructeurs
//     public Work() {}

//     public Work(String title, String description, double cost,
//                 LocalDate startDate, LocalDate endDate,
//                 Provider provider, Candidature candidature) {
//         this.title = title;
//         this.description = description;
//         this.cost = cost;
//         this.startDate = startDate;
//         this.endDate = endDate;
//         this.provider = provider;
//         this.candidature = candidature;
//         this.status = StatutProjet.PERMIT_ISSUED;   // statut initial
//         this.lastUpdate = LocalDateTime.now();
//     }

//     // Méthodes métier (logique de transition)

//     public void start() {
//         if (status == StatutProjet.PERMIT_ISSUED) {
//             this.status = StatutProjet.PROJECT_ONGOING;
//             this.startDate = LocalDate.now();
//             this.lastUpdate = LocalDateTime.now();
//         } else {
//             throw new IllegalStateException("Projet ne peut démarrer depuis le statut " + status);
//         }
//     }

//     public void suspend() {
//         if (status == StatutProjet.PROJECT_ONGOING) {
//             this.status = StatutProjet.PROJECT_DELAYED;
//             this.lastUpdate = LocalDateTime.now();
//         } else {
//             throw new IllegalStateException("Seul un projet en cours peut être suspendu");
//         }
//     }

//     public void resume() {
//         if (status == StatutProjet.PROJECT_DELAYED) {
//             this.status = StatutProjet.PROJECT_ONGOING;
//             this.lastUpdate = LocalDateTime.now();
//         } else {
//             throw new IllegalStateException("Seul un projet suspendu peut être repris");
//         }
//     }

//     public void complete() {
//         if (status == StatutProjet.PROJECT_ONGOING) {
//             this.status = StatutProjet.PROJECT_FINISHED;
//             this.endDate = LocalDate.now();
//             this.lastUpdate = LocalDateTime.now();
//         } else {
//             throw new IllegalStateException("Seul un projet en cours peut être terminé");
//         }
//     }


//     // Auto-mise à jour avant toute modification persistée
//     @PreUpdate
//     protected void onUpdate() {
//         this.lastUpdate = LocalDateTime.now();
//     }

//     // Getters et setters standards (tronqués pour lisibilité)
//     public Long getId() { return id; }
//     public void setId(Long id) { this.id = id; }
//     public String getTitle() { return title; }
//     public void setTitle(String title) { this.title = title; }
//     public String getDescription() { return description; }
//     public void setDescription(String description) { this.description = description; }
//     public double getCost() { return cost; }
//     public void setCost(double cost) { this.cost = cost; }
//     public LocalDate getStartDate() { return startDate; }
//     public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
//     public LocalDate getEndDate() { return endDate; }
//     public void setEndDate(LocalDate endDate) { this.endDate = endDate; }   
//     public StatutProjet getStatus() { return status; }
//     public List<Problem> getProblems() { return problems; }
//     public void setProblems(List<Problem> problems) { this.problems = problems; }
//     public LocalDateTime getLastUpdate() { return lastUpdate; }

//     public WorkType getCategory() { return category; }
//     public void setCategory(WorkType category) { this.category = category; }

//     public String getNeighbourhood() { return neighbourhood; }
//     public void setNeighbourhood(String neighbourhood) { this.neighbourhood = neighbourhood; }

//     public String getStreet() { return street; }
//     public void setStreet(String street) { this.street = street; }
// }
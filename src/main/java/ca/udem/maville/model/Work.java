package ca.udem.maville.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import ca.udem.maville.enums.StatutProjet;

// Unified Work model for displaying both Montreal API and local projects
public class Work {
    private int id;
    private String title;
    private String description;
    private String category;
    private String borough;
    private String street;
    private LocalDate startDate;
    private LocalDate endDate;
    private StatutProjet status;
    private String source; // "MONTREAL_API" or "LOCAL_PROJECT"
    private String serviceProvider;
    
    // Constructors
    public Work() {}
    
    public Work(int id, String title, String description, String category, String borough, String street,
                LocalDate startDate, LocalDate endDate, StatutProjet status, 
                String source, String serviceProvider) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.borough = borough;
        this.street = street;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.source = source;
        this.serviceProvider = serviceProvider;
    }
    
    // Static factory methods for easy creation
    public static Work fromProject(Candidature project) {
        return new Work(
            project.getId(),
            project.getTitle(),
            project.getDescription(),
            project.getProblemType().toString(),
            project.getProblem().getNeigbourhood(),
            project.getProblem().getStreet(),
            LocalDate.parse(project.getStartDate()),
            LocalDate.parse(project.getEndDate()),
            project.getStatus(),
            "LOCAL_PROJECT",
            project.getPrestataireContact()
        );
    }
    
    public static Work fromMontrealAPI(MontrealAPIWork apiWork) {
        return new Work(
            Integer.parseInt(apiWork.getId()),
            apiWork.getTitle(),
            apiWork.getDescription(),
            apiWork.getReasonCategory(),
            apiWork.getBoroughId(),
            apiWork.getOccupancy_name(),
            OffsetDateTime.parse(apiWork.getDuration_start_date()).toLocalDate(), // Ensure method name matches MontrealAPIWork
            OffsetDateTime.parse(apiWork.getDuration_end_date()).toLocalDate(),
            StatutProjet.fromApiValue(apiWork.getCurrentStatus()), // Use the pre-defined status variable
            "MONTREAL_API",
            apiWork.getOrganizationName()
        );
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBorough() { return borough; }
    public void setBorough(String borough) { this.borough = borough; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public StatutProjet getStatus() { return status; }
    public void setStatus(StatutProjet status) { this.status = status; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getServiceProvider() { return serviceProvider; }
    public void setServiceProvider(String serviceProvider) { this.serviceProvider = serviceProvider; }

    @Override
    public String toString() {
        return "---------------------------------\n" +
            "ID: " + this.id + "\n" +
            "Titre: " + this.title + "\n" +
            "Emplacement: " + this.borough + " " + this.street+ "\n" +
            "Statut: " + this.status + "\n" +
            "Fournisseur de services: " + this.serviceProvider + "\n" +
            "---------------------------------";
    }
}


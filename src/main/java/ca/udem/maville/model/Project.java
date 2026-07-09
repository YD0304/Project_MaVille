package ca.udem.maville.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private double proposedCost;
    private LocalDate proposedStartDate;
    private LocalDate proposedEndDate;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;

    private LocalDateTime lastUpdate;
    private int reportedCount;
    private String rejectionReason;

    public Project() {}

    public Project(String title, String description, double proposedCost,
                   LocalDate proposedStartDate, LocalDate proposedEndDate,
                   Problem problem, Provider provider) {
        this.title = title;
        this.description = description;
        this.proposedCost = proposedCost;
        this.proposedStartDate = proposedStartDate;
        this.proposedEndDate = proposedEndDate;
        this.problem = problem;
        this.provider = provider;
        this.status = ProjectStatus.PROPOSAL_SUBMITTED;
        this.lastUpdate = LocalDateTime.now();
        this.reportedCount = 0;
    }

    // --- Business logic ---

    public void accept() {
        if (status != ProjectStatus.PROPOSAL_SUBMITTED)
            throw new IllegalStateException("Only submitted proposals can be accepted");
        this.status = ProjectStatus.PERMIT_ISSUED;
        this.lastUpdate = LocalDateTime.now();
    }

    public void reject(String reason) {
        if (status != ProjectStatus.PROPOSAL_SUBMITTED)
            throw new IllegalStateException("Only submitted proposals can be rejected");
        this.status = ProjectStatus.PROPOSAL_REFUSED;
        this.rejectionReason = reason;
        this.lastUpdate = LocalDateTime.now();
    }

    public void startWork() {
        if (status != ProjectStatus.PERMIT_ISSUED)
            throw new IllegalStateException("Project can start only from PERMIT_ISSUED state");
        this.status = ProjectStatus.PROJECT_ONGOING;
        if (this.proposedStartDate == null) this.proposedStartDate = LocalDate.now();
        this.lastUpdate = LocalDateTime.now();
    }

    public void delay() {
        if (status != ProjectStatus.PROJECT_ONGOING)
            throw new IllegalStateException("Only ongoing projects can be delayed");
        this.status = ProjectStatus.PROJECT_DELAYED;
        this.lastUpdate = LocalDateTime.now();
    }

    public void resume() {
        if (status != ProjectStatus.PROJECT_DELAYED)
            throw new IllegalStateException("Only delayed projects can be resumed");
        this.status = ProjectStatus.PROJECT_ONGOING;
        this.lastUpdate = LocalDateTime.now();
    }

    public void complete(Double finalCost) {
        if (status != ProjectStatus.PROJECT_ONGOING)
            throw new IllegalStateException("Only ongoing projects can be completed");
        this.status = ProjectStatus.PROJECT_FINISHED;
        this.proposedEndDate = LocalDate.now();
        if (finalCost != null) this.proposedCost = finalCost;
        this.lastUpdate = LocalDateTime.now();
    }

    public void incrementReportedCount() {
        this.reportedCount++;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getProposedCost() { return proposedCost; }
    public void setProposedCost(double proposedCost) { this.proposedCost = proposedCost; }
    public LocalDate getProposedStartDate() { return proposedStartDate; }
    public void setProposedStartDate(LocalDate proposedStartDate) { this.proposedStartDate = proposedStartDate; }
    public LocalDate getProposedEndDate() { return proposedEndDate; }
    public void setProposedEndDate(LocalDate proposedEndDate) { this.proposedEndDate = proposedEndDate; }
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }
    public Problem getProblem() { return problem; }
    public void setProblem(Problem problem) { this.problem = problem; }
    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }
    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }
    public int getReportedCount() { return reportedCount; }
    public void setReportedCount(int reportedCount) { this.reportedCount = reportedCount; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
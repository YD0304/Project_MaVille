package ca.udem.maville.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "problems")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Problem {

    // FIX 3: Changed int → Long to match JpaRepository<Problem, Long>
    //        and the Long-typed findById / findByResidentId calls in the repository.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;
    private String neighbourhood;

    @Enumerated(EnumType.STRING)
    private WorkType type;

    private String description;
    private LocalDateTime reportTime;

    @Enumerated(EnumType.STRING)
    private Priorite prioriteType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id")
    private Resident resident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_problem_id")
    @JsonIgnoreProperties({"parentProblem", "childSignals"})
    private Problem parentProblem;

    public Problem() {}   // JPA no-arg

    public Problem(String street, String neighbourhood, WorkType type, String description, Resident resident) {
        this.street = street;
        this.neighbourhood = neighbourhood;
        this.type = type;
        this.description = description;
        this.resident = resident;
        this.reportTime = LocalDateTime.now();
        this.prioriteType = Priorite.NOT_ASSIGNED;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getNeighbourhood() { return neighbourhood; }
    public void setNeighbourhood(String neighbourhood) { this.neighbourhood = neighbourhood; }

    public WorkType getType() { return type; }
    public void setType(WorkType type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getReportTime() { return reportTime; }
    public void setReportTime(LocalDateTime reportTime) { this.reportTime = reportTime; }

    public Priorite getPrioriteType() { return prioriteType; }
    public void setPrioriteType(Priorite prioriteType) { this.prioriteType = prioriteType; }

    public Resident getResident() { return resident; }
    public void setResident(Resident resident) { this.resident = resident; }

    public Problem getParentProblem() { return parentProblem; }
    public void setParentProblem(Problem parentProblem) { this.parentProblem = parentProblem; }

    @Override
    public String toString() {
        return "---------------------------------\n" +
            "Problem id=" + id + "\n" +
            "Street: " + street + "\n" +
            "Neighbourhood: " + neighbourhood + "\n" +
            "Type: " + type + "\n" +
            "Description: " + description + "\n" +
            "Priority: " + prioriteType + "\n" +
            "Resident: " + (resident != null ? resident.getNomComplet() : "None") + "\n" +
            "---------------------------------\n";
    }
}
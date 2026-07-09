package ca.udem.maville.dto;

import ca.udem.maville.model.Priorite;
import ca.udem.maville.model.Problem;
import ca.udem.maville.model.WorkType;
import java.time.LocalDateTime;

public class ProblemResponseDTO {

    private Long id;           // Long to match Problem.id
    private String street;
    private String neighbourhood;
    private WorkType type;
    private String description;
    private LocalDateTime reportTime;
    private Priorite prioriteType;
    private String residentName;

    public static ProblemResponseDTO from(Problem p) {
        var dto = new ProblemResponseDTO();
        dto.id            = p.getId();
        dto.street        = p.getStreet();
        dto.neighbourhood = p.getNeighbourhood();
        dto.type          = p.getType();
        dto.description   = p.getDescription();
        dto.reportTime    = p.getReportTime();
        dto.prioriteType  = p.getPrioriteType();
        dto.residentName  = p.getResident() != null ? p.getResident().getNomComplet() : null;
        return dto;
    }

    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }
    public String getStreet()                  { return street; }
    public void setStreet(String street)       { this.street = street; }
    public String getNeighbourhood()           { return neighbourhood; }
    public void setNeighbourhood(String n)     { this.neighbourhood = n; }
    public WorkType getType()                  { return type; }
    public void setType(WorkType type)         { this.type = type; }
    public String getDescription()             { return description; }
    public void setDescription(String d)       { this.description = d; }
    public LocalDateTime getReportTime()       { return reportTime; }
    public void setReportTime(LocalDateTime t) { this.reportTime = t; }
    public Priorite getPrioriteType()          { return prioriteType; }
    public void setPrioriteType(Priorite p)    { this.prioriteType = p; }
    public String getResidentName()            { return residentName; }
    public void setResidentName(String n)      { this.residentName = n; }
}
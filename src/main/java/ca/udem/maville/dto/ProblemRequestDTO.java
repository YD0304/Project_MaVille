package ca.udem.maville.dto;

import ca.udem.maville.model.WorkType;

public class ProblemRequestDTO {
    private String street;
    private String neighbourhood;
    private WorkType type;
    private String description;
    private Long residentId;

    // Default constructor is required for JSON deserialization
    public ProblemRequestDTO() {}

    // Getters and Setters
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getNeighbourhood() { return neighbourhood; }
    public void setNeighbourhood(String neighbourhood) { this.neighbourhood = neighbourhood; }

    public WorkType getType() { return type; }
    public void setType(WorkType type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getResidentId() { return residentId; }
    public void setResidentId(Long residentId) { this.residentId = residentId; }
}
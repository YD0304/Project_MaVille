package ca.udem.maville.dto;

import ca.udem.maville.model.AbonnementType;

public class SubscribeRequest {
    private Long residentId;        // for residents
    private String providerCompanyNumber; // for providers
    private AbonnementType type;    // e.g., NEIGHBOURHOOD, STREET, PROBLEM_TYPE
    private String value;           // neighbourhood name, street name, or problem type

    // getters and setters
    public Long getResidentId() { return residentId; }
    public void setResidentId(Long residentId) { this.residentId = residentId; }
    public String getProviderCompanyNumber() { return providerCompanyNumber; }
    public void setProviderCompanyNumber(String providerCompanyNumber) { this.providerCompanyNumber = providerCompanyNumber; }
    public AbonnementType getType() { return type; }
    public void setType(AbonnementType type) { this.type = type; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
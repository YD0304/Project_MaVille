package ca.udem.maville.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.udem.maville.enums.Priorite;
import ca.udem.maville.enums.TravauxType;

public class Problem {
    private int id;
    String street;
    String neigbourhood;
    private TravauxType problemtype;
    private String description;
    @JsonIgnore
    private Resident resident;
    private Priorite prioriteType;

    public Problem() {
    }

    public Problem(String street, String neigbourhood, TravauxType problemtype, String description) {
        this.street = street;
        this.neigbourhood = neigbourhood;
        this.problemtype = problemtype;
        this.description = description;
    }

    public int getId() {
        return id;
    }
    public String getStreet() {
        return street;
    }
    public String getNeigbourhood() {
        return neigbourhood;
    }
    public TravauxType getProblemtype() {
        return problemtype;
    }
    public String getDescription() {
        return description;
    }
    public Resident getResident() {
        return resident;
    }
    public Priorite getPrioriteType() {
        return prioriteType;
    }


    public void setId(int id) {
        this.id = id;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public void setNeigbourhood(String neigbourhood) {
        this.neigbourhood = neigbourhood;
    }
    public void setProblemtype(TravauxType problemtype) {
        this.problemtype = problemtype;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setResident(Resident resident) {
        this.resident = resident;
    }
    public void setPrioriteType(Priorite prioriteType) {
        this.prioriteType = prioriteType;
    }

    public String getContact() {
        return resident.getContact();
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Quartier: " + neigbourhood + ", Rue: " + street + ", Type: " + problemtype + ", Priorité: " + prioriteType + ", Description: " + description + ", Contact: " + getContact();
    }
}
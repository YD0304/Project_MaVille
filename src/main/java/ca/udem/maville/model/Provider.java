package ca.udem.maville.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Table(name = "providers")
public class Provider {

    @Id
    private String companyNumber;   // natural key (NEQ)

    private String companyName;
    private String phone;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
private String password;
    private String submitterCategory;
    private String role = "PROVIDER";  // default role

    // JPA requires no-arg constructor (already have it)
    public Provider() {}

    public Provider(String companyNumber, String companyName, String phone, String email, String submitterCategory) {
        this.companyNumber = companyNumber;
        this.companyName = companyName;
        this.phone = phone;
        this.email = email;
        this.submitterCategory = submitterCategory;
    }

    public String getPassword() {
    return password;
}

public void setPassword(String password) {
    this.password = password;
}
    // Getters and setters (unchanged)
    public String getCompanyNumber() { return companyNumber; }
    public void setCompanyNumber(String companyNumber) { this.companyNumber = companyNumber; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSubmitterCategory() { return submitterCategory; }
    public void setSubmitterCategory(String submitterCategory) { this.submitterCategory = submitterCategory; }

    public String getContact() {
        return "Entreprise: " + companyName + ", Numero: " + companyNumber + ", Email: " + getEmail();
    }
    public String getRole() { return role; }
public void setRole(String role) { this.role = role; }

    // equals/hashCode/toString remain as you had them
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Provider that = (Provider) o;
        return Objects.equals(companyNumber, that.companyNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyNumber);
    }

    @Override
    public String toString() {
        return "Provider{companyName='" + companyName + "', companyNumber='" + companyNumber + "'}";
    }
}
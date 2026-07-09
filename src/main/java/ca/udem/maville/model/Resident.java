package ca.udem.maville.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Table(name = "residents")
public class Resident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // add an auto-generated ID (you may keep email as unique, but JPA needs an ID)

    private String last_name;
    private String first_name;
    private String phone;
    @Column(nullable = false, unique = true)
    private String email;
    @JsonIgnore
    private String password;
    private String neighbourhood;
    private String role = "RESIDENT";  // default role

    public Resident() {}

    public Resident(String last_name, String first_name, String phone, String email, String password, String neighbourhood) {
        this.last_name = last_name;
        this.first_name = first_name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.neighbourhood = neighbourhood;
    }

    // Getters and setters exactly as you have them
    // (add getId/setId for the new ID field)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLast_name() { return last_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }

    public String getFirst_name() { return first_name; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNeighbourhood() { return neighbourhood; }
    public void setNeighbourhood(String neighbourhood) { this.neighbourhood = neighbourhood; }

    public String getRole() { return role; }
public void setRole(String role) { this.role = role; }

    @JsonIgnore
    public String getNomComplet() {
        return first_name + " " + last_name;
    }

    @Override
    public String toString() {
        return getNomComplet() + " (" + email + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resident resident = (Resident) o;
        return Objects.equals(id, resident.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
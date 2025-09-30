package ca.udem.maville.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "role"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Resident.class, name = "Resident"),
    @JsonSubTypes.Type(value = Prestataire.class, name = "Prestataire")
})
public abstract class User {
    private String username;
    private String password;
    private String email;
    private String neighbourhood;

    // Default constructor for Jackson
    public User() {}

    public User(String username, String password, String email, String neighbourhood) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.neighbourhood = neighbourhood;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getQuartier() { return neighbourhood; }
    public void setQuartier(String quartier) { this.neighbourhood = neighbourhood; }

    public abstract String getRole();
}
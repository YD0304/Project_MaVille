package ca.udem.maville.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Resident extends User {
    private int id;
    private String name;
    private String phone;
    // Note: email is already inherited from User class

    public Resident() {
        super(); // Use default constructor
    }

    public Resident(String username, String password, String email, String neighbourhood,String name,String phone) {
        super(username, password, email, neighbourhood);
        this.id = id;
        this.name = name;
        this.phone = phone;
        // Set neighbourhood to quartier by default, or you can set it separately
    }

    @Override
    public String getRole() {
        return "Resident";
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getContact() {
        return "Name: " + name + ", Phone: " + phone + ", Email: " + getEmail();
    }

    @Override
    public String toString() {
        return "Resident{id=" + id + "', contact='" + getContact() + "'}";
    }
}
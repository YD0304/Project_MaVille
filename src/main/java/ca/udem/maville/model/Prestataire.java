package ca.udem.maville.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Prestataire extends User {
    private int id;
    private String nomEntreprise;
    private String numeroEntreprise;


    public Prestataire() {
        // Default constructor for Jackson
        super();
    }
    
    public Prestataire(String username, String password, String email, String neighbourhood,
                       int id, String nomEntreprise, String numeroEntreprise) {
        super(username, password, email, neighbourhood);
        this.id = id;
        this.nomEntreprise = nomEntreprise;
        this.numeroEntreprise = numeroEntreprise;
    }
    
    @Override
    public String getRole() {
        return "Prestataire";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomEntreprise() {
        return nomEntreprise;
    }

    public void setNomEntreprise(String nomEntreprise) {
        this.nomEntreprise = nomEntreprise;
    }

    public String getNumeroEntreprise() {
        return numeroEntreprise;
    }

    public void setNumeroEntreprise(String numeroEntreprise) {
        this.numeroEntreprise = numeroEntreprise;
    }

    public String getContact() {
        return "Entreprise: " + nomEntreprise + ", Numero: " + numeroEntreprise + ", Email: " + getEmail();
    }

    @Override
    public String toString() {
        return "Prestataire{id=" + id + ", nomEntreprise='" + nomEntreprise + "', numeroEntreprise='" + numeroEntreprise + "'}";
    }
}

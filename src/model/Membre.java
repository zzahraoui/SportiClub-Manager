// Membre.java
package model;

import java.time.LocalDate;
import java.time.Period;

public class Membre {

    // 1. Attributs privés
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private LocalDate dateNaissance;
    private boolean actif;

    // 2. Constructeur SANS id (création d'un nouveau membre)
    public Membre(String nom, String prenom, String email,
            String telephone, LocalDate dateNaissance, boolean actif) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.dateNaissance = dateNaissance;
        this.actif = actif;
    }

    // 3. Constructeur AVEC id (lecture depuis la BDD)
    public Membre(int id, String nom, String prenom, String email,
            String telephone, LocalDate dateNaissance, boolean actif) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.dateNaissance = dateNaissance;
        this.actif = actif;
    }

    // 4. Getters
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public boolean isActif() {
        return actif;
    }

    // 5. Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setDateNaissance(LocalDate d) {
        this.dateNaissance = d;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    // 6. Getter calculé — âge
    public int getAge() {
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    // 7. toString — affichage lisible
    @Override
    public String toString() {
        return prenom + " " + nom;
    }
}
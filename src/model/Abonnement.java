// Abonnement.java
package model;

import java.time.LocalDate;

public class Abonnement {

    // 1. Attributs privés
    private int id;
    private TypeOffre typeOffre;
    private double prixMensuel;
    private int dureeEngagement;
    private LocalDate dateDebut;
    private StatutAbonnement statut;
    private int membreId;

    // 2. Constructeur SANS id (création d'un nouvel abonnement)
    public Abonnement(TypeOffre typeOffre, double prixMensuel,
            int dureeEngagement, LocalDate dateDebut,
            StatutAbonnement statut, int membreId) {
        this.typeOffre = typeOffre;
        this.prixMensuel = prixMensuel;
        this.dureeEngagement = dureeEngagement;
        this.dateDebut = dateDebut;
        this.statut = statut;
        this.membreId = membreId;
    }

    // 3. Constructeur AVEC id (lecture depuis la BDD)
    public Abonnement(int id, TypeOffre typeOffre, double prixMensuel,
            int dureeEngagement, LocalDate dateDebut,
            StatutAbonnement statut, int membreId) {
        this.id = id;
        this.typeOffre = typeOffre;
        this.prixMensuel = prixMensuel;
        this.dureeEngagement = dureeEngagement;
        this.dateDebut = dateDebut;
        this.statut = statut;
        this.membreId = membreId;
    }

    // 4. Getters
    public int getId() {
        return id;
    }

    public TypeOffre getTypeOffre() {
        return typeOffre;
    }

    public double getPrixMensuel() {
        return prixMensuel;
    }

    public int getDureeEngagement() {
        return dureeEngagement;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public StatutAbonnement getStatut() {
        return statut;
    }

    public int getMembreId() {
        return membreId;
    }

    // 5. Getter calculé — dateFin (jamais stockée en BDD)
    public LocalDate getDateFin() {
        return dateDebut.plusMonths(dureeEngagement);
    }

    // 6. Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTypeOffre(TypeOffre t) {
        this.typeOffre = t;
    }

    public void setPrixMensuel(double p) {
        this.prixMensuel = p;
    }

    public void setDureeEngagement(int d) {
        this.dureeEngagement = d;
    }

    public void setDateDebut(LocalDate d) {
        this.dateDebut = d;
    }

    public void setStatut(StatutAbonnement s) {
        this.statut = s;
    }

    public void setMembreId(int membreId) {
        this.membreId = membreId;
    }

    // 7. toString
    @Override
    public String toString() {
        return typeOffre + " — " + statut;
    }
}
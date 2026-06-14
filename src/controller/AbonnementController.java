package controller;

import dao.AbonnementDAO;
import dao.MembreDAO;
import model.Abonnement;
import model.Membre;
import model.StatutAbonnement;
import model.TypeOffre;

import java.util.List;
import java.util.stream.Collectors;

public class AbonnementController {

    private AbonnementDAO abonnementDAO = new AbonnementDAO();
    private MembreDAO membreDAO = new MembreDAO();

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // CRUD Operations
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    public List<Abonnement> findAll() {
        return abonnementDAO.findAll();
    }

    public void create(Abonnement abonnement) {
        if (abonnement != null && validerAbonnement(abonnement)) {
            abonnementDAO.create(abonnement);
        }
    }

    public void update(Abonnement abonnement) {
        if (abonnement != null && validerAbonnement(abonnement)) {
            abonnementDAO.update(abonnement);
        }
    }

    public void delete(int id) {
        abonnementDAO.delete(id);
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // Data Accessors
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    public List<Membre> getAllMembres() {
        return membreDAO.findAll();
    }

    public List<TypeOffre> getAllTypeOffres() {
        return List.of(TypeOffre.values());
    }

    public List<StatutAbonnement> getAllStatuts() {
        return List.of(StatutAbonnement.values());
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // Filtering & Searching
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    public List<Abonnement> filtrerAbonnements(String recherche, String filtre) {
        return abonnementDAO.findAll().stream()
                .filter(a -> {
                    boolean matchRecherche = recherche == null || recherche.isEmpty()
                            || a.getTypeOffre().name().toLowerCase()
                                    .contains(recherche.toLowerCase());

                    boolean matchFiltre = "Tous".equals(filtre)
                            || a.getStatut().name().equals(filtre);

                    return matchRecherche && matchFiltre;
                })
                .collect(Collectors.toList());
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // Validation
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    private boolean validerAbonnement(Abonnement a) {
        return a.getTypeOffre() != null
                && a.getDateDebut() != null
                && a.getStatut() != null
                && a.getMembreId() > 0;
    }
}

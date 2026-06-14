package controller;

import dao.MembreDAO;
import model.Membre;

import java.util.List;
import java.util.stream.Collectors;

public class MembreController {

    private MembreDAO membreDAO = new MembreDAO();

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // CRUD Operations
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    
    public List<Membre> findAll() {
        return membreDAO.findAll();
    }

    public void create(Membre membre) {
        if (membre != null && validerMembre(membre)) {
            membreDAO.create(membre);
        }
    }

    public void update(Membre membre) {
        if (membre != null && validerMembre(membre)) {
            membreDAO.update(membre);
        }
    }

    public void delete(int id) {
        membreDAO.delete(id);
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // Filtering & Searching
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    public List<Membre> filtrerMembres(String recherche, String filtre) {
        return membreDAO.findAll().stream()
                .filter(m -> {
                    boolean matchRecherche = recherche == null || recherche.isEmpty()
                            || m.getNom().toLowerCase().contains(recherche.toLowerCase())
                            || m.getPrenom().toLowerCase().contains(recherche.toLowerCase());

                    boolean matchFiltre = "Tous".equals(filtre)
                            || ("Actifs".equals(filtre) && m.isActif())
                            || ("Inactifs".equals(filtre) && !m.isActif());

                    return matchRecherche && matchFiltre;
                })
                .collect(Collectors.toList());
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // Validation
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    private boolean validerMembre(Membre m) {
        return m.getNom() != null && !m.getNom().isEmpty()
                && m.getPrenom() != null && !m.getPrenom().isEmpty()
                && m.getEmail() != null && !m.getEmail().isEmpty()
                && m.getTelephone() != null && !m.getTelephone().isEmpty()
                && m.getDateNaissance() != null;
    }
}

package util;

import dao.AbonnementDAO;
import dao.MembreDAO;
import model.Abonnement;
import model.Membre;
import model.StatutAbonnement;
import model.TypeOffre;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CsvImporter {

    public static class ImportResult {
        private final int imported;
        private final int skipped;
        private final List<String> errors;

        public ImportResult(int imported, int skipped, List<String> errors) {
            this.imported = imported;
            this.skipped = skipped;
            this.errors = errors;
        }

        public int getImported() {
            return imported;
        }

        public int getSkipped() {
            return skipped;
        }

        public List<String> getErrors() {
            return errors;
        }
    }

    public static ImportResult importMembres(String chemin, MembreDAO membreDAO) {
        int imported = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(chemin))) {
            String line;
            boolean headerSkipped = false;
            while ((line = reader.readLine()) != null) {
                line = stripBom(line).trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (!headerSkipped && line.toLowerCase().startsWith("id,")) {
                    headerSkipped = true;
                    continue;
                }
                String[] parts = splitLine(line);
                int offset = parts.length >= 7 ? 1 : 0;
                if (parts.length < 7 + offset) {
                    skipped++;
                    addError(errors, "Ligne invalide: " + line);
                    continue;
                }
                try {
                    String nom = parts[offset].trim();
                    String prenom = parts[offset + 1].trim();
                    String email = parts[offset + 2].trim();
                    String telephone = parts[offset + 3].trim();
                    LocalDate dateNaissance = LocalDate.parse(parts[offset + 4].trim());
                    boolean actif = parseBoolean(parts[offset + 5].trim());

                    Membre membre = new Membre(nom, prenom, email, telephone, dateNaissance, actif);
                    membreDAO.create(membre);
                    imported++;
                } catch (Exception ex) {
                    skipped++;
                    addError(errors, "Erreur parsing membre: " + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            addError(errors, "Erreur lecture fichier: " + ex.getMessage());
        }

        return new ImportResult(imported, skipped, errors);
    }

    public static ImportResult importAbonnements(String chemin, AbonnementDAO abonnementDAO) {
        int imported = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(chemin))) {
            String line;
            boolean headerSkipped = false;
            while ((line = reader.readLine()) != null) {
                line = stripBom(line).trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (!headerSkipped && line.toLowerCase().startsWith("id,")) {
                    headerSkipped = true;
                    continue;
                }
                String[] parts = splitLine(line);
                int offset = parts.length >= 8 ? 1 : 0;
                if (parts.length < 6 + offset) {
                    skipped++;
                    addError(errors, "Ligne invalide: " + line);
                    continue;
                }
                try {
                    TypeOffre typeOffre = TypeOffre.valueOf(parts[offset].trim().toUpperCase());
                    double prixMensuel = Double.parseDouble(parts[offset + 1].trim());
                    int duree = Integer.parseInt(parts[offset + 2].trim());
                    LocalDate dateDebut = LocalDate.parse(parts[offset + 3].trim());
                    StatutAbonnement statut = StatutAbonnement
                            .valueOf(parts[offset + 5].trim().toUpperCase());
                    int membreId = Integer.parseInt(parts[offset + 6].trim());

                    Abonnement abonnement = new Abonnement(typeOffre, prixMensuel, duree, dateDebut, statut, membreId);
                    abonnementDAO.create(abonnement);
                    imported++;
                } catch (Exception ex) {
                    skipped++;
                    addError(errors, "Erreur parsing abonnement: " + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            addError(errors, "Erreur lecture fichier: " + ex.getMessage());
        }

        return new ImportResult(imported, skipped, errors);
    }

    private static boolean parseBoolean(String value) {
        String normalized = value.trim().toLowerCase();
        return normalized.equals("oui") || normalized.equals("true") || normalized.equals("1")
                || normalized.equals("yes");
    }

    private static String[] splitLine(String line) {
        if (line.contains(";") && !line.contains(",")) {
            return line.split(";", -1);
        }
        return line.split(",", -1);
    }

    private static String stripBom(String line) {
        if (line != null && !line.isEmpty() && line.charAt(0) == '\uFEFF') {
            return line.substring(1);
        }
        return line;
    }

    private static void addError(List<String> errors, String message) {
        if (errors.size() < 5) {
            errors.add(message);
        }
    }
}

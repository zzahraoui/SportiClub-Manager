package util;

import model.Abonnement;
import model.Membre;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvExporter {

    // Export Membres
    public static void exportMembres(List<Membre> membres, String chemin) {
        try (FileWriter writer = new FileWriter(chemin)) {

            // En-tête
            writer.write("ID,Nom,Prenom,Email,Telephone,DateNaissance,Actif\n");

            // Lignes
            for (Membre m : membres) {
                writer.write(String.format("%d,%s,%s,%s,%s,%s,%s\n",
                        m.getId(),
                        m.getNom(),
                        m.getPrenom(),
                        m.getEmail(),
                        m.getTelephone(),
                        m.getDateNaissance(),
                        m.isActif() ? "Oui" : "Non"));
            }
            System.out.println("Export membres réussi : " + chemin);

        } catch (IOException e) {
            System.out.println("Erreur export : " + e.getMessage());
        }
    }

    // Export Abonnements
    public static void exportAbonnements(List<Abonnement> abonnements, String chemin) {
        try (FileWriter writer = new FileWriter(chemin)) {

            // En-tête
            writer.write("ID,TypeOffre,PrixMensuel,Duree,DateDebut,DateFin,Statut,MembreID\n");

            // Lignes
            for (Abonnement a : abonnements) {
                writer.write(String.format("%d,%s,%.2f,%d,%s,%s,%s,%d\n",
                        a.getId(),
                        a.getTypeOffre(),
                        a.getPrixMensuel(),
                        a.getDureeEngagement(),
                        a.getDateDebut(),
                        a.getDateFin(),
                        a.getStatut(),
                        a.getMembreId()));
            }
            System.out.println("Export abonnements réussi : " + chemin);

        } catch (IOException e) {
            System.out.println("Erreur export : " + e.getMessage());
        }
    }
}
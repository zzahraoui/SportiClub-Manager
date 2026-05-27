package util;

import dao.AbonnementDAO;
import dao.MembreDAO;
import model.Abonnement;
import model.Membre;
import model.StatutAbonnement;
import model.TypeOffre;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

        try (BufferedReader reader = openReader(chemin)) {
            String line;
            boolean headerSkipped = false;
            while ((line = reader.readLine()) != null) {
                line = stripBom(line).trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (!headerSkipped && isHeaderLine(line)) {
                    headerSkipped = true;
                    continue;
                }
                String[] parts = splitLine(line);
                normalizeParts(parts);
                int offset = parts.length >= 7 ? 1 : 0;
                if (parts.length < 6 + offset) {
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

        try (BufferedReader reader = openReader(chemin)) {
            String line;
            boolean headerSkipped = false;
            while ((line = reader.readLine()) != null) {
                line = stripBom(line).trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (!headerSkipped && isHeaderLine(line)) {
                    headerSkipped = true;
                    continue;
                }
                String[] parts = splitLine(line);
                normalizeParts(parts);
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
        char delimiter = detectDelimiter(line);
        return line.split(String.valueOf(delimiter), -1);
    }

    private static char detectDelimiter(String line) {
        int commas = countChar(line, ',');
        int semicolons = countChar(line, ';');
        int tabs = countChar(line, '\t');

        if (semicolons >= commas && semicolons >= tabs && semicolons > 0) {
            return ';';
        }
        if (tabs >= commas && tabs > 0) {
            return '\t';
        }
        return ',';
    }

    private static int countChar(String line, char target) {
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == target) {
                count++;
            }
        }
        return count;
    }

    private static void normalizeParts(String[] parts) {
        for (int i = 0; i < parts.length; i++) {
            String value = parts[i].trim();
            if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                value = value.substring(1, value.length() - 1);
            }
            parts[i] = value;
        }
    }

    private static boolean isHeaderLine(String line) {
        String lower = line.toLowerCase();
        return lower.startsWith("id,") || lower.startsWith("id;") || lower.startsWith("id\t");
    }

    private static String stripBom(String line) {
        if (line != null && !line.isEmpty() && line.charAt(0) == '\uFEFF') {
            return line.substring(1);
        }
        return line;
    }

    private static BufferedReader openReader(String chemin) throws IOException {
        PushbackInputStream input = new PushbackInputStream(new FileInputStream(chemin), 512);
        byte[] sample = new byte[256];
        int read = input.read(sample, 0, sample.length);

        Charset charset = StandardCharsets.UTF_8;
        int unread = read;

        if (read >= 3 && (sample[0] & 0xFF) == 0xEF && (sample[1] & 0xFF) == 0xBB && (sample[2] & 0xFF) == 0xBF) {
            charset = StandardCharsets.UTF_8;
            unread = read - 3;
        } else if (read >= 2 && (sample[0] & 0xFF) == 0xFF && (sample[1] & 0xFF) == 0xFE) {
            charset = StandardCharsets.UTF_16LE;
            unread = read - 2;
        } else if (read >= 2 && (sample[0] & 0xFF) == 0xFE && (sample[1] & 0xFF) == 0xFF) {
            charset = StandardCharsets.UTF_16BE;
            unread = read - 2;
        } else if (read > 0 && looksLikeUtf16(sample, read)) {
            charset = guessUtf16Endian(sample, read);
        }

        if (unread > 0) {
            input.unread(sample, read - unread, unread);
        }

        return new BufferedReader(new InputStreamReader(input, charset));
    }

    private static boolean looksLikeUtf16(byte[] sample, int length) {
        int zeros = 0;
        for (int i = 0; i < length; i++) {
            if (sample[i] == 0) {
                zeros++;
            }
        }
        return zeros > length / 4;
    }

    private static Charset guessUtf16Endian(byte[] sample, int length) {
        int evenZeros = 0;
        int oddZeros = 0;
        for (int i = 0; i < length; i++) {
            if (sample[i] == 0) {
                if (i % 2 == 0) {
                    evenZeros++;
                } else {
                    oddZeros++;
                }
            }
        }
        return oddZeros >= evenZeros ? StandardCharsets.UTF_16LE : StandardCharsets.UTF_16BE;
    }

    private static void addError(List<String> errors, String message) {
        if (errors.size() < 5) {
            errors.add(message);
        }
    }
}

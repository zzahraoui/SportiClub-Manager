

## Club Sportif Manager

Application de bureau JavaFX pour gérer un club sportif : membres, abonnements et un tableau de bord avec graphiques. Les données sont stockées dans MySQL et l'interface utilise un style moderne.

## Lien Du Drive
https://drive.google.com/drive/folders/1pwwpiGqucMmVcaIcUSyomVyZAab2r5Ej?usp=sharing

## Fonctionnalités

- CRUD membres avec recherche et filtres
- CRUD abonnements avec état et lien membre
- Tableau de bord avec indicateurs et graphiques
- Import/export CSV pour membres et abonnements
- Persistance MySQL avec données de démonstration

## Technologies

- Java (JDK 17+ recommandé)
- JavaFX
- MySQL
- MySQL Connector (`lib/mysql-connector-j-9.7.0.jar`)

## Structure du projet

- `src/MainApp.java` : point d'entrée de l'application
- `src/view` : vues JavaFX et styles CSS
- `src/controller` : contrôleurs métiers pour les abonnements et les membres
- `src/dao` : accès aux données MySQL
- `src/model` : modèles du domaine
- `sql/init_db.sql` : création de la base et des tables
- `sql/demo_data.sql` : données de démonstration pour MySQL
- `sql/membres_50.csv`, `sql/abonnements_50.csv` : fichiers CSV de démonstration

## Installation

1) Créez la base de données et les tables :

```bash
mysql -u root -p < sql/init_db.sql
```

2) Configurez les informations de connexion si nécessaire :

- `src/dao/Database.java`
  - URL : `jdbc:mysql://localhost:3306/club_sportif`
  - `USER`
  - `PASSWORD`

3) Assurez-vous que MySQL est démarré et que la base `club_sportif` est accessible.

4) Assurez-vous que JavaFX et MySQL Connector sont accessibles dans le classpath.

## Exécution en IDE

Ouvrez le projet dans IntelliJ ou VS Code.

### VS Code
- Créez le dossier `.vscode` à la racine du projet si nécessaire.
- Installez l'extension Java pour VS Code (Java Extension Pack).
- Ajoutez ou vérifiez les fichiers suivants :
  - `.vscode/settings.json`
  - `.vscode/launch.json`
- Vérifiez que `lib/mysql-connector-j-9.7.0.jar` existe dans le projet.
- Exemple de configuration de `.vscode/settings.json` :

```json
{
  "java.project.sourcePaths": ["src"],
  "java.project.outputPath": "bin",
  "java.project.referencedLibraries": [
    "${workspaceFolder}/lib/mysql-connector-j-9.7.0.jar",
    "/path/to/java-openjfx/*.jar"
  ]
}
```

- Exemple de configuration de `.vscode/launch.json` :

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Launch ClubSportif",
      "request": "launch",
      "mainClass": "MainApp",
      "modulePaths": ["/path/to/java-openjfx"],
      "vmArgs": "--add-modules javafx.controls,javafx.fxml -Djava.library.path=/path/to/java-openjfx -Dprism.order=sw",
      "classPaths": [
        "${workspaceFolder}/bin",
        "${workspaceFolder}/lib/mysql-connector-j-9.7.0.jar"
      ]
    }
  ]
}
```

- Lancez `MainApp` depuis le panneau de débogage/Run de VS Code.

### IntelliJ
- Ouvrez le projet et vérifiez que `src` est défini comme source root.
- Ajoutez `lib/mysql-connector-j-9.7.0.jar` comme dépendance de projet.
- Exemple d'options VM pour la configuration de lancement IntelliJ :

```text
--module-path /path/to/java-openjfx --add-modules javafx.controls,javafx.fxml -Djava.library.path=/path/to/java-openjfx -Dprism.order=sw
```

- Lancez la classe `MainApp`.

## Import/Export CSV

Dans l'application, utilisez `Fichier -> Importer CSV` ou `Fichier -> Exporter CSV`.

Format CSV membres :

```csv
ID,Nom,Prenom,Email,Telephone,DateNaissance,Actif
1,Bennani,Omar,omar.bennani@example.com,0612002001,1997-02-11,Oui
```

Format CSV abonnements :

```csv
ID,TypeOffre,PrixMensuel,Duree,DateDebut,DateFin,Statut,MembreID
1,CLASSIQUE,290.00,12,2024-01-15,2025-01-15,ACTIF,1
```

Utilisez les fichiers de démonstration :

- `sql/membres_50.csv`
- `sql/abonnements_50.csv`

## Données de démonstration SQL

Pour injecter les données de démonstration en base :

```bash
mysql -u root -p < sql/demo_data.sql
```

## À propos du `.gitignore`

Un fichier `.gitignore` devrait généralement exclure :

- `bin/` ou `out/` : fichiers compilés
- `.idea/` : configuration IntelliJ locale
- `.vscode/` si elle contient des paramètres locaux spécifiques à un poste
- `*.log`, `*.tmp`, fichiers temporaires
- `*.iml` si le projet n'est pas partagé comme configuration d'IDE

Exemple minimal recommandé :

```gitignore
bin/
out/
.idea/
*.iml
.vscode/
```

Cela évite de committer des fichiers compilés et des configurations locales qui ne doivent pas être partagées.

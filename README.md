## Club Sportif Manager

JavaFX desktop app for managing a sports club: members, subscriptions, and a live dashboard with charts. Data is stored in MySQL and the UI uses a modern sporty theme.

## Features

- Members CRUD with search and filters
- Subscriptions CRUD with status and member links
- Dashboard KPIs and charts (offers, status, revenue trend)
- CSV import and export for members and subscriptions
- MySQL persistence with sample data

## Tech Stack

- Java (JDK 17+ recommended)
- JavaFX
- MySQL
- MySQL Connector/J (see lib/mysql-connector-j-9.7.0.jar)

## Project Structure

- src/MainApp.java: app entry point
- src/view: UI views and styles
- src/dao: database access
- src/model: domain models
- sql/init_db.sql: schema + initial data
- sql/membres_50.csv, sql/abonnements_50.csv: demo CSV data

## Setup

1) Create the database and tables:

```
mysql -u root -p < sql/init_db.sql
```

2) Configure DB credentials if needed:

- src/dao/Database.java
	- URL: jdbc:mysql://localhost:3306/club_sportif
	- USER and PASSWORD

3) Ensure JavaFX and MySQL Connector/J are on the classpath.

## Run (IDE)

Open the project in IntelliJ or VS Code, set the JavaFX SDK and classpath (include lib/mysql-connector-j-9.7.0.jar), then run:

- src/MainApp.java

## CSV Import/Export

In the app: File -> Importer CSV or Exporter CSV.

Members CSV format:

```
ID,Nom,Prenom,Email,Telephone,DateNaissance,Actif
1,Bennani,Omar,omar.bennani@example.com,0612002001,1997-02-11,Oui
```

Subscriptions CSV format:

```
ID,TypeOffre,PrixMensuel,Duree,DateDebut,DateFin,Statut,MembreID
1,CLASSIQUE,290.00,12,2024-01-15,2025-01-15,ACTIF,1
```

Use the provided demo files:

- sql/membres_50.csv
- sql/abonnements_50.csv

## Demo Data (SQL)

You can also inject demo data directly with:

```
mysql -u root -p < sql/demo_data.sql
```

## Notes

- The UI loads even if MySQL is unavailable, but lists will be empty.
- CSV import accepts files exported from Excel (comma, semicolon, or tab separators).

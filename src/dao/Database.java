package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/club_sportif";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Ajoute cette ligne AVANT getConnection
                Class.forName("com.mysql.cj.jdbc.Driver");

                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion MySQL etablie !");
            } catch (SQLException e) {
                System.out.println("Erreur connexion : " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println("Driver introuvable : " + e.getMessage());
            }
        }
        return connection;
    }
}
package org.example.acceso_datos.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {

    private static final String PROPERTIES_FILE = "db.properties";
    private static Properties properties = new Properties();
    private static String jdbcUrl;
    private static String username;
    private static String password;

    static {
        try (InputStream input = DatabaseConnector.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                System.err.println("Lo siento, no se pudo encontrar el archivo " + PROPERTIES_FILE);
                // Podrías lanzar una RuntimeException aquí si la configuración es crítica
            } else {
                properties.load(input);
                jdbcUrl = properties.getProperty("db.url");
                username = properties.getProperty("db.username");
                password = properties.getProperty("db.password");
                String driver = properties.getProperty("db.driver");

                // Cargar el driver JDBC (opcional para JDBC 4.0+, pero buena práctica)
                Class.forName(driver);
            }
        } catch (IOException ex) {
            System.err.println("Error al leer el archivo de propiedades de la base de datos: " + ex.getMessage());
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            System.err.println("Error: Driver JDBC de MySQL no encontrado: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (jdbcUrl == null || username == null || password == null) {
            throw new SQLException("Configuración de la base de datos no cargada correctamente.");
        }
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión a la base de datos: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    // Método principal para probar la conexión (opcional)
    public static void main(String[] args) {
        Connection conn = null;
        try {
            conn = DatabaseConnector.getConnection();
            if (conn != null) {
                System.out.println("¡Conexión a la base de datos MySQL exitosa!");
            } else {
                System.out.println("Falló la conexión a la base de datos.");
            }
        } catch (SQLException e) {
            System.err.println("Error de SQL al intentar conectar: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnector.closeConnection(conn);
        }
    }
}
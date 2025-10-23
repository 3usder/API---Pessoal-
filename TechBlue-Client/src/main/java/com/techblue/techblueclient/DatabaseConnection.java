package com.techblue.techblueclient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // ✅ CONFIGURAÇÕES CORRIGIDAS - usando banco 'mysql'
    private static final String URL = "jdbc:mysql://localhost:3306/mysql";
    private static final String USER = "root";
    private static final String PASSWORD = "5e5d3rElder@";

    public static Connection getConnection() throws SQLException {
        try {
            // Registrar o driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexão com MySQL estabelecida com sucesso! Banco: mysql");
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL não encontrado", e);
        }
    }

    // Método de teste para verificar conexão
    public static boolean testarConexao() {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Conexão com MySQL estabelecida com sucesso!");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Erro na conexão com MySQL: " + e.getMessage());
            return false;
        }
    }
}
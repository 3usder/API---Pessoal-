package com.techblue.techblueclient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // ✅ CONFIGURAÇÕES CORRIGIDAS
    private static final String URL = "jdbc:mysql://localhost:3306/techblue_db";
    private static final String USER = "root";
    private static final String PASSWORD = "5e5d3rElder@";

    public static Connection getConnection() throws SQLException {
        try {
            // Registrar o driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexão com MySQL estabelecida com sucesso! Banco: techblue_db");
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

    // ✅ MÉTODO NOVO - Testar se as tabelas existem
    public static void testarTabelas() {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Conectado ao banco: " + conn.getCatalog());

            // Testar se a tabela trabalhos_aluno existe
            var metaData = conn.getMetaData();
            var resultSet = metaData.getTables(null, null, "trabalhos_aluno", null);

            if (resultSet.next()) {
                System.out.println("✅ Tabela 'trabalhos_aluno' ENCONTRADA!");
            } else {
                System.out.println("❌ Tabela 'trabalhos_aluno' NÃO encontrada!");
            }

            // Testar se a tabela historico_trabalhos existe
            resultSet = metaData.getTables(null, null, "historico_trabalhos", null);
            if (resultSet.next()) {
                System.out.println("✅ Tabela 'historico_trabalhos' ENCONTRADA!");
            } else {
                System.out.println("❌ Tabela 'historico_trabalhos' NÃO encontrada!");
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao testar tabelas: " + e.getMessage());
        }
    }
}
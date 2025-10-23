package com.techblue.techblueclient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoService {

    // Classe para representar um trabalho
    public static class Trabalho {
        public String versao;
        public String dataEnvio;
        public String status;
        public String feedback;

        public Trabalho(String versao, String dataEnvio, String status, String feedback) {
            this.versao = versao;
            this.dataEnvio = dataEnvio;
            this.status = status;
            this.feedback = feedback;
        }
    }

    // Buscar histórico de trabalhos do aluno
    public List<Trabalho> carregarHistoricoTrabalhos(int alunoId) {
        List<Trabalho> historico = new ArrayList<>();
        String sql = "SELECT versao, data_envio, status, feedback FROM trabalhos WHERE aluno_id = ? ORDER BY data_envio DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, alunoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String versao = rs.getString("versao");
                String dataEnvio = rs.getString("data_envio");
                String status = rs.getString("status");
                String feedback = rs.getString("feedback");

                // Se feedback for null, usar string vazia
                if (feedback == null) feedback = "";

                historico.add(new Trabalho(versao, dataEnvio, status, feedback));
            }

            System.out.println("📊 Carregados " + historico.size() + " trabalhos do histórico");

        } catch (SQLException e) {
            System.out.println("❌ Erro ao carregar histórico: " + e.getMessage());
            // ✅ Dados de exemplo para teste
            historico.add(new Trabalho("v1.0", "2024-01-15", "✅ Aprovado", "Bom trabalho! Continue assim."));
            historico.add(new Trabalho("v1.1", "2024-01-20", "📝 Em Revisão", "Aguardando feedback do professor"));
        }
        return historico;
    }

    // Salvar novo trabalho
    public boolean salvarTrabalho(int alunoId, String conteudo, String versao) {
        String sql = "INSERT INTO trabalhos (aluno_id, versao, conteudo, data_envio, status) VALUES (?, ?, ?, NOW(), 'Enviado')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, alunoId);
            stmt.setString(2, versao);
            stmt.setString(3, conteudo);

            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("💾 Trabalho salvo no banco: " + versao);
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("❌ Erro ao salvar trabalho: " + e.getMessage());
            return false;
        }
    }

    // Buscar dados do aluno
    public String carregarDadosAluno(int alunoId) {
        String sql = "SELECT nome FROM alunos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, alunoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("nome");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erro ao carregar dados do aluno: " + e.getMessage());
        }
        return "Aluno";
    }
}
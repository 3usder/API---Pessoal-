package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrientacaoDAO {

    public List<Professor> getProfessoresDoBanco() {
        List<Professor> professores = new ArrayList<>();
        String sql = "SELECT p.id, p.nome, p.titulacao, COUNT(a.id) as qtd_alunos " +
                "FROM professores p " +
                "LEFT JOIN alunos a ON p.id = a.orientador_id " +
                "GROUP BY p.id, p.nome, p.titulacao " +
                "ORDER BY p.nome";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Professor professor = new Professor(rs.getString("nome"));

                // Adicionar alunos ao professor (se necessário)
                List<Aluno> alunosDoProfessor = getAlunosPorProfessor(rs.getInt("id"));
                for (Aluno aluno : alunosDoProfessor) {
                    professor.adicionarAluno(aluno);
                }

                professores.add(professor);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao carregar professores: " + e.getMessage());
            e.printStackTrace();
        }
        return professores;
    }

    public List<Aluno> getAlunosDoBanco() {
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT a.id, a.nome, a.email, p.id as orientador_id, p.nome as orientador_nome " +
                "FROM alunos a " +
                "LEFT JOIN professores p ON a.orientador_id = p.id " +
                "ORDER BY a.nome";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nomeOrientador = rs.getString("orientador_nome");
                Aluno aluno = new Aluno(rs.getString("nome"), rs.getString("email"));

                if (nomeOrientador != null) {
                    Professor orientador = new Professor(nomeOrientador);
                    aluno.setOrientador(orientador);
                }
                alunos.add(aluno);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao carregar alunos: " + e.getMessage());
            e.printStackTrace();
        }
        return alunos;
    }

    // Método auxiliar para pegar alunos de um professor específico
    private List<Aluno> getAlunosPorProfessor(int professorId) {
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT nome, email FROM alunos WHERE orientador_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, professorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Aluno aluno = new Aluno(rs.getString("nome"), rs.getString("email"));
                alunos.add(aluno);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao carregar alunos do professor: " + e.getMessage());
        }
        return alunos;
    }

    public boolean testarConexao() {
        return DatabaseConnection.testConnection();
    }

    // MÉTODOS PARA VINCULAR/DESVINCULAR (adicione esses)
    public boolean vincularAlunoOrientador(String alunoNome, String professorNome) {
        String sql = "UPDATE alunos SET orientador_id = (SELECT id FROM professores WHERE nome = ?) WHERE nome = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, professorNome);
            stmt.setString(2, alunoNome);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao vincular aluno: " + e.getMessage());
            return false;
        }
    }

    public boolean desvincularAluno(String alunoNome) {
        String sql = "UPDATE alunos SET orientador_id = NULL WHERE nome = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, alunoNome);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao desvincular aluno: " + e.getMessage());
            return false;
        }
    }
}
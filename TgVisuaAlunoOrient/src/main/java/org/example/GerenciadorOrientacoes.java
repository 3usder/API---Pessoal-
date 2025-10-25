package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

    public class GerenciadorOrientacoes {
        private List<Professor> professores;
        private List<Aluno> alunos;

        public GerenciadorOrientacoes() {
            this.professores = new ArrayList<>();
            this.alunos = new ArrayList<>();
        }

        // Métodos para gerenciar professores
        public void adicionarProfessor(Professor professor) {
            if (professor != null && !professores.contains(professor)) {
                professores.add(professor);
            }
        }

        public boolean removerProfessor(Professor professor) {
            if (professor == null) return false;

            // Remove o professor da lista
            boolean removido = professores.remove(professor);

            if (removido) {
                // Remove este professor como orientador de todos os alunos
                for (Aluno aluno : alunos) {
                    if (aluno.getOrientador() == professor) {
                        aluno.setOrientador(null);
                    }
                }
            }

            return removido;
        }

        public Professor buscarProfessorPorNome(String nome) {
            return professores.stream()
                    .filter(prof -> prof.getNome().equalsIgnoreCase(nome))
                    .findFirst()
                    .orElse(null);
        }

        // Métodos para gerenciar alunos
        public void adicionarAluno(Aluno aluno) {
            if (aluno != null && !alunos.contains(aluno)) {
                alunos.add(aluno);
                // Se o aluno já tem orientador, garante que está na lista do professor
                if (aluno.getOrientador() != null) {
                    aluno.getOrientador().adicionarAluno(aluno);
                }
            }
        }

        public boolean removerAluno(Aluno aluno) {
            if (aluno == null) return false;

            // Remove o aluno do seu orientador (se tiver)
            if (aluno.getOrientador() != null) {
                aluno.getOrientador().removerAluno(aluno);
            }

            // Remove o aluno da lista geral
            return alunos.remove(aluno);
        }

        public Aluno buscarAlunoPorEmail(String email) {
            return alunos.stream()
                    .filter(aluno -> aluno.getEmail().equalsIgnoreCase(email))
                    .findFirst()
                    .orElse(null);
        }

        // Método para vincular aluno a professor
        public boolean vincularAlunoOrientador(Aluno aluno, Professor professor) {
            if (aluno == null || professor == null) return false;

            // Remove o aluno do orientador anterior (se houver)
            if (aluno.getOrientador() != null) {
                aluno.getOrientador().removerAluno(aluno);
            }

            // Adiciona ao novo orientador
            professor.adicionarAluno(aluno);
            return true;
        }

        // Método para desvincular aluno de orientador
        public boolean desvincularAlunoOrientador(Aluno aluno) {
            if (aluno == null || aluno.getOrientador() == null) return false;

            aluno.getOrientador().removerAluno(aluno);
            return true;
        }

        // Getters
        public List<Professor> getProfessores() {
            return new ArrayList<>(professores);
        }

        public List<Aluno> getAlunos() {
            return new ArrayList<>(alunos);
        }

        public List<Aluno> getAlunosSemOrientador() {
            List<Aluno> semOrientador = new ArrayList<>();
            for (Aluno aluno : alunos) {
                if (aluno.getOrientador() == null) {
                    semOrientador.add(aluno);
                }
            }
            return semOrientador;
        }

        // Método para obter estatísticas básicas
        public void exibirEstatisticasBasicas() {
            System.out.println("=== ESTATÍSTICAS DO SISTEMA ===");
            System.out.println("Total de professores: " + professores.size());
            System.out.println("Total de alunos: " + alunos.size());
            System.out.println("Alunos sem orientador: " + getAlunosSemOrientador().size());

            if (!professores.isEmpty()) {
                double media = (double) (alunos.size() - getAlunosSemOrientador().size()) / professores.size();
                System.out.printf("Média de alunos por professor: %.1f\n", media);
            }
        }
    }


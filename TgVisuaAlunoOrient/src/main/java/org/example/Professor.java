package org.example;

import java.util.ArrayList;
import java.util.List;

    public class Professor {
        private String nome;
        private List<Aluno> alunos;

        // Construtor
        public Professor(String nome) {
            this.nome = nome;
            this.alunos = new ArrayList<>();
        }

        // Método para adicionar aluno
        public void adicionarAluno(Aluno aluno) {
            if (aluno == null) return;

            if (!alunos.contains(aluno)) {
                alunos.add(aluno);
                // Atualiza o orientador do aluno
                if (aluno.getOrientador() != this) {
                    aluno.setOrientador(this);
                }
            }
        }

        // Método para remover aluno
        public void removerAluno(Aluno aluno) {
            if (alunos.remove(aluno)) {
                // Remove o orientador do aluno apenas se for este professor
                if (aluno.getOrientador() == this) {
                    aluno.setOrientador(null);
                }
            }
        }

        // Verificar se o aluno está orientado por este professor
        public boolean possuiAluno(Aluno aluno) {
            return alunos.contains(aluno);
        }

        // Getters
        public String getNome() {
            return nome;
        }

        public List<Aluno> getAlunos() {
            return new ArrayList<>(alunos); // Retorna cópia para evitar modificações externas
        }

        // Método para obter quantidade de alunos
        public int getQuantidadeAlunos() {
            return alunos.size();
        }

        // Setters
        public void setNome(String nome) {
            this.nome = nome;
        }

        @Override
        public String toString() {
            return nome + " - " + getQuantidadeAlunos() + " aluno(s) orientado(s)";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Professor professor = (Professor) obj;
            return nome.equals(professor.nome);
        }

        @Override
        public int hashCode() {
            return nome.hashCode();
        }
    }


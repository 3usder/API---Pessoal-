package org.example;

public class Aluno {
        private String nome;
        private String email;
        private Professor orientador;

        // Construtor completo
        public Aluno(String nome, String email, Professor orientador) {
            this.nome = nome;
            this.email = email;
            this.orientador = orientador;
        }

        // Construtor sem orientador
        public Aluno(String nome, String email) {
            this.nome = nome;
            this.email = email;
            this.orientador = null;
        }

        // Getters
        public String getNome() {
            return nome;
        }

        public String getEmail() {
            return email;
        }

        public Professor getOrientador() {
            return orientador;
        }

        // Setters
        public void setNome(String nome) {
            this.nome = nome;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setOrientador(Professor orientador) {
            this.orientador = orientador;
        }

        @Override
        public String toString() {
            String orientadorNome = (orientador != null) ? orientador.getNome() : "Sem orientador";
            return nome + " (" + email + ") - Orientador: " + orientadorNome;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Aluno aluno = (Aluno) obj;
            return email.equals(aluno.email);
        }

        @Override
        public int hashCode() {
            return email.hashCode();
        }
    }



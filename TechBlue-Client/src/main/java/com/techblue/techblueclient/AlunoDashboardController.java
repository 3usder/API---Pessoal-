package com.techblue.techblueclient;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AlunoDashboardController implements Initializable {

    @FXML private TextArea editorTextArea;
    @FXML private TextArea feedbackTextArea;
    @FXML private TableView<Trabalho> historicoTable;
    @FXML private TableColumn<Trabalho, String> versaoColumn;
    @FXML private TableColumn<Trabalho, String> dataColumn;
    @FXML private TableColumn<Trabalho, String> statusColumn;
    @FXML private Label statusLabel;

    private int contadorVersoes = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("🚀 Sistema inicializado - Modo Demonstração");

        // Verificar componentes críticos
        if (editorTextArea == null) System.out.println("⚠️  Editor não encontrado");
        if (historicoTable == null) System.out.println("⚠️  Tabela não encontrada");
        if (statusLabel == null) System.out.println("⚠️  Status não encontrado");

        configurarComponentes();
        carregarDadosDemonstracao();
        mostrarStatus("✅ Sistema em Modo Demonstração - Pronto para uso!", "sucesso");
    }

    private void configurarComponentes() {
        // Configurar tabela se existir
        if (versaoColumn != null && dataColumn != null && statusColumn != null) {
            versaoColumn.setCellValueFactory(new PropertyValueFactory<>("versao"));
            dataColumn.setCellValueFactory(new PropertyValueFactory<>("dataEnvio"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            System.out.println("✅ Tabela configurada");
        }

        if (feedbackTextArea != null) {
            feedbackTextArea.setEditable(false);
            feedbackTextArea.setText("Área de feedback - Modo demonstração");
        }
    }

    private void carregarDadosDemonstracao() {
        if (historicoTable != null) {
            List<Trabalho> demonstracao = new ArrayList<>();
            demonstracao.add(new Trabalho("v1.0", LocalDateTime.now().minusDays(2), "Enviado", "Bom trabalho! Continue assim."));
            demonstracao.add(new Trabalho("v2.0", LocalDateTime.now().minusDays(1), "Revisado", "Ótimas melhorias na versão 2.0"));
            demonstracao.add(new Trabalho("v3.0", LocalDateTime.now(), "Rascunho", null));

            historicoTable.getItems().setAll(demonstracao);
            System.out.println("✅ Dados de demonstração carregados");
        }
    }

    // ========== BOTÕES QUE FUNCIONAM SEM MYSQL ==========

    @FXML
    private void onSalvarRascunho() {
        if (validarEditor()) {
            String versao = "v" + (contadorVersoes++) + ".0-Rascunho";
            adicionarAoTabela(versao, "Rascunho", "Rascunho salvo localmente");
            mostrarStatus("✅ Rascunho salvo - " + versao, "sucesso");
        }
    }

    @FXML
    private void onEnviarParaRevisao() {
        if (validarEditor()) {
            String versao = "v" + (contadorVersoes++) + ".0";
            adicionarAoTabela(versao, "Enviado", "Aguardando revisão");
            mostrarStatus("✅ Enviado para revisão - " + versao, "sucesso");
        }
    }

    @FXML
    private void onEnviarNovaVersao() {
        if (validarEditor()) {
            String versao = "v" + (contadorVersoes++) + ".0";
            adicionarAoTabela(versao, "Enviado", "Nova versão submetida");
            mostrarStatus("✅ Nova versão enviada - " + versao, "sucesso");
        }
    }

    @FXML
    private void onCancelarEdicao() {
        if (editorTextArea != null) {
            editorTextArea.clear();
            mostrarStatus("🗑️  Edição cancelada - Texto limpo", "sucesso");
        }
    }

    @FXML
    private void onVisualizarVersao() {
        if (historicoTable != null) {
            Trabalho selected = historicoTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                mostrarStatus("👁️  Visualizando: " + selected.getVersao(), "sucesso");
                if (feedbackTextArea != null) {
                    feedbackTextArea.setText("Feedback: " + selected.getFeedback());
                }
            } else {
                mostrarStatus("📋 Selecione uma versão na tabela", "erro");
            }
        } else {
            mostrarStatus("ℹ️  Tabela não disponível em demonstração", "sucesso");
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    private boolean validarEditor() {
        if (editorTextArea == null) {
            mostrarStatus("❌ Editor não disponível", "erro");
            return false;
        }

        if (editorTextArea.getText().trim().isEmpty()) {
            mostrarStatus("❌ Digite algum conteúdo antes de salvar", "erro");
            return false;
        }

        return true;
    }

    private void adicionarAoTabela(String versao, String status, String feedback) {
        if (historicoTable != null) {
            Trabalho novoTrabalho = new Trabalho(versao, LocalDateTime.now(), status, feedback);
            historicoTable.getItems().add(0, novoTrabalho); // Adiciona no início
        }
    }

    private void mostrarStatus(String mensagem, String tipo) {
        if (statusLabel != null) {
            statusLabel.setText(mensagem);
            statusLabel.setStyle("-fx-text-fill: " + ("sucesso".equals(tipo) ? "green" : "red") + "; -fx-font-weight: bold;");
        }
        System.out.println("📢 Status: " + mensagem);
    }

    // Classe do trabalho
    public static class Trabalho {
        private final String versao, status, feedback;
        private final LocalDateTime dataEnvio;

        public Trabalho(String versao, LocalDateTime dataEnvio, String status, String feedback) {
            this.versao = versao;
            this.dataEnvio = dataEnvio;
            this.status = status;
            this.feedback = feedback;
        }

        public String getVersao() { return versao; }
        public String getDataEnvio() {
            return dataEnvio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        public String getStatus() { return status; }
        public String getFeedback() { return feedback != null ? feedback : "Sem feedback"; }
    }
}
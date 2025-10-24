package com.techblue.techblueclient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AlunoDashboardController implements Initializable {

    @FXML private TextArea editorTextArea;
    @FXML private TextArea feedbackTextArea;
    @FXML private TableView<Trabalho> historicoTableView;
    @FXML private Label statusLabel;

    private ObservableList<Trabalho> trabalhos = FXCollections.observableArrayList();
    private Trabalho trabalhoSelecionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("🔍 Iniciando teste de conexão...");

        // ✅ TESTAR CONEXÃO E TABELAS
        DatabaseConnection.testarTabelas();

        System.out.println("🚀 Sistema inicializado - Modo Demonstração");

        // ✅ CORRIGIR AS COLUNAS - USANDO OS NOMES DO FXML
        if (historicoTableView != null && !historicoTableView.getColumns().isEmpty()) {
            TableColumn<Trabalho, ?> versaoColumn = historicoTableView.getColumns().get(0);
            TableColumn<Trabalho, ?> dataColumn = historicoTableView.getColumns().get(1);
            TableColumn<Trabalho, ?> statusColumn = historicoTableView.getColumns().get(2);
            TableColumn<Trabalho, ?> feedbackColumn = historicoTableView.getColumns().get(3);

            versaoColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            dataColumn.setCellValueFactory(new PropertyValueFactory<>("dataEnvio"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            feedbackColumn.setCellValueFactory(new PropertyValueFactory<>("feedback"));
        } else {
            System.out.println("⚠️  Tabela não encontrada, usando configuração manual");
        }

        // Carregar dados de exemplo se não conectar ao banco
        carregarDadosDemonstracao();

        // Configurar listeners
        configurarListeners();

        // Configurar área de texto
        editorTextArea.setWrapText(true);
        feedbackTextArea.setWrapText(true);

        System.out.println("✅ Tabela configurada");
        atualizarStatus("✅ Sistema em Modo Demonstração - Pronto para uso!");
    }

    private void configurarListeners() {
        historicoTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        trabalhoSelecionado = newValue;
                        editorTextArea.setText(newValue.getConteudo());
                        feedbackTextArea.setText(newValue.getFeedback());
                    }
                }
        );
    }

    private void carregarDadosDemonstracao() {
        try {
            // ✅ TENTAR CARREGAR DO BANCO PRIMEIRO
            carregarDadosBanco();
        } catch (Exception e) {
            // ❌ SE FALHAR, USAR DADOS DE EXEMPLO
            System.out.println("⚠️  Usando dados de demonstração: " + e.getMessage());
            trabalhos.addAll(
                    new Trabalho(1, "Trabalho de Matemática", "Conteúdo do trabalho de matemática...", "rascunho", "", "Nenhum feedback ainda"),
                    new Trabalho(2, "Projeto de Pesquisa", "Introdução da pesquisa sobre IA...", "enviado", "2024-01-15", "Bom trabalho, mas pode melhorar as referências"),
                    new Trabalho(3, "Relatório de Física", "Análise dos experimentos realizados...", "corrigido", "2024-01-10", "Excelente relatório! Nota: 9.5")
            );
            historicoTableView.setItems(trabalhos);
        }
    }

    // ✅ MÉTODO NOVO - Carregar dados do banco MySQL
    private void carregarDadosBanco() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM trabalhos_aluno")) {

            trabalhos.clear();
            while (rs.next()) {
                Trabalho trabalho = new Trabalho(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("conteudo"),
                        rs.getString("status"),
                        rs.getTimestamp("data_envio") != null ?
                                rs.getTimestamp("data_envio").toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "",
                        rs.getString("feedback")
                );
                trabalhos.add(trabalho);
            }
            historicoTableView.setItems(trabalhos);
            System.out.println("✅ Dados carregados do MySQL: " + trabalhos.size() + " trabalhos");

        } catch (SQLException e) {
            System.out.println("❌ Erro ao carregar dados do banco: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onSalvarRascunho() {
        if (trabalhoSelecionado == null) {
            atualizarStatus("❌ Selecione um trabalho para salvar");
            return;
        }

        trabalhoSelecionado.setConteudo(editorTextArea.getText());
        trabalhoSelecionado.setStatus("rascunho");
        atualizarStatus("✅ Rascunho salvo com sucesso!");
        historicoTableView.refresh();
    }

    @FXML
    private void onEnviarParaRevisao() {
        if (trabalhoSelecionado == null) {
            atualizarStatus("❌ Selecione um trabalho para enviar");
            return;
        }

        trabalhoSelecionado.setConteudo(editorTextArea.getText());
        trabalhoSelecionado.setStatus("enviado");
        trabalhoSelecionado.setDataEnvio(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        atualizarStatus("✅ Trabalho enviado para revisão!");
        historicoTableView.refresh();
    }

    @FXML
    private void enviarNovaVersao() {
        if (trabalhoSelecionado == null) {
            atualizarStatus("❌ Selecione um trabalho para nova versão");
            return;
        }

        trabalhoSelecionado.setConteudo(editorTextArea.getText());
        trabalhoSelecionado.setStatus("enviado");
        trabalhoSelecionado.setDataEnvio(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        atualizarStatus("✅ Nova versão enviada!");
        historicoTableView.refresh();
    }

    @FXML
    private void onCancelarEdicao() {
        if (trabalhoSelecionado != null) {
            editorTextArea.setText(trabalhoSelecionado.getConteudo());
        }
        atualizarStatus("✅ Edição cancelada - Alterações descartadas");
    }

    @FXML
    private void onVisualizarVersao() {
        if (trabalhoSelecionado == null) {
            atualizarStatus("❌ Selecione um trabalho para visualizar");
            return;
        }

        String versao = String.format(
                "📋 Trabalho: %s\n📊 Status: %s\n📅 Data: %s\n\n📝 Conteúdo:\n%s\n\n💬 Feedback:\n%s",
                trabalhoSelecionado.getTitulo(),
                trabalhoSelecionado.getStatus(),
                trabalhoSelecionado.getDataEnvio(),
                trabalhoSelecionado.getConteudo(),
                trabalhoSelecionado.getFeedback()
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Visualizar Versão");
        alert.setHeaderText("Detalhes do Trabalho");
        alert.setContentText(versao);
        alert.showAndWait();
    }

    private void atualizarStatus(String mensagem) {
        statusLabel.setText("📢 Status: " + mensagem);
        System.out.println(mensagem);
    }

    // Classe interna Trabalho
    public static class Trabalho {
        private final IntegerProperty id = new SimpleIntegerProperty();
        private final StringProperty titulo = new SimpleStringProperty();
        private final StringProperty conteudo = new SimpleStringProperty();
        private final StringProperty status = new SimpleStringProperty();
        private final StringProperty dataEnvio = new SimpleStringProperty();
        private final StringProperty feedback = new SimpleStringProperty();

        public Trabalho(int id, String titulo, String conteudo, String status, String dataEnvio, String feedback) {
            setId(id);
            setTitulo(titulo);
            setConteudo(conteudo);
            setStatus(status);
            setDataEnvio(dataEnvio);
            setFeedback(feedback);
        }

        // Getters e Setters
        public int getId() { return id.get(); }
        public void setId(int value) { id.set(value); }
        public IntegerProperty idProperty() { return id; }

        public String getTitulo() { return titulo.get(); }
        public void setTitulo(String value) { titulo.set(value); }
        public StringProperty tituloProperty() { return titulo; }

        public String getConteudo() { return conteudo.get(); }
        public void setConteudo(String value) { conteudo.set(value); }
        public StringProperty conteudoProperty() { return conteudo; }

        public String getStatus() { return status.get(); }
        public void setStatus(String value) { status.set(value); }
        public StringProperty statusProperty() { return status; }

        public String getDataEnvio() { return dataEnvio.get(); }
        public void setDataEnvio(String value) { dataEnvio.set(value); }
        public StringProperty dataEnvioProperty() { return dataEnvio; }

        public String getFeedback() { return feedback.get(); }
        public void setFeedback(String value) { feedback.set(value); }
        public StringProperty feedbackProperty() { return feedback; }
    }
}
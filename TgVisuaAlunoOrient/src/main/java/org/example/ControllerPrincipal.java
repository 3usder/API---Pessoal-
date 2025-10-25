package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerPrincipal implements Initializable {

    @FXML private TableView<Aluno> tableViewAlunos;
    @FXML private TableColumn<Aluno, String> colNomeAluno;
    @FXML private TableColumn<Aluno, String> colEmailAluno;
    @FXML private TableColumn<Aluno, String> colOrientadorAluno;

    @FXML private TableView<Professor> tableViewProfessores;
    @FXML private TableColumn<Professor, String> colNomeProfessor;
    @FXML private TableColumn<Professor, Integer> colQuantidadeAlunos;

    @FXML private ComboBox<Aluno> comboAlunos;
    @FXML private ComboBox<Professor> comboProfessores;

    @FXML private Label labelTotalAlunos;
    @FXML private Label labelAlunosComOrientador;
    @FXML private Label labelAlunosSemOrientador;
    @FXML private Label labelMediaPorProfessor;

    private OrientacaoDAO orientacaoDAO;
    private ObservableList<Aluno> todosAlunos;
    private ObservableList<Professor> todosProfessores;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        orientacaoDAO = new OrientacaoDAO();

        // TESTE SIMPLIFICADO - remova a verificação por enquanto
        System.out.println("🎯 Iniciando aplicação...");

        configurarTabelas();
        carregarDados();

        // Quando clicar em um professor, filtrar alunos
        tableViewProfessores.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> filtrarAlunosPorProfessor(newVal)
        );
    }

    private void configurarTabelas() {
        // Configurar tabela de alunos
        colNomeAluno.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEmailAluno.setCellValueFactory(new PropertyValueFactory<>("email"));
        colOrientadorAluno.setCellValueFactory(cellData -> {
            Professor orientador = cellData.getValue().getOrientador();
            return javafx.beans.binding.Bindings.createStringBinding(
                    () -> orientador != null ? orientador.getNome() : "Sem orientador"
            );
        });

        // Configurar tabela de professores
        colNomeProfessor.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colQuantidadeAlunos.setCellValueFactory(new PropertyValueFactory<>("quantidadeAlunos"));
    }

    private void carregarDados() {
        try {
            // Carregar professores do banco
            todosProfessores = FXCollections.observableArrayList(orientacaoDAO.getProfessoresDoBanco());
            tableViewProfessores.setItems(todosProfessores);

            // Carregar alunos do banco
            todosAlunos = FXCollections.observableArrayList(orientacaoDAO.getAlunosDoBanco());
            tableViewAlunos.setItems(todosAlunos);

            // Atualizar comboboxes
            comboAlunos.setItems(FXCollections.observableArrayList(todosAlunos));
            comboProfessores.setItems(FXCollections.observableArrayList(todosProfessores));

            atualizarEstatisticas();

        } catch (Exception e) {
            mostrarAlerta("Erro ao carregar dados: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void filtrarAlunosPorProfessor(Professor professor) {
        if (professor == null) {
            // Mostrar todos os alunos
            tableViewAlunos.setItems(todosAlunos);
            atualizarEstatisticas();
        } else {
            // Filtrar alunos do professor selecionado
            ObservableList<Aluno> alunosFiltrados = FXCollections.observableArrayList();
            for (Aluno aluno : todosAlunos) {
                if (aluno.getOrientador() != null &&
                        aluno.getOrientador().getNome().equals(professor.getNome())) {
                    alunosFiltrados.add(aluno);
                }
            }
            tableViewAlunos.setItems(alunosFiltrados);

            // Atualizar estatísticas para o professor selecionado
            labelTotalAlunos.setText(String.valueOf(alunosFiltrados.size()));
            labelAlunosComOrientador.setText(String.valueOf(alunosFiltrados.size()));
            labelAlunosSemOrientador.setText("0");
            labelMediaPorProfessor.setText(String.valueOf(alunosFiltrados.size()));
        }
    }

    @FXML
    private void mostrarTodosAlunos() {
        tableViewProfessores.getSelectionModel().clearSelection();
        tableViewAlunos.setItems(todosAlunos);
        atualizarEstatisticas();
    }

    @FXML
    private void atualizarDados() {
        carregarDados();
        mostrarAlerta("Dados atualizados do banco!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void vincularAlunoOrientador() {
        Aluno aluno = comboAlunos.getValue();
        Professor professor = comboProfessores.getValue();

        if (aluno == null || professor == null) {
            mostrarAlerta("Selecione um aluno e um professor!");
            return;
        }

        // Agora salva no banco
        boolean sucesso = orientacaoDAO.vincularAlunoOrientador(aluno.getNome(), professor.getNome());

        if (sucesso) {
            aluno.setOrientador(professor);
            professor.adicionarAluno(aluno);
            atualizarInterface();
            mostrarAlerta("Aluno vinculado com sucesso!", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Erro ao vincular aluno no banco!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void desvincularAluno() {
        Aluno aluno = comboAlunos.getValue();
        if (aluno == null) {
            mostrarAlerta("Selecione um aluno!");
            return;
        }

        // Agora salva no banco
        boolean sucesso = orientacaoDAO.desvincularAluno(aluno.getNome());

        if (sucesso) {
            if (aluno.getOrientador() != null) {
                aluno.getOrientador().removerAluno(aluno);
            }
            aluno.setOrientador(null);
            atualizarInterface();
            mostrarAlerta("Aluno desvinculado com sucesso!", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Erro ao desvincular aluno no banco!", Alert.AlertType.ERROR);
        }
    }

    private void atualizarInterface() {
        tableViewAlunos.refresh();
        tableViewProfessores.refresh();
        atualizarEstatisticas();
    }

    private void atualizarEstatisticas() {
        int totalAlunos = todosAlunos.size();
        int alunosComOrientador = 0;
        for (Aluno aluno : todosAlunos) {
            if (aluno.getOrientador() != null) {
                alunosComOrientador++;
            }
        }
        int alunosSemOrientador = totalAlunos - alunosComOrientador;

        labelTotalAlunos.setText(String.valueOf(totalAlunos));
        labelAlunosComOrientador.setText(String.valueOf(alunosComOrientador));
        labelAlunosSemOrientador.setText(String.valueOf(alunosSemOrientador));

        if (!todosProfessores.isEmpty()) {
            double media = (double) alunosComOrientador / todosProfessores.size();
            labelMediaPorProfessor.setText(String.format("%.1f", media));
        } else {
            labelMediaPorProfessor.setText("0.0");
        }
    }

    private void mostrarAlerta(String mensagem) {
        mostrarAlerta(mensagem, Alert.AlertType.WARNING);
    }

    private void mostrarAlerta(String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Sistema de Orientação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
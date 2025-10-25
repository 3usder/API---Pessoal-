package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Tenta carregar o FXML simples
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/InterfaceSimples.fxml"));

            primaryStage.setTitle("Sistema de Orientação - MySQL Test");
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.show();

        } catch (Exception e) {
            System.out.println("Erro ao carregar FXML: " + e.getMessage());
            // Se der erro, mostra a interface simples
            showInterfaceManual(primaryStage);
        }
    }

    private void showInterfaceManual(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label label = new Label("Sistema de Orientação - Modo Simples");
        Button btn = new Button("Testar MySQL");
        btn.setOnAction(e -> testarMySQL());

        root.getChildren().addAll(label, btn);

        primaryStage.setScene(new Scene(root, 400, 200));
        primaryStage.show();
    }

    private void testarMySQL() {
        OrientacaoDAO dao = new OrientacaoDAO();
        if (dao.testarConexao()) {
            System.out.println("✅ Conexão MySQL OK!");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MySQL Test");
            alert.setHeaderText("Conexão bem-sucedida!");
            alert.setContentText("Conectado ao banco de dados MySQL");
            alert.showAndWait();

        } else {
            System.out.println("❌ Falha na conexão MySQL");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("MySQL Test");
            alert.setHeaderText("Falha na conexão");
            alert.setContentText("Não foi possível conectar ao MySQL");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
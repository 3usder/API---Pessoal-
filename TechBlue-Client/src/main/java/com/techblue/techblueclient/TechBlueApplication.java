package com.techblue.techblueclient;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TechBlueApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // CORREÇÃO DO CAMINHO DO FXML
        FXMLLoader fxmlLoader = new FXMLLoader(TechBlueApplication.class.getResource("/com/techblue/techblueclient/aluno-dashboard.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("BlueTech - Aluno");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
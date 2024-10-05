package com.owltest;

import com.owltest.views.MainWindowController;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("views/MainWindow.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        Scene scene = new Scene(page, 1400, 800);

        this.primaryStage.setTitle("СППР");
        this.primaryStage.setScene(scene);
        this.primaryStage.setResizable(false);

        MainWindowController controller = loader.getController();
        controller.setStage(this.primaryStage);
        this.primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
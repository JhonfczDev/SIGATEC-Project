package com.sigatec.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage){
        BorderPane root = new BorderPane();
        root.setCenter(new DashboardView().build());

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(MainApp.class.getResource("app.css").toExternalForm());
        stage.setTitle("SIGATEC");
        stage.setScene(scene);
        stage.show();
    }

    public static void  main(String[] args){
        launch(args);
    }
}

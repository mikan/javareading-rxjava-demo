package com.github.mikan.jrd;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DemoApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("view/MapView.fxml"));
            scene = new Scene(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        primaryStage.setTitle("DemoApp");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

/*
package ca.udem.maville.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ca.udem.maville.api.client.MavilleRestClient;

public class MainApp extends Application {
    private final MavilleRestClient client = new MavilleRestClient("http://localhost:7000");

    @Override
    public void start(Stage stage) {
        Label label = new Label("Welcome to MaVille!");
        Button fetchButton = new Button("Fetch Works");
        fetchButton.setOnAction(e -> {
            try {
                var works = client.getInProgressWorks();
                label.setText("Fetched " + works.size() + " works!");
            } catch (Exception ex) {
                label.setText("Error: " + ex.getMessage());
            }
        });

        VBox root = new VBox(10, label, fetchButton);
        Scene scene = new Scene(root, 400, 200);
        stage.setScene(scene);
        stage.setTitle("MaVille - Dashboard");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} */

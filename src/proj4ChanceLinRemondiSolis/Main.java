/*
 * File: Main.java
 * Names: Graham Chance, Jenny Lin, Ana Sofia Solis Canales, Mike Remondi
 * Class: CS361
 * Project: 4
 * Due Date: October 11, 2016
 */

package proj4ChanceLinRemondiSolis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Launches JavaFX application
 *
 * @author Graham Chance
 * @author Jenny Lin
 * @author Ana Sofia Solis Canales
 * @author Mike Remondi
 */
public class Main extends Application {

    /**
     * Launches application.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Sets up the main GUI to play a scale.
     * Player contains a menu bar:
     * File menu: exit option
     * Edit menu: Select all and Delete options
     * Action menu: Play and Stop options
     *
     * @param primaryStage the stage to display the gui
     */
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        primaryStage.setTitle("Composition Sheet");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
    }
}

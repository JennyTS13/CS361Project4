/*
 * File: Main.java
 * Names: Graham Chance, Jenny Lin, Ana Sofia Solis Canales, Mike Remondi
 * Class: CS361
 * Project: 4
 * Due Date: October 11, 2016
 */

package proj4ChanceLinRemondiSolis;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *  Creates a JavaFX window for the user to compose a musical piece to play
 *  Controller for events in application
 *  @author Graham Chance
 *  @author Jenny Lin
 *  @author Ana Sofia Solis Canales
 *  @author Mike Remondi
 */
public class Main extends Application {

    private MidiPlayer midiPlayer = new MidiPlayer(100, 60);

    @FXML
    private Pane fxCompositionSheet;
    private CompositionSheet compositionSheet;

    @FXML
    private Line fxTempoLine;
    private TempoLine tempoLine;

    @FXML
    private ToggleGroup instrumentGroup;

    /**
     * Seeds our CompositionSheet and TempoLine objects with the
     * fields from the FXML file after the FXML has been initialized
     */
    public void initialize() {
        this.compositionSheet = new CompositionSheet(this.fxCompositionSheet);
        this.tempoLine = new TempoLine(this.fxTempoLine);
        handleInstrumentChange();
    }

    /**
     * Changes the instrument that the future notes will be played with
     */
    @FXML
    public void handleInstrumentChange(){
        this.compositionSheet.changeInstrument(((RadioButton)instrumentGroup.getSelectedToggle()).getTextFill());
    }

    /**
     * Adds a note to the composition panel
     *
     * @param mouseEvent this is the mouseEvent that is mapped
     * to this functionality (e.g. left click event)
     */
    @FXML
    protected void handleCompositionClick(MouseEvent mouseEvent) {
        if (this.tempoLine.isVisible()){
            this.handleStopMusic();
        }
        else {
            this.compositionSheet.addNoteToComposition(
                    mouseEvent.getX(), mouseEvent.getY());
        }
    }

    /**
     * starts the midiplayer and the animation so they are
     * very close in time with each other (milliseconds)
     */
    protected void playMusicAndAnimation() {
        this.tempoLine.playAnimation();
        this.midiPlayer.play();
    }


    /**
     * Stops the midi player, the animation, and hides the tempo bar
     * This code/docstring is "borrowed" by Alex Rinker from his group's proj 2
     *
     */
    @FXML
    protected void handleStopMusic() {
        this.midiPlayer.stop();
        this.tempoLine.stopAnimation();
        this.tempoLine.hideTempoLine();
    }


    /**
     * Safely exits the program without throwing an error
     *
     * @param event the event to trigger the exit.
     */
    @FXML
    protected void handleExit(ActionEvent event) {
        System.exit(0);
    }
    /**
     * Creates a dialog box prompting the user for the starting note
     * for use in the midi player.
     * Then calls the playMidi method using that note (Integer)
     * This code/docstring is "borrowed" by Alex Rinker from his group's proj 2
     *
     * @param event the event which should trigger the dialog box and
     * midiplayer combo functionality.
     */
    @FXML
    protected void handlePlayMidi(ActionEvent event) {
        this.midiPlayer.stop();
        this.midiPlayer.clear();
        double stopTime = compositionSheet.buildSong(this.midiPlayer);
        this.tempoLine.updateTempoLine(stopTime);
        playMusicAndAnimation();
    }

    /**
     * Sets up the main GUI to play a scale.
     * Player contains a menu bar with exit option and two buttons:
     * play and stop.
     *
     * @param primaryStage the stage to display the gui
     */
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = null;

        try{
            FXMLLoader fxmlLoader = new  FXMLLoader( getClass().getResource("Main.fxml") );
            fxmlLoader.setController( this );      // set Composition as the controller
            root = fxmlLoader.load();
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        primaryStage.setTitle("Composition Sheet");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
    }

    /**
     * Launches application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}

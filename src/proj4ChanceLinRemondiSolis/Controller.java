package proj4ChanceLinRemondiSolis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Optional;

/**
* Handles all user GUI interactions and coordinates with the MidiPlayer
* and Composition.
*/
public class Controller {

    private MidiPlayer midiPlayer = new MidiPlayer(100, 60);

    @FXML private Pane fxCompositionSheet;
    private CompositionPaneManager compositionPaneManager;
    @FXML private Line fxTempoLine;
    private TempoLine tempoLine;
    private Coordinates lastDragLocation = new Coordinates();


    @FXML private ToggleGroup instrumentGroup;

    /**
     * Seeds our CompositionPaneManager and TempoLine objects with the
     * fields from the FXML file after the FXML has been initialized
     */
    public void initialize() {
        this.compositionPaneManager = new CompositionPaneManager(this.fxCompositionSheet);
        this.tempoLine = new TempoLine(this.fxTempoLine);
        handleInstrumentChange();
    }


    /**
     * Sets all of the notes to be selected and adds them to the selected list.
     */
    public void handleSelectAll(){
        this.compositionPaneManager.selectAllNotes();
    }

    /**
     * Deletes the selected notes from the composition panel
     */
    @FXML
    public void handleDelete() {
        this.compositionPaneManager.deleteNotes();
    }

    /**
     * Changes the instrument that the future notes will be played with
     */
    @FXML
    public void handleInstrumentChange() {
        RadioButton instrument = (RadioButton) instrumentGroup.getSelectedToggle();
        this.compositionPaneManager.changeInstrument(instrument.getTextFill());
    }

    /**
     * Handles the different Mouse Click actions in the composition
     *
     * @param mouseEvent click on the composition
     */
    @FXML
    protected void handleCompositionClick(MouseEvent mouseEvent) {
        if (!mouseEvent.isStillSincePress()) { return; }
        this.handleStopMusic();
        if (mouseEvent.isControlDown()) {
            compositionPaneManager.handleControlClickAt(mouseEvent.getX(),mouseEvent.getY());
        } else {
            compositionPaneManager.handleClickAt(mouseEvent.getX(), mouseEvent.getY());
        }
    }

    @FXML
    public void handleMousePressed(MouseEvent mouseEvent) {
        lastDragLocation.x = mouseEvent.getX();
        lastDragLocation.y = mouseEvent.getY();
        compositionPaneManager.handleDragStartedAtLocation(mouseEvent.getX(), mouseEvent.getY());
    }

    @FXML
    public void handleMouseDrag(MouseEvent mouseEvent) {
        compositionPaneManager.handleDragMoved(mouseEvent.getX() - lastDragLocation.x, mouseEvent.getY() - lastDragLocation.y);
        lastDragLocation.x = mouseEvent.getX();
        lastDragLocation.y = mouseEvent.getY();
    }


    @FXML
    public void handleMouseReleased(MouseEvent mouseEvent) {
        compositionPaneManager.handleDragEnded();
    }

    /**
     * starts the reproduction  of the composition
     */
    private void playMusicAndAnimation() {
        this.tempoLine.playAnimation();
        this.midiPlayer.play();
    }


    /**
     * Stops the reproduction of the composition
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
     * Plays the sounds displayed in the composition.
     */
    @FXML
    protected void handlePlayMidi() {
        this.midiPlayer.stop();
        this.midiPlayer.clear();
        this.compositionPaneManager.buildSong(this.midiPlayer);
        double stopTime =this.compositionPaneManager.calculateStopTime();
        this.tempoLine.updateTempoLine(stopTime);
        playMusicAndAnimation();
    }

    class Coordinates { double x, y; }
}

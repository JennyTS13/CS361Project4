package proj4ChanceLinRemondiSolis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import java.util.ArrayList;

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


    public void handleSelectAll(){
        this.compositionPaneManager.clearSelectedNotes();
        ArrayList<MusicalNote> notes = this.compositionPaneManager.getNotes();
        for (MusicalNote note: notes){
            note.setSelected(true);
            this.compositionPaneManager.addNoteToSelectedNotes(note);
        }
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
        if (this.tempoLine.isVisible()) {
            this.handleStopMusic();
        }
        //checking this so that we dont confuse drags with clicks
        else if (mouseEvent.isStillSincePress()) {
            if (!mouseEvent.isControlDown()) {
                this.compositionPaneManager.clearSelectedNotes();
            }
            this.compositionPaneManager.addNoteToComposition(
                    mouseEvent.getX(),
                    mouseEvent.getY());
        }
    }


    @FXML
    public void handleMouseDrag() {


        System.out.println("Mouse drag");
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
}
/*
 * File: Controller.java
 * Names: Graham Chance, Jenny Lin, Ana Sofia Solis Canales, Mike Remondi
 * Class: CS361
 * Project: 4
 * Due Date: October 11, 2016
 */

package proj4ChanceLinRemondiSolis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

/**
 * This class handles all user GUI interactions
 */
public class Controller {
    @FXML
    private Pane fxCompositionSheet;
    @FXML
    private Line fxTempoLine;
    @FXML
    private ToggleGroup instrumentGroup;

    private CompositionManager compositionManager;
    private Coordinates lastDragLocation = new Coordinates();
    private boolean isDragging;

    /**
     * Seeds our CompositionPaneManager and TempoLine objects with the
     * fields from the FXML file after the FXML has been initialized
     */
    public void initialize() {
        this.compositionManager = new CompositionManager(this.fxCompositionSheet, new TempoLine(fxTempoLine));
        handleInstrumentChange();
    }

    /**
     * Sets all of the notes to be selected and adds them to the selected list.
     */
    public void handleSelectAll() {
        this.compositionManager.selectAllNotes();
    }

    /**
     * Deletes the selected notes from the composition panel
     */
    @FXML
    public void handleDelete() {
        this.compositionManager.deleteNotes();
    }

    /**
     * Changes the instrument that the future notes will be played with
     */
    @FXML
    public void handleInstrumentChange() {
        RadioButton instrument = (RadioButton) instrumentGroup.getSelectedToggle();
        this.compositionManager.changeInstrument(instrument.getTextFill());
    }

    /**
     * Handles the GUI's mousePressed event.
     *
     * @param mouseEvent the GUI's mouseEvent
     */
    @FXML
    public void handleMousePressed(MouseEvent mouseEvent) {
        lastDragLocation.x = mouseEvent.getX();
        lastDragLocation.y = mouseEvent.getY();
        compositionManager.handleDragStartedAtLocation(mouseEvent.getX(), mouseEvent.getY());
    }

    /**
     * Handles the GUI's mouseDrag event.
     *
     * @param mouseEvent the GUI's mouseEvent
     */
    @FXML
    public void handleMouseDrag(MouseEvent mouseEvent) {
        compositionManager.handleDragMoved(mouseEvent.getX() - lastDragLocation.x,
                mouseEvent.getY() - lastDragLocation.y);
        lastDragLocation.x = mouseEvent.getX();
        lastDragLocation.y = mouseEvent.getY();
        this.isDragging = true;
    }

    /**
     * Handles the GUI's mouseReleased event.
     *
     * @param mouseEvent the GUI's mouseEvent
     */
    @FXML
    public void handleMouseReleased(MouseEvent mouseEvent) {
        compositionManager.handleDragEnded();
        if (!isDragging) {
            if (mouseEvent.isControlDown()) {
                compositionManager.handleControlClickAt(mouseEvent.getX(), mouseEvent.getY());
            } else {
                compositionManager.handleClickAt(mouseEvent.getX(), mouseEvent.getY());
            }
        }
        isDragging = false;
    }

    /**
     * Handles the different Mouse Click actions in the composition
     *
     * @param mouseEvent click on the composition
     */
    @FXML
    protected void handleCompositionClick(MouseEvent mouseEvent) {
        if (!mouseEvent.isStillSincePress()) {
            return;
        }
        this.handleStopMusic();
        if (!mouseEvent.isControlDown()) {
            //compositionManager.handleClickAt(mouseEvent.getX(), mouseEvent.getY());
        }
        else{
            //compositionManager.handleControlClickAt(mouseEvent.getX(), mouseEvent.getY());
        }
    }

    /**
     * Plays the sounds displayed in the composition.
     */
    @FXML
    protected void handlePlayMidi() {
        this.compositionManager.play();
    }

    /**
     * Stops the reproduction of the composition
     */
    @FXML
    protected void handleStopMusic() {
        this.compositionManager.stop();
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
     * A class that has two fields: the x and y coordinates.
     */
    private class Coordinates {
        double x, y;
    }
}

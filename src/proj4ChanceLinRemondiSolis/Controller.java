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

    private CompositionPaneManager compositionPaneManager;
    private Coordinates lastDragLocation = new Coordinates();
    private boolean isDragging;

    /**
     * Seeds our CompositionPaneManager and TempoLine objects with the
     * fields from the FXML file after the FXML has been initialized
     */
    public void initialize() {
        this.compositionPaneManager = new CompositionPaneManager(this.fxCompositionSheet, new TempoLine(fxTempoLine));
        handleInstrumentChange();
    }

    /**
     * Sets all of the notes to be selected and adds them to the selected list.
     */
    public void handleSelectAll() {
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
        if (!mouseEvent.isStillSincePress()) {
            return;
        }
        this.handleStopMusic();
        if (!mouseEvent.isControlDown()) {
            compositionPaneManager.handleClickAt(mouseEvent.getX(), mouseEvent.getY());
        }
    }

    /**
     * Handles the GUI's mousePressed event.
     *
     * @param mouseEvent the GUI's mouseEvent
     */
    @FXML
    public void handleMousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.isControlDown()) {
            return;
        }
        lastDragLocation.x = mouseEvent.getX();
        lastDragLocation.y = mouseEvent.getY();
        compositionPaneManager.handleDragStartedAtLocation(mouseEvent.getX(), mouseEvent.getY());
    }

    /**
     * Handles the GUI's mouseDrag event.
     *
     * @param mouseEvent the GUI's mouseEvent
     */
    @FXML
    public void handleMouseDrag(MouseEvent mouseEvent) {
        compositionPaneManager.handleDragMoved(mouseEvent.getX() - lastDragLocation.x,
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
        compositionPaneManager.handleDragEnded();
        if (!isDragging) {
            if (mouseEvent.isControlDown()) {
                compositionPaneManager.handleControlClickAt(mouseEvent.getX(), mouseEvent.getY());
            } else {
                compositionPaneManager.handleClickAt(mouseEvent.getX(), mouseEvent.getY());
            }
        }
        isDragging = false;
    }

    /**
     * Stops the reproduction of the composition
     */
    @FXML
    protected void handleStopMusic() {
        this.compositionPaneManager.stop();
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
        this.compositionPaneManager.play();
    }

    /**
     * A class that has two fields: the x and y coordinates.
     */
    private class Coordinates {
        double x, y;
    }
}

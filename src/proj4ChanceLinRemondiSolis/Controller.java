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


    /**************************************************************************
     *                                                                        *
     *                              Fields                                    *
     *                            Initializer                                 *
     *                                                                        *
     **************************************************************************/

    /**
     * Holds the fxml injected composition pane
     */
    @FXML
    private Pane fxCompositionSheet;

    /**
     * Holds the fxml injected of the TempoLine
     */
    @FXML
    private Line fxTempoLine;

    /**
     * Holds the fxml injected instrument group
     */
    @FXML
    private ToggleGroup instrumentGroup;

    /**
     * Holds the compositionManager class
     */
    private CompositionManager compositionManager;

    /**
     * Holds the coordinates of the last dragged location
     */
    private Coordinates lastDragLocation;

    /**
     * Holds whether the note is being dragged
     */
    private boolean isDragging;

    /**
     * Seeds our CompositionPaneManager and TempoLine objects with the
     * fields from the FXML file after the FXML has been initialized
     */
    public void initialize() {
        this.lastDragLocation = new Coordinates();
        this.compositionManager = new CompositionManager(this.fxCompositionSheet, new TempoLine(fxTempoLine));
        handleInstrumentChange();
    }


    /**************************************************************************
     *                                                                        *
     *             Methods for note movement and manipulation                 *
     *                                                                        *
     **************************************************************************/

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
        compositionManager.handleDragStartedAtLocation(mouseEvent.getX(),
                                                       mouseEvent.getY(),
                                                       mouseEvent.isControlDown());
    }

    /**
     * Handles the GUI's mouseDrag event.
     *
     * @param mouseEvent the GUI's mouseEvent
     */
    @FXML
    public void handleMouseDrag(MouseEvent mouseEvent) {
        compositionManager.handleDragMoved(mouseEvent.getX() - lastDragLocation.x,
                mouseEvent.getY() - lastDragLocation.y, mouseEvent.isControlDown());
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
        if (!isDragging) {
            if (mouseEvent.isControlDown()) {
                compositionManager.handleControlClickAt(mouseEvent.getX(), mouseEvent.getY());
            } else {
                compositionManager.handleClickAt(mouseEvent.getX(), mouseEvent.getY());
            }
        }
        compositionManager.handleDragEnded();
        isDragging = false;
    }

    /**
     * A class that has two fields: the x and y coordinates.
     */
    private class Coordinates {
        double x, y;
    }

    /**************************************************************************
     *                                                                        *
     *             Music reproduction handling methods                        *
     *                                                                        *
     **************************************************************************/


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

}

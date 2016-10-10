/*
 * File: CompositionManager.java
 * Names: Graham Chance, Jenny Lin, Ana Sofia Solis Canales, Mike Remondi
 * Class: CS361
 * Project: 4
 * Due Date: October 11, 2016
 */

package proj4ChanceLinRemondiSolis;

import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import javax.sound.midi.ShortMessage;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Optional;

/**
 * This class models a composition sheet manager.
 * Deals with adding and manipulating all the data
 * regarding the composition
 *
 * @author Graham Chance
 * @author Jenny Lin
 * @author Ana Sofia Solis Canales
 * @author Mike Remondi
 */
public class CompositionManager {
    private MidiPlayer midiPlayer = new MidiPlayer(100, 60);
    private TempoLine tempoLine;

    private Pane composition;
    private ArrayList<MusicalNote> notes;
    private ArrayList<MusicalNote> selectedNotes;
    private Paint instrumentColor;
    private Hashtable<Paint, Integer> channelMapping;
    private boolean isMovingNotes;
    private ResizeDirection resizeDirection = ResizeDirection.NONE;
    private Rectangle dragBox;

    /**
     * Constructor
     *
     * @param composition Where the rest of the composition sheet lives
     */
    public CompositionManager(Pane composition, TempoLine line) {
        this.composition = composition;
        this.notes = new ArrayList<>();
        this.selectedNotes = new ArrayList<>();
        this.channelMapping = new Hashtable<>();
        setChannelMapping();
        createCompositionSheet();
        this.tempoLine = line;
    }

    /**
     * Maps channel numbers to specific instrument color association
     */
    public void setChannelMapping() {
        this.channelMapping.put(Color.GRAY, 0);
        this.channelMapping.put(Color.GREEN, 1);
        this.channelMapping.put(Color.BLUE, 2);
        this.channelMapping.put(Color.GOLDENROD, 3);
        this.channelMapping.put(Color.MAGENTA, 4);
        this.channelMapping.put(Color.DEEPSKYBLUE, 5);
        this.channelMapping.put(Color.BLACK, 6);
        this.channelMapping.put(Color.BROWN, 7);
    }

    /**
     * Updates the current color associated with an instrument
     *
     * @param newInstrumentColor new color associated with new instrument
     */
    public void changeInstrument(Paint newInstrumentColor) {
        this.instrumentColor = newInstrumentColor;
    }

    /**
     * Retrieves the channel number associated with the color of
     * the current instrument
     *
     * @param instrumentColor Text color of the instrument
     * @return Channel number which corresponds to the insturment color
     */
    public int getChannelNumber(Paint instrumentColor) {
        return this.channelMapping.get(instrumentColor);
    }

    /**
     * Creates a visual representation of a composition panel
     * similar to a staff lined workbook
     */
    public void createCompositionSheet() {
        Line staffLine;
        for (int i = 0; i < 127; i++) {
            staffLine = new Line(0, i * 10, 2000, i * 10);
            staffLine.getStyleClass().add("staffLine");
            this.composition.getChildren().add(staffLine);
        }
    }


    /**
     * Creates a visual representation of the the notes
     * in the composition sheet.
     *
     * @param xPos the input x position of the note
     * @param yPos the input y position of the note
     */
    public MusicalNote addNoteToComposition(double xPos, double yPos) {
        if (yPos >= 0 && yPos < 1280) {
            Rectangle noteBox = new Rectangle(100.0, 10.0);
            if (!getNoteExistsAtCoordinates(xPos, yPos)) {
                noteBox.getStyleClass().add("note");
                noteBox.setX(xPos);
                noteBox.setY(yPos - (yPos % 10));
                noteBox.setFill(this.instrumentColor);
                this.composition.getChildren().add(noteBox);
                MusicalNote note = new MusicalNote(noteBox, getChannelNumber(noteBox.getFill()));
                this.notes.add(note);
                selectNote(note);
                return note;
            }
        }
        return null;
    }

    /**
     * Adds a note to the list of selected notes.
     *
     * @param note A MusicalNote to be added to the list.
     */
    public void addNoteToSelectedNotes(MusicalNote note) {
        this.selectedNotes.add(note);
    }

    /**
     * Clears the list of selected notes
     */
    public void clearSelectedNotes() {
        for (MusicalNote note : this.selectedNotes) {
            note.setSelected(false);
        }
        this.selectedNotes.clear();
    }

    /**
     * Checks to see if we are resizing our notes.
     *
     * @return a boolean indication of whether we are resizing notes.
     */
    public boolean isResizingNotes() {
        return resizeDirection != ResizeDirection.NONE;
    }


    /**
     * Deletes all the selected notes from the composition pane
     */
    public void deleteNotes() {
        for (MusicalNote note : this.selectedNotes) {
            this.composition.getChildren().remove(note.getNoteBox());
            this.notes.remove(note);
        }
        this.selectedNotes.clear();
    }

    /**
     * Adds the note to the sound player
     *
     * @param midiPlayer MIDI sound player
     */
    public void buildSong(MidiPlayer midiPlayer) {
        addProgramChanges(midiPlayer);
        double stopTime = 0.0;
        for (MusicalNote note : this.notes) {
            midiPlayer.addNote(
                    note.getPitch(),            //pitch
                    note.getVolume(),          //volume
                    note.getStartTick(),      //startTick
                    note.getDuration(),      //duration
                    note.getChannel(),      //channel
                    note.getTrackIndex()   //trackIndex
            );
        }
    }

    /**
     * Selects all of the notes and adds them to the selected arraylist.
     */
    public void selectAllNotes() {
        this.clearSelectedNotes();
        ArrayList<MusicalNote> notes = this.getNotes();
        for (MusicalNote note : notes) {
            note.setSelected(true);
            this.addNoteToSelectedNotes(note);
        }
    }

    /**
     * Calculates the stop time for the composition created
     *
     * @return stopTime
     */
    public double calculateStopTime() {
        double stopTime = 0.0;
        for (MusicalNote note : this.notes) {
            if (stopTime < note.getStartTick() + note.getDuration()) {
                stopTime = note.getStartTick() + note.getDuration();
            }
        }
        return stopTime;
    }

    /**
     * Checks if note is in composition at the given location
     *
     * @param xPos x position of note in composition
     * @param yPos y position of note in composition
     * @return true or false in composition
     */
    private boolean getNoteExistsAtCoordinates(double xPos, double yPos) {
        if (!this.notes.isEmpty()) {
            for (MusicalNote note : this.notes) {
                if (note.getIsInBounds(xPos, yPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the list of notes.
     *
     * @return ArrayList of MusicalNotes
     */
    public ArrayList<MusicalNote> getNotes() {
        return this.notes;
    }

    /**
     * Finds a MusicalNote, if one exists, where the mouse click is inside of
     * its rectangle.
     *
     * @param x MouseEvent x coordinate
     * @param y MouseEvent y coordinate
     */
    public Optional<MusicalNote> getNoteAtMouseClick(double x, double y) {
        for (MusicalNote note : this.notes) {
            if (note.getIsInBounds(x, y)) {
                return Optional.of(note);
            }
        }
        return Optional.empty();
    }

    public void createDragBox(double x, double y){
        this.dragBox = new Rectangle(0, 0);
        this.dragBox.setX(x);
        this.dragBox.setY(y);
        this.dragBox.getStyleClass().add("dragBox");
        this.composition.getChildren().add(this.dragBox);
    }

    /**
     * Sets the given note to selected and adds it to the selectedNotes list.
     *
     * @param note A MusicalNote to be selected
     */
    public void selectNote(MusicalNote note) {
        note.setSelected(true);
        if (!selectedNotes.contains(note)) {
            selectedNotes.add(note);
        }
    }

    /**
     * Sets the given note to unselected and removes it from the selectedNotes list.
     *
     * @param note
     */
    public void unselectNote(MusicalNote note){
        note.setSelected(false);
        if (selectedNotes.contains(note)) {
            selectedNotes.remove(note);
        }
    }

    /**
     * Handles the drag movement of the mouse.
     *
     * Responds to mouse drag movement by moving the selected notes,
     * resizing the selected notes, or creates a DragBox and selects
     * and unselects the corresponding notes.
     *
     * @param dx
     * @param dy
     */
    public void handleDragMoved(double dx, double dy) {
        if (isMovingNotes) {
            moveSelectedNotes(dx, dy);
        } else if (isResizingNotes()) {
            resizeSelectedNotes(dx);
        } else {
            this.dragBox.setWidth(this.dragBox.getWidth() + dx);
            this.dragBox.setHeight(this.dragBox.getHeight() + dy);
            Bounds bounds = this.dragBox.getBoundsInParent();
            clearSelectedNotes();
            for (MusicalNote note : notes) {
                if (note.getIsInRectangleBounds(bounds.getMinX(), bounds.getMinY(),
                        bounds.getMaxX(), bounds.getMaxY())) {
                    note.setSelected(true);
                    if (!selectedNotes.contains(note)) {
                        selectedNotes.add(note);
                    }
                }
            }
        }
    }

    /**
     * Handles the release of the drag motion of the mouse.
     *
     * Responds to the end of the drag movement of the mouse by releasing
     * the moving/resizing notes if there are any and sets all of our
     * corresponding pieces back to their resting state.
     */
    public void handleDragEnded() {
        releaseMovedNotes();
        resizeDirection = ResizeDirection.NONE;
        isMovingNotes = false;
        composition.getChildren().remove(this.dragBox);
    }

    /**
     * Handles the click of the mouse.
     *
     * Responds to the click of the mouse and selects/unselects the appropriate notes.
     *
     * @param x the x location of the mouse click on the pane
     * @param y the y location of the mouse click on the pane
     */
    public void handleClickAt(double x, double y) {
        Optional<MusicalNote> noteAtClickLocation = getNoteAtMouseClick(x, y);
        if (noteAtClickLocation.isPresent()) {
            if (noteAtClickLocation.get().isSelected()){
                return;
            }
            else {
                clearSelectedNotes();
                selectNote(noteAtClickLocation.get());
            }
        } else {
            clearSelectedNotes();
            addNoteToComposition(x, y);
        }
    }

    /**
     * Handles the control click of the mouse.
     *
     * Responds to the control click of the mouse and selects/unselects
     * the appropriate notes.
     *
     * @param x the x location of the mouse click on the pane
     * @param y the y location of the mouse click on the pane
     */
    public void handleControlClickAt(double x, double y) {
        Optional<MusicalNote> noteAtClickLocation = getNoteAtMouseClick(x, y);
        // if there is a note at the click location
        if (noteAtClickLocation.isPresent()) {
            // if this note is already selected, unselect it
            if (noteAtClickLocation.get().isSelected()){
                unselectNote(noteAtClickLocation.get());
            }
            // if it is not selected, select it
            else {
                selectNote(noteAtClickLocation.get());
            }
        }
        // add a new note and select it
        else{
            MusicalNote note = addNoteToComposition(x, y);
            selectNote(note);
        }
    }

    /**
     * Handles the start of the drag motion of the mouse.
     *
     * Checks if the mouse is on a note, if it is, it checks whether to
     * move the note(s) or to resize them, if it is not on a note, it creates
     * a DragBox.
     *
     * @param x
     * @param y
     */
    public void handleDragStartedAtLocation(double x, double y) {
        resizeDirection = ResizeDirection.NONE;
        isMovingNotes = false;
        final boolean onNote = getNoteExistsAtCoordinates(x, y);

        // if the click is on a note
        if (onNote) {
            boolean onNoteEdge = false;
            for (MusicalNote note : notes) {
                // if it is in the bounds of a note and the note is not selected
                if (note.getIsInBounds(x, y) && !note.isSelected()) {
                    clearSelectedNotes();
                    selectNote(note);
                }
                // if it is on the edge of a note
                if (note.getIsOnEdge(x, y)) {
                    onNoteEdge = true;
                        resizeDirection = ResizeDirection.RIGHT;
                    break;
                }
            }
            // if it is not on a note's edge, we are moving notes
            if (!onNoteEdge) {
                isMovingNotes = true;
            }
        }
        // click is not on a note so create a DragBox
        else {
            clearSelectedNotes();
            createDragBox(x, y);
        }
    }

    /**
     * Moves the selected note(s) by the change in mouse movement.
     *
     * @param dx the change in the mouse's x coordinate
     * @param dy the change in the mouse's y coordinate
     */
    public void moveSelectedNotes(double dx, double dy) {
        for (MusicalNote note : selectedNotes) {
            Rectangle noteBox = note.getNoteBox();
            note.setPosition(noteBox.getX() + dx, noteBox.getY() + dy);
        }
    }

    /**
     * Releases the notes that were being moved and drops them in
     * the nearest horizontal bar.
     */
    public void releaseMovedNotes() {
        for (MusicalNote note : selectedNotes) {
            note.roundToNearestYLocation();
        }
    }

    /**
     * Resizes the notes horizontally.
     *
     * @param dx the change in the mouse's x coordinate
     */
    public void resizeSelectedNotes(double dx) {
        for (MusicalNote note : selectedNotes) {
            switch (resizeDirection) {
                case RIGHT:
                    note.resizeRight(dx);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Plays the sequence of notes and animates the TempoLine.
     */
    public void play(){
        this.midiPlayer.stop();
        this.midiPlayer.clear();
        this.buildSong(this.midiPlayer);
        double stopTime = this.calculateStopTime();
        this.tempoLine.updateTempoLine(stopTime);
        playMusicAndAnimation();
    }

    /**
     * Stops the midiPlayer and hides the tempoLine.
     */
    public void stop(){
        this.midiPlayer.stop();
        this.tempoLine.stopAnimation();
        this.tempoLine.hideTempoLine();
    }

    /**
     * starts the reproduction  of the composition
     */
    private void playMusicAndAnimation() {
        this.tempoLine.playAnimation();
        this.midiPlayer.play();
    }

    /**
     * Maps instruments to a given channel in the MIDI sounds player
     *
     * @param midiPlayer MIDI sounds player
     */
    private void addProgramChanges(MidiPlayer midiPlayer) {
        midiPlayer.addMidiEvent(ShortMessage.PROGRAM_CHANGE, 0, 0, 0, 0);
        midiPlayer.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 1, 6, 0, 0, 0);
        midiPlayer.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 2, 12, 0, 0, 0);
        midiPlayer.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 3, 19, 0, 0, 0);
        midiPlayer.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 4, 21, 0, 0, 0);
        midiPlayer.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 5, 25, 0, 0, 0);
        midiPlayer.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 6, 40, 0, 0, 0);
        midiPlayer.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 7, 60, 0, 0, 0);
    }

    /**
     * The possible set of directions to resize a note.
     */
    private enum ResizeDirection {
        RIGHT, NONE;
    }
}

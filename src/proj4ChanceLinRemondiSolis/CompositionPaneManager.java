/*
 * File: CompositionSheet.java
 * Names: Graham Chance, Jenny Lin, Ana Sofia Solis Canales, Mike Remondi
 * Class: CS361
 * Project: 4
 * Due Date: October 11, 2016
 * Takes in a Pane which it then adds lines to in order
 * to create a music composition sheet
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
public class CompositionPaneManager {
    private Pane composition;
    private ArrayList<MusicalNote> notes;
    private ArrayList<MusicalNote> selectedNotes;
    private Paint instrumentColor;
    private Hashtable<Paint, Integer> channelMapping;
    private boolean isMovingNotes;
    private ResizeDirection resizeDirection = ResizeDirection.NONE;
    private DragBox dragBox;

    /**
     * Constructor
     *
     * @param composition Where the rest of the composition sheet lives
     */
    public CompositionPaneManager(Pane composition) {
        this.composition = composition;
        this.notes = new ArrayList<>();
        this.selectedNotes = new ArrayList<>();
        this.channelMapping = new Hashtable<>();
        setChannelMapping();
        createCompositionSheet();
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

    public boolean getIsResizingNotes() {
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

    public void selectNote(MusicalNote note) {
        note.setSelected(true);
        if (!selectedNotes.contains(note)) {
            selectedNotes.add(note);
        }
    }

    public void unselectNote(MusicalNote note){
        note.setSelected(false);
        if (selectedNotes.contains(note)) {
            selectedNotes.remove(note);
        }
    }

    public void handleDragMoved(double dx, double dy) {
        if (isMovingNotes) {
            moveSelectedNotes(dx, dy);
        } else if (getIsResizingNotes()) {
            resizeSelectedNotes(dx);
        } else {
            this.dragBox.getBox().setWidth(this.dragBox.getBox().getWidth() + dx);
            this.dragBox.getBox().setHeight(this.dragBox.getBox().getHeight() + dy);
            Bounds bounds = this.dragBox.getBox().getBoundsInParent();
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

    public void handleDragEnded() {
        releaseSelectedNotes();
        resizeDirection = ResizeDirection.NONE;
        isMovingNotes = false;
        composition.getChildren().remove(this.dragBox.getBox());
    }

    public void handleClickAt(double x, double y) {
        clearSelectedNotes();
        Optional<MusicalNote> noteAtClickLocation = getNoteAtMouseClick(x, y);
        if (noteAtClickLocation.isPresent()) {
            selectNote(noteAtClickLocation.get());
        } else {
            addNoteToComposition(x, y);
        }

    }

    public void handleControlClickAt(double x, double y) {
        Optional<MusicalNote> noteAtClickLocation = getNoteAtMouseClick(x, y);
        System.out.println("HERE");
        // if there is a note at the click location
        if (noteAtClickLocation.isPresent()) {
            System.out.println("HERE1");
            // if this note is already selected, unselect it
            if (noteAtClickLocation.get().isSelected()){
                System.out.println("HERE2");
                unselectNote(noteAtClickLocation.get());
            }
            // if it is not selected, select it
            else {
                System.out.println("HERE3");
                selectNote(noteAtClickLocation.get());
            }
        }
        // add a new note and select it
        else{
            System.out.println("HERE4");
            MusicalNote note = addNoteToComposition(x, y);
            selectNote(note);
        }
    }

    public void handleDragStartedAtLocation(double x, double y) {
        resizeDirection = ResizeDirection.NONE;
        isMovingNotes = false;
        final boolean onNote = getNoteExistsAtCoordinates(x, y);
        if (onNote) {
            boolean onNoteEdge = false;
            for (MusicalNote note : notes) {

                if (note.getIsInBounds(x, y) && !note.isSelected()) {
                    clearSelectedNotes();
                    selectNote(note);
                }
                if (note.getIsOnEdge(x, y)) {
                    onNoteEdge = true;
                    if (x < note.getBounds().getMinX() + note.getBounds().getWidth() / 2) {
                        resizeDirection = ResizeDirection.LEFT;
                    } else {
                        resizeDirection = ResizeDirection.RIGHT;
                    }
                    break;
                }
            }
            if (!onNoteEdge) {
                isMovingNotes = true;
            }
        } else {
            clearSelectedNotes();
            Rectangle box = new Rectangle(0, 0);
            box.setX(x);
            box.setY(y);
            box.setFill(Color.color(0.1, 0.1, 0.1, 0));
            box.getStrokeDashArray().addAll(3.0, 7.0, 3.0, 7.0);
            box.setStrokeWidth(2);
            box.setStroke(Color.YELLOW);
            this.composition.getChildren().add(box);
            this.dragBox = new DragBox(box);
        }
    }


    public void moveSelectedNotes(double dx, double dy) {
        for (MusicalNote note : selectedNotes) {
            note.move(dx, dy);
        }
    }

    public void releaseSelectedNotes() {
        for (MusicalNote note : selectedNotes) {
            note.roundYLocation();
        }
    }

    public void resizeSelectedNotes(double dx) {
        for (MusicalNote note : selectedNotes) {
            switch (resizeDirection) {
                case RIGHT:
                    note.resizeRight(dx);
                    break;
                case LEFT:
                    note.resizeLeft(dx);
                    break;
                default:
                    break;
            }
        }
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

    private enum ResizeDirection {
        RIGHT, LEFT, NONE;
    }
}

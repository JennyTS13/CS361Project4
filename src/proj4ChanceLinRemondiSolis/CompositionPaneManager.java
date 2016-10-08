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
    public void addNoteToComposition(double xPos, double yPos) {
        if (yPos >= 0 && yPos < 1280) {
            Rectangle noteBox = new Rectangle(100.0, 10.0);
            if (!inComposition(xPos, yPos)) {
                noteBox.getStyleClass().add("note");
                noteBox.setX(xPos);
                noteBox.setY(yPos - (yPos % 10));
                noteBox.setFill(this.instrumentColor);
                this.composition.getChildren().add(noteBox);
                MusicalNote note = new MusicalNote(noteBox, getChannelNumber(noteBox.getFill()));
                this.notes.add(note);
                note.setSelected(true);
                this.selectedNotes.add(note);
            }
        }
    }

    public void addNoteToSelectedNotes(MusicalNote note){
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
     * @return stopTime
     */
    public double calculateStopTime(){
        double stopTime = 0.0;
        for (MusicalNote note: this.notes){
            if (stopTime < note.getStartTick() + note.getDuration()){
                stopTime = note.getStartTick() + note.getDuration();
            }
        }
        return stopTime;
    }
    /**
     * Checks if note is in composition at the location of the mouseClick
     *
     * @param xPos x position of note in composition
     * @param yPos y position of note in composition
     * @return true or false in composition
     */
    private boolean inComposition(double xPos, double yPos) {
        if (!this.notes.isEmpty()) {
            for (MusicalNote note : this.notes) {
                Bounds bVal = note.getBounds();
                if (bVal.contains(xPos, yPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<MusicalNote> getNotes(){
        return this.notes;
    }

    public void findNoteByMouseClick(double x, double y){
        for (MusicalNote note: this.notes) {
            if (note.isInBounds(x, y)){
                note.setSelected(true);
                selectedNotes.add(note);
            }
        }
    }

    /**
     * Maps instruments to a given channel in the MIDI sounds player
     *
     * @param midiPlayer MIDI sounds player
     */
    private void addProgramChanges(MidiPlayer midiPlayer) {
        midiPlayer.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 0, 0, 0, 0, 0);
        midiPlayer.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 1, 6, 0, 0, 0);
        midiPlayer.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 2, 12, 0, 0, 0);
        midiPlayer.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 3, 19, 0, 0, 0);
        midiPlayer.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 4, 21, 0, 0, 0);
        midiPlayer.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 5, 25, 0, 0, 0);
        midiPlayer.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 6, 40, 0, 0, 0);
        midiPlayer.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 7, 60, 0, 0, 0);
    }
}

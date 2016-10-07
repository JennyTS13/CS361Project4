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

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import javax.sound.midi.ShortMessage;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This class models a compostion sheet with notes.
 *
 * @author Graham Chance
 * @author Jenny Lin
 * @author Ana Sofia Solis Canales
 * @author Mike Remondi
 */
public class CompositionSheet {
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
    public CompositionSheet(Pane composition) {
        this.composition = composition;
        this.notes = new ArrayList<>();
        this.selectedNotes = new ArrayList<>();
        this.channelMapping = new Hashtable<>();
        setChannelMapping();

        createCompositionSheet();
    }

    /**
     * Maps channel numbers to specific colors
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
     * Updates the current instrument color
     *
     * @param newInstrumentColor
     */
    public void changeInstrument(Paint newInstrumentColor) {
        this.instrumentColor = newInstrumentColor;
    }

    /**
     *
     * @param instrumentColor Text color of the instrument
     * @return Channel number which corresponds to the insturment color
     */
    public int getChannelNumber(Paint instrumentColor) {
        return this.channelMapping.get(instrumentColor);
    }

    /**
     * Generates the Composition nodes by creating a scrollPane
     * which holds a regular pane object (serves as the canvas)
     * to which we add a bunch of rectangles which serve as the lines
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
     * Generates a rectangle which represents a note on the composition Pane
     * The rectangle will be colored blue, adjusted to fit within the
     * clicked lines, and be added to the composition pane.
     *
     * @param xPos the input x position of the note
     * @param yPos the input y position of the note
     */
    public void addNoteToComposition(double xPos, double yPos) {
        if (yPos >= 0 && yPos < 1280) {
            Rectangle noteBox = new Rectangle(100.0, 10.0);
            noteBox.getStyleClass().add("note");
            noteBox.setX(xPos);
            noteBox.setY(yPos - (yPos % 10));
            noteBox.setFill(this.instrumentColor);
            this.composition.getChildren().add(noteBox);

            MusicalNote note = new MusicalNote(noteBox, getChannelNumber(noteBox.getFill()));
            this.notes.add(note);
            this.selectedNotes.add(note);
        }
    }

    /**
     * Clears the list of selected notes
     */
    public void clearSelectedNotes(){
        for(MusicalNote note : this.selectedNotes){
            note.setSelected(false);
        }
        this.selectedNotes.clear();
    }

    /**
     * Deletes all the selected notes from the composition sheet
     */
    public void deleteNotes(){
        for(MusicalNote note: this.selectedNotes){
            this.composition.getChildren().remove(note.getNoteBox());
            this.notes.remove(note);
        }
        this.selectedNotes.clear();
    }

    /**
     * Adds program changes to the MIDI player, assigning an instrument to each channel
     * @param midiPlayer MIDI player that takes in the program changes
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

    /**
     * Creates a composition using all of the rectangles in our
     * compositionSheet, based on their X and Y positions
     * (timing and pitch respectively)
     * <p>
     * Also Keeps track of the last note and when it ends
     * and returns it for use in the animation
     */
    public double buildSong(MidiPlayer midiPlayer) {
        addProgramChanges(midiPlayer);
        double stopTime = 0.0;
        for (MusicalNote note : this.notes) {
            midiPlayer.addNote(
                    note.getPitch(),       //pitch
                    note.getVolume(),      //volume
                    note.getStartTick(),   //startTick
                    note.getDuration(),    //duration
                    note.getChannel(),     //channel
                    note.getTrackIndex()   //trackIndex
            );
            //Update Stoptime if the note is the last one so far
            if (stopTime < note.getStartTick() + note.getDuration()) {
                stopTime = note.getStartTick() + note.getDuration();
            }
        }
        return stopTime;
    }
}

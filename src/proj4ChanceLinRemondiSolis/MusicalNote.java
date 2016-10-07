/*
 * File: MusicalNote.java
 * Names: Graham Chance, Jenny Lin, Ana Sofia Solis Canales, Mike Remondi
 * Class: CS361
 * Project: 4
 * Due Date: October 11, 2016
 * Takes in a Pane which it then adds lines to in order
 * to create a music composition sheet
 */

package proj4ChanceLinRemondiSolis;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This class models a musical note
 *
 * @author Graham Chance
 * @author Jenny Lin
 * @author Ana Sofia Solis Canales
 * @author Mike Remondi
 */
public class MusicalNote {

    private Rectangle noteBox;
    private int volume;
    private int channel;
    private int trackIndex;
    private boolean selected;


    /**
     * Constructor
     * @param newNoteBox Rectangle object that represents the note graphically
     * @param channel the channel that the note belongs to
     */
    public MusicalNote(Rectangle newNoteBox, int channel) {
        this.noteBox = newNoteBox;
        this.volume = 100;
        this.channel = channel;
        this.trackIndex = 0;
        this.setSelected(true);
    }

    /**
     * Accessor method for the graphical note box
     * @return Graphical note box
     */
    public Rectangle getNoteBox(){
        return this.noteBox;
    }

    /**
     * Accessor method for pitch
     * @return Pitch of the note
     */
    public int getPitch(){
        return 127 - ((int) this.noteBox.getY() / 10);
    }

    /**
     * Accessor method for volume
     * @return Volume of the note
     */
    public int getVolume(){
        return this.volume;
    }

    /**
     * Accessor method for when the note starts
     * @return Starting tick of the note
     */
    public int getStartTick(){
        return (int) this.noteBox.getX();
    }

    /**
     * Accessor method for note duration
     * @return How long the note plays
     */
    public int getDuration(){
        return (int) this.noteBox.getWidth();
    }

    /**
     * Accessor method for channel information
     * @return Channel that the note is played on
     */
    public int getChannel(){
        return this.channel;
    }

    /**
     * Accessor method for track number
     * @return Track for the note
     */
    public int getTrackIndex(){
        return this.trackIndex;
    }

    /**
     * Accessor method for if a note is selected or not
     * @return Whether or not the note is selected
     */
    public boolean isSelected(){
        return this.selected;
    }

    /**
     * Sets whether or not the note is selected
     * @param isSelected Boolean value indicating if the note is selected or not
     */
    public void setSelected(boolean isSelected){
        this.selected = isSelected;
        if(this.selected) {
            this.noteBox.setStroke(Color.RED);
            this.noteBox.setStrokeWidth(3);
        }
        else{
            this.noteBox.setStroke(Color.BLACK);
            this.noteBox.setStrokeWidth(1);
        }
    }
}
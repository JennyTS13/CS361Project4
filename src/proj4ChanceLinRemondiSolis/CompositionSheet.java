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
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

/**
 *  This class creates a composition (music sheet and notes).
 *  @author Graham Chance
 *  @author Jenny Lin
 *  @author Ana Sofia Solis Canales
 *  @author Mike Remondi
 */
public class CompositionSheet {
    private Pane composition;
    private ArrayList<Rectangle> notes = new ArrayList<Rectangle>();

    /**
     * Constructor. Takes in a pane node
     */
    public CompositionSheet(Pane composition) {
        this.composition = composition;
        createCompositionSheet();
    }

    /**
     * Generates the Composition nodes by creating a scrollPane
     * which holds a regular pane object (serves as the canvas)
     * to which we add a bunch of rectangles which serve as the lines
     */
    public void createCompositionSheet() {
        Line staffLine;
        for(int i=0; i<127; i++) {
            staffLine = new Line(0,i*10,2000,i*10);
            staffLine.getStyleClass().add( "staffLine" );
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
     *        (will be adjusted to look nice)
     */
    public void addNoteToComposition(double xPos, double yPos) {
        if (yPos > 0 && yPos < 1270) {
            Rectangle note = new Rectangle(100.0, 10.0);
            note.getStyleClass().add("note");
            note.setX(xPos); note.setY(yPos - (yPos % 10));
            this.composition.getChildren().add(note);
            this.notes.add(note);
        }
    }

    /**
     * Creates a composition using all of the rectangles in our
     * compositionSheet, based on their X and Y positions
     * (timing and pitch respectively)
     *
     * Also Keeps track of the last note and when it ends
     * and returns it for use in the animation
     */
     public double buildSong( MidiPlayer midiPlayer) {
         double stopTime = 0.0;
         for (Rectangle note : this.notes) {
             midiPlayer.addNote(
                 //pitch
                 (int) (127.0 -(note.getY() - (note.getY()%10))/10),
                 100,                    //volume
                 (int) note.getX(),      //startTick
                 (int) note.getWidth(),  //duration
                 0,                      //channel
                 0                       //trackIndex
             );
             //Update Stoptime if the note is the last one so far
             if (stopTime < note.getX()+note.getWidth()) {
                 stopTime = note.getX()+note.getWidth();
             }
         }
         return stopTime;
     }
}

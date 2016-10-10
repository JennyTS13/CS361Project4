/*
 * File: MusicalNote.java
 * Names: Graham Chance, Jenny Lin, Ana Sofia Solis Canales, Mike Remondi
 * Class: CS361
 * Project: 4
 * Due Date: October 11, 2016
 */

package proj4ChanceLinRemondiSolis;

import javafx.geometry.Bounds;
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

    /**************************************************************************
     *                                                                        *
     *                                 Field                                  *
     *                               Constructor                              *
     *                                                                        *
     **************************************************************************/


    /**
     * The minimum width a note can be.
     */
    private static final int MINIMUM_WIDTH = 5;

    /**
     * The audio volume of the note.
     */
    private static final int VOLUME = 100;

    /**
     * The rectangle representing the note visually.
     */
    private Rectangle noteBox;

    /**
     * The instrument for the note.
     */
    private int channel;

    /**
     * The track index of the note.
     */
    private int trackIndex;

    /**
     * Whether the note is currently selected.
     */
    private boolean selected;


    /**
     * Constructor
     *
     * @param newNoteBox Rectangle object that represents the note graphically
     * @param channel    the channel that the note belongs to
     */
    public MusicalNote(Rectangle newNoteBox, int channel) {
        this.noteBox = newNoteBox;
        this.channel = channel;
        this.trackIndex = 0;
        this.setSelected(true);
    }

    /**************************************************************************
     *                                                                        *
     *                        Accessor Methods                                *
     *                                                                        *
     **************************************************************************/

    /**
     * Accessor method for if a note is selected or not
     *
     * @return Whether or not the note is selected
     */
    public boolean isSelected() {
        return this.selected;
    }


    /**
     * Accessor method for the bounds of the note in the compositon
     *
     * @return bounds of the note
     */
    public Bounds getBounds() {
        return this.noteBox.getBoundsInParent();
    }


    /**
     * Checks to see if the mouse click is inside of this note's rectangle.
     *
     * @param x MouseEvent x coordinate
     * @param y MouseEvent y coordinate
     * @return boolean value for whether the click is inside of the rectangle.
     */
    public boolean getIsInBounds(double x, double y) {
        Bounds bounds = getBounds();
        return (x <= bounds.getMaxX() &&
                y <= bounds.getMaxY() &&
                x >= bounds.getMinX() &&
                y >= bounds.getMinY());
    }

    /**
     * Checks to see if the note is within the given bounds of a rectangle.
     *
     * @param rectXMin the smallest x coordinate of the rectangle
     * @param rectYMin the smallest y coordinate of the rectangle
     * @param rectXMax the biggest x coordinate of the rectangle
     * @param rectYMax the bigget y coordinate of the rectangle
     * @return a boolean value indicating whether this note is within the rectangle.
     */
    public boolean getIsInRectangleBounds(double rectXMin, double rectYMin,
                                          double rectXMax, double rectYMax) {
        Bounds bounds = getBounds();
        boolean xInBounds = (bounds.getMinX() < rectXMax && bounds.getMinX() > rectXMin) ||
                (bounds.getMaxX() > rectXMin && bounds.getMaxX() < rectXMax);
        boolean yInBounds = (bounds.getMinY() > rectYMin && bounds.getMinY() < rectYMax) ||
                (bounds.getMaxY() > rectYMin && bounds.getMaxY() < rectYMax);
        return xInBounds && yInBounds;
    }

    /**
     * Checks whether the coordinates of a mouse click are within 5 pixels
     * of the right side of the note's rectangle.
     *
     * @param x the mouse click's x coordinate
     * @param y the mouse click's y coordinate
     * @return a boolean value indicating whether the mouse click is on the note's edge.
     */
    public boolean getIsOnEdge(double x, double y) {
        Bounds bounds = getBounds();
        return y < bounds.getMaxY() &&
                y > bounds.getMinY() &&
                ((x >= bounds.getMaxX() - 5) && x < bounds.getMaxX());
    }

    /**
     * Accessor method for the graphical note box
     *
     * @return Graphical note box
     */
    public Rectangle getNoteBox() {
        return this.noteBox;
    }

    /**
     * Accessor method for pitch
     *
     * @return Pitch of the note
     */
    public int getPitch() {
        return 127 - ((int) this.noteBox.getY() / 10);
    }

    /**
     * Accessor method for volume
     *
     * @return Volume of the note
     */
    public int getVolume() {
        return this.VOLUME;
    }

    /**
     * Accessor method for when the note starts
     *
     * @return Starting tick of the note
     */
    public int getStartTick() {
        return (int) this.noteBox.getX();
    }

    /**
     * Accessor method for note duration
     *
     * @return How long the note plays
     */
    public int getDuration() {
        return (int) this.noteBox.getWidth();
    }

    /**
     * Accessor method for channel information
     *
     * @return Channel that the note is played on
     */
    public int getChannel() {
        return this.channel;
    }

    /**
     * Accessor method for track number
     *
     * @return Track for the note
     */
    public int getTrackIndex() {
        return this.trackIndex;
    }

    /**************************************************************************
     *                                                                        *
     *                        Manipulation Methods                            *
     *                                                                        *
     **************************************************************************/


    /**
     * Rounds the y coordinate of the note's rectangle in order to snap to a
     * space between two horizontal bars.
     */
    public void roundToNearestYLocation() {
        if (noteBox.getY() % 10 < 5) {
            setPosition(noteBox.getX(), noteBox.getY() - (noteBox.getY() % 10));
        } else {
            setPosition(noteBox.getX(), noteBox.getY() + (10 - (noteBox.getY() % 10)));
        }
    }

    /**
     * Resizes the note's rectangle in the right direction.
     *
     * @param dx the distance to move the right edge
     */
    public void resizeRight(double dx) {
        if (noteBox.getWidth() < MINIMUM_WIDTH) {
            this.noteBox.setWidth(MINIMUM_WIDTH);
        } else {
            this.noteBox.setWidth(noteBox.getWidth() + dx);
        }
    }


    /**
     * Moves the note's rectangle to the given coordinates
     *
     * @param x the x coordinate to move to
     * @param y the y coordinate to move to
     */
    public void setPosition(double x, double y) {
        this.noteBox.setX(x);
        this.noteBox.setY(y);
    }

    /**
     * Sets whether or not the note is selected
     *
     * @param isSelected Boolean value indicating if the note is selected or not
     */
    public void setSelected(boolean isSelected) {
        this.selected = isSelected;
        if (this.selected) {
            this.noteBox.setStroke(Color.RED);
            this.noteBox.setStrokeWidth(3);
        } else {
            this.noteBox.setStroke(Color.BLACK);
            this.noteBox.setStrokeWidth(1);
        }
    }
}
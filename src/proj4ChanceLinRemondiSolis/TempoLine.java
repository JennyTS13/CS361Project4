/*
 * File: TempoLine.java
 * Names: Graham Chance, Jenny Lin, Ana Sofia Solis Canales, Mike Remondi
 * Class: CS361
 * Project: 4
 * Due Date: October 11, 2016
 */

package proj4ChanceLinRemondiSolis;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 * This class is a syncable vertical progress line.
 *
 * @author Graham Chance
 * @author Jenny Lin
 * @author Ana Sofia Solis Canales
 * @author Mike Remondi
 */

public class TempoLine {
    private Line tempoLine;
    private TranslateTransition tempoAnimation = new TranslateTransition();


    /**
     * Constructor
     *
     * @param tempoLine graphic representation of reproduction time
     */
    public TempoLine(Line tempoLine) {
        this.tempoLine = tempoLine;
        this.tempoAnimation.setNode(this.tempoLine);
        this.tempoAnimation.setInterpolator(Interpolator.LINEAR); // Don't ease
        this.tempoAnimation.setOnFinished(event -> hideTempoLine());
    }

    /**
     * if there is a tempo line in the composition pane, remove it.
     */
    public void hideTempoLine() {
        this.tempoLine.setVisible(false);
    }

    /**
     * tells whether the tempo bar is visible
     *
     * @return true or false
     */
    public boolean isVisible() {
        return this.tempoLine.isVisible();
    }

    /**
     * Updates the information in the tempo line and animation for
     * the reproduction of a composition
     *
     * @param stopTime this is the stop location (e.g time) which is the
     *                 location of the right edge of the final note to be played
     */
    public void updateTempoLine(double stopTime) {
        this.tempoAnimation.stop();
        this.tempoLine.setTranslateX(0);
        this.tempoAnimation.setDuration(new Duration(stopTime * 10));
        this.tempoAnimation.setToX(stopTime);
        this.tempoLine.setVisible(true);
    }

    /**
     * Accessor method for the animation field's play feature
     */
    public void playAnimation() {
        this.tempoAnimation.play();
    }

    /**
     * Accessor method for the animation field's stop feature
     */
    public void stopAnimation() {
        this.tempoAnimation.stop();
    }
}

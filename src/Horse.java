import java.awt.*;

/**
 * Horse data will be handled here
 * 
 * @author Usman Siddiqui
 * @version 2.0
 */
public class Horse {
    private String horseName;
    private String horseSymbol;
    private double horseConfidence;
    private int distanceTravelled = 0;
    private boolean hasFallen = false;
    private String breed;
    private Color coatColor;
    private int wins = 0;
    private int losses = 0;

    public Horse(String horseSymbol, String horseName, double horseConfidence, String breed, Color coatColor) {
        this.horseName = horseName;
        this.horseSymbol = horseSymbol;
        this.horseConfidence = horseConfidence;
        this.breed = breed;
        this.coatColor = coatColor;
    }

    public void fall() {
        this.setConfidence(this.horseConfidence - 0.05);
        this.hasFallen = true;
    }

    public double getConfidence() {
        return this.horseConfidence;
    }

    public int getDistanceTravelled() {
        return this.distanceTravelled;
    }

    public String getName() {
        return this.horseName;
    }

    public String getSymbol() {
        return this.horseSymbol;
    }

    public void goBackToStart() {
        this.distanceTravelled = 0;
        this.hasFallen = false;
    }

    public boolean hasFallen() {
        return this.hasFallen;
    }

    public void moveForward() {
        this.distanceTravelled += 1;
    }

    public void setConfidence(double newConfidence) {
        if (newConfidence >= 0 && newConfidence <= 1) {
            this.horseConfidence = newConfidence;
        }
    }

    public void setSymbol(String newSymbol) {
        this.horseSymbol = newSymbol;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Color getCoatColor() {
        return coatColor;
    }

    public void setCoatColor(Color coatColor) {
        this.coatColor = coatColor;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public void addWin() {
        wins++;
    }

    public void addLoss() {
        losses++;
    }

    @Override
    public String toString() {
        return horseName + " " + horseSymbol;
    }
}

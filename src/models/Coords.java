package models;

import java.io.Serializable;

/**
 * Created by Maciej on 2015-10-12.
 */
public class Coords implements Serializable {
    private double x;
    private double y;

    public Coords(Coords coords) {
        this.x = coords.getX();
        this.y = coords.getY();
    }

    public Coords(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "x = " + Integer.toString((int) x) + " y = " + Integer.toString((int) y);
    }
}

package models;

import interfaces.Draw;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import models.buildings.Building;
import models.vehicles.BaseVehicle;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Maciej on 2015-10-19.
 */
public class Connection implements Draw, Serializable{
    private Building first;
    private Building second;
    private Coords startFirst;
    private Coords startSecond;
    private Coords stopFirst;
    private Coords stopSecond;
    private transient Line line = new Line();
    private transient Line line2 = new Line();
    private ArrayList<BaseVehicle> firstVehicles = new ArrayList<>();
    private ArrayList<BaseVehicle> secondVehicles = new ArrayList<>();


    public Connection(Building first, Building second) {
        this.second = second;
        this.first = first;
        first.getConnections().add(this);
        second.getConnections().add(this);
        countCoordinates();
        first.getNeighbors().add(second);
        second.getNeighbors().add(first);
    }

    /**get Coords of route start from building
     * @param building
     * @return
     */
    public Coords getStart(Building building) {
        if (building == first) {
            return startFirst;
        } else {
            return startSecond;
        }
    }

    /**get Coords of route end from building
     * @param building
     * @return
     */
    public Coords getEnd(Building building) {
        if (building == first) {
            return stopFirst;
        } else {
            return stopSecond;
        }
    }

    /**Count angle of connection
     * @return
     * @throws Exception
     */
    private double getA() throws Exception {
        if (second.getCoords().getY() == first.getCoords().getY()) {
            throw new Exception("horizontally");
        } else if (second.getCoords().getX() == first.getCoords().getX()) {
            throw new Exception("vertically");
        }
        return (second.getCoords().getY() - first.getCoords().getY()) / (second.getCoords().getX() - first.getCoords().getX());
    }

    /**
     * Count Coords of each route in this connection
     */
    private void countCoordinates() {
        try {
            double x = getA();
            double y = 1.0d;
            double distance = Math.sqrt((x * x) + (y * y));
            double multiplier = WorldHelper.CONNECTION_DISTANCE / distance;
            x *= multiplier;
            y *= multiplier;
            setPoints(x, y);
        } catch (Exception e) {
            if (e.getMessage().equals("horizontally")) {
                setPoints(0, WorldHelper.CONNECTION_DISTANCE);
            } else if (e.getMessage().equals("vertically")) {
                setPoints(WorldHelper.CONNECTION_DISTANCE, 0);
            }
        }
    }

    /**set Coords of each route in this connection
     * @param x
     * @param y
     */
    private void setPoints(double x, double y) {
        startFirst = new Coords(first.getCenter().getX() + x, first.getCenter().getY() - y);
        stopFirst = new Coords(first.getCenter().getX() - x, first.getCenter().getY() + y);
        startSecond = new Coords(second.getCenter().getX() - x, second.getCenter().getY() + y);
        stopSecond = new Coords(second.getCenter().getX() + x, second.getCenter().getY() - y);
    }

    /**Check if this is connection between two building
     * @param one
     * @param two
     * @return
     */
    public boolean isThis(Building one, Building two) {
        return (one == first && two == second) || (one == second && two == first);
    }

    /**Add Vehicle to the connection starting in building
     * @param building
     * @param vehicle
     */
    public synchronized void addVehicle(Building building, BaseVehicle vehicle) {
        if (building == first) {
            firstVehicles.add(vehicle);
        } else {
            secondVehicles.add(vehicle);
        }
    }

    /**remove Vehicle from the connection starting in building
     * @param building
     * @param vehicle
     */
    public synchronized void removeVehicle(Building building, BaseVehicle vehicle) {
        if (building == first) {
            firstVehicles.remove(vehicle);
        } else {
            secondVehicles.remove(vehicle);
        }
    }

    /**Check if the vehicle could move on this connection
     * @param building
     * @param vehicle
     * @return
     */
    public synchronized boolean canIMove(Building building, BaseVehicle vehicle) {
        if (building == first) {
            return canIMove(firstVehicles, vehicle);
        } else {
            return canIMove(secondVehicles, vehicle);
        }
    }

    /**Check if the vehicle could move on this connection
     * @param vehicles
     * @param vehicle
     * @return
     */
    private synchronized boolean canIMove(ArrayList<BaseVehicle> vehicles, BaseVehicle vehicle) {
        int position = vehicles.indexOf(vehicle);
        return !(position > 0 && WorldHelper.distanceBetween(vehicles.get(position).getCoords(),
                vehicles.get(position - 1).getCoords()) < WorldHelper.DISTANCE_TO_BLOCK);
    }

    /**Draw connections on Pane
     * @param pane
     */
    @Override
    public void draw(Pane pane) {
        line = new Line(startFirst.getX(), startFirst.getY(), stopSecond.getX(), stopSecond.getY());
        line2 = new Line(stopFirst.getX(), stopFirst.getY(), startSecond.getX(), startSecond.getY());
        line.setStroke(Color.GRAY);
        line2.setStroke(Color.GRAY);
        pane.getChildren().add(line);
        pane.getChildren().add(line2);
    }

    @Override
    public void relocate() {

    }

    @Override
    public Image getImage() {
        return null;
    }
}
